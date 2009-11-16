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
	private protocol p_client
	{
		//^(Server.p_server)
		cbegin
		.?(ServerGreeting)
		.!<Helo>
		.?(HeloAck)
		.!<Mail>
		.?(MailAck)
		.!<Rcpt>
		.?{
			$2: 
				?(Rcpt2Ack)
				.!<Data>
				.?(DataAck)
				.!<MessageBody>
				.?(MessageBodyAck)
				.!<Quit>
				.?(QuitAck),
			$5:	
				?(Rcpt5Ack)	
				.!<Quit>
				.?(QuitAck)		
		}
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
			
			Rcpt rcpt = new Rcpt("ray.zh.hu@gmail.com");
			System.out.print("Sending: " + rcpt);
			s.send(rcpt);			
			
			s.inbranch()
			{
				case $2:
				{
					System.out.println("Received: " + (Rcpt2Ack) s.receive());
					
					Data data = new Data();
					System.out.print("Sending: " + data);
					s.send(data);
					System.out.println("Received: " + (DataAck) s.receive());
					
					MessageBody body = new MessageBody("SUBJECT:subject\n\nbody");				
					System.out.print("Sending: " + body);
					s.send(body);
					System.out.println("Received: " + (MessageBodyAck) s.receive());
					
					Quit quit = new Quit();
					System.out.print("Sending: " + quit);
					s.send(quit);
					System.out.println("Received: " + (QuitAck) s.receive());
				}
				case $5:
				{
					System.out.println("Received: " + (Rcpt5Ack) s.receive());
					
					Quit quit = new Quit();
					System.out.print("Sending: " + quit);
					s.send(quit);
					System.out.println("Received: " + (QuitAck) s.receive());					
				}
			}	
		}
		finally
		{
			
		}
	}
	
	public static void main(String[] args) throws Exception
	{
		boolean debug = Boolean.parseBoolean(args[0]);
		 
		String server = args[1];
		int port = Integer.parseInt(args[2]);
		
		new Client().run(debug, server, port);
	}
}
