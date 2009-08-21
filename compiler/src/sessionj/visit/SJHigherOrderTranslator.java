package sessionj.visit;

import java.util.*;

import polyglot.ast.*;
import polyglot.frontend.Job;
import polyglot.types.*;
import polyglot.util.*;
import polyglot.visit.*;

import sessionj.ast.*;
import sessionj.ast.sessvars.*;
import sessionj.ast.sessops.basicops.*;
import sessionj.types.SJTypeSystem;
import sessionj.types.sesstypes.*;
import sessionj.util.*;

import static sessionj.SJConstants.*;
import static sessionj.util.SJCompilerUtils.*;

// Does type encoding (like SJProtocolDeclTranslator) for session/channel send and receive.
public class SJHigherOrderTranslator extends ContextVisitor
{
	private SJTypeSystem sjts = (SJTypeSystem) typeSystem();
	private SJNodeFactory sjnf = (SJNodeFactory) nodeFactory();

	private SJTypeEncoder sjte = new SJTypeEncoder(sjts);

	public SJHigherOrderTranslator(Job job, TypeSystem ts, NodeFactory nf)
	{
		super(job, ts, nf);
	}

	protected Node leaveCall(Node old, Node n, NodeVisitor v) throws SemanticException
	{
		if (n instanceof SJPass || n instanceof SJReceive) // Includes both higher-order channel and session communication. // Channel-send takes a channel argument which the protocol can be obtained from directly.
		{
			Type t = ((SJMessageCommunicationType) getSessionType(n)).messageType();
			
			if (t instanceof SJSessionType) 
			{
				n = translateHOOperation((SJBasicOperation) n, (SJSessionType) t);
			}
		}		

		return n;
	}

	private SJBasicOperation translateHOOperation(SJBasicOperation bo, SJSessionType mt) throws SemanticException // Includes both higher-order channel and session communication.
	{
		List args = new LinkedList();

		StringLit encoded = sjnf.StringLit(bo.position(), sjte.encode(mt));
		
		args.addAll(bo.arguments());				
		
		if (bo instanceof SJPass) 
		{
			args.add(2, encoded); // Inserts argument into this position. // Factor out constant.
		}
		else //if (bo instanceof SJReceive)
		{
			args.add(1, encoded); // Factor out constant.
			
			if (mt instanceof SJBeginType) // Channel-receive.
			{
				bo = (SJBasicOperation) bo.name(SJ_RUNTIME_RECEIVECHANNEL);
			}
		}

		bo = (SJBasicOperation) bo.arguments(args);
		bo = (SJBasicOperation) buildAndCheckTypes(job(), this, bo);
		
		return bo;
	}
}
