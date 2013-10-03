package ind.web.nhp.worker;

import ind.web.nhp.utils.Utils;

import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.stereotype.Component;

@Component
public class TaskManager implements ITaskManager {

	private ConcurrentHashMap<String, ITask> scheduledTasks;
	private ConcurrentHashMap<String, ScheduledFuture<?>> scheduledPointer;
	private ScheduledThreadPoolExecutor threadExecutor;

	@PostConstruct
	public void init() {
		int numProc = Runtime.getRuntime().availableProcessors();
		threadExecutor = new ScheduledThreadPoolExecutor(numProc);
		threadExecutor.setMaximumPoolSize(100);
		threadExecutor.setKeepAliveTime(10, TimeUnit.SECONDS);
		threadExecutor.allowCoreThreadTimeOut(true);
		scheduledTasks = new ConcurrentHashMap<String, ITask>();
		scheduledPointer = new ConcurrentHashMap<String, ScheduledFuture<?>>();
	}

	@PreDestroy
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
			ScheduledFuture<?> schFulture = null;
			switch (type) {
			case REPEATED:
				schFulture = threadExecutor.scheduleAtFixedRate(taskThread, initialDelay, period,
						unit);
				scheduledTasks.put(task.getId(), task);
				scheduledPointer.put(task.getId(), schFulture);
				break;
			case REPEATED_AFTER_FINISH:
				schFulture = threadExecutor.scheduleWithFixedDelay(taskThread, initialDelay,
						period, unit);
				scheduledTasks.put(task.getId(), task);
				scheduledPointer.put(task.getId(), schFulture);
				break;
			case RUNONCE:
				threadExecutor.schedule(taskThread, initialDelay, unit);
				break;
			case RUN_AT_SPECIFICED_TIME:
				int[] startTime = taskInfo.getStartTime();
				long initDelay = calcInitialDelay(startTime);
				schFulture = threadExecutor
						.scheduleAtFixedRate(taskThread, initDelay, period, unit);
				scheduledTasks.put(task.getId(), task);
				scheduledPointer.put(task.getId(), schFulture);
				break;
			default:
				break;
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	private static long calcInitialDelay(int[] startTime) {
		int month = startTime[0] > 0 ? startTime[0] : 0;
		int week = startTime[1] > 0 ? startTime[1] : 0;
		switch (week) {
		case 2:
			week = Calendar.MONDAY;
			break;
		case 3:
			week = Calendar.TUESDAY;
			break;
		case 4:
			week = Calendar.WEDNESDAY;
			break;
		case 5:
			week = Calendar.THURSDAY;
			break;
		case 6:
			week = Calendar.FRIDAY;
			break;
		case 7:
			week = Calendar.SATURDAY;
			break;
		case 0:
			break;
		default:
			week = Calendar.SUNDAY;
			break;
		}
		int day = startTime[2] > 0 ? startTime[2] : 0;
		int hour = startTime[3] > 0 ? startTime[3] : 0;
		int minute = startTime[4] > 0 ? startTime[4] : 0;
		if (month <= 0 && week <= 0 && day <= 0 && hour <= 0 && minute <= 0) {
			return 0l;
		}
		Calendar cal = Calendar.getInstance();
		long curTime = cal.getTimeInMillis();
		int curMonth = cal.get(Calendar.MONTH) + 1;
		int curWeek = cal.get(Calendar.DAY_OF_WEEK);
		int curDay = cal.get(Calendar.DAY_OF_MONTH);
		int curHour = cal.get(Calendar.HOUR_OF_DAY);
		int curMinute = cal.get(Calendar.MINUTE);

		int nextMonth = month, nextDay = day, nextHour = hour;

		if (week != 0) {
			boolean notCurrent = false;
			// Set time.
			if (month != 0) {
				cal.set(Calendar.MONTH, month - 1);
			} else {
				nextMonth = curMonth;
			}
			nextHour = curHour;
			cal.set(Calendar.HOUR_OF_DAY, hour);
			cal.set(Calendar.MINUTE, minute);

			// Change if time went by.
			if (minute < curMinute) {
				nextHour--;
			}
			if (nextHour < curHour) {
				notCurrent = true;
			}
			int dayAdd = 0;
			if (notCurrent && curWeek == week) {
				dayAdd = 7;
			} else if (curWeek != week) {
				dayAdd = week > curWeek ? week - curWeek : week - curWeek + 7;
			}
			cal.add(Calendar.DATE, dayAdd);
			// if (nextDay < curDay) {
			// nextMonth--;
			// }
			// if (nextMonth < curMonth) {
			// if (month == 0) {
			// cal.add(Calendar.MONTH, 1);
			// } else {
			// cal.add(Calendar.YEAR, 1);
			// }
			// }
			long nextTime = cal.getTimeInMillis();
			return (nextTime - curTime) / 1000;
		}

		// Set time.
		if (month != 0) {
			cal.set(Calendar.MONTH, month - 1);
		} else {
			nextMonth = curMonth;
		}
		if (day != 0 || month != 0) {
			cal.set(Calendar.DAY_OF_MONTH, day == 0 ? 1 : day);
		} else {
			nextDay = curDay;
		}
		if (hour != 0 || month != 0 || day != 0) {
			cal.set(Calendar.HOUR_OF_DAY, hour);
		} else {
			nextHour = curHour;
		}
		cal.set(Calendar.MINUTE, minute);

		// Change if time went by.
		if (minute < curMinute) {
			nextHour--;
		}
		if (nextHour < curHour) {
			if (hour == 0 && month == 0 && day == 0) {
				cal.add(Calendar.HOUR_OF_DAY, 1);
			} else {
				nextDay--;
			}
		}
		if (nextDay < curDay) {
			if (day == 0 && month == 0) {
				cal.add(Calendar.DATE, 1);
			} else {
				nextMonth--;
			}
		}
		if (nextMonth < curMonth) {
			if (month == 0) {
				cal.add(Calendar.MONTH, 1);
			} else {
				cal.add(Calendar.YEAR, 1);
			}
		}

		long nextTime = cal.getTimeInMillis();
		return (nextTime - curTime) / 1000;
	}

	public static void main(String[] args) {
		int[] startTime = new int[] { 0, 6, 3, 18, 24 };
		System.out.println(calcInitialDelay(startTime));
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
