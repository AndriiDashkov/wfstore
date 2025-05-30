package WsMain;

/**
 * @author Andrii Dashkov license GNU GPL v3
 */

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WsTokenizer {
	
	static Pattern end = Pattern.compile("\\G\\z");	
	
	static Pattern word = Pattern.compile("\\G\\w+");	
	
	static Pattern wordRus = Pattern.compile("\\G[\\u0410-\\u044F]+", Pattern.UNICODE_CASE|
																		Pattern.UNICODE_CASE);	
	static Pattern punct = Pattern.compile("\\G[,.-]+");			
	
	static Pattern space = Pattern.compile("\\G\\s");		
	
	static Pattern number = Pattern.compile("\\G\\d+\\.?\\d*");								
	
	static Pattern forb = Pattern.compile(".*[~!@\\[\\]#$^&*:\\;={}|<>\\/,'?\"]+.*");	
	
	public WsTokenizer () {}
	

	static String getTextToken (Matcher mat) {

		mat.usePattern(space).find();
	
		if (mat.usePattern(forb).find()) return mat.group();

		
		return null;					
	}
	

	public static boolean isValidate (String str) {
		
		
		return true; //seems no need this validation for sqlite
		// Matcher
		//Matcher mat = forb.matcher(str);
		
		//return !mat.matches();
	
	}


}
