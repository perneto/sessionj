package sessionj.runtime.transport;

import sessionj.runtime.SJIOException;

/**
 * @author Raymond
 * 
 * FIXME: rename to SJChannel, and rename channels to services. Also, need an isClosed.  
 */
public interface SJConnection 
{
	void disconnect();

	void writeByte(byte b) throws SJIOException;
	void writeBytes(byte[] bs) throws SJIOException;

	byte readByte() throws SJIOException;
	void readBytes(byte[] bs) throws SJIOException;
	
	void flush() throws SJIOException;
	
	String getHostName();
	int getPort();
	
	int getLocalPort();

	String getTransportName();
}
