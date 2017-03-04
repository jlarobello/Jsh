import java.lang.Process;

/**
 * A Job Node that contains the command and process.
 *
 * @author Jonathan Robello
 */
public class JobNode {
    
    private int jobNum;
    private String command;
    private Process process;
    
    /**
     * Default Constructor
     */
    public JobNode() {
        jobNum  = -1;
        command = null;
        process = null;
    }
    
    /**
     * Constructor that sets the command and process.
     *
     * @param command the command that was executed.
     * @param pr the process.
     */
    public JobNode(String command, Process process, int jobNum) {
        this.jobNum  = jobNum;
        this.command = command;
        this.process = process;
    }

    /**
     * Get the command.
     *
     * @return the command of the process.
     */
    public String getCommand() {
        return command;
    }

    /**
     * Get the Process.
     *
     * @return the process.
     */
    public Process getProcess() {
        return process;
    }

    /**
     * Get the jobNum
     *
     * @return the jobNum.
     */
    public int getJobNum() {
        return jobNum;
    }
}
