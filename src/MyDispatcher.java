/* Implement this class. */

import java.util.List;

public class MyDispatcher extends Dispatcher {

	private int currentID = 0;

	public MyDispatcher(SchedulingAlgorithm algorithm, List<Host> hosts) {
		super(algorithm, hosts);
	}

	public void algorithm_RoundRobin(Task task) {
		Host currentHost = hosts.get(currentID);
		currentHost.addTask(task);
		currentID = (currentID + 1) % hosts.size();
	}

	public void algorithm_ShortestQueue(Task task) {
		Host bestHost = null;
		int minWorkLoad = Integer.MAX_VALUE;

		for (Host host : hosts) {
			int currentWorkLoad = host.getQueueSize();
			if (currentWorkLoad < minWorkLoad || (currentWorkLoad == minWorkLoad && host.getId() < bestHost.getId())) {
				bestHost = host;
				minWorkLoad = currentWorkLoad;
			}
		}

		if (bestHost != null) {
			bestHost.addTask(task);
		}
	}


	public void algorithm_SizeIntervalTaskAssignment(Task task) {
		Host hostShort = hosts.get(0);
		Host hostMedium = hosts.get(1);
		Host hostLong = hosts.get(2);

		switch (task.getType()) {
			case SHORT:
				hostShort.addTask(task);
				break;
			case MEDIUM:
				hostMedium.addTask(task);
				break;
			case LONG:
				hostLong.addTask(task);
				break;
		}
	}

	public void algorithm_LeastWorkLeft(Task task) {
		Host bestHost = null;
		long minWorkLoad = Long.MAX_VALUE;

		for (Host host : hosts) {
			long currentWorkLoad = host.getWorkLeft() / 1000;
			if (currentWorkLoad < minWorkLoad || (currentWorkLoad == minWorkLoad && host.getId() < bestHost.getId())) {
				bestHost = host;
				minWorkLoad = currentWorkLoad;
			}
		}

		if (bestHost != null) {
			bestHost.addTask(task);
		}

	}

	@Override
	public void addTask(Task task) {

		switch (this.algorithm) {
			case ROUND_ROBIN:
				algorithm_RoundRobin(task);
				break;
			case SHORTEST_QUEUE:
				algorithm_ShortestQueue(task);
				break;
			case SIZE_INTERVAL_TASK_ASSIGNMENT:
				algorithm_SizeIntervalTaskAssignment(task);
				break;
			case LEAST_WORK_LEFT:
				algorithm_LeastWorkLeft(task);
				break;
		}

	}
}
