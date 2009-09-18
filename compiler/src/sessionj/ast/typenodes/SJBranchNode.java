package sessionj.ast.typenodes;

import polyglot.frontend.Job;
import polyglot.types.SemanticException;
import polyglot.visit.ContextVisitor;
import sessionj.types.SJTypeSystem;

import java.util.List;

public interface SJBranchNode extends SJTypeNode
{
	SJTypeNode disambiguateSJTypeNode(Job job, ContextVisitor cv, SJTypeSystem sjts) throws SemanticException;
}
