package sessionj.types.sesstypes;

import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;

import java.util.*;

public class SJSetType_c extends SJSessionType_c implements SJSetType {
    private final Collection<SJSessionType_c> members;

    //private boolean isSingleton() 
    public boolean isSingleton() // Any reason why this shouldn't be public? // FIXME: should be implemented using (the proposed) flatten method in order to avoid e.g. {{S1, S2}} being considered a singleton. 
    {
    	//return (this.flatten()).members.size() == 1;
        return members.size() == 1;
    }

    public SJSessionType getSingletonMember() throws SemanticException
    {
    	if (!isSingleton())
    	{
    		throw new SemanticException("[SJSetType_c] Not a singleton so cannot obtain a singleton member: " + this);
    	}
    	
    	return singletonMember();
    }
    
    public SJSetType_c(TypeSystem ts, List<SJSessionType_c> members) {
        super(ts);
        this.members = Collections.unmodifiableList(members);
        // FIXME: Can't use a HashSet for now because hashCode is not implemented in SJSessionType_c subclasses 
        assert !members.contains(null);
    }

    private SJSessionType_c singletonMember() {
        Iterator<SJSessionType_c> it = members.iterator();
        return it.next();
    }

    protected boolean eligibleForEquals(SJSessionType st) {
        if (isSingleton()) {
            SJSessionType_c member = singletonMember();
            if (st instanceof SJSetType_c) {
                return member.eligibleForEquals(((SJSetType_c) st).singletonMember());
            } else {
                return member.eligibleForEquals(st);
            }
        } else {
            return st instanceof SJSetType
                && ((SJSetType) st).containsAllAndOnly(new LinkedList<SJSessionType>(members));            
        }
    }

    protected boolean eligibleForSubtype(SJSessionType potentialSupertype) {
        if (isSingleton()) {
            SJSessionType_c member = singletonMember();
            if (potentialSupertype instanceof SJSetType_c) {
                return member.eligibleForSubtype(
                    ((SJSetType_c) potentialSupertype).singletonMember()
                );
            } else {
                return member.eligibleForSubtype(potentialSupertype);
            }
        } else if (potentialSupertype instanceof SJSetType_c) {
            // When comparing 2 sets for subtyping, require that the supertype-set
            // be a superset of the subtype-set (contains all of its elements),
            // modulo subtyping of the individual elements
            for (SJSessionType member : members) {
                if (!((SJSetType_c) potentialSupertype).containsSupertypeOf(member))
                    return false;
            }
            return true;
        } else {
            // We are a non-singleton set and the other type is not a set: cannot be a subtype
            return false;
        }
    }

    private boolean containsSupertypeOf(Type type) {
        for (SJSessionType member : members)
            if (type.isSubtype(member)) return true;
        return false;
    }

    protected boolean eligibleForDualtype(SJSessionType st) {
        if (isSingleton()) {
            SJSessionType_c member = singletonMember();
            if (st instanceof SJSetType_c) {
                return member.eligibleForDualtype(((SJSetType_c) st).singletonMember());
            } else {
                return member.eligibleForDualtype(st);
            }
        }
        // TODO
        throw new UnsupportedOperationException("Non-singleton set types not supported yet");
    }

    protected boolean compareNode(NodeComparison o, SJSessionType st) {
        return !isSingleton() || singletonMember().compareNode(o, st);
    }

    private int subtypingPartialOrder(SJSessionType left, SJSessionType right) {
        if (left.isSubtype(right)) return -1;
        if (right.isSubtype(left)) return 1;
        return 0;
    }

    public SJSessionType nodeSubsume(SJSessionType st) throws SemanticException {
        if (isSingleton()) return singletonMember().nodeSubsume(st);        

        throw new UnsupportedOperationException("Non-singleton set types not supported yet");
        // TODO intersection of the two sets modulo subtyping
        /*
        if (st instanceof SJSetType_c) {
            List<SJSessionType_c> newMembers = new LinkedList<SJSessionType_c>();
            for (SJSessionType member : members) {
                SJSessionType_c mostSpecific = mostSpecific(member, (SJSetType_c) st);
                if (mostSpecific != null) newMembers.add(mostSpecific);
            }
        } else {
        }
        */
    }
/*
    private SJSessionType_c mostSpecific(SJSessionType member, SJSetType_c other) {
        for (SJSessionType otherMember : other.members) {
            int order = subtypingPartialOrder(otherMember, member);
            
        }
    }
*/
    public boolean nodeWellFormed() {
        if (members.isEmpty()) return false;
        for (SJSessionType elem : members) {
            if (!elem.treeWellFormed()) return false;
        }
        return true;
    }

    public SJSessionType nodeClone() {
        List<SJSessionType_c> copiedMembers = new LinkedList<SJSessionType_c>();
        for (SJSessionType m : members) copiedMembers.add((SJSessionType_c) m.copy());
        return typeSystem().SJSetType(copiedMembers);
    }

    @Override
    public SJSessionType treeClone() {
        // We clone our children ourselves in nodeClone() already.
        return nodeClone();
    }

    public String nodeToString() {
        StringBuilder builder = new StringBuilder("{ ");
        for (SJSessionType n : members) builder.append(n).append(", ");
        builder.replace(builder.length()-2, builder.length(), " }");
        return builder.toString();
    }

    @Override
    public String treeToString() {
        return nodeToString();
        // Overriden because a set type never has a child, and the parent implementation
        // gets confused with our implementation of getChild for singleton types.
    }

    @Override
    public boolean startsWith(Class<? extends SJSessionType> aClass) {
        if (isSingleton()) return singletonMember().startsWith(aClass);
        return super.startsWith(aClass);
    }

    @Override
    public boolean isWellFormed() {
        if (isSingleton()) return singletonMember().isWellFormed();
        // TODO
        return super.isWellFormed();
    }

    @Override
    public SJSessionType nodeDual() throws SemanticException {
        if (isSingleton()) return singletonMember().nodeDual();
        // TODO
        return super.nodeDual();
    }

    @Override
    protected SJSessionType getChild() {
        if (isSingleton()) return singletonMember().getChild();
        return null; // A set never has anything after it (enforced by grammar)
    }

    @Override
    public SJSessionType child(SJSessionType child) {
        throw new UnsupportedOperationException("cannot set child: set types do not accopt a child");
    }

    @Override
    public SJSessionType supertypeCandidate(SJSessionType potentialSubtype) {
        for (SJSessionType member : members) {
            if (potentialSubtype.isSubtype(member)) return member;
        }
        return this;
    }

    public boolean contains(SJSessionType sessionType) {
        return members.contains(sessionType);
    }

    public boolean containsAllAndOnly(Collection<SJSessionType> types) {    	
        return members.containsAll(types) && types.containsAll(members); // FIXME: should be modulo "flattening" (which is TODO. see SJSetType).
    }

    public int memberRank(SJSessionType member) {
        int i = 0;
        for (SJSessionType m : members) {
            if (m.equals(member)) return i;
            ++i;
        }
        throw new IllegalArgumentException("Session type "
            + member + " is not a member of set type " + this);
    }
}
