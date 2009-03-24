/**
 * 
 */
package org.meandre.components.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Logger;

/**
 * @author bernie acs
 * 
 * This class should provide all references for handling ConsoleOutput from a central core location.
 * 
 * TODO: Add LogLevel validation, consider investigating Logger(toConsole) to more tighly consolidate
 * with possible methods to allow for both console and/or log to be combined or used seperately.
 * In this revision this class is concerned with centralizing outputMethods and variables from 
 * the AbstractExecutableComponent.
 * 
 *
 */
public abstract class AbstractComponentConsoleOutputHandler {
	protected java.io.PrintStream printStreamHandle = null;
	protected Logger logger = null;	
	private static boolean outputToConsole = true;
	private static boolean outputToConsoleVerbose = false;
	private static String outputToConsoleLevel = "Info";
	
	public void initialize(java.io.PrintStream console, String logLevel, Logger logger){
		this.printStreamHandle = console;
		this.logger = logger;
		outputToConsoleLevel = ( logLevel != null)? logLevel: outputToConsoleLevel;
		if(logLevel.equalsIgnoreCase("Info")){
			outputToConsoleVerbose = false;
			outputToConsole = true;
		} else if (logLevel.equalsIgnoreCase("Debug")){
			outputToConsoleVerbose = true;
			outputToConsole = true;			
		}
	}
	/**
	 * 
	 * @param e
	 * @param message
	 */
	public void writeLogAndPrintStackTrace(Exception e,String message){
		//
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter( sw );
		//
		if(isOutputToConsole()){
			getConsoleOut().println(this.getClass().getName()+"\n"+message);
			e.printStackTrace(getConsoleOut());
		}
		//
		sw.getBuffer().append(this.getClass().getName()+"\n"+message);
		e.printStackTrace( pw );
		getLogger().severe( sw.getBuffer().toString() ); 
		//
	}
	public java.io.PrintStream getConsoleOut() {
		return printStreamHandle;
	}

	public void setConsoleOut(java.io.PrintStream stdoutSaved) {
		this.printStreamHandle = stdoutSaved;
	}

	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger inlogger) {
		logger = inlogger;
	}

	public boolean isOutputToConsole() {
		return outputToConsole;
	}

	public void setOutputToConsole(boolean inOutputToConsole) {
		outputToConsole = inOutputToConsole;
	}

	public boolean isOutputToConsoleVerbose() {
		return outputToConsoleVerbose;
	}

	/**
	 * @param inOutputToConsoleVerbose
	 */
	public void setOutputToConsoleVerbose(boolean inOutputToConsoleVerbose) {
		outputToConsoleVerbose = inOutputToConsoleVerbose;
	}
	
	/**
	 * @return the outputToConsoleLevel
	 */
	public static String getOutputToConsoleLevel() {
		return outputToConsoleLevel;
	}
	
	/**
	 * @param outputToConsoleLevel the outputToConsoleLevel to set
	 */
	public static void setOutputToConsoleLevel(String outputToConsoleLogLevel) {
		outputToConsoleLevel = outputToConsoleLogLevel;
	}
	
}
