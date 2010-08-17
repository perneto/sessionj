//$ bin/sessionjc tests/src/Test.sj -d tests/classes/
//$ bin/sessionj -cp tests/classes/ Test

import java.io.*;
import java.net.*;
import java.util.*;

import sessionj.runtime.*;
import sessionj.runtime.net.*;
import sessionj.runtime.util.*;

import thesis.benchmark.bmark1.ServerMessage;

public class Test  
{		
	public static void main(String[] args) throws Exception
	{		
		ServerMessage msg = new ServerMessage(-1, args[0], Integer.parseInt(args[1]));
		
		System.out.println(msg);
		System.out.println(SJRuntimeUtils.serializeObject(msg).length);
		System.out.println(SJRuntimeUtils.serializeObject(new Integer(1)).length);
		System.out.println(SJRuntimeUtils.serializeObject(new Boolean(true)).length);
	}
}
