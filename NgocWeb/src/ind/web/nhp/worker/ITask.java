package ind.web.nhp.worker;

public interface ITask {

	public static enum Status {
		RUNNING, // task is running
		IDLE, // task is idle
	};

	public static enum Scheduling {
		RUNONCE, // task runs only once
		REPEATED, // task runs repeatedly at a fixed rate
		REPEATED_AFTER_FINISH, // task runs repeatedly with a fixed delay
	};

	/**
	 * Gets task's unique id.
	 * 
	 * @return
	 */
	public String getId();

	/**
	 * Gets task's info.
	 * 
	 * @return
	 */
	public TaskInfo getTaskInfo();

	/**
	 * Executes task.
	 * 
	 * @param params
	 * @return
	 */
	public Object executeTask(Object params);
}
