package sessionj.ast.sessops.compoundops;

import sessionj.ast.sessops.TraverseTypeBuildingContext;
import sessionj.ast.SessionTypedNode;
import sessionj.extension.SJExtFactory;
import sessionj.types.sesstypes.SJSessionType;
import polyglot.types.SemanticException;
import polyglot.qq.QQ;
import polyglot.ast.Node;

public interface SJTypecase extends SJCompoundOperation, TraverseTypeBuildingContext, SessionTypedNode {
    SJCompoundOperation sessionTypeCheck(SJSessionType typeForNode) throws SemanticException;

    Node translate(QQ qq);
}
