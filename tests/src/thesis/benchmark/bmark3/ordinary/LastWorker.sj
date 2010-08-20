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

import thesis.benchmark.Killable;
import thesis.benchmark.bmark3.Common;
import thesis.benchmark.bmark3.NbodyTimer;
import thesis.benchmark.bmark3.Particle;
import thesis.benchmark.bmark3.ParticleV;

public class LastWorker extends NbodyTimer implements Killable
{	
	private static protocol LAST_LINK_CLIENT cbegin.![![?(Particle[])]*]* // No ring token message
	
	private volatile boolean run = true;
	private volatile boolean finished = false;
	private SJServerSocketCloser ssc;	
	 
	private int port; 
	private String host_l; 
	private int port_l; 
	private String host_r; 
	private int port_r; 
	private int numParticles;
	private int numProcessors = 0;
	private int steps;
	
	public LastWorker(boolean debug, int port, String host_l, int port_l, String host_r, int port_r, int numParticles/*, int numProcessors*/, int steps, int iters, String flag)
	{
		super(debug, iters, flag);
		this.port= port; 
		this.host_l = host_l; 
		this.port_l= port_l; 
		this.host_r = host_r; 
		this.port_r = port_r; 
		this.numParticles = numParticles;
		this.steps = steps;
	}
	
	public void run(boolean warmup, boolean timer) throws Exception
	{
		int len = (warmup) ? NbodyTimer.WARMUP_SESSION_LENGTH : steps; 
		
		final noalias SJServerSocket ss;				
		try (ss)
		{						
			ss = SJServerSocket.create(Common.NBODY_SERVER, port);
			ssc = ss.getCloser();
			
			debugPrintln("[Worker] Service started on port: " + port);		

			final noalias SJService c_r = SJService.create(Common.LINK_CLIENT, host_r, port_r);
			final noalias SJService c_l = SJService.create(LAST_LINK_CLIENT, host_l, port_l);

			final noalias SJSocket s;
			final noalias SJSocket s_r;
			final noalias SJSocket s_l;
			try (s, s_r, s_l)
			{ 				
				s = ss.accept();

				/*Particle[] particles = new Particle[numParticles]; // The particles
				ParticleV[] pvs = new ParticleV[numParticles];     // The particles' velocities*/ 
				Particle[] particles = (Particle[]) s.receive();
				ParticleV[] pvs = (ParticleV[]) s.receive();					
				
				long timeStarted = 0;		
				long timeFinished = 0;			
			
				s_r = c_r.request();
				s_l = c_l.request();

				startTimer();
				
				numProcessors = s_r.receiveInt() + 1;
				
				debugPrintln("[LastWorker] Number of processors: " + numProcessors);
				
				//initParticles(debug, particles, pvs);
												
				int i = 0;				
				<s_r, s_l>.outwhile(i < len)
				{	
					debugPrintln("\n[LastWorker] Simulation step: " + i);
					debugPrintln("[LastWorker] Particles: " + Arrays.toString(particles));
					
					Particle[] current = new Particle[numParticles];					
					System.arraycopy(particles, 0, current, 0, numParticles);				
					
					int j = 0;					
					<s_r, s_l>.outwhile(j < (numProcessors - 1))
					{									
						s_r.send(current);	
						Common.computeForces(particles, current, pvs);													
						current = (Particle[]) s_l.receive();						
						
						j++;
					}																						
					Common.computeForces(particles, current, pvs);										
					Common.computeNewPos(particles, pvs, i);
										
					i++;
				}

		  	stopTimer();
				
				debugPrintln("\n[LastWorker] Simulation step: " + i);
				debugPrintln("[LastWorker] Particles: " + Arrays.toString(particles));
				
				s.send(particles);
			}
			finally { }
		}
		finally { }
		
   	stopTimer();		  	  	
	  
	  if (timer)
  	{
  		printTimer();
  	}	 	  
	  
  	resetTimer();		
	}

	public void kill() throws Exception
  {  	  	
  	run = false; // It's important that no more clients are trying to connect after this point		
  	ssc.close(); // Break the accepting loop (make the blocked accept throw an exception)		
		while (!this.finished);
  }	
	
	/*private void initParticles(boolean debug, Particle[] particles, ParticleV[] pvs)
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
	}*/
	
	public static void main(String args[]) throws Exception
	{
		boolean debug = Boolean.parseBoolean(args[0]);
		int port = Integer.parseInt(args[1]);
		String host_l = args[2];
		int port_l = Integer.parseInt(args[3]);
		String host_r = args[4];
		int port_r = Integer.parseInt(args[5]);				
		int numParticles = Integer.parseInt(args[6]);
		//int numProcessors = Integer.parseInt(args[7]); // Determined from the ring token		
		int steps = Integer.parseInt(args[7]);
		int iters = Integer.parseInt(args[8]);
		String flag = args[9];
		
		if (numParticles > Common.MAX_PARTICLES/* && numParticles <= MAX_PROCESSORS*/)
		{	
			throw new RuntimeException("[LastWorker] Too many particles: " + numParticles);
		}
		
		LastWorker lw = new LastWorker(debug, port, host_l, port_l, host_r, port_r, numParticles, steps, iters, flag); 		
		lw.run();
	}	
}
