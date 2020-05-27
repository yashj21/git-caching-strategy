package jenkins.git.cache;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;
import org.jenkinsci.plugins.gitclient.CloneCommand;
import org.jenkinsci.plugins.gitclient.Git;
import org.jenkinsci.plugins.gitclient.GitClient;
import org.kohsuke.stapler.DataBoundConstructor;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.AbortException;
import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Util;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.plugins.git.GitException;
import hudson.plugins.git.GitSCM;
import hudson.plugins.git.extensions.GitSCMExtension;
import hudson.plugins.git.extensions.GitSCMExtensionDescriptor;
import hudson.plugins.git.extensions.impl.CachingStrategy;
import hudson.plugins.git.extensions.impl.SparseCheckoutPath;


public class CachingStrategyImpl extends CachingStrategy {

	@NonNull //this can be null::create a custom workspace location
    private String url;
	private static HashMap<String,ReentrantLock> agentCacheLocks = new HashMap<>();
	@DataBoundConstructor
	public CachingStrategyImpl(String url) {
	        this.url = url;
	    }
	public void setUrl(String url) {
        this.url = url;
    }
	public String getUrl() {
		return url;
	}
	public void cacheImpl(GitSCM scm, Run<?, ?> build, GitClient gitClient, TaskListener listener) {
		List<RemoteConfig> repos = null;
		try {
		repos = scm.getParamExpandedRepos(build, listener);
		if (repos == null || repos.isEmpty())    return;
		RemoteConfig rc = repos.get(0);
		String cacheEntry = createCacheEntry(rc.getURIs());
        FilePath cacheDir = new FilePath(new File(url));
		if(!cacheDir.isDirectory()) {
			cacheDir.mkdirs();
		}
		Lock cacheLock = getCacheLock(cacheEntry);
		cacheLock.lock();
		try {
			EnvVars env =build.getEnvironment(listener);
			GitClient git = scm.createClient(listener, env, build, cacheDir);
			if(git.hasGitRepo()) {
				  if (repos.size() == 1)
		               listener.getLogger().println("Fetching changes from the cache Git repository");
		            else
		            listener.getLogger().println("Fetching changes from {0} cache Git repositories " +repos.size());
			}
			listener.getLogger().println("Cloning the remote Git repository in cache dir");

            try {
                CloneCommand cmd = git.clone_().url(rc.getURIs().get(0).toPrivateString()).repositoryName(rc.getName());
                for (GitSCMExtension ext : scm.getExtensions()) {
                	if(!ext.getClass().equals(SparseCheckoutPath.class))
                		ext.decorateCloneCommand(scm, build, git, listener, cmd);
                }
                cmd.execute();
            } catch (GitException ex) {
                ex.printStackTrace(listener.error("Error cloning remote repo '" + rc.getName() + "'"));
                throw new AbortException("Error cloning remote repo '" + rc.getName() + "'");
            }
        }catch (Exception e) {
		}
		}catch(IOException | InterruptedException e) {
			//continue for now
		}
		//create cache entry from repo url;
		//retrieve the lock
		//get the cache dir from the hash 
		//create git folder or git object at the given dir
		//check if it is a git repo or create one
		//get the origin repo name
		//compute the difference with remote repo name
		//use git CLI getHeadRev
		//
	}

	private String createCacheEntry(List<URIish> urIs) {
		String remotename = "";
		if(urIs!=null && urIs.size()>0) {
			remotename = urIs.get(0).toPrivateString();
		}
		return "git-" + Util.getDigestOf(remotename);
	}
	
	private Lock getCacheLock(String cacheEntry) {
		  Lock cacheLock;
	        while (null == (cacheLock = agentCacheLocks.get(cacheEntry))) {
	        	agentCacheLocks.putIfAbsent(cacheEntry, new ReentrantLock());
	        }
	        return cacheLock;
	}

	@Extension
	public static class DescriptorImpl extends GitSCMExtensionDescriptor {
		
		@NonNull
		public String getDisplayName() {
			return "Caching Strategy";
		}
	    }

}
