package ind.web.nhp.us;

import ind.web.nhp.encrypt.IAuthenticationAgent;
import ind.web.nhp.encrypt.IPasswordEncoder;
import ind.web.nhp.encrypt.ISaltGenerator;

/**
 * A simple implementation of {@link IAuthenticationAgent}.
 * 
 */
public class SimpleAuthenticationAgent implements IAuthenticationAgent {

	private IPasswordEncoder passwordEncoder;

	private ISaltGenerator saltGenerator;

	/**
	 * Initialiazing method.
	 */
	public void init() {
		// EMPTY
	}

	/**
	 * Destruction method.
	 */
	public void destroy() {
		// EMPTY
	}

	/**
	 * Supplies a salt generator instance to the agent.
	 * 
	 * @param saltGenerator
	 */
	public void setSaltGenerator(ISaltGenerator saltGenerator) {
		this.saltGenerator = saltGenerator;
	}

	/**
	 * Gets the salt generator instance associated with the agent.
	 * 
	 * @return
	 */
	public ISaltGenerator getSaltGenerator() {
		return this.saltGenerator;
	}

	/**
	 * Supplies a password encoder instance to the agent.
	 * 
	 * @param passwordEncoder
	 */
	public void setPasswordEncoder(IPasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	/**
	 * Gets the password encoder instance associated with the agent.
	 * 
	 * @return
	 */
	public IPasswordEncoder getPasswordEncoder() {
		return this.passwordEncoder;
	}

	/**
	 * Returns the salt to use for the indicated user.
	 * 
	 * @param user
	 * @return Object
	 */
	protected Object getSalt(IUser user) {
		return saltGenerator != null ? saltGenerator.generateSalt(user) : null;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean authenticate(IUser user, String password) {
		return user != null ? getPasswordEncoder().isPasswordValid(
				user.getPassword(), password, getSalt(user)) : false;
	}
}
