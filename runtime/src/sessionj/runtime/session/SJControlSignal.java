/**
 * 
 */
package sessionj.runtime.session;

import java.io.Serializable;

import sessionj.runtime.SJException;

import static sessionj.SJConstants.*;

/**
 * @author Raymond
 *
 */
abstract public class SJControlSignal extends SJException implements Serializable
{
  //private static final long serialVersionUID = SJ_VERSION;
	
	public SJControlSignal()
	{
		super();
	}
}
