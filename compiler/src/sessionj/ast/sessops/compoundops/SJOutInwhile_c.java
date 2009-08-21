package sessionj.ast.sessops.compoundops;

import java.util.*;

import polyglot.ast.*;
import polyglot.util.Position;

public class SJOutInwhile_c extends SJWhile_c implements SJOutInwhile
{
	public SJOutInwhile_c(Position pos, Expr cond, Stmt body, List targets)
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
}
