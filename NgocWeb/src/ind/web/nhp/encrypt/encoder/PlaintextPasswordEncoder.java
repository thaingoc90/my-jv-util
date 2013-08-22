package ind.web.nhp.encrypt.encoder;

import ind.web.nhp.encrypt.IPasswordEncoder;

import org.apache.commons.lang3.StringUtils;

/**
 * Plain text implementation of {@link IPasswordEncoder}.
 * 
 */
public class PlaintextPasswordEncoder extends BasePasswordEncoder {

	/**
	 * {@inheritDoc}
	 */
	public boolean isDecodingSupported() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public String[] decodePassword(String encodedPassword) {
		return splitPasswordAndSalt(encodedPassword);
	}

	/**
	 * {@inheritDoc}
	 */
	public String encodePassword(String rawPassword, Object salt) {
		return mergePasswordAndSalt(rawPassword, salt);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isPasswordValid(String encodedPassword, String rawPassword,
			Object salt) {
		String temp = mergePasswordAndSalt(rawPassword, salt);
		return StringUtils.equals(temp, encodedPassword);
	}
}
