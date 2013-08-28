package ind.web.nhp.worker;

import java.util.concurrent.TimeUnit;

public class TaskInfo {

	private long initialDelay, fixedRateDelay;
	private TimeUnit timeUnit;
	private ITask.Scheduling scheduling;
	private Object params;

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

}
