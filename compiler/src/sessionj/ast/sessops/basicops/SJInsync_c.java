package sessionj.ast.sessops.basicops;

import polyglot.ast.Expr;
import polyglot.util.Position;
import static sessionj.SJConstants.SJ_SOCKET_INSYNC;
import sessionj.ast.SJNodeFactory;

import java.util.LinkedList;
import java.util.List;

public class SJInsync_c extends SJBasicOperation_c implements SJInsync  
{	
	public SJInsync_c(SJNodeFactory nf, Position pos, List targets)
	{
        super(pos, nf, SJ_SOCKET_INSYNC, new LinkedList<Expr>(), targets);
	}
}
