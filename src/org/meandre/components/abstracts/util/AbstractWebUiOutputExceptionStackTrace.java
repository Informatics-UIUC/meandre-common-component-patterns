/**
 * 
 */
package org.meandre.components.abstracts.util;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Logger;

/**
 * @author bernie acs
 *
 */
public abstract class AbstractWebUiOutputExceptionStackTrace {
	
	private static PrintStream consolePrintStream = null;
	private static Logger logger = null;
	private static boolean outputToConsole = false;
	
	/**
	 * Show Stack trace on console, in the log, and try to send it to the client as Response
	 * 
	 * @param response
	 * @param e
	 * @param message
	 */
	public void htmlOutputStackTrace(
			javax.servlet.http.HttpServletResponse response, 
			Exception e, 
			String message
	){
		if(isOutputToConsole()){
			getConsolePrintStream().println( message );
			e.printStackTrace(getConsolePrintStream());
		}
		//
		response.setContentType("html/text");
		//
		try {
			//
			e.printStackTrace( response.getWriter() );
			//
		} catch (IOException e1) {
			//
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter( sw );
			//
			if(isOutputToConsole())
				e1.printStackTrace(getConsolePrintStream());
			//
			e1.printStackTrace( pw );
			getLogger().severe( sw.getBuffer().toString() ); 
		}
	}

	/**
	 * @return the consolePrintStream
	 */
	public static PrintStream getConsolePrintStream() {
		return consolePrintStream;
	}

	/**
	 * @param inConsolePrintStream the consolePrintStream to set
	 */
	public void setConsolePrintStream(PrintStream inConsolePrintStream) {
		consolePrintStream = inConsolePrintStream;
	}

	/**
	 * @return the logger
	 */
	public static Logger getLogger() {
		return logger;
	}

	/**
	 * @param inLogger the logger to set
	 */
	public void setLogger(Logger inLogger) {
		logger = inLogger;
	}

	/**
	 * @return the outputToConsole
	 */
	public static boolean isOutputToConsole() {
		return outputToConsole;
	}

	/**
	 * @param inOutputToConsole the outputToConsole to set
	 */
	public void setOutputToConsole(boolean inOutputToConsole) {
		outputToConsole = inOutputToConsole;
	}
}
