package sessionj.types.sesstypes;

import java.util.Collection;

import polyglot.types.SemanticException;

public interface SJSetType extends SJSessionType { 
    boolean contains(SJSessionType sessionType);
    boolean containsAllAndOnly(Collection<SJSessionType> types);

    int memberRank(SJSessionType member);    
    
    //SJSetType flatten(); // TODO: "flatten" the set type.
    
    boolean isSingleton();
    SJSessionType getSingletonMember() throws SemanticException;
}
