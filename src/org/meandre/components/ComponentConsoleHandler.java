/**
 * 
 */
package org.meandre.components;

import java.io.PrintStream;
import java.util.logging.Logger;

import org.meandre.components.util.AbstractComponentConsoleOutputHandler;

/**
 * @author bernie acs
 *
 */
public class ComponentConsoleHandler extends
		AbstractComponentConsoleOutputHandler {

	/**
	 * Uses Method from super to implement abstract intialize() call in constructor.
	 * 
	 * @param console
	 * @param level
	 * @param logger
	 */
	public ComponentConsoleHandler(PrintStream console,String level, Logger logger ) {
		this.initialize(console, level, logger);
	}

	/**
	 * 
	 * @param logLevel
	 * @param message
	 */
	public void printlnOutput( String logLevel, String message){
		if(this.isOutputToConsole()){
			if(getOutputToConsoleLevel().equalsIgnoreCase(logLevel)){
				getConsoleOut().println(message);
			}
		} else {
			// ?? print an error when it is not okay to print ??
		}
	}
	
	/**
	 * 
	 * @param logLevel
	 * @param message
	 */
	public void printOutput( String logLevel, String message){
		if(this.isOutputToConsole()){
			if(getOutputToConsoleLevel().equalsIgnoreCase(logLevel)){
				getConsoleOut().print(message);
			}
		} else {
			// ?? print an error when it is not okay to print ??
		}
	}
	
}
