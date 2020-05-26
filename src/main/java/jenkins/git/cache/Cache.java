package jenkins.git.cache;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

public class Cache {
	private static HashMap<String,ReentrantLock> agentLocks = new HashMap<>();
	private String repo;
	private String hash;
	public Cache(String repo) {
		this.repo = repo;
	}
	//create hash of repo;
}
