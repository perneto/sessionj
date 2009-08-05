/**
 * 
 */
package sessionj.types.contexts;

import java.util.*;

import sessionj.ast.sessops.compoundops.*;
import sessionj.types.sesstypes.*;
import sessionj.types.typeobjects.*;
import sessionj.util.SJLabel;

/**
 * @author Raymond
 *
 */
public class SJOutbranchContext_c extends SJBranchCaseContext_c implements SJOutbranchContext
{
	private SJOutbranch ob; // Basically duplicated from SJSessionContext_c.	
	private List<String> targets;	
	
	public SJOutbranchContext_c()
	{
		
	}
	
	public SJOutbranchContext_c(SJContextElement ce, SJOutbranch ob, List<String> targets, SJLabel lab)
	{
		super(ce, lab);	
		
		this.targets = targets;
		this.ob = ob;
	}
	
	public SJOutbranch node()
	{
		return ob;
	}
	
	/* (non-Javadoc)
	 * @see sessionj.types.contexts.SJSessionContext#targets()
	 */
	@Override
	public List<String> targets()
	{
		return targets;
	}	
}
