/**
 * 
 */
package org.meandre.components.abstracts.util;

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
	private boolean outputToConsole = true;
	private boolean outputToConsoleVerbose = false;
	private String outputToConsoleLevel = "Info";
	
	/**
	 * This method contains sets all the relative variable references that should be 
	 * used during the lifetime of this object. It is expected that implementations would
	 * create or have some means of using this method in constructor or similar timing.
	 * 
	 * @param console
	 * @param logLevel
	 * @param logger
	 */
	public void initialize(java.io.PrintStream console, String logLevel, Logger logger){
		this.printStreamHandle = console;
		this.logger = logger;
		outputToConsoleLevel = ( logLevel != null)? logLevel: outputToConsoleLevel;
		if(outputToConsoleLevel.equalsIgnoreCase("Info")){
			outputToConsoleVerbose = false;
			outputToConsole = true;
		} else if (outputToConsoleLevel.equalsIgnoreCase("Debug")){
			outputToConsoleVerbose = true;
			outputToConsole = true;			
		} else if (outputToConsoleLevel.equalsIgnoreCase("Off")){
			outputToConsoleVerbose = false;
			outputToConsole = false;			
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
	/**
	 * 
	 * @return
	 */
	public java.io.PrintStream getConsoleOut() {
		return printStreamHandle;
	}

	/**
	 * 
	 * @param stdoutSaved
	 */
	public void setConsoleOut(java.io.PrintStream stdoutSaved) {
		this.printStreamHandle = stdoutSaved;
	}

	/**
	 * 
	 * @return
	 */
	public Logger getLogger() {
		return this.logger;
	}

	/**
	 * 
	 * @param inlogger
	 */
	public void setLogger(Logger inlogger) {
		this.logger = inlogger;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isOutputToConsole() {
		return this.outputToConsole;
	}

	/**
	 * 
	 * @param inOutputToConsole
	 */
	public void setOutputToConsole(boolean inOutputToConsole) {
		this.outputToConsole = inOutputToConsole;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isOutputToConsoleVerbose() {
		return this.outputToConsoleVerbose;
	}

	/**
	 * @param inOutputToConsoleVerbose
	 */
	public void setOutputToConsoleVerbose(boolean inOutputToConsoleVerbose) {
		this.outputToConsoleVerbose = inOutputToConsoleVerbose;
	}
	
	/**
	 * @return the outputToConsoleLevel
	 */
	public String getOutputToConsoleLevel() {
		return this.outputToConsoleLevel;
	}
	
	/**
	 * @param outputToConsoleLevel the outputToConsoleLevel to set
	 */
	public void setOutputToConsoleLevel(String outputToConsoleLogLevel) {
		this.outputToConsoleLevel = outputToConsoleLogLevel;
	}
	
}
