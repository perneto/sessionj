package sessionj.ast.sessvars;

import polyglot.ast.Id;
import polyglot.ast.Local_c;
import polyglot.util.Position;

public class SJLocalSocket_c extends Local_c implements SJLocalSocket
{
	public SJLocalSocket_c(Position pos, Id id)
	{
		super(pos, id);
	}
	
	public String sjname()
	{
		return name();
	}
}
