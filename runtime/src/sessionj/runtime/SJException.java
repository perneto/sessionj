package sessionj.runtime;

public abstract class SJException extends Exception
{
	public SJException()
	{
		super();
	}

	public SJException(String message)
	{
		super(message);
	}

	public SJException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public SJException(Throwable cause)
	{
		super(cause);
	}
}
