package sessionj.ast.sessops;

import polyglot.types.SemanticException;
import sessionj.types.contexts.SJContextElement;
import sessionj.types.contexts.SJContextInterface;

public interface TraverseTypeBuildingContext {
    void enterSJContext(SJContextInterface sjcontext) throws SemanticException;
    SJContextElement leaveSJContext(SJContextInterface sjcontext) throws SemanticException;
}
