/**
 * 
 */
package org.meandre.components.abstracts;

import java.util.concurrent.Semaphore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.meandre.annotations.ComponentOutput;
import org.meandre.annotations.ComponentProperty;
import org.meandre.components.abstracts.util.EmptyHttpServletRequest;
import org.meandre.core.ComponentContext;
import org.meandre.core.ComponentContextException;
import org.meandre.core.ComponentExecutionException;
import org.meandre.webui.WebUIException;
import org.meandre.webui.WebUIFragmentCallback;

/**
 * @author bernie acs
 * 
 * <p>
 * General purpose WebServiceComponent that is designed to forward 
 * ExecutionInstanceId, HttpServletRequest, HttpServletResponses, 
 * and a Semaphore Object. The assumed tasks that should happen with
 * the FLOW that this Component is heading up should consist of an 
 * optional (Http) Session handler, optional HttpServletRequest handler 
 * (or parser) followed by some set of logic that will formulate the 
 * HttpServletResponse (that returns something to the web-client).   
 * </p><p>
 * This Component Provides outputs in two Forms; first is the 
 * PackedDataComponents Object which contains all of the individual 
 * outputs that are pushed to the outputs of this component. This 
 * PackedDataComponents Object is a java.util.HashMap<String,Object> 
 * where the String is the name of the ComponentOutput and the Object
 * is the DataComponent payload. The net-effect of this construct is 
 * that two references are pushed for data output produced by this 
 * Component.  
 * </p><p>
 * The critical mechanical obligation of the above set of mechanics is
 * send something back to the web-client and to execute the release() 
 * method of the Semaphore Object output from this Component. Two general
 * purpose abstract objects are available to act as the tail of a WebService
 * Flow; 
 * <p>
 * 1). AbstractWebServiceTailPrintWriterComponent;<br> 
 * 2). AbstractWebServiceTailOutputStreamComponent;
 * </p><p>
 * A basic general purpose AbstractWebServiceSessionComponent is also 
 * available.
 * <p>
 * GeneralPurpose Flow Example;
 * </p><p>
 * (BEGINS FLOW) extended AbstractWebServiceHeadComponent-><br> 
 * (Optional) extended AbstractWebServiceSessionComponent-><br>
 * (Implimentation Defined) WebServiceRequestHandler-><br>
 * (Implimentation Defined) WebServiceWork-><br>
 * extended AbstractWebServiceTail[PrintWriter|OutputStream]Component (ENDS FLOW)<br>
 * </p>
 */
public abstract class AbstractWebServiceHeadComponent extends AbstractExecutableComponent implements
		WebUIFragmentCallback {

	////////////////////////// ComponentProperties ///////////////////////////
	
	/**
	 * "Controls the number of milliseconds of sleep between evaluations of ComponentContext.isFlowAborting(), a value of 1000 is approximately 1 second " 
	 */
	@ComponentProperty(
	description="Controls the number of milliseconds of sleep between evaluations of ComponentContext.isFlowAborting(), a value of 1000 is approximately 1 second " , 
	name="isFlowAbortingSleep",
    defaultValue="1000"
    )
	public static final String isFlowAbortingSleep = "isFlowAbortingSleep";

	////////////////////////// ComponentOutput ///////////////////////////
	
	/**
	 * "HttpServletRequest object is blindly forwarded, assumes that parsing will happen elsewhere " 
	 */
	@ComponentOutput(
	description="HttpServletRequest object is blindly forwarded, assumes that parsing will happen elsewhere " , 
	name="HttpServletRequest"
    )
	public static final String httpServletRequest = "HttpServletRequest";
	
	/**
	 * "HttpServletResponse object is blindly forwarded, assumes that client response will be written elsewhere " 
	 */
	@ComponentOutput(
	description="HttpServletResponse object is blindly forwarded, assumes that client response will be written elsewhere " , 
	name="HttpServletResponse"
    )
	public static final String httpServletResponse = "HttpServletResponse";

	/**
	 * "Semaphore object is blindly forwarded; normally would go to httpServletResponse writter to be released when client message is completed"
	 */
	@ComponentOutput(
	description="Semaphore object is blindly forwarded; normally would go to httpServletResponse writter to be released when client message is completed" , 
	name="Semaphore"
    )
	public static final String semaphore = "Semaphore";	

	/**
	 * "ExecutionInstanceId String object is blindly forwarded; normally would be used as a reference to generated URL that would resolve to this component"
	 */
	@ComponentOutput(
	description="ExecutionInstanceId String object is blindly forwarded; normally would be used as a reference to generated URL that would resolve to this component" , 
	name="ExecutionInstanceId"
    )
	public static final String executionInstanceId = "ExecutionInstanceId";	

	/**
	 * ComponentOutput(
	description="PackedOutputCollection combines all output ports into a java.util.Map<String,Object> to simplify flow building" , 
	name="PackedDataComponent"
    )
	 */
	@ComponentOutput(
	description="PackedOutputCollection combines all output ports into a java.util.Map<String,Object> to simplify flow building" , 
	name="PackedDataComponent"
    )
	public static final String OutPackedDataComponent = "PackedDataComponent";	
		
	/* (non-Javadoc)
	 * @see org.meandre.core.ExecutableComponent#execute(org.meandre.core.ComponentContext)
	 */
	/**
	 * General purpose execute method which will be executed only once and enters a 
	 * forever loop that tests the ComponentContext.isFlowAborting(), if this should
	 * become true the logical service will be Shutdown immedately.  The ComponentProperty
	 * 
	 */
	public void execute(ComponentContext cc)
			throws ComponentExecutionException, ComponentContextException {
		super.execute(cc);
		//
		if(isOutputToConsole())
			getConsoleOut().println("Execute called for: " + this.getClass().getName());
		//
        try {
        	setCcHandle(cc);
            cc.startWebUIFragment(this);
            if(isOutputToConsole())
            	getConsoleOut().println("Starting " + this.getClass().getName()+ " at " + cc.getFlowID());
            long sleepTime = Long.parseLong(cc.getProperty("isFlowAbortingSleep"));
            while (!cc.isFlowAborting()) {
                Thread.sleep(sleepTime);
            }
            if(isOutputToConsole())
            	getConsoleOut().println("Aborting " + this.getClass().getName()+ " at " + cc.getFlowID()+ " Requested");
            cc.stopWebUIFragment(this);
       } catch (Exception e) {
            throw new ComponentExecutionException(e);
       }
       //
	}

	/* (non-Javadoc)
	 * @see org.meandre.webui.WebUIFragmentCallback#emptyRequest(javax.servlet.http.HttpServletResponse)
	 */
	/**
	 * empty HttpServletRequest may be recieved at anytime and special prevision has been 
	 * made to provide a default behavior in that event. For the General purpose case this
	 * Component class creates a (java.Lang.Object)null which cast as HttpServletRequest which 
	 * is then sent with the HttpServletResponse to the this.handle() method to processing. 
	 * Implicitly this means that the Component designated to recieve the HttpServletRequest
	 * should be prepared to handle an input event the is an emptyRequest ( or null )
	 */
	public void emptyRequest(HttpServletResponse response) throws WebUIException {
		if(isOutputToConsole())
			getConsoleOut().println("EmptyRequest called for: " + this.getClass().getName());
		    HttpServletRequest request = (HttpServletRequest) new EmptyHttpServletRequest();
			handle( request, response);
	}

	/* (non-Javadoc)
	 * @see org.meandre.webui.WebUIFragmentCallback#handle(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	/**
	 * handle all HttpServletRequest (including a potentially emptyRequest), for the General
	 * Purpose case this method is blindly passing both the HttpServletRequest an the HttpServletResponse
	 * Object out for processing, in addition a Semaphore is used to hold this method busy (holding 
	 * open the network connection to the client) which should be sent to the Component that will be 
	 * writting a response for this request, this lock does not prevent this method from receiving new 
	 * request (which would come on a different logical thread controlled by the Infrastructure's 
	 * embedded ApplicationServer). The Writter should execute the Semaphore.release() when it has completed
	 * the task of making the response which inturn will allow this component (thread) to continue closing
	 * the logical network connection with the originating client.  
	 */
	public void handle(HttpServletRequest request, HttpServletResponse response)
			throws WebUIException {
		//
		if(isOutputToConsole())
			getConsoleOut().println("Handle called for: " + this.getClass().getName() );
        try {
            Semaphore sem = new Semaphore(1, true);
            sem.acquire();
            //
            // Build a new PackedDataComponents Object to populate and Push Out.
            packedDataComponentsOutput.put(httpServletRequest,  request);
            packedDataComponentsOutput.put(httpServletResponse, response);
            packedDataComponentsOutput.put(semaphore, sem);
            packedDataComponentsOutput.put(executionInstanceId, getCcHandle().getExecutionInstanceID());
            //
            getCcHandle().pushDataComponentToOutput(OutPackedDataComponent,  packedDataComponentsOutput);
            //
            getCcHandle().pushDataComponentToOutput(httpServletRequest,  request);
            getCcHandle().pushDataComponentToOutput(httpServletResponse, response);
            getCcHandle().pushDataComponentToOutput(semaphore, sem);
            getCcHandle().pushDataComponentToOutput(executionInstanceId, getCcHandle().getExecutionInstanceID());
            //
            sem.acquire();
            sem.release();
       } catch (InterruptedException e) {
            throw new WebUIException(e);
       } catch (ComponentContextException e) {
            throw new WebUIException(e);
       } 
	}
}
