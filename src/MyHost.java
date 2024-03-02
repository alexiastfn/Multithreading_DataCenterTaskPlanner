/* Implement this class. */

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;


public class MyHost extends Host {

	private int finishedTasks = 0;
	private int totalTasks = 0;
	private int oneSec = 0;
	private final HashMap<Long, LinkedList<Task>> addedTasks = new HashMap<Long, LinkedList<Task>>();
	private final LinkedList<Task> runningTasks = new LinkedList<>();
	private boolean isItrunning = true;

	@Override
	public void run() {
		while (runningTasks.size() != 0 || isItrunning) {
			Instant start = Instant.now();
			if (oneSec > 0 && runningTasks.size() != 0) {
				runningTasks.get(0).setLeft(runningTasks.get(0).getLeft() - 1000);

				if (runningTasks.get(0).getLeft() <= 0) {
					finishedTasks++;
					runningTasks.get(0).finish();
					runningTasks.remove(0);
				}
			}
			try {
				Thread.sleep(950);
			} catch (InterruptedException e) {}
			updateRunningTasks(oneSec);


			while (Duration.between(start, Instant.now()).toMillis() <= 999);
			oneSec++;
		}
	}

	public void updateRunningTasks(int seconds) {
		LinkedList<Task> tasksAtSecond = addedTasks.get((long) seconds);

		if (tasksAtSecond != null) {
			for (Task task : tasksAtSecond) {
				if (runningTasks.size() == 0) {
					runningTasks.add(task);
				} else if (runningTasks.getFirst().isPreemptible()) {
					if (task.getPriority() > runningTasks.getFirst().getPriority()) {
						runningTasks.addFirst(task);
					} else {
						orderRunningTasks(task);
					}
				} else {
					orderRunningTasks(task);
				}
			}
		}
	}

	public void orderRunningTasks(Task task) {
		boolean running = false;
		for (int i = 1; i < runningTasks.size(); i++) {
			Task auxTask = runningTasks.get(i);
			if (task.getPriority() > auxTask.getPriority()) {
				runningTasks.add(i, task);
				running = true;
				break;
			}
		}

		if (!running) {
			runningTasks.addLast(task);
		}
	}


	@Override
	public void addTask(Task task) {
		totalTasks++;
		long key = task.getStart();

		if (addedTasks.get(key) == null) {
			LinkedList<Task> auxList = new LinkedList<>();
			auxList.add(task);
			addedTasks.put(key, auxList);
		} else {
			LinkedList<Task> auxList = addedTasks.get(key);
			auxList.add(task);
		}
	}

	@Override
	public int getQueueSize() {
		return totalTasks - finishedTasks;
	}

	@Override
	public long getWorkLeft() {
		int result = 0;
		for (Map.Entry<Long, LinkedList<Task>> e : addedTasks.entrySet()) {
			for (Task auxTask : e.getValue()) {
				result += auxTask.getLeft();
			}
		}
		return result;
	}

	@Override
	public void shutdown() {
		isItrunning = false;
		interrupt();
	}
}
