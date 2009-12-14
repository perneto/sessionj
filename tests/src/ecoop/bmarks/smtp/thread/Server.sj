//$ bin/sessionj -cp tests/classes ecoop.bmarks.smtp.thread.Server false 2525 s

package ecoop.bmarks.smtp.thread;

import java.util.*;

import sessionj.runtime.*;
import sessionj.runtime.net.*;
import sessionj.runtime.net.SJSessionParameters.*;
import sessionj.runtime.transport.*;
import sessionj.runtime.transport.tcp.*;
import sessionj.runtime.transport.sharedmem.*;
import sessionj.runtime.transport.httpservlet.*;
import sessionj.runtime.session.*;

import ecoop.bmarks.smtp.messages.*;
import ecoop.bmarks.smtp.SmtpServerFormatter;

public class Server
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

	static protocol smtp_server_body
	{
		!<ServerGreeting>				
		.?(Ehlo)
		.!<EhloAck>		
		.@(smtp_server_loop)
	}
	
	static protocol smtp_server
	{
		sbegin		
		.@(smtp_server_body)
	}
		
	public void run(boolean debug, int port, String setups) throws Exception
	{
		//SJSessionParameters params = SJTransportUtils.createSJSessionParameters(SJCompatibilityMode.CUSTOM, setups, transports, SmtpServerFormatter.class);
		SJSessionParameters params = SJTransportUtils.createSJSessionParameters(SJCompatibilityMode.CUSTOM, setups, setups, SmtpServerFormatter.class);
		
		final noalias SJServerSocket ss;
		
		try (ss)
		{
			ss = SJServerSocket.create(smtp_server, port, params);
			
			while (true)
			{			
				noalias SJSocket s;			
				
				try (s)
				{
					s = ss.accept();

					s.spawn(new Server3Thread());
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

	private class Server3Thread extends SJThread
	{
		public void srun(noalias @(smtp_server_body) s)
		{
			try (s)
			{
				//220 smtp1.cc.ic.ac.uk ESMTP Exim 4.69 Sun, 22 Nov 2009 14:36:55 +0000
				ServerGreeting greeting = new ServerGreeting("localhost ESMTP blah blah blah");
				//System.out.print("Sending: " + greeting);			
				s.send(greeting);			
				Ehlo ehlo = (Ehlo) s.receive();
				//System.out.print("Received: " + ehlo);
	
				/*250-smtp1.cc.ic.ac.uk Hello tui.doc.ic.ac.uk [146.169.2.83]
				250-SIZE 26214400
				250-PIPELINING
				250-STARTTLS
				250 HELP*/
				EhloAck ehloAck = new EhloAck("250 Hello foobar [1.2.3.4]");
				//System.out.print("Sending: " + ehloAck);			
				s.send(ehloAck);			
				
				doMainLoop(s);			
			}
			catch (Exception x)
			{
				x.printStackTrace();
			}
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
					//221 smtp1.cc.ic.ac.uk closing connection
					QuitAck quitAck = new QuitAck("localhost closing connection"); // Unlike the "ack bodies", already prefixes the reply code.
					System.out.print("Sending: " + quitAck);			
					s.send(quitAck);	
				}
			}
		}
	}
	
	private static final void doMailFrom(final noalias @(smtp_server_mail) s) throws SJIOException, ClassNotFoundException
	{
        EmailAddress email = (EmailAddress) s.receive();
		//System.out.print("Received: " + email);
		
		//250 OK
		s.outbranch($250)
		{
			MailAckBody mailAckBody = new MailAckBody(SmtpMessage.SPACE_SEPARATOR + "OK"); // "Ack bodies" need the space/hyphen separator. 
			//System.out.print("Sending: " + mailAckBody);			
			s.send(mailAckBody);
		}
	}
	
	private static final void doRcptTo(final noalias @(smtp_server_rcpt) s) throws SJIOException, ClassNotFoundException
	{
	    EmailAddress email = (EmailAddress) s.receive();
		//System.out.print("Received: " + email);
		
		//250 Accepted
		s.outbranch($250)
		{
			RcptAckBody rcptAckBody = new RcptAckBody(SmtpMessage.SPACE_SEPARATOR + "Accepted");
			//System.out.print("Sending: " + rcptAckBody);			
			s.send(rcptAckBody);
		}
	}
	
	private static final void doData(final noalias @(smtp_server_data) s) throws SJIOException, ClassNotFoundException
	{	
		//354 Enter message, ending with "." on a line by itself
		DataAck dataAck = new DataAck("Enter message, ending with \".\" on a line by itself"); // Unlike the "ack bodies", already prefixes the reply code.
		//System.out.print("Sending: " + dataAck);			
		s.send(dataAck);	
				
		MessageBody body = (MessageBody) s.receive();
		//System.out.print("Received: " + body);
		
		//250 OK id=1NCDaj-0001P0-V7
		MessageBodyAck messageBodyAck = new MessageBodyAck("OK id=1ABCde-2345F6-G7");
		//System.out.print("Sending: " + messageBodyAck);			
		s.send(messageBodyAck);
	}
		
	public static void main(String[] args) throws Exception
	{
		boolean debug = Boolean.parseBoolean(args[0]);
		int port = Integer.parseInt(args[1]);

		String setups = args[2];
		//String transports = args[3];			
		
		new Server().run(debug, port, setups);
	}
}
