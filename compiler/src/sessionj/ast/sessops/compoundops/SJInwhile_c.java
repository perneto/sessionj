package sessionj.ast.sessops.compoundops;

import java.util.*;

import polyglot.ast.*;
import polyglot.util.Position;

public class SJInwhile_c extends SJWhile_c implements SJInwhile
{
	public SJInwhile_c(Position pos, Expr cond, Stmt body, List targets)
	{
		super(pos, cond, body, targets);
	}

	public SJInwhile cond(Expr cond)
	{
		return (SJInwhile) super.cond(cond);
	}

	public SJInwhile body(Stmt body)
	{
		return (SJInwhile) super.body(body);
	}
}
