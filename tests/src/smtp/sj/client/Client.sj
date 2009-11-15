//$ bin/sessionjc -sourcepath tests/src/smtp/sj/';'tests/src/smtp/sj/messages/';'tests/src/smtp/sj/client/ tests/src/smtp/sj/client/Client.sj -d tests/classes/
//$ bin/sessionjc -cp tests/classes/ tests/src/smtp/sj/client/Client.sj -d tests/classes/
//$ bin/sessionj -cp tests/classes/ smtp.sj.client.Client false smtp.cc.ic.ac.uk 25 

package smtp.sj.client;

import java.net.InetAddress;
import java.util.*;

import sessionj.runtime.*;
import sessionj.runtime.net.*;
import sessionj.runtime.net.SJSessionParameters.*;
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
		.?(RcptAck)
		.!<Data>
		.?(DataAck)
		.!<Data>
		.?(DataAck)
		.!<Quit>
		.?(QuitAck)
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
			
			String msg;
			
			msg = "HELO " + fqdn + "\n";
			System.out.print("Sending: " + msg);			
			s.send(new Helo(msg));			
			System.out.println("Received: " + (HeloAck) s.receive());
			
			msg = "MAIL FROM:<rhu@doc.ic.ac.uk>\n";
			System.out.print("Sending: " + msg);
			s.send(new Mail(msg));
			System.out.println("Received: " + (MailAck) s.receive());
			
			msg = "RCPT TO:<ray.zh.hu@gmail.com>\n";
			System.out.print("Sending: " + msg);
			s.send(new Rcpt(msg));
			System.out.println("Received: " + (RcptAck) s.receive());
			
			msg = "DATA";
			System.out.print("Sending: " + msg);
			s.send(new Data(msg));
			System.out.println((DataAck) s.receive());
			
			msg = "test\n.\n";
			System.out.print("Sending: " + msg);
			s.send(new Data(msg));
			System.out.println("Received: " + (DataAck) s.receive());
			
			msg = "QUIT\n";
			System.out.print("Sending: " + msg);
			s.send(new Quit(msg));
			System.out.println("Received: " + (QuitAck) s.receive());
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
