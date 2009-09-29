package sessionj.ast.sessops.compoundops;

import polyglot.ast.Node;
import polyglot.types.SemanticException;
import sessionj.types.SJTypeSystem;
import sessionj.types.sesstypes.SJSessionType;
import sessionj.types.contexts.SJTypeBuildingContext;
import sessionj.types.contexts.SJContextElement;
import sessionj.extension.SJExtFactory;
import sessionj.ast.sessops.SJSessionOperation;

public interface SJTypecase extends SJCompoundOperation {
    Node buildType(SJTypeSystem sjts, SJExtFactory sjef);

    void enterSJContext(SJTypeBuildingContext sjcontext) throws SemanticException;
    SJContextElement leaveSJContext(SJTypeBuildingContext sjcontext) throws SemanticException;

    SJCompoundOperation sessionTypeCheck(SJSessionType implemented, SJExtFactory sjef);
}
