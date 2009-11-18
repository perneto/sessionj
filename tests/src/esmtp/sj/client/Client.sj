//$ bin/sessionjc -sourcepath tests/src/esmtp/sj/';'tests/src/esmtp/sj/messages/';'tests/src/esmtp/sj/client/ tests/src/esmtp/sj/client/Client.sj -d tests/classes/
//$ bin/sessionjc -cp tests/classes/ tests/src/esmtp/sj/client/Client.sj -d tests/classes/
//$ bin/sessionj -cp tests/classes/ esmtp.sj.client.Client false esmtp.cc.ic.ac.uk 25 

package esmtp.sj.client;

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

//import esmtp.sj.SJSmtpFormatter; // Doesn't work when specifying -sourcepath (without -cp).
import esmtp.sj.*;
import esmtp.sj.messages.*;
//import esmtp.sj.server.Server;

public class Client
{			
	private MailAckTwoDigits foo = null;
	
	private protocol p_client
	{
		//^(Server.p_server)
		cbegin
		.?(ServerGreeting)
		.!<Helo>.?(HeloAck)
		.rec MAIL [
		  !<Mail>
		  .rec MAIL_ACK [
		     ?{
		    	  $2:
		    	  	?(MailAckTwoDigits)
		    	  	//?(MailAck)
		    		  .?{
		    	 			_HYPHEN: 
		    	 				?(MailTwoAck)
		    				  .#MAIL_ACK,
		    				_SPACE: // Need full "dependently-typed" labels (in order to use SJSmtpFormatter.SPACE here).
		    					?(MailTwoAck)
		     			},
		    	 $5:
   	 				?(MailFiveAck)
   	 				.#MAIL
		     }
		  ]		  
		]
		.rec RCPT [
			!{
				RCPT:
					!<RcptTo>
					.?{
						$2: 
							?(RcptTwoAck)
							.#RCPT,
						$5:	
							?(RcptFiveAck)	
					},
			  DATA: 
					!<DataLineFeed>.?(DataAck)
					.!<MessageBody>.?(MessageBodyAck)			
			}
		]
		.!<Quit>.?(QuitAck)		
	}
	
	public void run(boolean debug, String server, int port) throws Exception
	{
		Scanner sc = new Scanner(System.in);
		
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
			
			/*System.out.print("Sender's address? (e.g. sender@domain.com): ");			
			Mail mail = new Mail(readUserInput(sc));
			System.out.print("Sending: " + mail);
			s.send(mail);
			System.out.println("Received: " + (MailAck) s.receive());*/
									
			s.recursion(MAIL)
			{
				System.out.print("Sender's address? (e.g. sender@domain.com): ");			
				Mail mail = new Mail(readUserInput(sc));
				System.out.print("Sending: " + mail);
				s.send(mail);
				
				s.recursion(MAIL_ACK)
				{
					s.inbranch()
					{						
						case $2:
						{
							String ack = ((MailAckTwoDigits) s.receive()).toString();							
							
							s.inbranch()
							{
								case _HYPHEN:
								{
									System.out.println("Received: " + "2" + ack + "-" + (MailTwoAck) s.receive());									
									
									s.recurse(MAIL_ACK);
								}
								case _SPACE:
								{
									System.out.println("Received: " + "2" + ack + " " + (MailTwoAck) s.receive());
								}
							}
						}
						case $5:
						{
							/*String ack = ((MailAckTwoDigits) s.receive()).toString();							
							
							System.out.println("Received: " + "5" + ack + (MailFiveAck) s.receive());*/
							
							System.out.println("Received: " + "5" + (MailFiveAck) s.receive());
							
							s.recurse(MAIL);
						}
					}
				}
			}
			
			boolean firstIteration = true;
			boolean anotherRecipient = true;
						
			s.recursion(RCPT)
			{
				if (firstIteration)
				{
					firstIteration = false;
				}
				else
				{				
					System.out.print("Another recipient? [y/n]: ");
					
					String reply = readUserInput(sc);
					
					if (reply.equals("y"))
					{
						anotherRecipient = true;
					}
				}
							
				if (anotherRecipient)
				{
					s.outbranch(RCPT)
					{
						System.out.print("Recipient's address?: ");
						RcptTo rcptTo = new RcptTo(readUserInput(sc));
						System.out.print("Sending: RCPT" + rcptTo); // Need to re-add the "RCPT" that was sent by the outbranch.
						s.send(rcptTo);									
						
						s.inbranch()
						{
							case $2:
							{
								System.out.println("Received: " + "2" + (RcptTwoAck) s.receive()); // Need to re-add the "2" that was already consumed by the inbranch.
								
								s.recurse(RCPT);								
							}
							case $5:
							{
								System.out.println("Received: " + "5" + (RcptFiveAck) s.receive());
								
								//doQuit(s); // Currently, delegation within loop contexts completely forbidden, even though this branch does not loop.					
							}
						}					
					}
					
					anotherRecipient = false;
				}
				else
				{
					s.outbranch(DATA)
					{
						DataLineFeed dataLF = new DataLineFeed();
						System.out.print("Sending: DATA" + dataLF); 
						s.send(dataLF);												
						System.out.println("Received: " + (DataAck) s.receive());
						
						System.out.print("Message subject?: ");
						String subject = readUserInput(sc);
						
						System.out.print("Message body? (Enter ends the message.): ");
						String text = readUserInput(sc);
						
						MessageBody body = new MessageBody("SUBJECT:" + subject + "\n\n" + text);				
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
	
	private static String readUserInput(Scanner sc)
	{
		String m = sc.nextLine();
		
		//return m.substring(0, m.length() - "\n".length()); // FIXME: "\n" not supported as a call target.
		//return m.substring(0, m.length() - 1);
		
		return m;
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
