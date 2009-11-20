//$ bin/sessionjc tests/src/ticketagency/Customer.sj -d tests/classes/
//$ bin/sessionj -cp tests/classes/ ticketagency.Customer d d localhost 9999 100.00

package ticketagency;

import java.net.*;
import java.util.*;

import sessionj.runtime.*;
import sessionj.runtime.net.*;
import sessionj.runtime.transport.tcp.*;
import sessionj.runtime.transport.httpservlet.*;

class Customer 
{
	final noalias protocol p_ca 
	{
		cbegin
		.![
			!<String>
			.?(Double)
		]*
		.!{
				ACCEPT: !<Address>.?(Date)
			, REJECT:
		}
	}

	private String TRAVEL_METHOD = "Paris by Eurostar";
	private Double MAX_PRICE;
	private Address ADDRESS = new Address("Customer's address.");

	Customer(String setups, String transports, String addr_a, int port_a, double maxPrice) throws Exception 
	{
		MAX_PRICE = new Double(maxPrice);

		final noalias SJService c_ca = SJService.create(p_ca, addr_a, port_a);
		final noalias SJSocket s_ca;
		
		boolean decided = false;
		int retry = 3;

		try (s_ca)
		{
			s_ca = c_ca.request(createSJSessionParameters(setups, transports));
			
			s_ca.outwhile(!decided && (retry-- > 0)) 
			{
				s_ca.send(TRAVEL_METHOD);
				
				Double cost = (Double) s_ca.receive();
				
				System.out.println("Received quote: " + cost);
				
				if (cost.compareTo(MAX_PRICE) < 0) 
				{
					decided = true;
				}
			}
			
			if (retry >= 0) 
			{
				s_ca.outbranch(ACCEPT) 
				{					
					System.out.println("Quote accepted.");
					
					s_ca.send(ADDRESS);
					
					//System.out.println("Dispatch date: " + (Date) s_ca.receive());
					System.out.println("Received dispatch date: " + s_ca.receive());
				}
			}
			else 
			{
				s_ca.outbranch(REJECT) 
				{
				
					System.out.println("Quote rejected.");
					
				}			
			}
		}
		finally
		{
			
		}
	}

	public static void main(String[] args) throws Exception
	{
		String setups = args[0];
		String transports = args[1];
		
		String host_a = args[2];
		int port_a = Integer.parseInt(args[3]);
		
		double maxPrice = Double.parseDouble(args[4]);
		
		new Customer(setups, transports, host_a, port_a, maxPrice);
	}
	
	private static SJSessionParameters createSJSessionParameters(String setups, String transports)
	{
		SJSessionParameters params;
		
		if (setups.contains("d") && transports.contains("d"))
		{
			params = new SJSessionParameters();
		}
		else
		{
			List ss = new LinkedList();
			List ts = new LinkedList();				
			
			parseTransportFlags(ss, setups);
			parseTransportFlags(ts, transports);
								
			params = new SJSessionParameters(ss, ts);
		}

		return params;
	}
	
	private static void parseTransportFlags(List ts, String transports)
	{
		if (transports.contains("d"))
		{
			//ts.add(new SJFifoPair());
			ts.add(new SJStreamTCP());
			
			return;
		}
		
		char[] cs = transports.toCharArray();
		
		for (int i = 0; i < cs.length; i++)
		{
			switch (cs[i])
			{
				/*case 'f':
				{
					ts.add(new SJFifoPair());
					
					break;
				}*/
				case 's':
				{
					ts.add(new SJStreamTCP());
					
					break;
				}					
				case 'm':
				{			
					ts.add(new SJManualTCP());
					
					break;
				}					
				case 'h':
				{			
					ts.add(new SJHTTPServlet());
					
					break;
				}
			}
		}					
	}	
}
