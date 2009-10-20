//$ bin/sessionjc tests/src/selector/Test1.sj -d tests/classes/
//$ bin/sessionj -cp tests/classes/ selector.Test1

package selector;

import sessionj.runtime.*;
import sessionj.runtime.net.*;

public class Test1 
{
	private static protocol p1 { !<String> } 
	private static protocol p { sbegin.@(p1) }
	private static protocol p_select { @(p1) }

	public static void main(String[] args) throws SJIOException, SJIncompatibleSessionException 
	{
		final noalias SJSelector selector = SJRuntime.selectFor(p_select);
		
		try (selector)
		{
			noalias SJServerSocket ss;
			
			try (ss)			       
			{
				ss = SJServerSocket.create(p, 8888);
				
				selector.registerAccept(ss);

				while (true) 
				{
					noalias SJSocket s;
					
					try (s)
					{
						s = selector.select(SJSelector.ACCEPT);
						
            typecase (s) 
            {
              when (@(p1)) 
              {
              	//s.send("ABC");
              	//s.send(123);
              }
            }
          }
					finally 
					{
						
					}
				}
			}
			finally
			{
				
			}
		}
		finally
		{
			
		}		
	}
}
