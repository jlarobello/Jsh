import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.lang.Thread;

/**
 * Program that sends an InputStream from an
 * OutputStream in a thread.
 *
 * @author Jonathan Robello
 */
public class PipeThread extends Thread {
    
    InputStream input;
    OutputStream output;

    /**
     * Default Constructor.
     */
    PipeThread() {
        this.input  = null;
        this.output = null;
    }

    /**
     * Constructor that sets Input and OutPut
     * streams.
     *
     * @param input the InputStream.
     * @param output the OutputStream.
     */
     PipeThread(InputStream input, OutputStream output) {
         this.input  = input;
         this.output = output;
     }
     
    /**
     * Run the thread.
     */
     public void run() {
         int in;
         byte[] buf = new byte[1024];

         try {
             while ((in = input.read(buf)) != -1) {
                 output.write(buf, 0, in);
             }
         } catch (IOException e) {
             e.printStackTrace();
         }
     }

    /**
     * Set the InputStream.
     *
     * @param input the InputStream.
     */
     public void setInput(InputStream input) {
         this.input = input;
     }

   /**
    * Set the OutputStream.
    *
    * @param output the OutputStream.
    */
    public void setOutput(OutputStream output) {
        this.output = output;
    }
}

