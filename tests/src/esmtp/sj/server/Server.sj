//$ bin/sessionjc -cp tests/classes/ tests/src/esmtp/sj/server/Server.sj -d tests/classes/
//$ bin/sessionj -cp tests/classes/ esmtp.sj.server.Server false 8888 

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

import esmtp.sj.SJSmtpFormatter;
import esmtp.sj.messages.*;

public class Server
{			
	public static final MyMessage LAB = new MyMessage("LAB");
	
	public protocol p_server
	{

	}
	
	public void run(boolean debug, int port) throws Exception
	{
		
	}

	public static void main(String[] args) throws Exception
	{
		boolean debug = Boolean.parseBoolean(args[0]);
		int port = Integer.parseInt(args[1]);
		
		new Server().run(debug, port);
	}
}
