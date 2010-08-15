package thesis.benchmark.bmark1.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

import thesis.benchmark.bmark1.ServerMessage;

public interface RMIServer extends Remote 
{
	public static final String RMI_SERVER_OBJECT = "RMI_SERVER_OBJECT"; 
	
	public void init() throws RemoteException;
	public void setServerMessageSize(int serverMessageSize) throws RemoteException;
	public ServerMessage getServerMessage(boolean flag) throws RemoteException;
	public void close() throws RemoteException;
}
