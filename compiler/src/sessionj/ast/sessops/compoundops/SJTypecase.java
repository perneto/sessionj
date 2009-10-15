package sessionj.ast.sessops.compoundops;

import polyglot.ast.Node;
import polyglot.qq.QQ;
import polyglot.types.SemanticException;
import sessionj.ast.SessionTypedNode;
import sessionj.ast.sessops.TraverseTypeBuildingContext;
import sessionj.types.sesstypes.SJSessionType;

public interface SJTypecase extends SJCompoundOperation, TraverseTypeBuildingContext, SessionTypedNode {
    SJCompoundOperation sessionTypeCheck(SJSessionType typeForNode) throws SemanticException;

    Node translate(QQ qq);
}
