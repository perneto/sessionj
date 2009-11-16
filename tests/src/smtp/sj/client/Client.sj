//$ bin/sessionjc -sourcepath tests/src/smtp/sj/';'tests/src/smtp/sj/messages/';'tests/src/smtp/sj/client/ tests/src/smtp/sj/client/Client.sj -d tests/classes/
//$ bin/sessionjc -cp tests/classes/ tests/src/smtp/sj/client/Client.sj -d tests/classes/
//$ bin/sessionj -cp tests/classes/ smtp.sj.client.Client false smtp.cc.ic.ac.uk 25 

package smtp.sj.client;

import java.net.InetAddress;
import java.util.*;

import sessionj.runtime.*;
import sessionj.runtime.net.*;
import sessionj.runtime.net.SJSessionParameters;
import sessionj.runtime.transport.*;
import sessionj.runtime.transport.tcp.*;
import sessionj.runtime.transport.sharedmem.*;
import sessionj.runtime.transport.httpservlet.*;
import sessionj.runtime.session.*;

//import smtp.sj.SJSmtpFormatter; // Doesn't work when specifying -sourcepath (without -cp).
import smtp.sj.*;
import smtp.sj.messages.*;
//import smtp.sj.server.Server;

public class Client
{			
	/*private protocol p_client
	{
		//^(Server.p_server)
		cbegin
		.?(ServerGreeting)
		.!<Helo>.?(HeloAck)
		.!<Mail>.?(MailAck)
		.!<Rcpt>.?{
			$2: 
				?(Rcpt2Ack)
				.!<Data>.?(DataAck)
				.!<MessageBody>.?(MessageBodyAck)
				.!<Quit>.?(QuitAck),
			$5:	
				?(Rcpt5Ack)	
				.!<Quit>.?(QuitAck)		
		}
	}*/
	
	private protocol p_client
	{
		//^(Server.p_server)
		cbegin
		.?(ServerGreeting)
		.!<Helo>.?(HeloAck)
		.!<Mail>.?(MailAck)
		.rec RCPT [
			!{
				RCPT:
					!<RcptTo>
					.?{
						$2: 
							?(Rcpt2Ack)
							.#RCPT,
						$5:	
							?(Rcpt5Ack)	
					},
			  DATA:
			  	?(DataAck)
					.!<MessageBody>.?(MessageBodyAck)			
			}
		]
		.!<Quit>.?(QuitAck)		
	}
	
	public void run(boolean debug, String server, int port) throws Exception
	{
		final String fqdn = InetAddress.getLocalHost().getHostName().toString(); //getCanonicalHostName().toString();
		
		System.out.println("fqdn: " + fqdn);
		
		SJSessionParameters sparams = SJTransportUtils.createSJSessionParameters(SJCompatibilityMode.CUSTOM, new SJSmtpFormatter());
		
		final noalias SJSocket s;	
			
		try (s)
		{
			System.out.println("Requesting SMTP session with: " + server + ":" + port);
			
			s = SJService.create(p_client, server, port).request(sparams);	
			
			System.out.println((ServerGreeting) s.receive());
			
			Helo helo = new Helo(fqdn);
			System.out.print("Sending: " + helo);			
			s.send(helo);			
			System.out.println("Received: " + (HeloAck) s.receive());
			
			Mail mail = new Mail("rhu@doc.ic.ac.uk");
			System.out.print("Sending: " + mail);
			s.send(mail);
			System.out.println("Received: " + (MailAck) s.receive());
			
			int i = 0;
			
			s.recursion(RCPT)
			{
				if (i++ < 2)
				{
					s.outbranch(RCPT)
					{
						RcptTo rcptTo = new RcptTo((i == 1) ? "ray.zh.hu@gmail.com" : "ray.zh.hu@hotmail.com");
						System.out.print("Sending: " + rcptTo);
						s.send(rcptTo);									
						
						s.inbranch()
						{
							case $2:
							{
								System.out.println("Received: " + "2" + (Rcpt2Ack) s.receive()); // Need to re-add the "2" that was already consumed by the inbranch.
								
								s.recurse(RCPT);								
							}
							case $5:
							{
								System.out.println("Received: " + "5" + (Rcpt5Ack) s.receive());
								
								//doQuit(s); // Currently, delegation within loops completely forbidden.					
							}
						}					
					}
				}
				else
				{
					s.outbranch(DATA)
					{
						System.out.println("Received: " + (DataAck) s.receive());
						
						MessageBody body = new MessageBody("SUBJECT:subject\n\nbody");				
						System.out.print("Sending: " + body);
						s.send(body);
						System.out.println("Received: " + (MessageBodyAck) s.receive());
						
						//doQuit(s);					
					}
				}
			}
			
			doQuit(s);
		}
		finally
		{
			
		}
	}
	
	private static void doQuit(final noalias !<Quit>.?(QuitAck) s) throws SJIOException, ClassNotFoundException
	{
		Quit quit = new Quit();
		System.out.print("Sending: " + quit);
		s.send(quit);
		System.out.println("Received: " + (QuitAck) s.receive());		
	}
	
	public static void main(String[] args) throws Exception
	{
		boolean debug = Boolean.parseBoolean(args[0]);
		 
		String server = args[1];
		int port = Integer.parseInt(args[2]);
		
		new Client().run(debug, server, port);
	}
}
