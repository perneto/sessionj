//$ bin/sessionjc -sourcepath tests/src/esmtp/sj/messages/';'tests/src/esmtp/sj/server/ tests/src/esmtp/sj/server/Server4.sj -d tests/classes/
//$ bin/sessionjc -cp tests/classes/ tests/src/esmtp/sj/server/Server4.sj -d tests/classes/
//$ bin/sessionj -cp te\sts/classes/ esmtp.sj.server.Server4 false 2525 a

package esmtp.sj.server;

import java.util.*;

import sessionj.runtime.*;
import sessionj.runtime.net.*;
import sessionj.runtime.net.SJSessionParameters.*;
import sessionj.runtime.transport.*;
import sessionj.runtime.transport.tcp.*;
import sessionj.runtime.transport.sharedmem.*;
import sessionj.runtime.transport.httpservlet.*;
import sessionj.runtime.session.*;

//import esmtp.sj.SJSmtpFormatter;
import esmtp.sj.messages.*;

public class Server4
{			
	static protocol smtp_server_mail
	{
		?(EmailAddress)
		.!{
			$250: 
				!<MailAckBody>
		}
	}
	
	static protocol smtp_server_rcpt
	{
		?(EmailAddress)
		.!{
			$250: 
				!<RcptAckBody>
		}
	}
	
	static protocol smtp_server_data	
	{
		!<DataAck>
		.?(MessageBody)
		.!<MessageBodyAck>
	}
	
	static protocol smtp_server_loop
	{
		rec LOOP
		[
			?{
				MAIL_FROM:
					@(smtp_server_mail)
					.#LOOP,						
				RCPT_TO: // This session type does not ensure RCPT comes after a valid MAIL, DATA after a valid RCPT, etc.
					@(smtp_server_rcpt)
					.#LOOP,
				DATA:
					@(smtp_server_data)
					.#LOOP,
				QUIT:
					!<QuitAck>
			}
		]		
	}
	
	static protocol smtp_event_ehlo
	{
		?(Ehlo)
		.!<EhloAck>		
		.@(smtp_server_loop)
	}

	static protocol smtp_event_mail
	{
		@(smtp_server_mail)
		.@(smtp_server_loop)
	}
	
	static protocol smtp_event_rcpt
	{
		@(smtp_server_rcpt)		
		.@(smtp_server_loop)
	}
	
	static protocol smtp_event_data
	{
		?(MessageBody)
		.!<MessageBodyAck>
		.@(smtp_server_loop)
	}
	
	static protocol smtp_event_loop
	{
		?{
			MAIL_FROM:
				@(smtp_server_mail)
				.@(smtp_server_loop),
			RCPT_TO: 
				@(smtp_server_rcpt)
				.@(smtp_server_loop),
			DATA:
				@(smtp_server_data)
				.@(smtp_server_loop),
			QUIT:
				!<QuitAck>
		}		
	}
	
	static protocol smtp_server_body
	{
		!<ServerGreeting>				
		.?(Ehlo)
		.!<EhloAck>		
		.@(smtp_server_loop)
	}
		
	static protocol p_select
	{
		@(smtp_server_body),
		@(smtp_event_ehlo),
		@(smtp_event_loop),
		@(smtp_event_mail),
		@(smtp_event_rcpt),
		@(smtp_event_data)
	}
	
	static protocol smtp_server
	{
		sbegin		
		.@(smtp_server_body)
	}		
	
	public void run(boolean debug, int port, String setups) throws Exception
	{
		// Although we currently need to give e.g. "s" and "a" as the negotiation and session transports for asynch. comm. support, non-SJ compatibility mode disables the session initiation handshake and so we can just use "a" directly (as the negotiation (and session) transport). 
		//SJSessionParameters params = SJTransportUtils.createSJSessionParameters(SJCompatibilityMode.CUSTOM, setups, transports, SmtpServerFormatter.class);
		SJSessionParameters params = SJTransportUtils.createSJSessionParameters(SJCompatibilityMode.CUSTOM, setups, setups, SmtpServerFormatter.class);
				
		final noalias SJSelector sel = SJRuntime.selectorFor(p_select);
		
		try (sel)
		{			
			noalias SJServerSocket ss;
			
			try (ss)
			{
				ss = SJServerSocket.create(smtp_server, port, params);
				
				sel.registerAccept(ss);
				
				while (true)
				{			
					noalias SJSocket s;			
					
					try (s)
					{
						s = sel.select();
	
						typecase (s)
						{
							when (@(smtp_server_body))
							{
								//220 smtp1.cc.ic.ac.uk ESMTP Exim 4.69 Sun, 22 Nov 2009 14:36:55 +0000
								ServerGreeting greeting = new ServerGreeting("server greeting");
								System.out.print("Sending: " + greeting);			
								s.send(greeting);
								
								sel.registerInput(s);
							}
							when (@(smtp_event_ehlo))
							{
								System.out.print("Received: " + (Ehlo) s.receive());
								
								/*250-smtp1.cc.ic.ac.uk Hello tui.doc.ic.ac.uk [146.169.2.83]
								250-SIZE 26214400
								250-PIPELINING
								250-STARTTLS
								250 HELP*/
								EhloAck ehloAck = new EhloAck("250 ehlo ack");
								System.out.print("Sending: " + ehloAck);			
								s.send(ehloAck);
								
								s.recursion(LOOP)
								{
									sel.registerInput(s);
								}
							}
							when (@(smtp_event_loop))
							{								
								s.inbranch()
								{
									case MAIL_FROM:
									{
										sel.registerInput(s);													
									}
									case RCPT_TO:
									{
										sel.registerInput(s);
									}
									case DATA:
									{
										//354 Enter message, ending with "." on a line by itself
										DataAck dataAck = new DataAck("data ack"); // Unlike the "ack bodies", already prefixes the reply code.
										System.out.print("Sending: " + dataAck);			
										s.send(dataAck);											
										
										sel.registerInput(s);
									}
									case QUIT:
									{
										//221 smtp1.cc.ic.ac.uk closing connection
										QuitAck quitAck = new QuitAck("quit ack"); // Unlike the "ack bodies", already prefixes the reply code.
										System.out.print("Sending: " + quitAck);			
										s.send(quitAck);	
									}
								}								
							}
							when (@(smtp_event_mail))
							{
								System.out.print("Received: " + (EmailAddress) s.receive());
								
								//250 OK
								s.outbranch($250)
								{
									MailAckBody mailAckBody = new MailAckBody(SmtpMessage.SPACE_SEPARATOR + "mail ack body"); // "Ack bodies" need the space/hyphen separator. 
									System.out.print("Sending: " + mailAckBody);			
									s.send(mailAckBody);
								}
								
								s.recursion(LOOP)
								{
									sel.registerInput(s);
								}
							}
							when (@(smtp_event_rcpt))
							{
								System.out.print("Received: " + (EmailAddress) s.receive());
								
								//250 Accepted
								s.outbranch($250)
								{
									RcptAckBody rcptAckBody = new RcptAckBody(SmtpMessage.SPACE_SEPARATOR + "rcpt ack body");
									System.out.print("Sending: " + rcptAckBody);			
									s.send(rcptAckBody);
								}					
								
								s.recursion(LOOP)
								{
									sel.registerInput(s);
								}								
							}
							when (@(smtp_event_data))
							{
								System.out.print("Received: " + (MessageBody) s.receive());
								
								//250 OK id=1NCDaj-0001P0-V7
								MessageBodyAck messageBodyAck = new MessageBodyAck("message body ack");
								System.out.print("Sending: " + messageBodyAck);			
								s.send(messageBodyAck);
								
								s.recursion(LOOP)
								{
									sel.registerInput(s);
								}								
							}
						}
					}
					catch (SJIOException ioe)
					{
						ioe.printStackTrace();
					}
				}
			}
			finally
			{
				
			}
		}				
		finally 
		{
			
		}
	}
	
	public static void main(String[] args) throws Exception
	{
		boolean debug = Boolean.parseBoolean(args[0]);
		int port = Integer.parseInt(args[1]);
		
		String setups = args[2];
		//String transports = args[3];					
		
		new Server4().run(debug, port, setups);
	}
}
