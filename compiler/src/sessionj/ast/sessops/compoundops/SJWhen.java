package sessionj.ast.sessops.compoundops;

import sessionj.types.contexts.SJTypeBuildingContext;
import sessionj.types.contexts.SJContextElement;
import polyglot.types.SemanticException;
import polyglot.ast.Block;

public interface SJWhen extends Block {
    SJContextElement leaveSJContext(SJTypeBuildingContext sjcontext) throws SemanticException;
}
