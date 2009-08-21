package sessionj.types.sesstypes;

import polyglot.types.*;

import sessionj.types.*;
import sessionj.types.typeobjects.*;

import static sessionj.SJConstants.*;

abstract public class SJSessionType_c extends SJType_c implements SJSessionType // Doesn't inherit any code from SJTypeObject_c (no new code there anyway*).
{
	protected static enum NodeComparison { EQUALS, SUBTYPE, DUALTYPE } // Move to SJConstants?
	
	public static final long serialVersionUID = SJ_VERSION;

	private SJSessionType child;   

	protected SJSessionType_c()
	{
		super();
	}

	public SJSessionType_c(TypeSystem ts)
	{
		super(ts);
	}

	public SJSessionType child()
	{
		SJSessionType st = getChild(); 
		
		return (st == null) ? null : st.copy();
	}

	public SJSessionType child(SJSessionType child) // Immutable.
	{		
		SJSessionType st = nodeClone(); // Don't need full treeCopy (would loop forever anyway).
		
		if (child != null)
		{
			((SJSessionType_c) st).setChild(child.copy()); // Clone the child.
		}
		
		return st; 
	}

	public SJSessionType append(SJSessionType st)
	{
		if (st != null) // Should this be removed? 
		{
			SJSessionType copy = copy();
			
			((SJSessionType_c) ((SJSessionType_c) copy).leaf()).setChild(st);
			
			return copy; // child clones the child.
		}

		return this; 
	}
	
	public boolean isSubtypeImpl(Type t) // No need to override if descendsFrom was implemented properly (default implementation uses typeEquals and descendsFrom). 
	{
		return (t instanceof SJSessionType) ? treeSubtype((SJSessionType) t) : false; // Equality is already built into node/treeSubtype.
	}
	
	public boolean typeEqualsImpl(Type t) // Unlike regular Polyglot types, pointer equality is not maintained for equal session types.
	{
		return (t instanceof SJSessionType) ? treeEquals((SJSessionType) t) : false;
	}

	public boolean descendsFromImpl(Type t) 
	{
		return isSubtype(t); // FIXME: make strict descendsFrom? (by modifying node/treeSubtype?) // If so, then isSubtypeImpl needs to check for subtyping between each pairs of type elements (not just the equals or descendsFrom over the whole).
	}

	public boolean isDualtype(Type t)
	{
		return (t instanceof SJSessionType) ? treeDualtype((SJSessionType) t) : false;
	}
	
	public SJSessionType subsume(SJSessionType st) throws SemanticException
	{
		if (this instanceof SJDelegatedType)
		{
			if (!(st instanceof SJDelegatedType))
			{
				return typeSystem().SJDelegatedType(((SJDelegatedType) this).getDelegatedType().subsume(st));
			}
		}
		else if (st instanceof SJDelegatedType)
		{
			return typeSystem().SJDelegatedType(subsume(((SJDelegatedType) st).getDelegatedType()));
		}
		
		return treeSubsume(st);
	}
	
	public boolean wellFormed()
	{
		if (!(this instanceof SJBeginType && typeSystem().wellFormedRecursions(this)))
		{
			return false;
		}

		SJSessionType st = getChild();
		
		return (st == null) ? true : st.treeWellFormed();
	}
	
	public boolean treeEquals(SJSessionType tree)
	{
		SJSessionType ours = getChild();
		SJSessionType theirs = getChild();
		
		return nodeEquals(tree) && ((ours == null) ? (theirs == null) : ours.typeEquals(theirs));
	}

	public boolean treeSubtype(SJSessionType tree)
	{
		SJSessionType ours = getChild();
		SJSessionType theirs = ((SJSessionType_c) tree).getChild();		
		
		return nodeSubtype(tree) && ((ours == null) ? (theirs == null) : ours.isSubtype(theirs));
	}

	public boolean treeDualtype(SJSessionType tree)
	{
		SJSessionType ours = getChild();
		SJSessionType theirs = ((SJSessionType_c) tree).getChild();	
		
		return nodeDualtype(tree) && ((ours == null) ? (theirs == null) : ours.isDualtype(theirs));
	}

	public SJSessionType treeSubsume(SJSessionType tree) throws SemanticException
	{
		SJSessionType ours = getChild();
		SJSessionType theirs = ((SJSessionType_c) tree).getChild();

		if (ours == null)
		{
			if (theirs != null)
			{
				throw new SemanticException("[SJSessionType_c] Not subsumable: " + this + ", " + tree);
			}

			return nodeSubsume(tree);
		}
		else
		{
			if (theirs == null)
			{
				throw new SemanticException("[SJSessionType_c] Not subsumable: " + this + ", " + tree);
			}

			return nodeSubsume(tree).child(ours.subsume(theirs)); // nodeSubsume returns a copy.
		}
	}

	public boolean treeWellFormed()
	{
		if (!nodeWellFormed())
		{
			return false;
		}

		SJSessionType st = getChild();
		
		return (st == null) ? true : st.treeWellFormed();
	}

	public boolean nodeEquals(SJSessionType st)
	{
		return (eligibleForEquals(st) ? compareNode(NodeComparison.EQUALS, st) : false);
	}
	
	public boolean nodeSubtype(SJSessionType st)
	{
		return (eligibleForSubtype(st) ? compareNode(NodeComparison.SUBTYPE, st) : false);
	}
	
	public boolean nodeDualtype(SJSessionType st)
	{
		return (eligibleForDualtype(st) ? compareNode(NodeComparison.DUALTYPE, st) : false);
	}
	
	abstract protected boolean eligibleForEquals(SJSessionType st);
	abstract protected boolean eligibleForSubtype(SJSessionType st); 
	abstract protected boolean eligibleForDualtype(SJSessionType st);
	
	abstract protected boolean compareNode(NodeComparison o, SJSessionType st);
	
	public SJSessionType treeClone()
	{
		SJSessionType st = getChild();
		
		return (st == null) ? nodeClone() : nodeClone().child(st.treeClone());
	}
	
	public SJSessionType clone()
	{
		return copy();
	}
	
	public SJSessionType copy()
	{
		return treeClone();
	}
	
	public String toString()
	{
		return treeToString();
	}

	public String treeToString()
	{
		SJSessionType st = getChild(); 
		
		return nodeToString() + ((st == null) ? "" : SJ_STRING_SEPARATOR + st.treeToString());
	}

	public String translate(Resolver c) // FIXME: hacked. Should just call the super method and wrap the appropriate session type constructor symbols around it.
	{
		return null;
	}

	public boolean equalsImpl(TypeObject to) // Used instead of standard Object equals.
	{
		if (to instanceof SJSessionType)
		{
			return typeEquals((SJSessionType) to);
		}
		else
		{
			return false;
		}
	}

	protected SJSessionType getChild()
	{
		return child;
	}
	
	protected void setChild(SJSessionType st)
	{
		this.child = st;
	}	
	
	private SJSessionType leaf()
	{
		if (child == null)
		{
			return this;
		}
		else
		{
			return ((SJSessionType_c) child).leaf(); // No defensive copy, so private.
		}
	}
	
	/*public int hashCode()
	{
		return toString().hashCode(); // Is this an acceptable design?
	}*/
	
	public SJSessionType getLeaf()
	{
		return leaf();
	}
}
