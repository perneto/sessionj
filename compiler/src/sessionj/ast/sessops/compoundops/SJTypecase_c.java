package sessionj.ast.sessops.compoundops;

import polyglot.util.*;
import polyglot.ast.*;
import polyglot.visit.*;
import polyglot.types.SemanticException;
import polyglot.types.Context;

import java.util.List;
import java.util.Collections;
import java.util.LinkedList;

import sessionj.ast.sessops.SJSessionOperation;
import sessionj.ast.sessops.basicops.SJInlabel;
import sessionj.ast.sessvars.SJVariable;
import sessionj.types.sesstypes.SJSessionType;
import sessionj.types.sesstypes.SJSessionType_c;
import sessionj.types.SJTypeSystem;
import sessionj.types.contexts.SJContextElement;
import sessionj.types.contexts.SJTypeBuildingContext;
import static sessionj.visit.SJSessionOperationTypeBuilder.getTargetNames;
import sessionj.visit.SJSessionTypeChecker;
import sessionj.util.SJCompilerUtils;
import sessionj.extension.SJExtFactory;
import sessionj.SJConstants;

public class SJTypecase_c extends Stmt_c implements SJTypecase {
    private final Receiver ambiguousSocket;
    private final SJVariable socket;
    private final List<SJWhen> whenStatements;

    public SJTypecase_c(Position pos, Receiver ambiguousSocket, List<SJWhen> whenStatements, SJVariable socket) {
        super(pos);
        assert ambiguousSocket != null;
        this.ambiguousSocket = ambiguousSocket;
        this.socket = socket;
        assert whenStatements != null;
        this.whenStatements = Collections.unmodifiableList(whenStatements);
    }

    public List<Receiver> ambiguousTargets() {
        return Collections.singletonList(ambiguousSocket);
    }

    public List<SJVariable> resolvedTargets() {
        return Collections.singletonList(socket);
    }

    public SJSessionOperation resolvedTargets(List<SJVariable> resolved) {
        return targets(resolved);
    }

    public List targets() {
        return Collections.singletonList(ambiguousSocket);
    }

    public SJSessionOperation targets(List target) {
        if (target.size() != 1) throw new IllegalArgumentException
            ("Typecase only supports one target, got: " + target);
        Object o = target.get(0);
        if (!(o instanceof SJVariable))
            throw new IllegalArgumentException("Tried to update target with a non-SJVariable:" + o);
        return new SJTypecase_c(position, ambiguousSocket, whenStatements, (SJVariable) o);
    }

    public Node buildType(SJTypeSystem sjts, SJExtFactory sjef) {
        List<String> sjnames = getTargetNames(resolvedTargets(), false);
        SJSessionType st = sjts.SJSetType(Collections.singletonList((SJSessionType_c) sjts.SJUnknownType()));
        return SJCompilerUtils.setSJSessionOperationExt(sjef, this, st, sjnames);
    }

    public SJContextElement leaveSJContext(SJTypeBuildingContext sjcontext) throws SemanticException {
        return sjcontext.pop();
    }

    public SJCompoundOperation sessionTypeCheck(SJSessionType implemented, SJExtFactory sjef) {
        return (SJCompoundOperation) SJSessionTypeChecker.decorateWithSessionType(this, implemented, sjef);
    }

    public void enterSJContext(SJTypeBuildingContext sjcontext) throws SemanticException {
        sjcontext.pushSJTypecase(this);
    }

    // The following are adapted from Switch_c.
    public void prettyPrint(CodeWriter w, PrettyPrinter tr) // This is largely redundant (except for debugging) due to later translation.
    {
        w.write(SJConstants.SJ_KEYWORD_TYPECASE + '(' + ambiguousSocket + ')');
        w.write(" {");
        w.allowBreak(4, " ");
        w.begin(0);

        for (SJWhen when : whenStatements) {
            w.allowBreak(4, " ");
            print(when, w, tr);
        }

        w.end();
        w.allowBreak(0, " ");
        w.write("}");
    }

    public Context enterScope(Context c)
    {
        return c.pushBlock();
    }

    public Node visitChildren(NodeVisitor v)
    {
        List<SJWhen> newWhenStatements = visitList(whenStatements, v);

        Node newThis = new SJTypecase_c(position, ambiguousSocket, newWhenStatements, socket);
        if (ext() != null) {
            newThis = newThis.ext((Ext) ext.copy());
        }
        if (del != null) { // the del() method returns this if del is null, so use field to check
            newThis = newThis.del((JL) del().copy());
        }

        return newThis;
    }


    public Term firstChild()
    {
        return whenStatements.isEmpty() ? null : whenStatements.get(0);
    }

    public List acceptCFG(CFGBuilder v, List succs)
    {
        List<Term> cases = new LinkedList<Term>();
        List<Integer> entry = new LinkedList<Integer>();

        for (SJWhen when : whenStatements)
        {
            cases.add(when);
            entry.add(ENTRY);
        }

        cases.add(this);
        entry.add(EXIT);

        v.visitCFG(firstChild(), FlowGraph.EDGE_KEY_OTHER, cases, entry); // entry...

        v.push(this).visitCFGList(whenStatements, this, EXIT); // ...and exit points?

        return succs;
    }

}
