package sessionj.visit;

import polyglot.ast.*;
import polyglot.frontend.Job;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import static sessionj.SJConstants.*;
import sessionj.ast.SJNodeFactory;
import sessionj.ast.SJSpawn;
import sessionj.ast.sessops.SJSessionOperation;
import sessionj.ast.sesstry.SJTry;
import sessionj.ast.sessvars.SJVariable;
import static sessionj.util.SJCompilerUtils.buildAndCheckTypes;

import java.util.LinkedList;
import java.util.List;

public class SJVariableParser extends ContextVisitor
{
    private SJNodeFactory sjnf = (SJNodeFactory) nodeFactory();

	public SJVariableParser(Job job, TypeSystem ts, NodeFactory nf)
	{
		super(job, ts, nf);
	}

	protected Node leaveCall(Node old, Node n, NodeVisitor v) throws SemanticException
	{
		if (n instanceof Variable)
		{
			if (n instanceof Field)
			{
				n = parseSJField((Field) n);
			}
			else if (n instanceof Local)
			{
				n = parseSJLocal((Local) n);
			}
			else //if (n instanceof ArrayAccess)
			{
				n = parseSJArrayAccess((ArrayAccess) n);				
			}		
		}
		else if (n instanceof SJTry) // Should be SJAmbiguousTry.
		{
			n = parseSJTry((SJTry) n);
		}
		else if (n instanceof SJSessionOperation)
		{
			n = parseSJSessionOperation((SJSessionOperation) n);
		}
		else if (n instanceof SJSpawn)
		{
			n = parseSJSpawn((SJSpawn) n);
		}

		return n;
	}
	
	private Field parseSJField(Field f) throws SemanticException // Doesn't attach extension objects (SJVariables are not SJTypeable).
	{
		Type t = f.type();
		
		if (t.isSubtype(SJ_ABSTRACT_CHANNEL_TYPE))
		{
			throw new SemanticException("[SJVariableParser] Session-typed fields not yet supported: " + f);
		}
		
		return f;
	}		
	
	private Local parseSJLocal(Local l) throws SemanticException // Doesn't attach extension objects (SJVariables are not SJTypeable).
	{
		SJVariable v = null;
		
		if (l.type().isSubtype(SJ_CHANNEL_TYPE))
		{
			v = sjnf.SJLocalChannel(l.position(), l.id());
		}
		else if (l.type().isSubtype(SJ_SOCKET_INTERFACE_TYPE))
		{
			v = sjnf.SJLocalSocket(l.position(), l.id());
		}		
		else if (l.type().isSubtype(SJ_SERVER_INTERFACE_TYPE))
		{
			v = sjnf.SJLocalServer(l.position(), l.id());
		}				
		 
		if (v != null)
		{
			l = (Local) buildAndCheckTypes(job(), this, v); // Instead could just reassign the existing type objects of `l' to `v'?
		}
		
		return l;
	}	
	
	private ArrayAccess parseSJArrayAccess(ArrayAccess aa) throws SemanticException
	{
		Type t = aa.type();
		
		if (t.isSubtype(SJ_ABSTRACT_CHANNEL_TYPE))
		{
			throw new SemanticException("[SJVariableParser] Session-typed array accesses not yet supported: " + aa);
		}
		
		return aa;
	}		
	
	// Session operation targets currently not visited by base passes.
	private SJTry parseSJTry(SJTry st) throws SemanticException
	{										
		return st.targets(parseSocketList(st.targets(), false));
	}
		
	// Currently duplicated with SJTry (could make SJTry a SJNamed).
	private SJSessionOperation parseSJSessionOperation(SJSessionOperation so) throws SemanticException
	{
		return so.targets(parseSocketList(so.targets(), false));
	}	
	
	private SJSpawn parseSJSpawn(SJSpawn s) throws SemanticException // Based on parseSJSessionOperation.
	{
		return s.targets(parseSocketList(s.targets(), true));		
	}	
	
	// Should check no repeated sockets for session-try and session operations. // Now also optionally does channels (for SJSpawn).
	private List<SJVariable> parseSocketList(List l, boolean channelsAllowed) throws SemanticException
	{
		List<SJVariable> targets = new LinkedList<SJVariable>();

        for (Object aL : l) {
            Receiver r = (Receiver) buildAndCheckTypes(job(), this, (Receiver) aL); // Runs AmbiguityRemover.

            Type t = r.type();

            if (r instanceof Local) {
                if (t.isSubtype(SJ_SOCKET_INTERFACE_TYPE) || t.isSubtype(SJ_SERVER_INTERFACE_TYPE)) {
                    targets.add((SJVariable) parseSJLocal((Local) r));
                } else if (channelsAllowed && t.isSubtype(SJ_CHANNEL_TYPE)) {
                    targets.add((SJVariable) parseSJLocal((Local) r));
                } else {
                    throw new SemanticException("[SJVariableParser] Expected session socket or server variable, not: " + r);
                }
            } else {
                throw new SemanticException("[SJVariableParser] Expected local variable, not: " + r);
            }
        }
		
		return targets;
	}	
}
