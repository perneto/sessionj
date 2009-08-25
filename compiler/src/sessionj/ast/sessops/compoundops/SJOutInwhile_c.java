package sessionj.ast.sessops.compoundops;

import polyglot.ast.Expr;
import polyglot.ast.Stmt;
import polyglot.ast.Receiver;
import polyglot.util.Position;
import polyglot.parse.Name;

import java.util.LinkedList;
import java.util.List;

public class SJOutInwhile_c extends SJWhile_c implements SJOutInwhile
{
    private int sourcesStart;

    public SJOutInwhile_c(Position pos, Expr cond, Stmt body, List targets, List sources)
	{
		super(pos, cond, body, addAllToList(targets, sources));
        sourcesStart = targets.size();
        // This weird hack is because the SJVariableParser visitor comes over and changes the contents
        // of the targets() list - disambiguating from AmbReceivers to Exprs. So we can't really
        // use generics either, it makes things even more confusing...
    }

    private static List addAllToList(List targets, List sources) {
        List l = new LinkedList(targets);
        l.addAll(sources);
        return l;
    }

    public List<?> outsyncTargets() {
        return targets().subList(0, sourcesStart);
    }

    public List insyncSources() {
        return targets().subList(sourcesStart, targets().size());
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
