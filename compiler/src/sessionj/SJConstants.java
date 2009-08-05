/**
 * 
 */
package sessionj;

import polyglot.types.Type;

/**
 * @author Raymond
 *
 */
public class SJConstants
{
	public static final long SJ_VERSION = 1L;
	
	public static final boolean SJ_DEBUG_PRINT = true;
	
	public static final String SJ_KEYWORD_NOALIAS = "noalias";
	
	public static final String SJ_KEYWORD_PROTOCOL = "protocol";
	//public static final String SJ_KEYWORD_SESSION = "sess"; 
	//public static final String SJ_KEYWORD_CHANNEL = "servaddr";
	
	public static final String SJ_KEYWORD_CBEGIN = "cbegin";
	public static final String SJ_KEYWORD_SBEGIN = "sbegin";
	
	public static final String SJ_KEYWORD_CHANNELCREATE = "create";
	public static final String SJ_KEYWORD_SOCKETCREATE = "create";
	public static final String SJ_KEYWORD_SERVERCREATE = "create";
	
	public static final String SJ_KEYWORD_REQUEST = "request";
	public static final String SJ_KEYWORD_SEND = "send";
	public static final String SJ_KEYWORD_PASS = "pass";
	public static final String SJ_KEYWORD_COPY = "copy";
	public static final String SJ_KEYWORD_RECEIVE = "receive";
	public static final String SJ_KEYWORD_RECEIVEINT = "receiveInt";
	public static final String SJ_KEYWORD_RECEIVEBOOLEAN = "receiveBoolean";
	public static final String SJ_KEYWORD_RECEIVEDOUBLE = "receiveDouble";
	public static final String SJ_KEYWORD_REC = "rec";
	public static final String SJ_KEYWORD_RECURSE = "recurse";

	public static final String SJ_KEYWORD_OUTBRANCH = "outbranch";
	public static final String SJ_KEYWORD_INBRANCH = "inbranch";
	public static final String SJ_KEYWORD_OUTWHILE = "outwhile";	
	public static final String SJ_KEYWORD_INWHILE = "inwhile";
	public static final String SJ_KEYWORD_RECURSION = "recursion";
	
	public static final String SJ_KEYWORD_ACCEPT = "accept";
	
	public static final String SJ_KEYWORD_SPAWN = "spawn";
	
	public static final String SJ_CHANNEL_CREATE = "create";
	public static final String SJ_SOCKET_CREATE = "create";
	public static final String SJ_SERVER_CREATE = "create";
	
	public static final String SJ_CHANNEL_REQUEST = "request";
	
	public static final String SJ_SOCKET_CLOSE = "close"; // The direct socket operations are not relevant anymore - compiler now translates to SJRuntime calls.
	public static final String SJ_SOCKET_SEND = "send";
	public static final String SJ_SOCKET_PASS = "pass";
	public static final String SJ_SOCKET_COPY = "copy";
	public static final String SJ_SOCKET_RECEIVE = "receive";
	public static final String SJ_SOCKET_RECEIVEINT = "receiveInt";
	public static final String SJ_SOCKET_RECEIVEBOOLEAN = "receiveBoolean";
	public static final String SJ_SOCKET_RECEIVEDOUBLE = "receiveDouble";
	public static final String SJ_SOCKET_OUTLABEL = "outlabel";
	public static final String SJ_SOCKET_INLABEL= "inlabel";	
	public static final String SJ_SOCKET_OUTSYNC = "outsync";
	public static final String SJ_SOCKET_INSYNC = "insync";
	public static final String SJ_SOCKET_RECURSE = "recurse";	
	public static final String SJ_SOCKET_RECURSIONENTER = "recursionEnter";
	public static final String SJ_SOCKET_RECURSIONEXIT = "recursionExit";
	
	public static final String SJ_RUNTIME_RECEIVECHANNEL = "receiveChannel";
	
	public static final String SJ_SERVER_ACCEPT = "accept";
	public static final String SJ_SERVER_GETCLOSER = "getCloser";
	
	public static final String SJ_THREAD_SPAWN = "spawn";
	
	// Used for printing type objects and nodes. Not suitable to declare as first-class parsing tokens because symbols already used for base language.
	public static final String SJ_STRING_SEPARATOR = ".";
	public static final String SJ_STRING_CBEGIN = "cbegin";
	public static final String SJ_STRING_SBEGIN = "sbegin";
	public static final String SJ_STRING_SEND_OPEN = "!<";
	public static final String SJ_STRING_SEND_CLOSE = ">";
	public static final String SJ_STRING_RECEIVE_OPEN = "?(";
	public static final String SJ_STRING_RECEIVE_CLOSE = ")";
	public static final String SJ_STRING_OUTBRANCH_OPEN = "!{";
	public static final String SJ_STRING_OUTBRANCH_CLOSE = "}";
	public static final String SJ_STRING_INBRANCH_OPEN = "?{";
	public static final String SJ_STRING_INBRANCH_CLOSE = "}";
	public static final String SJ_STRING_LABEL = ":";
	public static final String SJ_STRING_CASE_SEPARATOR = ",";
	public static final String SJ_STRING_OUTWHILE_OPEN = "![";
	public static final String SJ_STRING_OUTWHILE_CLOSE = "]*"; // '*' avoids clash with array types
	public static final String SJ_STRING_INWHILE_OPEN = "?["; 
	public static final String SJ_STRING_INWHILE_CLOSE = "]*";
	public static final String SJ_STRING_RECURSION_OPEN = "[";
	public static final String SJ_STRING_RECURSION_CLOSE = "]";
	public static final String SJ_STRING_REC = SJ_KEYWORD_REC;
	public static final String SJ_STRING_RECURSE_PREFIX = "#";
	public static final String SJ_STRING_PROTOCOL_REF_PREFIX = "@";
	public static final String SJ_STRING_PROTOCOL_DUAL_PREFIX = "^";
	public static final String SJ_STRING_UNKNOWN_TYPE = "SJUnknownType";
	public static final String SJ_STRING_DELEGATED_TYPE = "SJDelegatedType";
	
	public static final String SJ_INBRANCH_LABEL_FIELD_PREFIX = "_sjbranch_";
	public static final String SJ_RECURSION_PREFIX = "_sjrecursion_";	

	public static final String SJ_LABEL_EQUALITY_TEST = "equals";
	
	//Initialised by the parser (first pass).
	public static Type SJ_PROTOCOL_TYPE;
	public static Type SJ_CHANNEL_TYPE;
	public static Type SJ_SOCKET_INTERFACE_TYPE;
	public static Type SJ_ABSTRACT_SOCKET_TYPE; 
	public static Type SJ_SERVER_INTERFACE_TYPE;
	public static Type SJ_SERVER_TYPE;
	public static Type SJ_LABEL_TYPE;
	public static Type SJ_RUNTIME_TYPE;
	public static Type SJ_THREAD_TYPE;
	
	public static final String SJ_PROTOCOL_CLASS = "sessionj.runtime.SJProtocol";
	public static final String SJ_CHANNEL_CLASS = "sessionj.runtime.net.SJService";
	public static final String SJ_SOCKET_INTERFACE = "sessionj.runtime.net.SJSocket";
	public static final String SJ_ABSTRACT_SOCKET_CLASS = "sessionj.runtime.net.SJAbstractSocket"; 
	public static final String SJ_SERVER_INTERFACE = "sessionj.runtime.net.SJServerSocket";	
	public static final String SJ_SERVER_CLASS = "sessionj.runtime.net.SJServerSocketImpl";	
	public static final String SJ_RUNTIME_CLASS = "sessionj.runtime.net.SJRuntime"; 
	public static final String SJ_THREAD_CLASS = "sessionj.runtime.SJThread"; 	
	
	//public static final String SJ_LABEL_CLASS = "sessionj.util.SJLabel";
	public static final String SJ_LABEL_CLASS = "java.lang.String";
	
	public static final String SJ_THREAD_RUN = "srun";
	public static final String SJ_THREAD_THIS = "_sjthis";
	
	public static final String SJ_TMP_LOCAL = "_sjtmp";
	
	public static Type SJ_CHANNEL_SOCKET_HACK_TYPE;
	public static final String SJ_CHANNEL_SOCKET_HACK_CLASS = "sessionj.runtime.net.SJServiceSocketHack";
	
	public static final String JAVA_STRING_CLASS = "java.lang.String";

	public static final String POLYGLOT_TYPEDECODER_NAME_ARG = ""; // Don't know what this argument is for. Think it's just for error reporting (setting the message for a nested exception).
	
	private SJConstants() { }
}
