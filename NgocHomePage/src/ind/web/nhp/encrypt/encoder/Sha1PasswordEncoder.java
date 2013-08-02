package ind.web.nhp.encrypt.encoder;

import ind.web.nhp.encrypt.IPasswordEncoder;

/**
 * SHA-1 implementation of {@link IPasswordEncoder}.
 * <p>
 * This is a convenience class that extends the
 * {@link MessageDigestPasswordEncoder} and passes SHA-1 as the algorithm to
 * use.
 * </p>
 * 
 */
public class Sha1PasswordEncoder extends MessageDigestPasswordEncoder {
	/**
	 * Constructs a new Sha1PasswordEncoder object.
	 */
	public Sha1PasswordEncoder() {
		super("SHA-1");
	}
}
