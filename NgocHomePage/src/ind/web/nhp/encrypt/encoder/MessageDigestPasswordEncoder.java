package ind.web.nhp.encrypt.encoder;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.security.crypto.codec.Hex;

/**
 * Base class for digest password encoders.
 * <p>
 * This class can be used stand-alone, then you must specify a <a href=
 * "http://java.sun.com/j2se/1.5/docs/guide/security/CryptoSpec.html#AppA"
 * >Message Digest Algorithms</a>
 * </p>
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public class MessageDigestPasswordEncoder extends BasePasswordEncoder {

	private final String algorithm;

	private final MessageDigest messageDigest;

	/**
	 * Constructs a new MessageDigestPasswordEncoder object.
	 * 
	 * @param algorithm
	 *            String The digest algorithm to use. Supports the named <a
	 *            href=
	 *            "http://java.sun.com/j2se/1.5/docs/guide/security/CryptoSpec.html#AppA"
	 *            >Message Digest Algorithms</a> in the Java environment.
	 */
	public MessageDigestPasswordEncoder(String algorithm) {
		this.algorithm = algorithm;
		this.messageDigest = getMessageDigest();
	}

	/**
	 * Gets name of the algorithm.
	 * 
	 * @return String
	 */
	public String getAlgorithm() {
		return this.algorithm;
	}

	/**
	 * Get a MessageDigest instance for the given algorithm.
	 * 
	 * @return MessageDigest
	 * @throws IllegalArgumentException
	 *             if NoSuchAlgorithmException is thrown
	 */
	protected MessageDigest getMessageDigest() throws IllegalArgumentException {
		try {
			return MessageDigest.getInstance(algorithm);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalArgumentException("No such algorithm ["
					+ algorithm + "]");
		}
	}

	/**
	 * {@inheritDoc} Normally digest password encoder is a one-way hash, which
	 * does not support decoding of the encoded password.
	 */
	public boolean isDecodingSupported() {
		return false;
	}

	/**
	 * {@inheritDoc} Normally digest password encoder is a one-way hash, which
	 * does not support decoding of the encoded password.
	 */
	public String[] decodePassword(String encodedPassword) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @return String the digest password as a hex string
	 */
	public String encodePassword(String rawPassword, Object salt) {
		String saltedPass = mergePasswordAndSalt(rawPassword, salt);

		byte[] digest;
		try {
			digest = messageDigest.digest(saltedPass.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("UTF-8 not supported!");
		}
		return new String(Hex.encode(digest).toString().toLowerCase());
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isPasswordValid(String encodedPassword, String rawPassword,
			Object salt) {
		String pass2 = encodePassword(rawPassword, salt);
		return (encodedPassword != null && encodedPassword.equals(pass2));
	}

}
