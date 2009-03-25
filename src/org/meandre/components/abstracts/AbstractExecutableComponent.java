/**
 *
 */
package org.meandre.components.abstracts;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.meandre.annotations.ComponentProperty;
import org.meandre.components.ComponentConsoleHandler;
import org.meandre.components.ComponentInputCache;
import org.meandre.components.PackedDataComponents;
import org.meandre.core.ComponentContext;
import org.meandre.core.ComponentContextException;
import org.meandre.core.ComponentContextProperties;
import org.meandre.core.ComponentExecutionException;
import org.meandre.core.ExecutableComponent;

/**
 * @author bernie acs
 *
 */
public abstract class AbstractExecutableComponent implements ExecutableComponent {

	@ComponentProperty(
	description="Controls ConsoleOutput during runtime; values maybe (off, on, verbose)" ,
	name="ConsoleOutput",
    defaultValue="off"
    )
	public static final String ConsoleOutput = "ConsoleOutput";

	protected ComponentConsoleHandler componentConsoleHandler = null;
	//
	// Should depreciate next 5 variables in favor of using the above object
	protected java.io.PrintStream stdoutSaved = null;
	protected Logger logger = null;
	private boolean outputToConsole = true;
	private boolean outputToConsoleVerbose = false;
	private String outputToConsoleLogLevel = "Info";
	//
	private ComponentContext ccHandle = null;
	private Set<String> componentInputConnected = new HashSet<String>();
	private Set<String> componentOutputConnected = new HashSet<String>();

	protected ComponentInputCache componentInputCache = new ComponentInputCache();
	protected PackedDataComponents packedDataComponentsInput = null;
	protected PackedDataComponents packedDataComponentsOutput = null;

	/**
	 * Enables runtime interogation to determine if a ComponentInput is connected in a flow.
	 * @param componentInputName
	 * @return
	 */
	public boolean isComponentInputConnected(String componentInputName){
		return componentInputConnected.contains(componentInputName);
	}

	/**
	 * Enables runtime interogation to determine if a ComponentOutput is connected in a flow.
	 * @param componentOutputName
	 * @return
	 */
	public boolean isComponentOutputConnected(String componentOutputName){
		return componentOutputConnected.contains(componentOutputName);
	}

	/**
	 *
	 * @param e
	 * @param message
	 */
	public void writeLogAndPrintStackTrace(Exception e,String message){
		componentConsoleHandler.writeLogAndPrintStackTrace(e,message);
	}

	/* (non-Javadoc)
	 * @see org.meandre.core.ExecutableComponent#dispose(org.meandre.core.ComponentContextProperties)
	 */
	public void dispose(ComponentContextProperties ccp)
			throws ComponentExecutionException, ComponentContextException {
		logger.info("Disposing " + this.getClass().getName());
		if(outputToConsoleVerbose)
			getConsoleOut().println("Disposing " + this.getClass().getName());

		try {
			disposeCallBack(ccp);
		} catch (Exception e) {
			String message = this.getClass().getName() +
				" encounter Exception during disposeCallBack()" ;
			this.writeLogAndPrintStackTrace(e, message);
			throw new ComponentContextException( e );
		}
	}

	/* (non-Javadoc)
	 * @see org.meandre.core.ExecutableComponent#initialize(org.meandre.core.ComponentContextProperties)
	 */
	public void initialize(ComponentContextProperties ccp)
			throws ComponentExecutionException, ComponentContextException {

		//

		componentConsoleHandler =
			new ComponentConsoleHandler(
					ccp.getOutputConsole(),
					ccp.getProperty(ConsoleOutput),
					ccp.getLogger()
			);
		if(componentConsoleHandler == null){
			logger = ccp.getLogger();
			logger.warning("Initializing " +
					this.getClass().getName()+
					"Failed to initialize ComponentConsoleOutputHandler" );
			logger = ccp.getLogger();
			//
			// Maybe this should terminate on failure.

		} else {
			//
			logger = componentConsoleHandler.getLogger();
			logger.info("Initializing " + this.getClass().getName() );
			//
			// Backward compatibility
			stdoutSaved = componentConsoleHandler.getConsoleOut();
			outputToConsole = componentConsoleHandler.isOutputToConsole();
			outputToConsoleVerbose = componentConsoleHandler.isOutputToConsoleVerbose();
			outputToConsoleLogLevel = componentConsoleHandler.getOutputToConsoleLevel();
		}

		//
		String s[] = ccp.getInputNames();
		for(String p : s)
			componentInputConnected.add(p);
		//
		s = ccp.getOutputNames();
		for(String p : s)
			componentOutputConnected.add(p);
		//
		componentInputCache.setConsoleOut(getConsoleOut());
		componentInputCache.setOutputToConsole(outputToConsole);
		componentInputCache.setOutputToConsoleVerbose(outputToConsoleVerbose);
		//
		try{
			initializeCallBack(ccp);
		}catch (Exception e){
			String message = this.getClass().getName() +
						" encounter Exception during initializeCallBack()" ;
			this.writeLogAndPrintStackTrace(e, message);
			throw new ComponentContextException( e );
		}
	}

	/* (non-Javadoc)
	 * @see org.meandre.core.ExecutableComponent#execute(org.meandre.core.ComponentContext)
	 */
	public void execute(ComponentContext cc)
			throws ComponentExecutionException, ComponentContextException {
		//
		logger.info("Execute called for: " + this.getClass().getName() );
		//
		if(outputToConsole)
			getConsoleOut().println("Execute called for: " + this.getClass().getName());
		//
		setCcHandle(cc);
		//
		// Initialize the PackedDataComponent variables each iteration
		packedDataComponentsInput = new PackedDataComponents();
		packedDataComponentsOutput = new PackedDataComponents();
		//
		try{
			executeCallBack(cc);
		}catch (Exception e){
			String message = this.getClass().getName() + " encounter Exception during executeCallBack()" ;
			this.writeLogAndPrintStackTrace(e, message);
			throw new ComponentContextException( e );
		}
	}

	public abstract void disposeCallBack(ComponentContextProperties ccp)
	throws Exception;

	public abstract void initializeCallBack(ComponentContextProperties ccp)
			throws Exception;

	public abstract void executeCallBack(ComponentContext cc)
			throws Exception;

	public ComponentContext getCcHandle() {
		return ccHandle;
	}

	public void setCcHandle(ComponentContext ccHandle) {
		this.ccHandle = ccHandle;
	}
	public java.io.PrintStream getConsoleOut() {
		return this.stdoutSaved;
	}

	public void setConsoleOut(java.io.PrintStream stdoutSaved) {
		this.stdoutSaved = stdoutSaved;
	}

	public Logger getLogger() {
		return this.logger;
	}

	public void setLogger(Logger inlogger) {
		this.logger = inlogger;
	}

	public boolean isOutputToConsole() {
		return this.outputToConsole;
	}

	public void setOutputToConsole(boolean inOutputToConsole) {
		this.outputToConsole = inOutputToConsole;
	}

	public boolean isOutputToConsoleVerbose() {
		return outputToConsoleVerbose;
	}

	/**
	 * @param outputToConsoleLogLevel the outputToConsoleLogLevel to set
	 */
	public void setOutputToConsoleLogLevel(
			String outputToConsoleLogLevel) {
		this.outputToConsoleLogLevel = outputToConsoleLogLevel;
	}

	/**
	 * @return the outputToConsoleLogLevel
	 */
	public String getOutputToConsoleLogLevel() {
		return this.outputToConsoleLogLevel;
	}


}
