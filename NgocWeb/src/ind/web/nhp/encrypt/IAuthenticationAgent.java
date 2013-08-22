package ind.web.nhp.encrypt;

import ind.web.nhp.us.IUser;

/**
 * Provides APIs to authenticate users.
 * 
 */
public interface IAuthenticationAgent {
	/**
	 * Authenticates a user against a password.
	 * 
	 * @param user
	 *            IUser
	 * @param password
	 *            String
	 * @return boolean
	 */
	public boolean authenticate(IUser user, String password);
}