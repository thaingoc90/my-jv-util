package my.library.us.salt;

import my.library.us.ISaltGenerator;
import my.library.us.IUser;

/**
 * This implementation of {@link ISaltGenerator} simply returns <code>null</code> as salt.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public class NullSaltGenerator implements ISaltGenerator {
	/**
	 * {@inheritDoc}
	 */
	public Object generateSalt(IUser user) {
		return null;
	}
}
