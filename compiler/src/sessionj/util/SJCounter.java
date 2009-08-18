package sessionj.util;

public class SJCounter
{
	private int i;

	public SJCounter()
	{
		i = 0;
	}

	public SJCounter(int i)
	{
		this.i = i;
	}

	public String nextValue()
	{
		return String.valueOf(i++);
	}
}
