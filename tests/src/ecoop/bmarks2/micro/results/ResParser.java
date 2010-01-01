import java.io.*;
import java.math.*;
import java.net.*;
import java.util.*;

public class ResParser
{
	public enum Version 
	{ 
		JT, JE, ST, SE; 
	
		public static Version parseVersion(String m)
		{
			if (m.equals("JT"))
			{
				return Version.JT;
			}
			else if (m.equals("JE"))
			{
				return Version.JE;
			}
			else if (m.equals("ST"))
			{
				return Version.ST;
			}
			else if (m.equals("SE"))
			{
				return Version.SE;
			}
			else
			{
				throw new RuntimeException("Version value cannot be parsed from : " + m);
			}
		}
	}	
	
	private static final String RESULT_PREFIX = "[TimerClient] Session duration: ";
	private static final String RESULT_SUFFIX = " nanos";
	
	//private static final Version[] versions = new Version[] {Version.JT};
	private static Version[] versions;
	private static final int[] sizes = new int[] {100, 1024};
	private static final int[] lengths = new int[] {0, 1, 10, 100};
	private static final int[] numClients = new int[] {10, 100, 300, 500};
	
	public static void main(String[] args) throws Exception
	{
		String inFile = args[0];
		int repeats = Integer.parseInt(args[1]);
		String outFile = args[2];
		String vers = args[3];
		
		ResParser.versions = new Version[] {Version.parseVersion(vers)}; 
		
		List<Results> results = new LinkedList<Results>();
		
		parseResults(inFile, results, repeats);
		
		System.out.println("Parsing summary: ");
		
		for (Results res : results)
		{
			System.out.println(res.params);
		}				
		
		//sort(results); // Already in order.
		
		writeResults(results, outFile, repeats);
	}
	
	private static void writeResults(List<Results> results, String outFile, int repeats) throws IOException
	{
		FileWriter fw = null;
		PrintWriter pw = null; 
		
		try
		{
			fw = new FileWriter(outFile);
		  pw = new PrintWriter(fw);	
			
			for (Version version : versions)
			{
				for (int size : sizes)
				{
					pw.print(size + ",");
					
					boolean first1 = true;
					
					for (int length : lengths)
					{
						if (first1)
						{
							first1 = false;
						}
						else
						{
							pw.print(",");
						}
						
						pw.print(length + ",");
						
						boolean first2 = true;
						
						for (int clients : numClients)
						{
							if (first2)
							{
								first2 = false;
							}
							else
							{
								pw.print(",,");
							}
							
							pw.print(clients + ",");
							
							Results res = filterResults(results, new Parameters(version, clients, size, length, repeats));
							
							for (long value : res.results)
							{
								pw.print(value + ",");
							}
							
							pw.println();
						}
					}
					
					pw.println();
				}
			}
		}
		finally
		{
			if (pw != null)
			{
				pw.flush();
				pw.close();
			}
			
			if (fw != null)
			{				
				fw.close();				
			}
		}		
	}
	
	private static Results filterResults(List<Results> results, Parameters params)
	{
		for (Results res : results)
		{
			if (res.params.equals(params))
			{
				return res;
			}
		}
		
		throw new RuntimeException("Results not found for parameters: " + params);
	}
	
	private static void parseResults(String inFile, List<Results> results, int repeats) throws IOException
	{
		BufferedReader in = null;

		try
		{
			in = new BufferedReader(new FileReader(inFile)); 
			
			String m; 
			
			do
			{
				in.mark(1024 * 4);
				
				m = in.readLine();				
			}
			while (Parameters.parseParameters(m) == null);

			in.reset();
			
			while ((m = in.readLine()) != null)
			{
				Results current = ((LinkedList<Results>) results).peekLast();				
				
				Parameters params = Parameters.parseParameters(m); 				 
								
				if (current == null || !params.equals(current.params))
				{
					params.trial = repeats;
					
					current = new Results(params, repeats);
					
					results.add(current);
				}
				
				long[] values = new long[repeats];
				
				for (int i = 0; i < repeats; i++)
				{
					values[i] = parseResult(in.readLine());	
				}									
				
				current.addTrial(values);				
			}			
		}		
		finally
		{
			if (in != null)
			{
				in.close();
			}
		}		
	}
	
	private static long parseResult(String m)
	{
		m = m.substring(RESULT_PREFIX.length());
		m = m.substring(0, m.length() - RESULT_SUFFIX.length());
		
		return Long.parseLong(m);
	}
}

class Results
{
	public Parameters params;	
	public long[] results;	
	public int trials = 0;
	
	public Results(Parameters params, int repeats)
	{
		this.params = params;
		this.results = new long[repeats];
	}
	
	public void addTrial(long[] toAdd) // FIXME: doing too much rounding overall, should use e.g. BigDecimal.
	{
		for (int i = 0; i < results.length; i++)
		{
			long x = results[i] * trials;
			
			results[i] = Math.round(((double) (x + toAdd[i])) / ((double) (trials + 1)));
		}
		
		trials++;
	}
	
	public ResParser.Version getVersion()
	{
		return params.version;
	}
	
	public int getClients()
	{
		return params.clients;
	}
	
	public int getSize()
	{
		return params.size;
	}
	
	public int getLength()
	{
		return params.length;
	}
	
	public String toString()
	{
		return params.toString() + "\n" + Arrays.toString(results);
	}
}

class Parameters
{
	private static final String PARAMETERS_HEADER = "PARAMETERS:";

	public ResParser.Version version;
	public int clients; // numClients;
	public int size; // serverMessageSize;
	public int length; // sessionLength;
	public int trial; // As a result of parsing, this field contains the trial number. But we overwrite this field from the outsie when we use this class to contain the total number of trials.

	public Parameters(ResParser.Version version, int clients, int size, int length, int trial)
	{
		this.version = version;
		this.clients = clients;
		this.size = size;
		this.length = length;
		this.trial = trial;
	}
	
	//Parameters: version=JE, clients=10, size=100, length=1, trial=0
	public static Parameters parseParameters(String m)
	{
		m = m.toUpperCase();
		
		if (!m.startsWith(PARAMETERS_HEADER))
		{
			return null;
		}
		
		m = m.substring(PARAMETERS_HEADER.length());
		
		String[] params = m.split(",", -1);
		
		ResParser.Version version = null;
		int clients = -1; 
		int size = -1; 
		int length = -1; 
		int trial = -1;
		
		for (String param : params)
		{
			param = param.trim();
			
			if (param.startsWith("VERSION="))
			{
				version = ResParser.Version.parseVersion(param.substring("VERSION=".length()));
			}
			else if (param.startsWith("CLIENTS="))
			{
				clients = Integer.parseInt(param.substring("CLIENTS=".length()));
			}
			else if (param.startsWith("SIZE="))
			{
				size = Integer.parseInt(param.substring("SIZE=".length()));
			}
			else if (param.startsWith("LENGTH="))
			{
				length = Integer.parseInt(param.substring("LENGTH=".length()));
			}
			else if (param.startsWith("TRIAL="))
			{
				trial = Integer.parseInt(param.substring("TRIAL=".length()));
			}
			else
			{
				throw new RuntimeException("[ResParser] Bad parameter: " + param);
			}
		}
		
		return new Parameters(version, clients, size, length, trial);
	}
	
	public boolean equals(Object o)
	{
		if (!(o instanceof Parameters))
		{
			return false;
		}
		
		Parameters them = (Parameters) o;
		
		return (version == them.version && clients == them.clients && size == them.size && length == them.length); // Not including trial.
	}
	
	public String toString()
	{
		return "Parameters[version=" + version + ", clients=" + clients + ", size=" + size + ", length=" + length + ", trial=" + trial + "]";
	}
}