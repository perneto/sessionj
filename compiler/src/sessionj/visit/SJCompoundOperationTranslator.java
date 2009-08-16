package sessionj.visit;

import polyglot.ast.*;
import polyglot.frontend.Job;
import polyglot.qq.QQ;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import static sessionj.SJConstants.*;
import sessionj.ast.SJNodeFactory;
import sessionj.ast.sessops.basicops.SJRecurse;
import sessionj.ast.sessops.compoundops.SJCompoundOperation;
import sessionj.ast.sessops.compoundops.SJInbranch;
import sessionj.ast.sessops.compoundops.SJInbranchCase;
import sessionj.ast.sessops.compoundops.SJRecursion;
import sessionj.extension.sessops.SJSessionOperationExt;
import sessionj.types.SJTypeSystem;
import static sessionj.util.SJCompilerUtils.buildAndCheckTypes;
import static sessionj.util.SJCompilerUtils.getSJSessionOperationExt;
import sessionj.util.SJCounter;
import sessionj.util.SJLabel;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * 
 * @author Raymond
 *
 * Also translates basic recurse operation.
 *
 */
public class SJCompoundOperationTranslator extends ContextVisitor //ErrorHandlingVisitor
{
	private SJTypeSystem sjts = (SJTypeSystem) typeSystem();
	private SJNodeFactory sjnf = (SJNodeFactory) nodeFactory();

	private SJCounter sjc = new SJCounter();

	private Stack<SJCompoundOperation> compounds = new Stack<SJCompoundOperation>(); // Actually, only SJInbranch and SJRecursion.
	
	public SJCompoundOperationTranslator(Job job, TypeSystem ts, NodeFactory nf)
	{
		super(job, ts, nf);
	}

	protected NodeVisitor enterCall(Node parent, Node n) throws SemanticException
	{
		if (n instanceof SJInbranch || n instanceof SJRecursion)
		{
			compounds.push((SJCompoundOperation) n);
		}
		
		return this;
	}
	
	protected Node leaveCall(Node parent, Node old, Node n, NodeVisitor v) throws SemanticException
	{
		if (n instanceof SJCompoundOperation)
		{
			SJCompoundOperation so = (SJCompoundOperation) n;

			Position pos = so.position();

			if (so instanceof SJInbranch)
			{	
				compounds.pop(); // We're only using this to tell if we're dealing with the outermost inbranch/recursion for type checking purposes.
				
				n = translateSJInbranch((SJInbranch) so);
			}
			else if (so instanceof SJRecursion)
			{
				compounds.pop();
				
				n = translateSJRecursion((SJRecursion) so);
			}
		}
		else if (n instanceof SJRecurse)
		{
			n = translateSJRecurse(parent, (SJRecurse) n);
		}

		return n;
	}

	private Assign translateSJRecurse(Node parent, SJRecurse r) throws SemanticException
	{
		if (!(parent instanceof Eval))
		{
			throw new RuntimeException("[SJCompoundOperationTranslator] Shouldn't get here.");			
		}
				
		QQ qq = new QQ(sjts.extensionInfo(), r.position());		
		
		String translation = "";
		List<Object> mapping = new LinkedList<Object>();
		
		translation += "%s = %E";
		mapping.add(getRecursionBooleanName(getSJSessionOperationExt(r).sjnames(), r.label()));		
		mapping.add(r);

        //a = (Assign) buildAndCheckTypes(job(), this, a); // Can't build the types now because the assignment target variable is not in the context - but it will be built when we translate the outer(most) recursion statement.
		
		return (Assign) qq.parseExpr(translation, mapping.toArray());
	}
	
	private Stmt translateSJInbranch(SJInbranch ib) throws SemanticException 
	{
		Position pos = ib.position();		
		QQ qq = new QQ(sjts.extensionInfo(), pos);
		
		String translation = "{ ";
		List<Object> mapping = new LinkedList<Object>();
		
		String labVar = SJ_INBRANCH_LABEL_FIELD_PREFIX + sjc.nextValue();
		
		translation += "%T %s = %E; ";
		mapping.add(qq.parseType(SJ_LABEL_CLASS));
		mapping.add(labVar);
		mapping.add(ib.inlabel());
		
		for (Iterator<SJInbranchCase> i = ib.branchCases().iterator(); i.hasNext(); )
		{
			SJInbranchCase ibc = i.next();
			
			//translation += "if (%s.equals(\"%s\")) { %LS } ";
			translation += "if (%s.equals(%E)) { %LS } ";
			mapping.add(labVar);
			//mapping.add(ibc.label().labelValue());
			mapping.add(sjnf.StringLit(pos, ibc.label().labelValue()));
			mapping.add(ibc.statements());
			
			if (i.hasNext())
			{
				translation += "else ";
			}
		}
		
		translation += "}";
		
		Stmt s = qq.parseStmt(translation, mapping.toArray());		
		
		if (compounds.isEmpty())
		{
			s = (Stmt) buildAndCheckTypes(job(), this, s); // (Re-)building types might erase previously built SJ type information. Maybe we don't need to rebuild types in translation phase. Or maybe no important SJ type information is lost (e.g. protocol fields, method signatures, etc.).
		}
		
		return s;
	}
	
	private Block translateSJRecursion(SJRecursion r) throws SemanticException // recursionEnter inserted by node factory, but translation is finished here..
	{
		SJSessionOperationExt soe = getSJSessionOperationExt(r);
		
		Position pos = r.position();
		QQ qq = new QQ(sjts.extensionInfo(), pos);
		
		String translation = null;
		List<Object> mapping = new LinkedList<Object>();
		
		String bname = getRecursionBooleanName(soe.sjnames(), r.label());
		
		translation = "for (boolean %s = true; %s; ) { }";
		mapping.add(bname);
		mapping.add(bname);
		
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
		
		for (String sjname : soe.sjnames()) // Unicast optimisation for SJRecursionExit is done within the NodeFactory method - this pass comes after SJUnicastOptimiser.
		{
			targets.add(sjnf.Local(pos, sjnf.Id(pos, sjname))); // Would it be bad to instead alias the recursionEnter targets? 
		}				
		
		Block b = sjnf.Block(pos, r, sjnf.Eval(pos, sjnf.SJRecursionExit(pos, targets))); // The alternative would be to have the node factory create the recursionExit in the recursion node (then have it parsed and type built by SJSessionOperationParser, etc.) and then we just move it to the right position here. // Should recursionExit take the label as an argument?
		
		if (compounds.isEmpty())
		{
			b = (Block) buildAndCheckTypes(job(), this, b); // Need to build types in one go because cannot build types for e.g. the assignment expression separately from the newly inserted variable declaration for the assignment target.
		}
		
		return b;
	}
	
	private String getRecursionBooleanName(List<String> sjnames, SJLabel lab)
	{
		String bname = SJ_RECURSION_PREFIX;
		
		for (String sjname : sjnames)
		{
			bname += sjname + "_"; 
		}
		
		return bname + lab.labelValue();		
	}	
}
