package my.library.us.encoder;

import my.library.us.IPasswordEncoder;

import org.apache.commons.lang3.StringUtils;

/**
 * Base class for {@link IPasswordEncoder} implementations.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public abstract class BasePasswordEncoder implements IPasswordEncoder {

	/**
	 * Used by sub-class to generate a merged password and salt into string. The generated string
	 * will be in the form of <code>pwdLen:password:salt</code> . The toString() method of the salt
	 * will be used to obtain the salt.
	 * 
	 * @param password
	 *            String
	 * @param salt
	 *            Object
	 * @return String
	 */
	protected String mergePasswordAndSalt(String password, Object salt) {
		if (password == null) {
			password = "";
		}
		if (salt == null) {
			salt = "";
		}
		String strSalt = salt.toString();
		return password.length() + ":" + password + ":" + strSalt;
	}

	/**
	 * Used by sub-class to extract the password and salt from a string created using
	 * {@link #mergePasswordAndSalt(String, Object)}.
	 * 
	 * @param mergedPasswordAndSalt
	 *            String
	 * @return String[] the first element is the password and the second one is the salt
	 * @throws IllegalArgumentException
	 *             if mergedPasswordAndSalt is null or empty.
	 */
	protected String[] splitPasswordAndSalt(String mergedPasswordAndSalt) {
		if (StringUtils.isBlank(mergedPasswordAndSalt)) {
			throw new IllegalArgumentException("Argument is null or empty!");
		}
		int pwdBegins = mergedPasswordAndSalt.indexOf(":");
		if (pwdBegins < 1) {
			throw new IllegalArgumentException("Invalid format!");
		}
		String strPwdLen = mergedPasswordAndSalt.substring(0, pwdBegins);
		int pwdLen = Integer.parseInt(strPwdLen);
		int pwdEnds = pwdBegins + pwdLen;
		String password = mergedPasswordAndSalt.substring(pwdBegins + 1, pwdEnds);

		String salt = mergedPasswordAndSalt.substring(pwdEnds + 2);
		return new String[] { password, salt };
	}
}
