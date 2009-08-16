package sessionj.ast.sessops.basicops;

import polyglot.ast.Expr;
import polyglot.util.Position;
import sessionj.ast.SJNodeFactory;

import java.util.List;

public class SJPass_c extends SJBasicOperation_c implements SJPass 
{	
	public SJPass_c(Position pos, SJNodeFactory nf, String name, List arguments, List targets)
	{
		super(pos, nf, name, arguments, targets);
	}

    public Expr encodedSessionType() {
        return (Expr) arguments().get(1);
    }

    public Expr argument() {
        return (Expr) arguments().get(0);
    }        
}
