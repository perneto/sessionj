//$ javac -cp tests/classes/ tests/src/smtp/java/client/Client.java -d tests/classes/
//$ java -cp tests/classes/ smtp.java.client.Client localhost 8888 /

package smtp.java.client;

import java.io.*;
import java.nio.charset.*;
import java.net.*;
import java.util.*;

class Client
{
	public static void main(String[] args) throws Exception
	{
		Charset cs = Charset.forName("UTF8");

		System.out.println("Using charset: " + cs.name());

		String host = args[0];
		int port = Integer.parseInt(args[1]);
		String name = args[2];

		Socket s = null;

		BufferedWriter bw = null;
		BufferedReader br = null;

		//Scanner sc = new Scanner(System.in);

		try
		{
			s = new Socket(host, port);

			bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(), cs));
			br = new BufferedReader(new InputStreamReader(s.getInputStream(), cs));

			//String msg = "GET " + name + "\n\n";

			//System.out.println("Sending: " + msg);

			//bw.write(msg);
			//bw.flush();

			for (int x = br.read(); x != -1; x = br.read())
			{
				System.out.print((char) x);
			}
		}
		finally
		{
			bw.flush();
			bw.close();

			br.close();

			s.close();
		}
	}
}
