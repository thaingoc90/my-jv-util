package ind.web.nhp.encrypt.salt;

import ind.web.nhp.encrypt.ISaltGenerator;
import ind.web.nhp.us.IUser;

/**
 * This implementation of {@link ISaltGenerator} simply returns <code>null</code> as salt.
 * 
 */
public class NullSaltGenerator implements ISaltGenerator {
	/**
	 * {@inheritDoc}
	 */
	public Object generateSalt(IUser user) {
		return null;
	}
}
