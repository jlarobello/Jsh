import java.io.*;

import java.util.Arrays;
import java.util.List;
import java.util.LinkedList;

import java.lang.Process;
import java.lang.ProcessBuilder;

/**
 * A simple shell with basic commands.
 * 
 * @author Jonathan Robello
 */
public class Jsh {

    public final static String working_dir = System.getProperty("user.dir");
    private static String current_dir = working_dir;

    private static LinkedList< JobNode > jobs = new LinkedList< JobNode >();

    public static void main(String[] args) throws IOException {
        shell_loop();
    }

    /**
     * The main loop of the shell.
     */
    private static void shell_loop() {
        String input = "";
        List < String > command = null;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        
        while (true) {
            System.out.print("jsh> ");

            try {
                input = br.readLine();
                input = input.trim();
                command = new LinkedList< String >(Arrays.asList(input.split("\\s+")));

                if (input.equals(""))
                    continue;
                else if (command.get(0).equals("exit"))
                    break;
                else
                    execCommand(command);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                System.out.println();
                break;
            }
        }
    }

    /**
     * Executes command with parameters if valid. Otherwise prints command
     * as invalid. Each command has its own error output.
     * 
     * @param commands the command and any additonal parameters.
     */
    private static void execCommand(List < String > command) {
        
        if (command.get(0).equals("cd"))
            cd(command);
        else if (command.get(0).equals("jobs"))
            jobs(command);
        else if (command.get(0).equals("fg"))
            fg(command);
        else
            createProcess(command);
    }

    /**
     * Create a new PipeThread.
     *
     * @param in the input stream.
     * @param out the output stream.
     * @return a new PipeThread.
     */
    public static PipeThread createPipe(InputStream in, OutputStream out) {
        return new PipeThread(in, out);
    }

    /**
     * The Change Directory program.
     *
     * @param command the command list.
     */
    private static void cd(List< String > command) {
        String temp_dir = current_dir;
       
        if (command.size() == 1) {
           current_dir = working_dir;
        } else if (command.size() == 2) {
            if (command.get(1).charAt(0) == '/')
                temp_dir = command.get(1);
            else
                temp_dir += ("/" + command.get(1));
            if (new File(temp_dir).exists())
                current_dir = temp_dir;
            else {
                System.out.println("Invalid directory " + command.get(1));
            }
        } else {
            System.out.println("Syntax error");
        }
    }

    /**
     * Creates and runs a process.
     *
     * @param command the command list.
     */
    private static void createProcess(List< String > command) {
        int error = 0;
        ProcessBuilder pb = null;
        Process process = null;
        
        if (command.get(command.size() - 1).equals("&")) {
            
            command.remove(command.size() - 1);

            try {
                pb = new ProcessBuilder(command);
                pb.directory(new File(current_dir));
                
                if (jobs.size() == 0)
                    jobs.add(new JobNode(listToString(command), pb.start(), 1));
                else {
                    JobNode lastJob = jobs.getLast();
                    jobs.add(new JobNode(listToString(command), pb.start(), lastJob.getJobNum() + 1));
                }
                new JobCheck(jobs.getLast().getProcess()).start();
            } catch (IOException e) {
                System.out.println("Unknown command '" + command.get(0) + "'");
            }
        } else {
            try {
                pb = new ProcessBuilder(command);
                pb.directory(new File(current_dir));
                process = pb.start();
                createPipe(process.getInputStream(), new PrintStream(System.out)).start();
                createPipe(process.getErrorStream(), new PrintStream(System.err)).start();
                error = process.waitFor();
            } catch (IOException e) {
                System.out.println("Unknown command '" + command.get(0) + "'");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * List jobs in jobs list.
     */
    private static void jobs(List< String > command) {
        if (command.get(command.size() - 1).equals("&"))
            System.out.println("Error: cannot put 'jobs' in the background");
        else {
            for (JobNode job: jobs)
                System.out.println("["+job.getJobNum()+"] " + job.getCommand());
        }
    }

    /**
     * Move background process to foreground.
     *
     * @param command the command list.
     */
    private static void fg(List< String > command) {
        if (command.get(command.size() - 1).equals("&"))
            System.out.println("Error: cannot put 'fg' in the bacground");
        else {
            try {
                int i = 0, num = Integer.valueOf(command.get(1));
                Process temp = null;
                
                for (JobNode job: jobs) {
                    if (job.getJobNum() == num) {
                        temp = job.getProcess();
                        break;
                    }
                    i++;
                }

                if (temp == null)
                    System.out.println("No such background command");
                else {
                    createPipe(temp.getInputStream(), new PrintStream(System.out)).start();
                    createPipe(temp.getErrorStream(), new PrintStream(System.err)).start();
                    
                    try {
                        temp.waitFor();
                    } catch (InterruptedException e) {
                        temp.destroy();
                    }
                  
                    jobs.remove(i);
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid Input: " + command.get(1));
            }
        }
    }

    /**
     * Turn String List to a String.
     * 
     * @param list the string list.
     * @return a String representation of the list.
     */
    public static String listToString(List< String > list) {
       String temp = "";
       for (String s: list)
           temp += s + " ";
       return temp;
    }

    /**
     * Check if a process is still running.
     *
     * @param process the process.
     * @return whether the process is running or not.
     */
    public static boolean isRunning(Process process) {
        try {
            process.exitValue();
            return false;
        } catch (Exception e) {
            return true;
        }
    }
}    

