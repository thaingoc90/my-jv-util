package ind.web.nhp.encrypt;

import ind.web.nhp.us.IUser;

/**
 * Provides alternative sources of the salt to use for encoding passwords.
 * 
 */
public interface ISaltGenerator {
	/**
	 * Generates salt from an {@link IUser} object.
	 * 
	 * @param user
	 *            IUser
	 * @return Object
	 */
	public Object generateSalt(IUser user);
}
