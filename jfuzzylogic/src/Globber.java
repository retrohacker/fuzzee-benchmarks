import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.nio.file.Path;

public class Globber implements FilenameFilter {
		
	PathMatcher glob;
	public Globber(String pattern) {
		this.glob = FileSystems.getDefault().getPathMatcher("glob:"+pattern);
	}
	
	@Override
	public boolean accept(File dir, String file) {
		Path p = dir.toPath().resolve(file).getFileName();
		return glob.matches(p);
	}
	
}
