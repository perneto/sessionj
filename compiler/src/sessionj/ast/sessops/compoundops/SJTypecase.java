package sessionj.ast.sessops.compoundops;

import polyglot.ast.Node;
import polyglot.qq.QQ;
import polyglot.types.SemanticException;
import sessionj.ast.SessionTypedNode;
import sessionj.ast.sessops.TraverseTypeBuildingContext;
import sessionj.types.sesstypes.SJSessionType;
import sessionj.util.SJTypeEncoder;

public interface SJTypecase extends SJCompoundOperation, TraverseTypeBuildingContext, SessionTypedNode {
    SJCompoundOperation sessionTypeCheck(SJSessionType typeForNode) throws SemanticException; // TODO: factor out the session type checking interface.

    Node translate(QQ qq, SJTypeEncoder sjte) throws SemanticException;
}
