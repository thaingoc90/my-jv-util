package my.library.us;

/**
 * Provides alternative sources of the salt to use for encoding passwords.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
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
