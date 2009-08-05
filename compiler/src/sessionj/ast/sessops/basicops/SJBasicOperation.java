package sessionj.ast.sessops.basicops;

import java.util.*;

import polyglot.ast.Call;

import sessionj.ast.sessops.SJSessionOperation;
import sessionj.ast.sessvars.SJSocketVariable;

/**
 * 
 * @author Raymond
 *
 * Basic operations are Calls.
 *
 */
public interface SJBasicOperation extends Call, SJSessionOperation
{
	public SJBasicOperation targets(List targets);
}
