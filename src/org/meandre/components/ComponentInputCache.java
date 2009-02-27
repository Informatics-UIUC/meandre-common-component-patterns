/**
 * 
 */
package org.meandre.components;

import org.meandre.components.abstracts.util.AbstractComponentInputCache;

/**
 * @author bernie acs
 *
 * 
 */
public class ComponentInputCache extends AbstractComponentInputCache {

	/**
	 * 
	 */
	public ComponentInputCache() {
	}
	
	public void setConsoleOut(java.io.PrintStream consoleOut){
		super.setConsoleOut(consoleOut);
	}
	
	public void setOutputToConsole(boolean outputToConsole){
		super.setOutputToConsole(outputToConsole);
	}

	public void setOutputToConsoleVerbose(boolean outputToConsoleVerbose){
		super.setOutputToConsoleVerbose(outputToConsoleVerbose);
	}

}
