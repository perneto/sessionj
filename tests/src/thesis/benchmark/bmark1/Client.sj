package thesis.benchmark.bmark1;

public interface Client  
{
	void run() throws Exception;

	boolean isDebug();
	String getHost();  
  int getPort();      
  int getClientId();
  int getServerMessageSize();
  int getSessionLength();
  int getIterations();  
}
