package my.library.us.salt;

import my.library.us.ISaltGenerator;
import my.library.us.IUser;

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
