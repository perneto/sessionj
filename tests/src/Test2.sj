//$ bin/sessionjc tests/src/Test2.sj -d tests/classes/
//$ bin/sessionj -cp tests/classes/ Test2

import java.io.*;
import java.net.*;
import java.util.*;

import sessionj.runtime.*;
import sessionj.runtime.net.*;

public class Test2 extends SJThread 
{	
	private Test dummy;
	
	public void srun(noalias @(Test.p) s)
	//public void srun(noalias !<String> s)
	//public void srun(noalias !<int> s)
	{
		try (s)
		{
			s.send("");
			//s.send(123);
		}
		catch (Exception x)
		{
			
		}
	}
}
