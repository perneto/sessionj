package sessionj.ast.selectorops;

import java.util.List;

import polyglot.ast.*;
import polyglot.util.Position;

public class SJRegisterInput_c extends SJSelectorOperation_c implements SJRegisterInput  
{	
	public SJRegisterInput_c(Position pos, Receiver target, Id name, List arguments)
	{
		super(pos, target, name, arguments);
	}
}
