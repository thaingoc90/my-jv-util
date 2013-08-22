package ind.web.nhp.encrypt.encoder;

import ind.web.nhp.encrypt.IPasswordEncoder;

/**
 * MD2 implementation of {@link IPasswordEncoder}.
 * <p>
 * This is a convenient class that extends the
 * {@link MessageDigestPasswordEncoder} and passes MD2 as the algorithm to use.
 * </p>
 * 
 */
public class Md2PasswordEncoder extends MessageDigestPasswordEncoder {
	/**
	 * Constructs a new {@link Md2PasswordEncoder} object.
	 */
	public Md2PasswordEncoder() {
		super("MD2");
	}
}
