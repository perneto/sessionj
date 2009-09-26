package sessionj.ast.sessops.compoundops;

import polyglot.util.Position;
import polyglot.ast.Stmt;
import polyglot.ast.Stmt_c;
import polyglot.ast.Term;
import polyglot.visit.CFGBuilder;
import sessionj.ast.typenodes.SJTypeNode;
import sessionj.ast.sessops.SJSessionOperation;

import java.util.List;

/**
 * Not quite a statement, but the switch cases {@link polyglot.ast.Case}
 * are also considered to be statements.
 */
public class SJWhen_c extends Stmt_c implements SJWhen {
    private final SJTypeNode type;
    private final Stmt body;

    public SJWhen_c(Position pos, SJTypeNode type, Stmt body) {
        super(pos);
        this.type = type;
        this.body = body;
    }

    public Term firstChild() {
        return null;
    }

    public List acceptCFG(CFGBuilder v, List succs) {
        return null;
    }

    public List targets() {
        return null;
    }

    public SJSessionOperation targets(List target) {
        return null;
    }
}
