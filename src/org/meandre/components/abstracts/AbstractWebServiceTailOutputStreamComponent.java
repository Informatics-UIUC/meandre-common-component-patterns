/**
 * 
 */
package org.meandre.components.abstracts;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.concurrent.Semaphore;
import java.util.logging.Logger;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.meandre.annotations.ComponentInput;
import org.meandre.annotations.ComponentProperty;
import org.meandre.core.ComponentContext;
import org.meandre.core.ComponentContextException;
import org.meandre.core.ComponentExecutionException;



/**
 * @author bernie acs
 * 
 * General purpose WebServiceComponent that is designed to end a Flow that
 * is functioning in the capacity of a presistent running service. The General
 * purpose case assumes that some InputStream Object will be recieved on ComponentInput
 * Port identified as httpServletResponsePayload and will attempt to push the 
 * Data through using HttpServletResponse.getOutputStream() by default. Setting 
 * ComponentProperty.. A provision has been included that if an Data Event arrives
 * that does not result in a InputStream Object then the payload will be pushed 
 * out using the standard PrintWriter method. 
 */

public abstract class AbstractWebServiceTailOutputStreamComponent extends AbstractExecutableComponent {
	
	/**
	 * "Controls ConsoleOutput during runtime; values maybe (off, on, verbose)"
	 */
	@ComponentProperty(
	description="Defaul buffer size in bytes that will be used to pass data from InputStream to OutputStream" , 
	name="DefaultStreamBuffer",
    defaultValue="1024"
    )
	public static final String DefaultStreamBuffer = "DefaultStreamBuffer";
	
	/**
	 * HttpServletResponse object that will be blindly forwarded to the client "
	 */
	@ComponentInput(
	description="HttpServletResponse object that will be blindly forwarded to the client. This Object is expected to be fully cooked and ready for delievery (HttpHeader, MimeType, and Size Should be handled externally)" , 
	name="HttpServletResponse"
    )
	public static final String httpServletResponse = "HttpServletResponse";

	/**
	 * "Semaphore object is blindly forwarded; normally would go to httpServletResponse writter to be released when client message is completed"
	 */
	@ComponentInput(
	description="Semaphore object that will be used to execute Semaphore.release() when the ResponsePayload is sent away" , 
	name="Semaphore"
    )
	public static final String semaphore = "Semaphore";	

	/**
	 * "The Data Object recieved on this ComponentInput Port will be blindly passed \"PrintWriter\" to transmit to the web-based client"
	 */
	@ComponentInput(
	description="The Data Object recieved will be \"InputStream\" on this ComponentInput Port will be blindly passed \"OutputStream\" to transmit to the web-based client" , 
	name="httpServletResponsePayload"
    )
	public static final String httpServletResponsePayload = "httpServletResponsePayload";	

	private static Logger logger = null;
	/* (non-Javadoc)
	 * @see org.meandre.core.ExecutableComponent#execute(org.meandre.core.ComponentContext)
	 */
	/**
	 * General purpose execute method which will be executed only once and enters a 
	 * forever loop that tests the ComponentContext.isFlowAborting(), if this should
	 * become true the logical service will be Shutdown immediately.  The ComponentProperty
	 * 
	 */
	public void execute(ComponentContext cc)
			throws ComponentExecutionException, ComponentContextException {

		logger = getLogger();
		//
		if(isOutputToConsole())
			getConsoleOut().println("Execute called for: " + this.getClass().getName());
		
        Object inputStream = (Object)cc.getDataComponentFromInput(httpServletResponsePayload);
        Semaphore sem = (Semaphore) cc.getDataComponentFromInput(semaphore);
        HttpServletResponse response = (HttpServletResponse) cc.getDataComponentFromInput(httpServletResponse);

        try {
        	if(isOutputToConsoleVerbose())
        		getConsoleOut().println("Attempting to send using outputStream assuming an InputStream was Recieved.");

            ServletOutputStream outputStream = response.getOutputStream();
            if(inputStream instanceof java.io.InputStream){
            	
            	// 
            	// Yummie all is well in lala Land for now..
            	int bufferSize = Integer.parseInt(cc.getProperty(DefaultStreamBuffer));	
            	pipeStream( (InputStream)inputStream , outputStream, bufferSize );
            	
            } else {
            	
            	//
            	// If not an InputStream let' try to push out the object using PrintWriter method
            	PrintWriter pw = response.getWriter();
                pw.println( (Object)inputStream );
                response.getWriter().flush();

            }
            sem.release();
            //
       } catch (IOException e) {
    	   
	       	if(isOutputToConsoleVerbose()){
	    		getConsoleOut().println("IOException: Will send StackTrace of Execption to client as well");
	    		e.printStackTrace(getConsoleOut());
	       	}
	       	
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos);
            e.printStackTrace(ps);
            
       }
	}
    /**
     * Copies the contents of an InputStream to an OutputStream, then closes
     * both.
     * 
     * @param in
     *        The source stream.
     * @param out
     *        The target stram.
     * @param bufSize
     *        Number of bytes to attempt to copy at a time.
     * @throws IOException
     *         If any sort of read/write error occurs on either stream.
     */
    private static void pipeStream(InputStream in, OutputStream out, int bufSize)
            throws IOException {
        try {
            byte[] buf = new byte[bufSize];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        } finally {
            try {
                in.close();
                out.close();
            } catch (IOException e) {
            	logger.info("Error with close stream:\n" + e.getMessage() );
            }
        }
    }

}
