/**
 * 
 */
package org.meandre.components.abstracts.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author bernie acs
 * 
 * This class should provide all references for handling ConsoleOutput from a central core location.
 * 
 * todo: Add LogLevel validation, consider investigating Logger(toConsole) to more tighly consolidate
 * with possible methods to allow for both console and/or log to be combined or used seperately.
 * In this revision this class is concerned with centralizing outputMethods and variables from 
 * the AbstractExecutableComponent.
 * 
 * DONE: Added some methods with prefix "when" these methods attempt to provide;
 * 1. common means for producing output to Console and Logging System using common conventions
 * 2. enables access to Console and Logging individually or collectively
 * 3. will provide a potential mechanism centralized output handling. 
 * 
 */
public abstract class AbstractComponentConsoleOutputHandler {
	
	protected java.io.PrintStream printStreamHandle = null;
	protected Logger logger = null;	
	private boolean outputToConsoleMirrorToLog = false;
	private boolean outputToConsole = true;
	private boolean outputToConsoleVerbose = false;
	private boolean outputToConsoleAndLog = false;
	private String outputToConsoleLevel = "OFF";
	private int outputToConsoleLevelInteger = Integer.MAX_VALUE;
	
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
		outputToConsoleLevel = ( logLevel == null)? outputToConsoleLevel:logLevel;
		//
		Level logLevelObj = null;
		try{
			logLevelObj = getLogLevelObject(outputToConsoleLevel.toUpperCase());
			outputToConsoleLevel = logLevelObj.getName();
			setOutputToConsoleLevelInteger(logLevelObj.intValue());
			
		} catch(IllegalArgumentException iae){
			//
			// In this case we should force a hard-code reference to "OFF"
			outputToConsoleLevel = "OFF" ;
			setOutputToConsoleLevelInteger(Integer.MAX_VALUE);
			
		}
		//
		// 
		// String loggerLevel = logLevelObj.getName();
		// outputToConsoleLevel = ( loggerLevel != null)? loggerLevel: outputToConsoleLevel;
		//
		//
		if(outputToConsoleLevel.equalsIgnoreCase("Info")){
			outputToConsoleVerbose = false;
			outputToConsole = true;
		} else if (outputToConsoleLevel.equalsIgnoreCase("All")){
			outputToConsoleVerbose = true;
			outputToConsole = true;
		} else if (outputToConsoleLevel.equalsIgnoreCase("Off")){
			outputToConsoleVerbose = false;
			outputToConsole = false;
		} else {
			// All other logging levels cause state == OFF.
			outputToConsoleVerbose = false;
			outputToConsole = false;
		}
		
	}
	
	public Level getLogLevelObject( String s ){
		Level logLevelObj = null;
		String sOutputToConsoleLevel = s;
		//
		if(sOutputToConsoleLevel.equalsIgnoreCase("All")){
			sOutputToConsoleLevel="ALL";
			// Level.ALL.intValue(); Integer.MIN_VALUE
		} else if(sOutputToConsoleLevel.equalsIgnoreCase("Info")){
			sOutputToConsoleLevel="INFO";
		} else if (sOutputToConsoleLevel.equalsIgnoreCase("On")){
			sOutputToConsoleLevel="INFO";
		} else if (sOutputToConsoleLevel.equalsIgnoreCase("Verbose")){
			sOutputToConsoleLevel="ALL";
		} else if (sOutputToConsoleLevel.equalsIgnoreCase("Debug")){
			sOutputToConsoleLevel="FINEST";
		} else if (sOutputToConsoleLevel.equalsIgnoreCase("fine")){
			sOutputToConsoleLevel="FINE";
		} else if (sOutputToConsoleLevel.equalsIgnoreCase("finer")){
			sOutputToConsoleLevel="FINER";
		} else if (sOutputToConsoleLevel.equalsIgnoreCase("severe")){
			sOutputToConsoleLevel="SEVERE";
		} else if (sOutputToConsoleLevel.equalsIgnoreCase("config")){
			sOutputToConsoleLevel="CONFIG";
		} else if (sOutputToConsoleLevel.equalsIgnoreCase("Off")){
			sOutputToConsoleLevel="OFF";
			// Level.OFF.intValue() Integer.MAX_VALUE
		} else {
			sOutputToConsoleLevel="OFF";
		}
		
		try {
			logLevelObj = Level.parse(sOutputToConsoleLevel.toUpperCase());
		} catch(IllegalArgumentException iae){
			logLevelObj = Level.parse("OFF");	
		}
		return logLevelObj;
		
	}
	
	/**
	 * 
	 * @deprecated 
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
	 * Utility method to extract formated output Stacktrace into a String Object
	 * 
	 * @param e
	 * @return
	 */
	private String getExceptionStackTrace(Exception e){
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter( sw );
		//
		sw.getBuffer().append(this.getClass().getName()+"\n");
		e.printStackTrace( pw );
		//
		return sw.getBuffer().toString();
	}

	/**
	 * Compares input logLevel and cause Exception.StackTrace 
	 * to be output to Console Only
	 * 
	 * @param logLevelString
	 * @param e
	 */
	public void whenLogLevelOutputToConsoleOnly(String logLevel,Exception e){
		String str = getExceptionStackTrace(e );
		whenLogLevelOutputToConsole(logLevel, str);
	}

	/**
	 * Compares input logLevel and cause Exception.StackTrace 
	 * to be output to Logger Only
	 * 
	 * @param logLevelString
	 * @param e
	 */
	public void whenLogLevelOutputToLogOnly(String logLevel,Exception e){
		String str = getExceptionStackTrace(e );
		whenLogLevelOutputToLog(logLevel, str);
	}
	
	/**
	 * Compares input logLevel and cause Exception.StackTrace 
	 * to be output to Console & Logger
	 * 
	 * @param logLevelString
	 * @param e
	 */
	public void whenLogLevelOutput(String logLevel,Exception e){
		String str = getExceptionStackTrace(e );
		//
		boolean undo = false;
		if (! isOutputToConsoleMirrorToLog()){
			this.setOutputToConsoleMirrorToLog(true);
			undo=true;
		}
		//
		whenLogLevelOutput(logLevel, str);
		//
		if(undo){
			this.setOutputToConsoleMirrorToLog(false);
		}
		//
	}

	/**
	 * Compares input logLevel and cause input String 
	 * to be output to Console & Logger 
	 * 
	 * @param logLevel
	 * @param message
	 */
	public void whenLogLevelOutput(String logLevel, String message){
		whenLogLevelOutputToConsole(logLevel, message);
		if(outputToConsoleMirrorToLog)
			whenLogLevelOutputToLog(logLevel, message);
	}
	
	/**
	 * Compares input logLevel and cause input String 
	 * to be output to Console Only using println() 
	 * 
	 * @param logLevel
	 * @param message
	 */
	public void whenLogLevelOutputToConsole(String logLevel, String message ){
		int inputLogLevelKey = getLogLevelObject(logLevel).intValue();
		int setupLogLevelKey = getLogLevelObject(outputToConsoleLevel).intValue();
		//
		if(setupLogLevelKey==Integer.MAX_VALUE){
			// do nothing setting is off
		} else if ((setupLogLevelKey==Integer.MIN_VALUE) || ( setupLogLevelKey<=inputLogLevelKey)) {
			// showall
			// anything smaller
			if( (message != null ) && (message != "") ){
				//
				this.getConsoleOut().println(message);
			}
		} else {
			// nothing 
		}

	}

	/**
	 * Create java.util.logging.Level Object using parse String logLevel input
	 * blindly passes Level and message to the Logger for handling. No checks
	 * 
	 * @param logLevel
	 * @param message
	 */
	public void whenLogLevelOutputToLog(String logLevel, String message){
		//
		Level logLevelObj = null;
		try{
			logLevelObj = Level.parse(logLevel.toUpperCase());
		} catch(IllegalArgumentException iae){
			logLevelObj = Level.parse(outputToConsoleLevel);
		}
		this.getLogger().log(logLevelObj, message);
	}
	
	/**
	 * 
	 * @return the value cached in initialize() or last setConsoleOut invocation. 
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
	 * @param logLevelString
	 * @return comparative result of equalsIngnoreCase
	 */
	public boolean isOutputToConsoleLevel(String logLevelString){
		
		return outputToConsoleLevel.equalsIgnoreCase(logLevelString);
	}

	/**
	 * @return the outputToConsoleAndLog
	 */
	public boolean isOutputToConsoleAndLog() {
		return outputToConsoleAndLog;
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
	 * @return
	 */
	public boolean isOutputToConsoleVerbose() {
		return this.outputToConsoleVerbose;
	}

	/**
	 * @param outputToConsoleAndLog the outputToConsoleAndLog to set
	 */
	public void setOutputToConsoleAndLog(boolean outputToConsoleAndLog) {
		this.outputToConsoleAndLog = outputToConsoleAndLog;
	}

	/**
	 * 
	 * @param inOutputToConsole
	 */
	public void setOutputToConsole(boolean inOutputToConsole) {
		this.outputToConsole = inOutputToConsole;
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

	/**
	 * @param outputToConsoleLevelInteger the outputToConsoleLevelInteger to set
	 */
	public void setOutputToConsoleLevelInteger(int outputToConsoleLevelInteger) {
		this.outputToConsoleLevelInteger = outputToConsoleLevelInteger;
	}

	/**
	 * @return the outputToConsoleLevelInteger
	 */
	public int getOutputToConsoleLevelInteger() {
		return outputToConsoleLevelInteger;
	}

	/**
	 * @return the outputToConsoleMirrorToLog
	 */
	public boolean isOutputToConsoleMirrorToLog() {
		return outputToConsoleMirrorToLog;
	}

	/**
	 * @param outputToConsoleMirrorToLog the outputToConsoleMirrorToLog to set
	 */
	public void setOutputToConsoleMirrorToLog(boolean outputToConsoleMirrorToLog) {
		this.outputToConsoleMirrorToLog = outputToConsoleMirrorToLog;
	}
	
}
