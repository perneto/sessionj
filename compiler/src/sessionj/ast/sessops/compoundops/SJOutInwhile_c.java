package sessionj.ast.sessops.compoundops;

import java.util.*;

import polyglot.ast.*;
import polyglot.util.Position;
import sessionj.util.SJCompilerUtils;

public class SJOutInwhile_c extends SJWhile_c implements SJOutInwhile
{
    private List outsyncTargets;
    private List insyncSources;

    public SJOutInwhile_c(Position pos, Expr cond, Stmt body, List targets, List sources)
	{
		super(pos, cond, body, addAllToList(targets, sources));
        outsyncTargets = targets;
        insyncSources = sources;
	}

    private static List addAllToList(List targets, List sources) {
        List l = new LinkedList(targets);
        l.addAll(sources);
        return l;
    }

    public List outsyncTargets() {
        return outsyncTargets;
    }

    public List insyncSources() {
        return insyncSources;
    }

    public SJOutInwhile cond(Expr cond)
	{
		return (SJOutInwhile) super.cond(cond);
	}

	public SJOutInwhile body(Stmt body)
	{
		return (SJOutInwhile) super.body(body);
	}
}
