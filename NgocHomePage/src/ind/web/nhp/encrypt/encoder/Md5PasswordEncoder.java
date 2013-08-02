package ind.web.nhp.encrypt.encoder;

/**
 * MD5 implementation of {@link IPasswordEncoder}.
 * <p>
 * This is a convenient class that extends the
 * {@link MessageDigestPasswordEncoder} and passes MD5 as the algorithm to use.
 * </p>
 * 
 */
public class Md5PasswordEncoder extends MessageDigestPasswordEncoder {
	/**
	 * Constructs a new {@link Md5PasswordEncoder} object.
	 */
	public Md5PasswordEncoder() {
		super("MD5");
	}

}
