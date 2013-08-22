package ind.web.nhp.worker;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

public abstract class AbstractTask implements ITask, Serializable {

	private static final long serialVersionUID = "$Id: $".hashCode();

	private String id = null;
	private boolean interrupted = false;

	public void setId(String id) {
		this.id = id;
	}

	public void init() {}

	public void destroy() {}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getId() {
		return StringUtils.isBlank(id) ? this.getClass().getName() : id;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object executeTask(Object params) {
		if (interrupted) {
			return null;
		}
		interrupted = false;
		ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
		try {
			return internalExecuteTask(params);
		} finally {
			Thread.currentThread().setContextClassLoader(oldClassLoader);
		}
	}

	/**
	 * Interrupts task's execution.
	 */
	public void interruptExecution() {
		this.interrupted = true;
	}

	/**
	 * Checks if task execution has been interrupted.
	 * 
	 * @return
	 */
	protected boolean isInterrupted() {
		return interrupted;
	}

	/**
	 * Sub-class overrides this method to implement its own business.
	 * 
	 * @param params
	 * @return
	 */
	protected abstract Object internalExecuteTask(Object params);

}
