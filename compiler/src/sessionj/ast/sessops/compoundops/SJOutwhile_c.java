package sessionj.ast.sessops.compoundops;

import java.util.*;

import polyglot.ast.*;
import polyglot.util.Position;

public class SJOutwhile_c extends SJWhile_c implements SJOutwhile
{
	public SJOutwhile_c(Position pos, Expr cond, Stmt body, List targets)
	{
		super(pos, cond, body, targets);
	}

	public SJOutwhile cond(Expr cond)
	{
		return (SJOutwhile) super.cond(cond);
	}

	public SJOutwhile body(Stmt body)
	{
		return (SJOutwhile) super.body(body);
	}
}
