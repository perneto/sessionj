package sessionj.visit;

import java.util.*;

import polyglot.ast.*;
import polyglot.frontend.Job;
import polyglot.types.*;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;

import sessionj.ast.*;
import sessionj.ast.protocoldecls.*;
import sessionj.ast.sessops.*;
import sessionj.ast.sessops.basicops.*;
import sessionj.ast.sesstry.*;
import sessionj.ast.sessvars.*;
import sessionj.ast.typenodes.*;
import sessionj.extension.*;
import sessionj.types.*;
import sessionj.types.sesstypes.*;
import sessionj.types.typeobjects.*;

import static sessionj.SJConstants.*;
import static sessionj.util.SJCompilerUtils.*;

public class SJVariableParser extends ContextVisitor
{
	private SJTypeSystem sjts = (SJTypeSystem) typeSystem();
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
		
		if (t.isSubtype(SJ_CHANNEL_TYPE) || t.isSubtype(SJ_SOCKET_INTERFACE_TYPE) || t.isSubtype(SJ_SERVER_INTERFACE_TYPE)) // Make a common supertype?
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
		
		if (t.isSubtype(SJ_CHANNEL_TYPE) || t.isSubtype(SJ_SOCKET_INTERFACE_TYPE) || t.isSubtype(SJ_SERVER_INTERFACE_TYPE))
		{
			throw new SemanticException("[SJVariableParser] Session-typed array accesses not yet supported: " + aa);
		}
		
		return aa;
	}		
	
	// Session operation targets currently not visited by base passes.
	private SJTry parseSJTry(SJTry st) throws SemanticException
	{										
		st = (SJTry) st.targets(parseSocketList(st.targets(), false));
		
		return st;
	}
		
	// Currently duplicated with SJTry (could make SJTry a SJNamed).
	private SJSessionOperation parseSJSessionOperation(SJSessionOperation so) throws SemanticException
	{
		so = (SJSessionOperation) so.targets(parseSocketList(so.targets(), false));
		
		return so;
	}	
	
	private SJSpawn parseSJSpawn(SJSpawn s) throws SemanticException // Based on parseSJSessionOperation.
	{
		s = (SJSpawn) s.targets(parseSocketList(s.targets(), true));
		
		return s;
	}	
	
	// Should check no repeated sockets for session-try and session operations. // Now also optionally does channels (for SJSpawn).
	private List<SJVariable> parseSocketList(List l, boolean channelsAllowed) throws SemanticException
	{
		List<SJVariable> targets = new LinkedList<SJVariable>();
		
		for (Iterator i = l.iterator(); i.hasNext(); )
		{
			Receiver r = (Receiver) buildAndCheckTypes(job(), this, (Receiver) i.next()); // Runs AmbiguityRemover. 
			
			Type t = r.type();
			
			if (r instanceof Local)
			{
				if (t.isSubtype(SJ_SOCKET_INTERFACE_TYPE) || t.isSubtype(SJ_SERVER_INTERFACE_TYPE))
				{
					targets.add((SJVariable) parseSJLocal((Local) r));	 
				}
				else if (channelsAllowed && t.isSubtype(SJ_CHANNEL_TYPE))
				{
					targets.add((SJVariable) parseSJLocal((Local) r));	 
				}
				else 
				{
					throw new SemanticException("[SJVariableParser] Expected session socket or server variable, not: " + r);
				}
			}
			else
			{
				throw new SemanticException("[SJVariableParser] Expected local variable, not: " + r);
			}					
		}
		
		return targets;
	}	
}
