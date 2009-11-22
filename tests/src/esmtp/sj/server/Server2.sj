//$ bin/sessionjc -sourcepath tests/src/esmtp/sj/messages/';'tests/src/esmtp/sj/server/ tests/src/esmtp/sj/server/Server2.sj -d tests/classes/
//$ bin/sessionjc -cp tests/classes/ tests/src/esmtp/sj/server/Server2.sj -d tests/classes/
//$ bin/sessionj -cp tests/classes/ esmtp.sj.server.Server2 false 2525 

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

public class Server2
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
	
	static protocol smtp_server
	{
		sbegin
		.!<ServerGreeting>				
		.?(Ehlo)
		.!<EhloAck>		
		.@(smtp_server_loop)
	}
		
	public void run(boolean debug, int port) throws Exception
	{
		SJSessionParameters params = SJTransportUtils.createSJSessionParameters(SJCompatibilityMode.CUSTOM, new SmtpServerFormatter());
		
		final noalias SJServerSocket ss;
		
		try (ss)
		{
			ss = SJServerSocket.create(smtp_server, port, params);
			
			while (true)
			{			
				final noalias SJSocket s;			
				
				try (s)
				{
					s = ss.accept();
					
					ServerGreeting greeting = new ServerGreeting("server greeting");
					System.out.print("Sending: " + greeting);			
					s.send(greeting);			
					System.out.print("Received: " + (Ehlo) s.receive());
	
					EhloAck ehloAck = new EhloAck("250 ehlo ack");
					System.out.print("Sending: " + ehloAck);			
					s.send(ehloAck);			
					
					doMainLoop(s);
					
					/*s.recursion(LOOP)
					{
						s.inbranch()
						{
							case MAIL_FROM:
							{
								doMailFrom(s);								
																
								s.recurse(LOOP);								
							}
							case RCPT_TO:
							{
								doRcptTo(s);
								
								s.recurse(LOOP);
							}
							case DATA:
							{
								doData(s);	
								
								s.recurse(LOOP);
							}
							case QUIT:
							{
								QuitAck quitAck = new QuitAck("quit ack");
								System.out.print("Sending: " + quitAck);			
								s.send(quitAck);	
							}			
						}
					}*/
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

	private static final void doMainLoop(final noalias @(smtp_server_loop) s) throws SJIOException, ClassNotFoundException
	{
		s.recursion(LOOP)
		{
			s.inbranch()
			{
				case MAIL_FROM:
				{
					doMailFrom(s);								
					doMainLoop(s);													
				}
				case RCPT_TO:
				{
					doRcptTo(s);
					doMainLoop(s);
				}
				case DATA:
				{
					doData(s);	
					doMainLoop(s);
				}
				case QUIT:
				{
					QuitAck quitAck = new QuitAck("quit ack");
					System.out.print("Sending: " + quitAck);			
					s.send(quitAck);	
				}
			}
		}
	}
	
	private static final void doMailFrom(final noalias @(smtp_server_mail) s) throws SJIOException, ClassNotFoundException
	{
		System.out.print("Received: " + (EmailAddress) s.receive());
		
		s.outbranch($250)
		{
			MailAckBody mailAckBody = new MailAckBody(SmtpMessage.SPACE_SEPARATOR + "mail ack body"); // "Ack bodies" need the space/hyphen separator. 
			System.out.print("Sending: " + mailAckBody);			
			s.send(mailAckBody);
		}
	}
	
	private static final void doRcptTo(final noalias @(smtp_server_rcpt) s) throws SJIOException, ClassNotFoundException
	{
		System.out.print("Received: " + (EmailAddress) s.receive());
		
		s.outbranch($250)
		{
			RcptAckBody rcptAckBody = new RcptAckBody(SmtpMessage.SPACE_SEPARATOR + "rcpt ack body");
			System.out.print("Sending: " + rcptAckBody);			
			s.send(rcptAckBody);
		}
	}
	
	private static final void doData(final noalias @(smtp_server_data) s) throws SJIOException, ClassNotFoundException
	{	
		DataAck dataAck = new DataAck("data ack"); // Unlike the "ack bodies", already prefixes the reply code.
		System.out.print("Sending: " + dataAck);			
		s.send(dataAck);	
		
		System.out.print("Received: " + (MessageBody) s.receive());
		MessageBodyAck messageBodyAck = new MessageBodyAck("message body ack");
		System.out.print("Sending: " + messageBodyAck);			
		s.send(messageBodyAck);
	}
		
	public static void main(String[] args) throws Exception
	{
		boolean debug = Boolean.parseBoolean(args[0]);
		int port = Integer.parseInt(args[1]);
		
		new Server2().run(debug, port);
	}
}