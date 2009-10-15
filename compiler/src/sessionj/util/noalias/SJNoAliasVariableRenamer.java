/**
 * 
 */
package sessionj.util.noalias;

import java.util.*;

import polyglot.ast.*;
import polyglot.frontend.Job;
import polyglot.qq.*;
import polyglot.types.*;
import polyglot.visit.*;

import sessionj.ast.*;
import sessionj.types.*;
import sessionj.types.typeobjects.*;
import sessionj.types.noalias.*;

import static sessionj.SJConstants.*;
import static sessionj.util.SJCompilerUtils.*;

/**
 * @author Raymond
 * 
 * Also translates Field/ArrayAccessAssign to LocalAssign if the LHS has been renamed for translation to a Local.
 * 
 */
public class SJNoAliasVariableRenamer extends ErrorHandlingVisitor
{
	SJTypeSystem sjts = (SJTypeSystem) typeSystem();
	SJNodeFactory sjnf = (SJNodeFactory) nodeFactory();
	
	private Set<Variable> vars;
	private ContextVisitor cv;
	
	/**
	 * @param job
	 * @param ts
	 * @param nf
	 */
	public SJNoAliasVariableRenamer(Job job, ContextVisitor cv, Set<Variable> vars)
	{
		super(job, cv.typeSystem(), cv.nodeFactory());
		
		this.cv = cv;
		this.vars = vars;
	}

	protected ErrorHandlingVisitor enterCall(Node parent, Node n)  
	{
		if (n instanceof Field) 
		{
			return (ErrorHandlingVisitor) this.bypass(((Field) n).target()); // toString seems to get the (full) name of the field, so no need to visit the field target.			
		}
		
		return this;
	}
	
	protected Node leaveCall(Node parent, Node old, Node n, NodeVisitor v) throws SemanticException
	{		
		if (n instanceof Assign && !(n instanceof LocalAssign)) // FieldAssign or ArrayAccessAssign.
		{
			Assign a = (Assign) n; 
			Expr left = a.left();
			
			if (left instanceof Local)
			{						
				LocalAssign la = sjnf.LocalAssign(a.position(), (Local) a.left(), Assign.ASSIGN, a.right());
				la = (LocalAssign) la.type(a.type());
				
				n = la;
			}
		}		
		else if (n instanceof Variable && isNoAlias(n))
		{						
			Variable var = (Variable) n;			

			boolean translate = false;
			
			for (Variable foo : vars)
			{
				if (foo.toString().equals(var.toString()))
				{
					translate = true;
					
					break;
				}				
			}
			
			if (translate)
			{
				if (var instanceof Field)
				{											
					String vname = renameNoAliasVariable((Field) var);
					
					Local local = sjnf.Local(var.position(), sjnf.Id(var.position(), vname));
					//n = buildAndCheckTypes(job(), n, cv); // The existing context has not yet recorded the variables that we have just declared.								
					
					local = (Local) local.type(var.type());
					local = local.localInstance(sjts.SJLocalInstance(sjts.localInstance(var.position(), Flags.NONE, local.type(), vname), false, false)); 
					
					n = local;
				}
				else if (var instanceof Local)
				{
					String vname = renameNoAliasVariable((Local) var);			
					
					n = ((Local) var).name(vname);
				}
				else //if (var instanceof ArrayAccess)
				{
					ArrayAccess aa = (ArrayAccess) var;
					String vname = renameNoAliasVariable(aa);			
					
					Local local = sjnf.Local(aa.position(), sjnf.Id(aa.position(), vname));								
					
					local = (Local) local.type(aa.array().type());
					local = local.localInstance(sjts.SJLocalInstance(sjts.localInstance(aa.position(), Flags.NONE, local.type(), vname), false, false));
					
					n = local;
				}
			}											
		}
		
		return n;
	}
	
	public static String renameNoAliasVariable(Variable v) throws SemanticException
	{
		String vname = v.toString(); 
		
		if (v instanceof Field)
		{
			vname = SJ_TMP_LOCAL + "_" + vname;
		}
		else if (v instanceof Local)
		{
			vname = SJ_TMP_LOCAL + "_" + vname;
		}
		else //if (v instanceof ArrayAccess)
		{			
			vname = SJ_TMP_LOCAL + "_" + vname;
		}
		
		return vname.replace(".", "_").replace("[", "_").replace("]", "");
	}
}
	