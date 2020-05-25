package jenkins.git.cache;

import java.util.List;

import org.jenkinsci.plugins.gitclient.GitClient;
import org.kohsuke.stapler.DataBoundConstructor;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.plugins.git.GitSCM;
import hudson.plugins.git.extensions.CachingStrategy;
import hudson.plugins.git.extensions.CachingStrategyDescriptor;


public class CacheImpl extends CachingStrategy {

	@NonNull
    private final String url;
	
	@DataBoundConstructor
	public CacheImpl(String url) {
	        this.url = url;
	    }
	public void cacheImpl(GitSCM scm, Run<?, ?> build, GitClient git, TaskListener listener) {
		
	}
	
	@Extension
	public static class DescriptorImpl extends CachingStrategyDescriptor {
		
		@NonNull
		public String getDisplayName() {
			return "Caching Strategy";
		}
	    }

}
