package my.library.us;

/**
 * Provides APIs to authenticate users.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
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