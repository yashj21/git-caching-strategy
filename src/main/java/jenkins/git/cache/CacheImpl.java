package jenkins.git.cache;

import org.kohsuke.stapler.DataBoundConstructor;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.plugins.git.extensions.CachingStrategy;
import hudson.plugins.git.extensions.CachingStrategyDescriptor;


public class CacheImpl extends CachingStrategy {

	@NonNull
    private final String url;
	
	@DataBoundConstructor
	public CacheImpl(String url) {
	        this.url = url;
	    }
	public boolean cacheImpl() {
		return false;
	}
	
	@Extension
	public static class DescriptorImpl extends CachingStrategyDescriptor {
		
		@NonNull
		public String getDisplayName() {
			return "Caching Strategy";
		}
	    }

}
