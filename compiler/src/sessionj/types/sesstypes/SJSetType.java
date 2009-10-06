package sessionj.types.sesstypes;

import java.util.Collection;

public interface SJSetType extends SJSessionType { 
    boolean contains(SJSessionType sessionType);
    boolean containsAllAndOnly(Collection<SJSessionType> types);

    int memberRank(SJSessionType member);
}
