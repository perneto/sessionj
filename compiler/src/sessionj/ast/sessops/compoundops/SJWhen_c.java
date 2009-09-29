package sessionj.ast.sessops.compoundops;

import polyglot.util.Position;
import polyglot.ast.*;
import polyglot.types.SemanticException;
import sessionj.ast.typenodes.SJTypeNode;
import sessionj.ast.sessops.SJSessionOperation;
import sessionj.ast.sessvars.SJVariable;
import sessionj.types.contexts.SJContextElement;
import sessionj.types.contexts.SJTypeBuildingContext;

import java.util.List;
import java.util.Collections;

/**
 * Not quite a statement, but the switch cases {@link polyglot.ast.Case}
 * are also considered to be statements.
 */
public class SJWhen_c extends Block_c implements SJWhen {
    private final SJTypeNode type;

    public SJWhen_c(Position pos, SJTypeNode type, List<Stmt> body) {
        super(pos, body);
        this.type = type;
    }

    public SJContextElement leaveSJContext(SJTypeBuildingContext sjcontext) throws SemanticException {
        return sjcontext.pop();
    }

    @Override
    public String toString() {
        return "when (" + type + ')' + super.toString();
    }
}
