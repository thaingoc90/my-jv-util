package my.library.us.salt;

import my.library.us.ISaltGenerator;
import my.library.us.IUser;

/**
 * This implementation of {@link ISaltGenerator} returns user's login name as salt.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public class LoginNameSaltGenerator implements ISaltGenerator {
	/**
	 * {@inheritDoc}
	 */
	public Object generateSalt(IUser user) {
		return user.getLoginName();
	}
}
