package ind.web.nhp.worker;

import java.util.concurrent.TimeUnit;

public class TaskInfo {

	private long initialDelay, fixedRateDelay;
	private TimeUnit timeUnit;
	private ITask.Scheduling scheduling;
	private Object params;
	/**
	 * startTime have 5 elements, represent for periods. Just use for type RUN_AT_SPECIFICED_TIME.
	 * 
	 * 1 -> month, 2 -> week, 3 -> day, 4 -> hour. 5 -> minute.
	 */
	private int[] startTime;

	public TaskInfo() {
		timeUnit = TimeUnit.SECONDS;
		initialDelay = 0;
	}

	public TaskInfo(ITask.Scheduling scheduling, Object params, long fixedRateDelay) {
		this();
		this.params = params;
		this.scheduling = scheduling;
		this.fixedRateDelay = fixedRateDelay;
	}

	public TaskInfo(ITask.Scheduling scheduling, Object params, long initialDelay,
			long fixedRateDelay, TimeUnit timeUnit) {
		this.params = params;
		this.scheduling = scheduling;
		this.initialDelay = initialDelay;
		this.fixedRateDelay = fixedRateDelay;
		this.timeUnit = timeUnit;
	}

	public TaskInfo(ITask.Scheduling scheduling, Object params, long fixedRateDelay,
			TimeUnit timeUnit, int[] startTime) {
		this(scheduling, params, fixedRateDelay);
		this.timeUnit = timeUnit;
		this.startTime = startTime;
	}

	public long getInitialDelay() {
		return initialDelay;
	}

	public void setInitialDelay(long initialDelay) {
		this.initialDelay = initialDelay;
	}

	public long getFixedRateDelay() {
		return fixedRateDelay;
	}

	public void setFixedRateDelay(long fixedRateDelay) {
		this.fixedRateDelay = fixedRateDelay;
	}

	public TimeUnit getTimeUnit() {
		return timeUnit;
	}

	public void setTimeUnit(TimeUnit timeUnit) {
		this.timeUnit = timeUnit;
	}

	public ITask.Scheduling getScheduling() {
		return scheduling;
	}

	public void setScheduling(ITask.Scheduling scheduling) {
		this.scheduling = scheduling;
	}

	public Object getParams() {
		return params;
	}

	public void setParams(Object params) {
		this.params = params;
	}

	public int[] getStartTime() {
		return startTime;
	}

	public void setStartTime(int[] startTime) {
		this.startTime = startTime;
	}

}
