package jenkins.git.cache;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.jgit.transport.RemoteConfig;
import org.jenkinsci.plugins.gitclient.GitClient;
import org.kohsuke.stapler.DataBoundConstructor;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.plugins.git.GitSCM;
import hudson.plugins.git.extensions.GitSCMExtensionDescriptor;
import hudson.plugins.git.extensions.impl.CachingStrategy;


public class CachingStrategyImpl extends CachingStrategy {

	@NonNull
    private String url;
	
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
	public void cacheImpl(GitSCM scm, Run<?, ?> build, GitClient git, TaskListener listener) {
		List<RemoteConfig> repos = null;
		try {
		repos = scm.getParamExpandedRepos(build, listener);
		}catch(IOException | InterruptedException e) {
			//continue for now
		}
		if (repos == null || repos.isEmpty())    return;
		
        File cacheDir = new File(url);
		if(!cacheDir.isDirectory()) {
			cacheDir.mkdir();
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

	@Extension
	public static class DescriptorImpl extends GitSCMExtensionDescriptor {
		
		@NonNull
		public String getDisplayName() {
			return "Caching Strategy";
		}
	    }

}
