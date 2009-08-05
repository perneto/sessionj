/**
 * 
 */
package sessionj.runtime.net;

import sessionj.runtime.SJIOException;

/**
 * @author Raymond
 *
 * Hack for satisfying base type checking when channel/session receive cannot be distinguished.
 *
 */
abstract public class SJServiceSocketHack extends SJService implements SJSocket
{

}
