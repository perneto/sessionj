package sessionj.ast.sessops.compoundops;

import java.util.List;

import polyglot.ast.*;
import polyglot.types.Context;
import polyglot.util.Position;

import sessionj.util.SJLabel;

public class SJInbranchCase_c extends Block_c implements SJInbranchCase
{
	private SJLabel lab;

	public SJInbranchCase_c(Position pos, List statements, SJLabel lab)
	{
		super(pos, statements);

		this.lab = lab;
	}

	public Context enterScope(Context c)
	{
		return c; // Push block?
	}

	public SJLabel label()
	{
		return lab;
	}
}
