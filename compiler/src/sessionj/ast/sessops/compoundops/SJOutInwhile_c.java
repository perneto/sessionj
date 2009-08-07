package sessionj.ast.sessops.compoundops;

import java.util.*;

import polyglot.ast.*;
import polyglot.util.Position;

public class SJOutInwhile_c extends SJWhile_c implements SJOutInwhile
{
	public SJOutInwhile_c(Position pos, Expr cond, Stmt body, List targets, Expr condition)
	{
		super(pos, cond, body, targets);
	}

	public SJOutInwhile cond(Expr cond)
	{
		return (SJOutInwhile) super.cond(cond);
	}

	public SJOutInwhile body(Stmt body)
	{
		return (SJOutInwhile) super.body(body);
	}

	// Is it actually necessary to override this method?
	protected SJOutInwhile_c reconstruct(Expr cond, Stmt body)
	{
		if (cond != this.cond || body != this.body)
		{
			SJOutInwhile_c n = (SJOutInwhile_c) copy();

			n.cond = cond;
			n.body = body;

			return n;
		}

		return this;
	}
}
