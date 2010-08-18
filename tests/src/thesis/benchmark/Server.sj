package thesis.benchmark;

// Implementing this interface (or extending AbstractServer), the Server should perform a GC step with each iteration.  
public interface Server  
{
	void run() throws Exception; // Allow Clients to connect and run sessions; implementations should do a GC step (AbstractServer won't do it) 
  void kill() throws Exception; // Wait for Clients to finish and then end
  	
  boolean isDebug();
  int getPort();
}
