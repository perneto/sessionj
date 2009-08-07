package sessionj.types.sesstypes;

import polyglot.types.*;

import sessionj.types.*;

import static sessionj.SJConstants.*;

abstract public class SJLoopType_c extends SJSessionType_c implements SJLoopType // Used by copy/clone routines, so those cannot be used here, else mutual dependency (will loop forever).
{
	private SJSessionType body;

	public SJLoopType_c(TypeSystem ts)
	{
		super(ts);
	}
	
	public SJSessionType body()
	{
		SJSessionType st = getBody();
		
		return (st == null) ? null : st.copy();
	}

	public SJLoopType body(SJSessionType body) // Used by copy/clone routines, so those cannot be used here, else mutual dependency (will loop forever).
	{
		SJLoopType lt = (SJLoopType) skeleton(); // SJRecursionType skeleton includes label.
		
		if (body != null)
		{
			((SJLoopType_c) lt).setBody(body.copy()); // Defensive copy.
		}

		SJSessionType child = child(); // Returns a defensive copy.
		
		if (child != null)
		{
			((SJLoopType_c) lt).setChild(child);
		}
		
		return lt;
	}

	protected boolean compareNode(NodeComparison op, SJSessionType st)
	{
		SJSessionType ours = getBody();
		SJSessionType theirs = ((SJLoopType_c) st).getBody();
		
		switch (op)
		{
			case EQUALS: return (ours == null) ? theirs == null : ours.typeEquals(theirs); // Could use treeEquals directly.
			case SUBTYPE: return (ours == null) ? theirs == null : ours.isSubtype(theirs); 
			case DUALTYPE: return (ours == null) ? theirs == null : ours.isDualtype(theirs);
		}
		
		throw new RuntimeException("[SJLoopType_c] Shouldn't get here: " + op);
	}	
	
	public SJSessionType nodeSubsume(SJSessionType st) throws SemanticException
	{
		if (!eligibleForSubsume(st)) 
		{
			throw new SemanticException("[SJLoopType_c] Not subsumable: " + this + ", " + st);
		}

		SJSessionType ours = getBody();
		SJSessionType theirs = ((SJLoopType_c) st).getBody();
		SJLoopType res = skeleton();

		if (ours == null)
		{
			if (theirs != null)
			{
				throw new SemanticException("[SJLoopType_c] Not subsumable: " + this + ", " + st);
			}
		}
		else
		{
			if (theirs == null)
			{
				throw new SemanticException("[SJLoopType_c] Not subsumable: " + this + ", " + st);
			}

			res = res.body(ours.subsume(theirs));
		}

		return res;
	}

	public boolean nodeWellFormed()
	{
		SJSessionType st = getBody();
		
		return (st == null) ? true : st.treeWellFormed();
	}

	public SJSessionType nodeClone()
	{
		SJLoopType lt = skeleton(); 
		SJSessionType st = getBody();
		
		return (st == null) ? lt : lt.body(st.copy());
	}

	public String nodeToString()
	{
		SJSessionType st = getBody();
		String m = loopConstructorOpen();  
		
		if (st != null)
		{
			m += st.toString();
		}

		return m + loopConstructorClose();
	}

	protected SJSessionType getBody()
	{
		return body;
	}
	
	protected void setBody(SJSessionType body)
	{
		this.body = body;
	}
	
	abstract protected SJLoopType skeleton();
	
	abstract protected boolean eligibleForSubsume(SJSessionType st);
	
	abstract protected String loopConstructorOpen();
	abstract protected String loopConstructorClose();
}
