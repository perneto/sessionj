package sessionj.types.sesstypes;

import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;

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
        throw new UnsupportedOperationException("Non-singleton set types not supported yet");
    }

    protected boolean eligibleForSubtype(SJSessionType st) {
        if (isSingleton()) {
            SJSessionType_c member = members.get(0);
            if (st instanceof SJSetType_c) {
                return member.eligibleForSubtype(singletonMember(st));
            } else {
                return member.eligibleForSubtype(st);
            }
        }
        throw new UnsupportedOperationException("Non-singleton set types not supported yet");
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
        throw new UnsupportedOperationException("Non-singleton set types not supported yet");
    }

    protected boolean compareNode(NodeComparison o, SJSessionType st) {
        if (isSingleton()) return members.get(0).compareNode(o,st);        
        throw new UnsupportedOperationException("Non-singleton set types not supported yet");
    }

    public SJSessionType nodeSubsume(SJSessionType st) throws SemanticException {
        if (isSingleton()) return members.get(0).nodeSubsume(st);        
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
        // We never have anything after a set type, and the parent implementation
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
        return super.isWellFormed();
    }

    @Override
    public SJSessionType nodeDual() throws SemanticException {
        if (isSingleton()) return members.get(0).nodeDual();
        return super.nodeDual();
    }

    @Override
    protected SJSessionType getChild() {
        if (isSingleton()) return members.get(0).getChild();
        return null; // A set never has anything after it (enforced by grammar)
    }

    @Override
    public SJSessionType child(SJSessionType child) {
        throw new UnsupportedOperationException("cannot set child for set type");
    }
}
