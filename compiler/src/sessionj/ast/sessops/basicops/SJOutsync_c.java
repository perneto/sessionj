package sessionj.ast.sessops.basicops;

import java.util.List;

import polyglot.util.Position;
import static sessionj.SJConstants.SJ_RUNTIME_TYPE;
import static sessionj.SJConstants.SJ_SOCKET_OUTSYNC;
import sessionj.ast.SJNodeFactory;

public class SJOutsync_c extends SJBasicOperation_c implements SJOutsync  
{	
	public SJOutsync_c(SJNodeFactory nf, Position pos, List arguments, List targets)
	{
        super(pos, nf.CanonicalTypeNode(pos, SJ_RUNTIME_TYPE), nf.Id(pos, SJ_SOCKET_OUTSYNC), arguments, targets);
	}
}
