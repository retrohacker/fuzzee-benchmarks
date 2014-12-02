import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.jFuzzyLogic.FIS;


public class Init {
	public static void main(String[] args) {
		File validation = new File("tests/validation");
		File stress = new File("tests/stress");
		//Only match .fcl files
		Globber glob = new Globber("*.fcl");
		File[] validationFCL = validation.listFiles(glob);
		File[] validationTXT = new File[validationFCL.length];
		File[] stressFCL = stress.listFiles(glob);
		for(int i = 0; i < validationFCL.length; i++) {
			Path path = validationFCL[i].toPath();
			String string = path.getFileName().toString();
			String name = string.substring(0,string.length()-3)+"txt";
			validationTXT[i] = path.getParent().resolve(name).toFile();
		}
		
		for(int i = 0; i < validationFCL.length; i++) {
			validation(validationFCL[i],validationTXT[i]);
		}
		for(int i = 0; i < stressFCL.length; i++) {
			stress(stressFCL[i]);
		}
	}
	
	public static void validation(File fcl, File txt) {
		try {
			List<String> txtContents = Files.readAllLines(txt.toPath(),Charset.forName("utf-8"));
			Iterator<String> lines = txtContents.iterator();
			String[] Headers = lines.next().split("\t");
			FIS fis = FIS.load(fcl.toString());
			if(fis == null) {
				System.out.println("Could not open "+fcl);
				return;
			}
			Integer[] vars = new Integer[Headers.length];
			int varIndex = 0;
			int fuzzyIndex = 0;
			Integer[] fuzzy = new Integer[Headers.length];
			//Get variable identifiers
			for(int i = 0; i < Headers.length; i++) {
				if(fis.getFunctionBlock(null).getVariables().containsKey(Headers[i])) {
					vars[varIndex++] = i;
					continue;
				}
				fuzzy[fuzzyIndex++] = i;
			}
			//Iterate through tests
			while(lines.hasNext()) {
				String[] vals = lines.next().split("\t");
				//Set vars
				for(int i = 0; i < varIndex; i++) {
					fis.setVariable(Headers[vars[i]], Double.parseDouble(vals[vars[i]])/100);
				}
				fis.evaluate();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void stress(File fcl) {
		FIS fis = FIS.load(fcl.toString(),true);
		if(fis == null) {
			System.out.println("Could not open "+fcl);
			return;
		}
		try{fis.evaluate();}catch(Exception e){}
	}
}