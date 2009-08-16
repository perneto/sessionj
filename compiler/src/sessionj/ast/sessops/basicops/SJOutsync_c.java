package sessionj.ast.sessops.basicops;

import polyglot.util.Position;
import static sessionj.SJConstants.SJ_SOCKET_OUTSYNC;
import sessionj.ast.SJNodeFactory;

import java.util.List;

public class SJOutsync_c extends SJBasicOperation_c implements SJOutsync  
{	
	public SJOutsync_c(SJNodeFactory nf, Position pos, List arguments, List targets)
	{
        super(pos, nf, SJ_SOCKET_OUTSYNC, arguments, targets);
	}
}
