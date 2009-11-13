//$ bin/sessionjc -cp tests/classes/ tests/src/smtp/sj/client/Client.sj -d tests/classes/
//$ bin/sessionj -cp tests/classes/ smtp.sj.client.Client false localhost 8888 

package smtp.sj.client;

import java.util.*;

import sessionj.runtime.*;
import sessionj.runtime.net.*;
import sessionj.runtime.net.SJSessionParameters.*;
import sessionj.runtime.transport.*;
import sessionj.runtime.transport.tcp.*;
import sessionj.runtime.transport.sharedmem.*;
import sessionj.runtime.transport.httpservlet.*;
import sessionj.runtime.session.*;

import smtp.sj.SJSmtpFormatter;
import smtp.sj.messages.*;
import smtp.sj.server.Server;

public class Client
{			
	private protocol p_client
	{
		//^(Server.p_server)
		cbegin
		.?(String)
		.?(String)
		.?(MyMessage)
		//.!<String>
	}
	
	public void run(boolean debug, String server, int port) throws Exception
	{
		SJSessionParameters sparams = SJTransportUtils.createSJSessionParameters(SJCompatibilityMode.CUSTOM, new SJSmtpFormatter());
		
		final noalias SJSocket s;	
			
		try (s)
		{
			s = SJService.create(p_client, server, port).request(sparams);
			
			System.out.println("Received: " + (String) s.receive());
			System.out.println("Received: " + (String) s.receive());
			System.out.println("Received: " + (MyMessage) s.receive());
			
			//s.send("D");
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
