package sessionj.ast.sessops.basicops;

import polyglot.util.Position;
import static sessionj.SJConstants.SJ_RUNTIME_TYPE;
import static sessionj.SJConstants.SJ_SOCKET_INSYNC;
import sessionj.ast.SJNodeFactory;

import java.util.List;

public class SJInsync_c extends SJBasicOperation_c implements SJInsync  
{	
	public SJInsync_c(SJNodeFactory nf, Position pos, List arguments, List targets)
	{
        super(pos, nf.CanonicalTypeNode(pos, SJ_RUNTIME_TYPE), nf.Id(pos, SJ_SOCKET_INSYNC), arguments, targets);
	}
}
