package sessionj.ast.sessops.compoundops;

import polyglot.util.Position;
import polyglot.ast.Receiver;
import polyglot.ast.Stmt_c;
import polyglot.ast.Term;
import polyglot.visit.CFGBuilder;

import java.util.List;
import java.util.Collections;

import sessionj.ast.sessops.SJSessionOperation;
import sessionj.ast.sessvars.SJVariable;

public class SJTypecase_c extends Stmt_c implements SJTypecase {
    private final Receiver ambiguousSocket;
    private SJVariable socket;
    private final List<SJWhen> cases;

    public SJTypecase_c(Position pos, Receiver ambiguousSocket, List<SJWhen> cases) {
        super(pos);
        this.ambiguousSocket = ambiguousSocket;
        this.cases = Collections.unmodifiableList(cases);
    }

    public Term firstChild() {
        return null;
    }

    public List acceptCFG(CFGBuilder v, List succs) {
        return null;
    }

    public List targets() {
        return Collections.singletonList(ambiguousSocket);
    }

    public SJSessionOperation targets(List target) {
        if (target.size() != 1) throw new IllegalArgumentException
            ("Typecase only supports one target, got: " + target);
        Object o = target.get(0);
        if (o instanceof SJVariable) socket = (SJVariable) o;
        else throw new IllegalArgumentException("Tried to update target with a non-SJVariable:" + o);
        return this;
    }
}
