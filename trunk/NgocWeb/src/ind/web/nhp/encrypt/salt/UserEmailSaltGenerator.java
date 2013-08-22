package ind.web.nhp.encrypt.salt;

import ind.web.nhp.encrypt.ISaltGenerator;
import ind.web.nhp.us.IUser;

/**
 * 
 * This implementation of {@link ISaltGenerator} returns user's email as salt.
 * 
 */
public class UserEmailSaltGenerator implements ISaltGenerator {
	/**
	 * {@inheritDoc}
	 */
	public Object generateSalt(IUser user) {
		return user.getEmail();
	}
}
