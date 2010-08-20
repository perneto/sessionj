//$ bin/sessionjc -cp tests/classes/ tests/src/thesis/benchmark/bmark3/ordinary/LastWorker.sj -d tests/classes/
//$ bin/sessionj -cp tests/classes/ thesis.benchmark.bmark3.ordinary.LastWorker false 8888 localhost 5550 localhost 4441 10 2 1 BODY

package thesis.benchmark.bmark3.ordinary;

import java.util.Arrays;

import sessionj.runtime.SJIOException;
import sessionj.runtime.SJProtocol;
import sessionj.runtime.net.SJServerSocket;
import sessionj.runtime.net.SJServerSocketCloser;
import sessionj.runtime.net.SJService;
import sessionj.runtime.net.SJSocket;

import thesis.benchmark.bmark3.Common;
import thesis.benchmark.bmark3.Particle;
import thesis.benchmark.bmark3.ParticleV;

public class Client
{	
	private String host; 
	private int port; 
	
	public Client(String host, int port)
	{
		this.host = host;
		this.port = port;
	}
	
	public void run() throws Exception
	{
		final noalias SJService c = SJService.create(NBODY_CLIENT, host, port);
		final noalias SJSocket s;
		try (s)
		{ 				
			s = c.request();			
			s.send(particles);
			Particle[] particles = (Particle[]) s.receive();	
		}
		finally { }
	}

	private void initParticles(Particle[] particles, ParticleV[] pvs)
	{
		for(int i = 0; i < particles.length; i++)
		{		
			Particle p = new Particle();
			ParticleV pv = new ParticleV();
			
			if (debug)
			{
				p.x = i;
				p.y = i;
				p.m = 1.0;
			}
			else
			{
				p.x = 10.0 * Math.random();
				p.y = 10.0 * Math.random();
				p.m = 10.0 * Math.random();
			}
			
			pv.vi_old = 0;
			pv.vj_old = 0;
			pv.ai_old = 0;
			pv.aj_old = 0;
			pv.ai = 0;
			pv.aj = 0;
	
			particles[i] = p;			
			pvs[i] = pv;
		}
	}
	
	public static void main(String args[]) throws Exception
	{
		String host = args[0];
		int port = Integer.parseInt(args[1]);
		
		Client c = new Client(host, port); 		
		c.run();
	}	
}
