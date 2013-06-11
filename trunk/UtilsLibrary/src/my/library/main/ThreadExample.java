package my.library.main;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

public class ThreadExample {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
		executor.setCorePoolSize(1);
		executor.setMaximumPoolSize(2);
		List<Task> listTasks = new LinkedList<Task>();
		int numThread = 2;
		Task task;
		for (int i = 0; i < numThread; i++) {
			task = new Task(i + 1);
			listTasks.add(task);
		}
		long current = System.currentTimeMillis();
		List<Future<Integer>> results = executor.invokeAll(listTasks);
		for (Future<Integer> result : results) {
			if (result.isCancelled()) {
				System.out.println("time out");
			} else {
				System.out.println(result.get());
			}
		}
		long duration = System.currentTimeMillis() - current;
		System.out.println("thoi gian :" + duration);
		executor.shutdown();
	}
}

class Task implements Callable<Integer> {

	private Integer code;

	Task(Integer code) {
		this.code = code;
	}

	@Override
	public Integer call() throws Exception {
		System.out.println("task " + code + " is running");
		Thread.sleep(1000);
		return code;
	}

}
