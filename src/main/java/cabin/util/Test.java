package cabin.util;

import java.util.SortedSet;

public class Test {

	private Cabin cabin = null;
	private FloorRequest bestRequest = null;
	private SortedSet<FloorRequest> youngRequests = null;
	private SortedSet<FloorRequest> oldRequests = null;

	public Test(Cabin cabin, SortedSet<FloorRequest> youngRequests, SortedSet<FloorRequest> oldRequests) {
		this.bestRequest = null;

		this.cabin = cabin;
		this.youngRequests = youngRequests;
		this.oldRequests = oldRequests;
	}

	public Cabin getCabin() {
		return cabin;
	}

	public FloorRequest getBestRequest() {
		return bestRequest;
	}

	public SortedSet<FloorRequest> getYoungRequests() {
		return youngRequests;
	}

	public SortedSet<FloorRequest> getOldRequests() {
		return oldRequests;
	}
}
