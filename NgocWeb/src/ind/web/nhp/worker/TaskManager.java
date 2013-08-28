package ind.web.nhp.worker;

import ind.web.nhp.utils.Utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TaskManager implements ITaskManager {

	private ConcurrentHashMap<String, ITask> scheduledTasks;
	private ConcurrentHashMap<String, ScheduledFuture<?>> scheduledPointer;
	private ScheduledThreadPoolExecutor threadExecutor;

	public void init() {
		int numProc = Runtime.getRuntime().availableProcessors();
		threadExecutor = new ScheduledThreadPoolExecutor(numProc);
		threadExecutor.setMaximumPoolSize(100);
		threadExecutor.setKeepAliveTime(10, TimeUnit.SECONDS);
		threadExecutor.allowCoreThreadTimeOut(true);
		scheduledTasks = new ConcurrentHashMap<String, ITask>();
		scheduledPointer = new ConcurrentHashMap<String, ScheduledFuture<?>>();
	}

	public void destroy() {
		scheduledTasks.clear();
		for (ScheduledFuture<?> schFulture : scheduledPointer.values()) {
			schFulture.cancel(true);
		}
		scheduledPointer.clear();
		threadExecutor.shutdown();
	}

	@Override
	public boolean scheduleTask(ITask task) {
		TaskInfo taskInfo = task.getTaskInfo();
		long initialDelay = taskInfo.getInitialDelay();
		long period = taskInfo.getFixedRateDelay();
		TimeUnit unit = taskInfo.getTimeUnit();
		ITask.Scheduling type = taskInfo.getScheduling();

		try {
			TaskThread taskThread = new TaskThread(task);
			switch (type) {
			case REPEATED:
				ScheduledFuture<?> schFulture = threadExecutor.scheduleAtFixedRate(taskThread,
						initialDelay, period, unit);
				scheduledTasks.put(task.getId(), task);
				scheduledPointer.put(task.getId(), schFulture);
				break;
			case RUNONCE:
				threadExecutor.schedule(taskThread, initialDelay, unit);
				break;
			default:
				break;
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	@Override
	public boolean unscheduleTask(String taskId) {
		ScheduledFuture<?> schFulture = scheduledPointer.get(taskId);
		if (schFulture != null) {
			boolean flag = schFulture.cancel(false);
			if (flag) {
				scheduledPointer.remove(taskId);
				scheduledTasks.remove(taskId);
			} else {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean unscheduleTask(ITask task) {
		String taskId = task.getId();
		return unscheduleTask(taskId);
	}

	/**
	 * Not use yet
	 */
	@Override
	public boolean isRegister(ITask task) {
		return scheduledTasks.containsKey(task.getId());
	}

	public Map<String, ITask> getScheduledTasks() {
		return scheduledTasks;
	}

	private class TaskThread implements Runnable {

		private ITask task;

		public TaskThread(ITask task) {
			this.task = task;
		}

		@Override
		public void run() {
			String runId = Utils.generateId64();
			System.out.println("RunId :" + runId);
			System.out.println("TaskId :" + task.getId());
			long start = System.currentTimeMillis();
			TaskInfo taskInfo = task.getTaskInfo();
			try {
				task.executeTask(taskInfo.getParams());
			} catch (Exception e) {
			} finally {
				long duration = System.currentTimeMillis() - start;
				System.out.println("Duration: " + duration);
			}
		}
	}
}
