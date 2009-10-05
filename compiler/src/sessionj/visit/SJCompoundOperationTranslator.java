package sessionj.visit;

import polyglot.ast.*;
import polyglot.frontend.Job;
import polyglot.qq.QQ;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import polyglot.util.UniqueID;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import static sessionj.SJConstants.*;
import sessionj.ast.SJNodeFactory;
import sessionj.ast.sessops.basicops.SJRecurse;
import sessionj.ast.sessops.compoundops.*;
import sessionj.extension.sessops.SJSessionOperationExt;
import static sessionj.util.SJCompilerUtils.buildAndCheckTypes;
import static sessionj.util.SJCompilerUtils.getSJSessionOperationExt;
import sessionj.util.SJLabel;

import java.util.*;

/**
 * 
 * @author Raymond
 *
 * Also translates basic recurse operation.
 *
 */
public class SJCompoundOperationTranslator extends ContextVisitor
{
	private final TypeSystem sjts = typeSystem();
	private final SJNodeFactory sjnf = (SJNodeFactory) nodeFactory();

	private final Stack<SJCompoundOperation> compounds = new Stack<SJCompoundOperation>();
    // Actually, only SJInbranch and SJRecursion.
	
	public SJCompoundOperationTranslator(Job job, TypeSystem ts, NodeFactory nf)
	{
		super(job, ts, nf);
	}

	public NodeVisitor enterCall(Node parent, Node n) throws SemanticException
	{
		if (n instanceof SJInbranch || n instanceof SJRecursion)
		{
			compounds.push((SJCompoundOperation) n);
		}
		
		return this;
	}
	
	public Node leaveCall(Node parent, Node old, Node n, NodeVisitor v) throws SemanticException
	{
		if (n instanceof SJCompoundOperation) {
			SJCompoundOperation so = (SJCompoundOperation) n;

            if (so instanceof SJInbranch) {	
				compounds.pop();
                // We're only using this to tell if we're dealing with the
                // outermost inbranch/recursion for type checking purposes.
				
				return translateSJInbranch((SJInbranch) so, createQQ(so));
			} else if (so instanceof SJRecursion) {
				compounds.pop();
				
				return translateSJRecursion((SJRecursion) so, createQQ(so));
			} else if (so instanceof SJOutwhile) {
                return translateSJOutwhile((SJOutwhile) so, createQQ(so));
            } else if (so instanceof SJInwhile) {
                return translateSJInwhile((SJInwhile) so, createQQ(so));
            } else if (so instanceof SJOutInwhile) {
                return translateSJOutinwhile((SJOutInwhile) so, createQQ(so));
            }

		} else if (n instanceof SJRecurse) {
			return translateSJRecurse(parent, (SJRecurse) n, createQQ(n));
		}

		return n;
	}

    private QQ createQQ(Node node) {
        return new QQ(sjts.extensionInfo(), node.position());
    }

    private Expr buildNewArray(Position pos, List contents) {
        NewArray na = sjnf.makeSocketsArray(pos, contents.size());

        ArrayInit ai = sjnf.ArrayInit(pos, contents);
        na = na.init(ai)
               .dims(Collections.emptyList())
               .additionalDims(1);
        return na;
    }

    private Node translateSJOutwhile(SJOutwhile outwhile, QQ qq) throws SemanticException {
        String unique = UniqueID.newID("loopCond");
        Expr sockArray = buildNewArray(outwhile.position(), outwhile.targets());

        BooleanLit interruptible = new BooleanLit_c(outwhile.position(), outwhile.isInterruptible());
        Stmt block = qq.parseStmt(
"{ sessionj.runtime.net.LoopCondition %s = " +
"sessionj.runtime.net.SJRuntime.negotiateOutsync(%E, %E);" +
" while (%s.call(%E)) %S }",
                unique,
                interruptible, sockArray,
                unique, outwhile.cond(), outwhile.body()
        );
        buildAndCheckTypes(this, block);
        return block;
    }

    private Node translateSJInwhile(SJInwhile inwhile, QQ qq) throws SemanticException {
        Expr sockArray = buildNewArray(inwhile.position(), inwhile.targets());

        Stmt block = qq.parseStmt(
"{ sessionj.runtime.net.SJRuntime.negotiateNormalInwhile(%E);" +
" while (sessionj.runtime.net.SJRuntime.insync(%E)) %S }",
                sockArray, sockArray, inwhile.body()
        );
        buildAndCheckTypes(this, block);
        return block;
    }

    private Node translateSJOutinwhile(SJOutInwhile outinwhile, QQ qq) throws SemanticException {
        Expr sourcesArray = buildNewArray(outinwhile.position(), outinwhile.insyncSources());
        Expr targetsArray = buildNewArray(outinwhile.position(), outinwhile.outsyncTargets());
        String loopCond = UniqueID.newID("loopCond");
        String peerInterruptible = UniqueID.newID("peerInterruptible");

        List<Object> subst = new LinkedList<Object>(Arrays.asList(
            loopCond, targetsArray
        ));
        String code =
            "{ sessionj.runtime.net.LoopCondition %s = " +
            "sessionj.runtime.net.SJRuntime.negotiateOutsync(false, %E); ";
        if (outinwhile.hasCondition()) {
            code += "boolean %s = ";
            subst.add(peerInterruptible);              
        }
        code += "sessionj.runtime.net.SJRuntime.";
        code += outinwhile.hasCondition() ?
            "negotiateInterruptingInwhile" : "negotiateNormalInwhile";
        code += "(%E); while(%s.call(sessionj.runtime.net.SJRuntime.";

        subst.add(sourcesArray);
        subst.add(loopCond);
        
        if (outinwhile.hasCondition()) {
            code += "interruptingInsync(%E, %s, %E)";
            subst.add(outinwhile.cond());
            subst.add(peerInterruptible);
            subst.add(sourcesArray);
        } else {
            code += "insync(%E)";
            subst.add(sourcesArray);
        }
        code += ")) %S  }";
        subst.add(outinwhile.body());

        Stmt block = qq.parseStmt(code, subst);
        buildAndCheckTypes(this, block);
        return block;
    }

    private Assign translateSJRecurse(Node parent, SJRecurse r, QQ qq) {
		if (!(parent instanceof Eval))
		{
			throw new RuntimeException("[SJCompoundOperationTranslator] Shouldn't get here.");			
		}

        String translation = "";
		List<Object> mapping = new LinkedList<Object>();
		
		translation += "%s = %E";
		mapping.add(getRecursionBooleanName(getSJSessionOperationExt(r).targetNames(), r.label()));
		mapping.add(r);

        //a = (Assign) buildAndCheckTypes(job(), this, a); 
        // Can't build the types now because the assignment target variable is not in the context
        //  - but it will be built when we translate the outer(most) recursion statement.
		
		return (Assign) qq.parseExpr(translation, mapping.toArray());
	}
	
	private Stmt translateSJInbranch(SJInbranch ib, QQ qq) throws SemanticException
	{
        StringBuilder translation = new StringBuilder("{ ");
		Collection<Object> mapping = new LinkedList<Object>();
		
		String labVar = UniqueID.newID(SJ_INBRANCH_LABEL_FIELD_PREFIX);
		
		translation.append("%T %s = %E; ");
		mapping.add(qq.parseType(SJ_LABEL_CLASS));
		mapping.add(labVar);
		mapping.add(ib.inlabel());
		
		for (Iterator<SJInbranchCase> i = ib.branchCases().iterator(); i.hasNext(); )
		{
			SJInbranchCase ibc = i.next();
			
			translation.append("if (%s.equals(%E)) { %LS } ");
			mapping.add(labVar);
			mapping.add(sjnf.StringLit(ib.position(), ibc.label().labelValue()));
			mapping.add(ibc.statements());
			
			if (i.hasNext())
			{
				translation.append("else ");
			}
		}
		
		translation.append('}');
		
		Stmt s = qq.parseStmt(translation.toString(), mapping.toArray());		
		
		if (compounds.isEmpty())
		{
			s = (Stmt) buildAndCheckTypes(this, s);
            // (Re-)building types might erase previously built SJ type information.
            // Maybe we don't need to rebuild types in translation phase.
            // Or maybe no important SJ type information is lost
            // (e.g. protocol fields, method signatures, etc.).
		}
		
		return s;
	}

    private Block translateSJRecursion(SJRecursion r, QQ qq) throws SemanticException
    // recursionEnter inserted by node factory, but translation is finished here..
	{
		SJSessionOperationExt soe = getSJSessionOperationExt(r);
		
		Position pos = r.position();

        Collection<Object> mapping = new LinkedList<Object>();
		
		String bname = getRecursionBooleanName(soe.targetNames(), r.label());

        mapping.add(bname);
		mapping.add(bname);

        String translation = "for (boolean %s = true; %s; ) { }";
        For f = (For) qq.parseStmt(translation, mapping.toArray());
		
		mapping.clear();
		
		r = (SJRecursion) r.inits(f.inits());
		r = (SJRecursion) r.cond(f.cond());
		
		List stmts = new LinkedList();
		
		stmts.addAll(r.body().statements()); 
		
		translation = "%s = %E;";
		mapping.add(bname);
		mapping.add(((Eval) stmts.remove(0)).expr()); // Factor out constant.
		
		Eval e = (Eval) qq.parseStmt(translation, mapping.toArray());
		
		stmts.add(0, e);
		
		r = (SJRecursion) r.body(sjnf.Block(pos, stmts));
	
		List<Local> targets = new LinkedList<Local>(); // FIXME: should be SJLocalSockets.
		
		for (String sjname : soe.targetNames()) // Unicast optimisation for SJRecursionExit is done within the NodeFactory method - this pass comes after SJUnicastOptimiser.
		{
			targets.add(sjnf.Local(pos, sjnf.Id(pos, sjname))); // Would it be bad to instead alias the recursionEnter targets? 
		}				
		
		Block b = sjnf.Block(pos, r, sjnf.Eval(pos, sjnf.SJRecursionExit(pos, targets))); // The alternative would be to have the node factory create the recursionExit in the recursion node (then have it parsed and type built by SJSessionOperationParser, etc.) and then we just move it to the right position here. // Should recursionExit take the label as an argument?
		
		if (compounds.isEmpty())
		{
			b = (Block) buildAndCheckTypes(this, b); // Need to build types in one go because cannot build types for e.g. the assignment expression separately from the newly inserted variable declaration for the assignment target.
		}
		
		return b;
	}
	
	private String getRecursionBooleanName(Iterable<String> sjnames, SJLabel lab)
	{
		StringBuilder bname = new StringBuilder(SJ_RECURSION_PREFIX);
		
		for (String sjname : sjnames)
		{
			bname.append(sjname).append('_'); 
		}
		
		return bname + lab.labelValue();		
	}
}
