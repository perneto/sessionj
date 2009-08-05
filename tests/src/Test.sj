//$ bin/sessionjc tests/src/Test.sj -d tests/classes/
//$ bin/sessionj -cp tests/classes/ Test

import java.io.*;
import java.net.*;
import java.util.*;

import sessionj.runtime.*;
import sessionj.runtime.net.*;

public class Test  
{		
	public static void main(String[] args) throws Exception
	{
		final noalias protocol p1 { cbegin.?[!<int>]* }
		final noalias protocol p2 { cbegin.?[!<int>]* }
		
		final noalias SJService c1 = SJService.create(p1, "", 1234);
		final noalias SJService c2 = SJService.create(p2, "", 1234);
		
		noalias SJSocket s1, s2;
		
		try (s1, s2)
		{
			s1 = c1.request();
			s2 = c2.request();
			
			<s1, s2>.inwhile()
			{
				<s1, s2>.send(123);
			}
		}
		finally
		{
			
		}
	}
}
