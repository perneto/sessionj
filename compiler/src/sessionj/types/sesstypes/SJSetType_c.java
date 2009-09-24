package sessionj.types.sesstypes;

import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.types.Type;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class SJSetType_c extends SJSessionType_c implements SJSetType {
    private final List<SJSessionType_c> members;

    private boolean isSingleton() {
        return members.size() == 1;
    }

    public SJSetType_c(TypeSystem ts, List<SJSessionType_c> members) {
        super(ts);
        this.members = Collections.unmodifiableList(members);
        assert !members.contains(null);
    }

    private SJSessionType singletonMember(SJSessionType st) {
        return ((SJSetType_c) st).members.get(0);
    }

    protected boolean eligibleForEquals(SJSessionType st) {
        if (isSingleton()) {
            SJSessionType_c member = members.get(0);
            if (st instanceof SJSetType_c) {
                return member.eligibleForEquals(singletonMember(st));
            } else {
                return member.eligibleForEquals(st);
            }
        }
        // TODO
        throw new UnsupportedOperationException("Non-singleton set types not supported yet");
    }

    protected boolean eligibleForSubtype(SJSessionType potentialSupertype) {
        if (isSingleton()) {
            SJSessionType_c member = members.get(0);
            if (potentialSupertype instanceof SJSetType_c) {
                return member.eligibleForSubtype(singletonMember(potentialSupertype));
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
            SJSessionType_c member = members.get(0);
            if (st instanceof SJSetType_c) {
                return member.eligibleForDualtype(singletonMember(st));
            } else {
                return member.eligibleForDualtype(st);
            }
        }
        // TODO
        throw new UnsupportedOperationException("Non-singleton set types not supported yet");
    }

    protected boolean compareNode(NodeComparison o, SJSessionType st) {
        //noinspection SimplifiableIfStatement
        if (isSingleton()) return members.get(0).compareNode(o,st);

        return true;
    }


    public SJSessionType nodeSubsume(SJSessionType st) throws SemanticException {
        if (isSingleton()) return members.get(0).nodeSubsume(st);        
        // TODO
        throw new UnsupportedOperationException("Non-singleton set types not supported yet");
    }

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
        if (isSingleton()) return members.get(0).startsWith(aClass);
        return super.startsWith(aClass);
    }

    @Override
    public boolean isWellFormed() {
        if (isSingleton()) return members.get(0).isWellFormed();
        // TODO
        return super.isWellFormed();
    }

    @Override
    public SJSessionType nodeDual() throws SemanticException {
        if (isSingleton()) return members.get(0).nodeDual();
        // TODO
        return super.nodeDual();
    }

    @Override
    protected SJSessionType getChild() {
        if (isSingleton()) return members.get(0).getChild();
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
}
