//$ bin/sessionjc tests/src/Test.sj -d tests/classes/
//$ bin/sessionj -cp tests/classes/ Test

import java.io.*;
import java.net.*;
import java.util.*;

import sessionj.runtime.*;
import sessionj.runtime.net.*;

public class Test  
{		
	static protocol foo { !<String> }
	static protocol bar { !<String>.@(foo) }	
	
	public static void main(String[] args) throws Exception
	{		
		final noalias protocol p1 { cbegin.rec X [?{L1: !<String>.#X}] }
		//final noalias protocol p1 { cbegin.?{L1: !<String>} }
		final noalias protocol p2 { cbegin.?[!<int>]* }
		
		final noalias SJService c1 = SJService.create(p1, "A", 1234);
		final noalias SJService c2 = SJService.create(p2, "B", 1234);
		
		final noalias SJSocket s1, s2;
		
		try (s1, s2)
		{
			s1 = c1.request();
			
			m(s1);
		}
		finally
		{
			
		}
	}
	
	private static void m(final noalias rec X [?{L1: !<String>.#X}] s1) throws Exception
	{
		s1.recursion(X)
		{
			s1.inbranch()
			{
				case L1:
				{
					//s1.send("C");
					//mm(s1);
					
					//s1.recurse(X);
					
					selector.register(s1);
				}
			}
		}
	}
	
	private static void mm(final noalias !<String> s1) throws Exception
	{
		s1.send("C");		
	}
}
