package my.library.main;

import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ThreadScheduler {
	private static ScheduledExecutorService schedule1;
	private static ScheduledFuture<?> thread1, thread2;

	public static void main(String[] args) throws InterruptedException {

		System.out.println("Start time: " + Calendar.getInstance().getTime());
		schedule1 = Executors.newSingleThreadScheduledExecutor();

		schedule1.schedule(new Runnable() {

			@Override
			public void run() {
				System.out.println("Fix thread: Time: " + Calendar.getInstance().getTime());

			}
		}, 1, TimeUnit.SECONDS);

		thread1 = schedule1.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				System.out.println("thread 1: after 5 second " + Calendar.getInstance().getTime());
			}
		}, 0, 5, TimeUnit.SECONDS);

		thread2 = schedule1.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				System.out.println("thread 2: after 3 second " + Calendar.getInstance().getTime());
			}
		}, 0, 3, TimeUnit.SECONDS);

		Thread.sleep(10000);
		thread1.cancel(true);
		thread2.cancel(true);

		System.out.println("Finished main thread");
		
	}
}
