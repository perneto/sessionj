package sessionj.runtime.transport;

import java.io.*;

import sessionj.runtime.*;
import sessionj.runtime.net.SJServerIdentifier;

/**
 * @author Raymond
 * 
 * FIXME: rename to SJChannel, and rename channels to services. Also, need an isClosed.  
 */
public interface SJConnection 
{
	public void disconnect();// throws SJIOException;

	public void writeByte(byte b) throws SJIOException;
	public void writeBytes(byte[] bs) throws SJIOException;

	public byte readByte() throws SJIOException;
	public void readBytes(byte[] bs) throws SJIOException;
	
	public void flush() throws SJIOException;
	
	public String getHostName();
	public int getPort();
	
	public int getLocalPort();
	
	public String getTransportName();
}
