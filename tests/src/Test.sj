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
		final noalias protocol p1 { cbegin.?{$1:!<int>} }
		final noalias protocol p2 { cbegin.?[!<int>]* }
		
		final noalias SJService c1 = SJService.create(p1, "", 1234);
		final noalias SJService c2 = SJService.create(p2, "", 1234);
		
		noalias SJSocket s1, s2;
		
		try (s1, s2)
		{
			s1 = c1.request();
			//s2 = c2.request();

			/*int i=0;
			s1.outwhile(s2.inwhile() ; i<10)
			{
			    ++i;
				<s1, s2>.send(123);
			}*/
			
			s1.inbranch()
			{
				case $1: 
				{
					s1.send(123);
					//s1.send("ABC");
				}
			}
		}
		finally
		{
			
		}
	}
}
