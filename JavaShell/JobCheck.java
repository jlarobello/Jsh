import java.io.PrintStream;

import java.lang.Process;
import java.lang.Thread;

/**
 * A thread that checks a Processes status. Processes its input/err
 * streams once process is done.
 *
 * @author Jonathan Robello
 */
public class JobCheck extends Thread {
    
    private Process process;

    public JobCheck(Process process) {
        this.process = process;
    }

    public void run() {
        while (isRunning(process));

        Jsh.createPipe(process.getInputStream(), new PrintStream(System.out)).start();
        Jsh.createPipe(process.getErrorStream(), new PrintStream(System.err)).start();
        
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
            process.destroy();
        }
    }

    /**
     * Check if a process is still running.
     *
     * @param process the process.
     * @return whether the process is running or not.
     */
    private boolean isRunning(Process process) {
        try {
            process.exitValue();
            return false;
        } catch (Exception e) {
            return true;
        }
    }
}
