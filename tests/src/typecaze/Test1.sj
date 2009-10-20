//$ bin/sessionjc tests/src/typecaze/Test1.sj -d tests/classes/
//$ bin/sessionj -cp tests/classes/ typecaze.Test1

package typecaze;

import sessionj.runtime.*;
import sessionj.runtime.net.*;

public class Test1 
{
	private static protocol p { ?(String) }
	private static protocol p_service { cbegin.@(p) } 

	public static void main(String[] args) throws SJIOException, SJIncompatibleSessionException 
	{
		final noalias SJSelector selector = SJRuntime.selectorFor(p);
		
		final noalias SJService c = SJService.create(p_service, "localhost", 8888);
				
		try (selector)
		{
			noalias SJSocket s;
			
			try (s)			       
			{
				s = c.request();
				
				selector.registerReceive(s);

				noalias SJSocket s1;
					
				try (s1)
				{
					s1 = selector.select(SJSelector.RECEIVE);
					
          typecase (s1) // FIXME: currently, 
          {
            when (@(p)) 
            {
            	String m = (String) s1.receive();
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
		finally
		{
			
		}		
	}
}
