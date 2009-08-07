package sessionj.types;

import polyglot.frontend.Source;
import polyglot.types.*;
import sessionj.types.noalias.*;
import sessionj.types.typeobjects.*;
import sessionj.types.sesstypes.*;
import sessionj.util.SJLabel;

public interface SJTypeSystem extends TypeSystem 
{
	public SJCBeginType SJCBeginType();
	public SJSBeginType SJSBeginType();
	public SJSendType SJSendType(Type messageType) throws SemanticException;
	public SJReceiveType SJReceiveType(Type messageType) throws SemanticException;
	public SJOutbranchType SJOutbranchType();
	public SJInbranchType SJInbranchType();
	public SJOutwhileType SJOutwhileType();
	public SJInwhileType SJInwhileType();
	public SJRecursionType SJRecursionType(SJLabel lab);
	public SJRecurseType SJRecurseType(SJLabel lab);
	public SJUnknownType SJUnknownType();
	public SJDelegatedType SJDelegatedType(SJSessionType st);	
	
	public SJParsedClassType SJParsedClassType(LazyClassInitializer init, Source fromSource);
	public SJFieldInstance SJFieldInstance(FieldInstance fi, boolean isNoAlias, boolean isFinal);	
	public SJConstructorInstance SJConstructorInstance(ConstructorInstance ci);
	public SJMethodInstance SJMethodInstance(MethodInstance mi);
	public SJLocalInstance SJLocalInstance(LocalInstance li, boolean isNoAlias, boolean isFinal);
		
	public SJFieldProtocolInstance SJFieldProtocolInstance(SJFieldInstance fi, SJSessionType st, String sjname);
	public SJLocalProtocolInstance SJLocalProtocolInstance(SJLocalInstance li, SJSessionType st, String sjname);
	public SJLocalChannelInstance SJLocalChannelInstance(SJLocalInstance ci, SJSessionType st, String sjname);
	public SJLocalSocketInstance SJLocalSocketInstance(SJLocalInstance si, SJSessionType st, String sjname);
	public SJLocalServerInstance SJLocalServerInstance(SJLocalInstance ci, SJSessionType st, String sjname);
	
	public SJNoAliasReferenceType SJNoAliasReferenceType(ReferenceType rt); 	
	public SJNoAliasReferenceType SJNoAliasFinalReferenceType(ReferenceType rt, boolean isFinal);
	
	public boolean wellFormedRecursions(SJSessionType st);
}
