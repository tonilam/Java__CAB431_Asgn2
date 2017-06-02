/**
 * 
 */
package lib.files;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

/**
 * @author Toni Lam
 *
 * @since
 * @version ?, Jun 2, 2017
 */
public class Log {
	
	static File outfile;
	static StringBuffer sb;
	static boolean verbose;

	public Log() {
		outfile = null;
		sb = null;
		verbose = false;
	}

	public static void create(String resourceAddress) {
		outfile =  new File(resourceAddress);
		sb = new StringBuffer();
	}
	
	public static void verbose(boolean switcher) {
		verbose = switcher;
	}

	public static void out(char line[]) {
		sb.append(line);
		sb.append("\n");
		if (verbose) {
			System.out.println(line);
		}
	}
		
	/**out 
	 * @since 2.1
	 *
	 * @param text
	 * @param args 
	 */
	public static void out(String text, Object... args) {
		if (args.length > 0) {
			out(String.format(text, args).toCharArray());
		} else {
			out(text.toCharArray());
		}
	}
	
	
	public static void linebreak() {
		sb.append("\n");
		if (verbose) {
			System.out.println();
		}
	}
	
	public static void write() {
		PrintStream out = null;
		try {
	    	out = new PrintStream(new FileOutputStream(outfile));

    		out.print(sb);
        } catch ( IOException e ) {
            e.printStackTrace();
        } finally {
        	if ( out != null ) {
        		out.close();
        	}
        }
	}

	public static void echo(String text, Object... args) {
		System.out.println(String.format(text, args));
	}

}
