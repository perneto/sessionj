import sessionj.runtime.*;
import sessionj.runtime.net.*;

public class Test1 
{
	private static protocol p2 { ?(String) }
	private static protocol p1 { !<int>.@(p2) } 
	private static protocol p { sbegin.@(p1) }
	private static protocol p_select { @(p1), @(p2) }

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
						s = selector.select(SJSelector.ACCEPT | SJSelector.RECEIVE);
						
            typecase (s) 
            {
              when (@(p1)) 
              {
              	s.send(123);
              	
              	selector.registerReceive(s);
              }
              when (@(p2)) 
              {
              	System.out.println("Received: " + (String) s.receive());
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
