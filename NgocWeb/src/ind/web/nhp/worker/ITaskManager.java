package ind.web.nhp.worker;

import java.util.Map;

public interface ITaskManager {

	public boolean scheduleTask(ITask task);

	public boolean unscheduleTask(String taskId);

	public boolean unscheduleTask(ITask task);

	public boolean isRegister(ITask task);

	public Map<String, ITask> getScheduledTasks();
}
