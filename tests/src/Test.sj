//$ bin/sessionjc tests/src/Test.sj -d tests/classes/
//$ bin/sessionj -cp tests/classes/ Test

import java.io.*;
import java.net.*;
import java.util.*;

import sessionj.runtime.*;
import sessionj.runtime.net.*;
import sessionj.runtime.util.*;

import thesis.benchmark.ServerMessage;
import thesis.benchmark.bmark2.NoaliasBinaryTree;

public class Test  
{		
	private noalias String m;
	
	public static void main(String[] args) throws Exception
	{		
		/*noalias Test test = new Test();
		
		//ServerMessage msg = new ServerMessage(-1, args[0], Integer.parseInt(args[1]));
		NoaliasBinaryTree msg = NoaliasBinaryTree.createDepth(-1, 0, Integer.parseInt(args[0]));
		
		msg.incrementMessageId();
		
		System.out.println(msg);
		System.out.println(SJRuntimeUtils.serializeObject(msg).length);
		System.out.println(SJRuntimeUtils.serializeObject(new Integer(1)).length);
		System.out.println(SJRuntimeUtils.serializeObject(new Boolean(true)).length);*/
		
		protocol p1 cbegin.?[!<int>]*
		protocol p2 cbegin.![!<int>]*

		noalias SJSocket s1;
		noalias SJSocket s2;
		noalias SJSocket s1b;
		noalias SJSocket s2b;
		try (s1, s2, s1b, s2b)
		{
			s1 = SJService.create(p1, "", 1234).request();
			//s1b = SJService.create(p1, "", 1234).request();
			s2 = SJService.create(p1, "", 1234).request();
			//s2b = SJService.create(p2, "", 1234).request();
			
			<s1, s2>.inwhile() {
			//<s1, s2>.outwhile(new Boolean(false).booleanValue()) {
			//<s2>.outwhile(<s1>.inwhile()) {
			//<s2, s2b>.outwhile(<s1, s1b>.inwhile()) {
				//<s1, s2, s1b, s2b>.send(1234);
				<s1, s2>.send(1234);
			}		
			/*s2.outwhile(new Boolean(false).booleanValue()) {
				s2.send(1234);
			}
			s1.inwhile() 
			{
				s1.send(1234);
			}*/				
		}
		finally { }
	}
}
