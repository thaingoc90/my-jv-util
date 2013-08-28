package ind.web.nhp.worker;

import java.util.Calendar;

public class TestTask extends AbstractTask {

	private static final long serialVersionUID = 1L;

	public TestTask(String id, TaskInfo info) {
		super(id, info);
	}

	@Override
	protected Object internalExecuteTask(Object params) {
		System.out.println(Calendar.getInstance().getTime() + " - test task");
		return null;
	}

	public static void main(String[] args) {
		TaskInfo taskInfo = new TaskInfo(Scheduling.REPEATED, null, 10);
		taskInfo.setInitialDelay(2);
		String taskId = "TEST_TASK";
		TestTask testTask = new TestTask(taskId, taskInfo);
		TaskManager taskManager = new TaskManager();
		taskManager.init();
		System.out.println(Calendar.getInstance().getTime() + " - start");
		taskManager.scheduleTask(testTask);
	}
}
