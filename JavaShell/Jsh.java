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
                command = processInput(input);

                if (input.equals("")) {
                    continue;
                }
                else if (input.charAt(0) == '|' || input.charAt(input.length() - 1) == '|') {
                    System.out.println("syntax error near unexpected token `|'");
                    continue;
                } else if (command.get(command.size() - 1).equals("&")) {
                    System.out.println("syntax error near unexpected token '&'");
                    continue;
                }
                else if (command.get(0).equals("exit")) {
                    break;
                }
                else {
                    execCommand(command);
                }
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
        
        if (command.get(command.size() - 1).charAt(command.get(command.size() - 1).length() - 1) == '&') {
            createJobs(command);
        } else {
            createProcesses(command);
        }
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
    private static InputStream createProcess(List< String > command, InputStream in) {
        int error = 0;
        ProcessBuilder pb = null;
        Process process   = null;
        
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

                pipe(in, process.getOutputStream());
                createPipe(process.getErrorStream(), new PrintStream(System.err)).start();
                error = process.waitFor();

                return process.getInputStream();
            } catch (IOException e) {
                System.out.println("Unknown command '" + command.get(0) + "'");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    /**
     * pipe input to output stream.
     * 
     * @param in the input.
     * @param out the output.
     */
    public static void pipe(InputStream in, OutputStream out) throws IOException {
        int n;
        byte[] buf = new byte[1024];
        while ((n = in.read(buf)) > -1) {
            out.write(buf, 0, n);
        }
        out.close();
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
            System.out.println("Error: cannot put 'fg' in the background");
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
     * Process input
     *
     * @param input the input.
     * @return a list of proccessed strings.
     */
    public static List< String > processInput(String input) {
        String command = "";
        List< String > commands = new LinkedList< String >(Arrays.asList(input.split("\\|")));

        for (int i = 0; i < commands.size(); i++) {
            command = commands.get(i);
            command = command.trim().replaceAll(" +", " ");
            commands.set(i, command);
        }

        return commands;
    }

    /**
     * Creates processes
     *
     * @param commands the list of commands.
     */
    public static void createProcesses(List< String > commands) {
        List< String > command = null;

        PipeThread pipe  = null;
        InputStream in = new ByteArrayInputStream("".getBytes());;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        for (int i = 0; i < commands.size(); i++) {
            command = new LinkedList< String >(Arrays.asList(commands.get(i).split("\\s+")));

            if (command.get(0).equals("cd")) {
                cd(command);
                in = new ByteArrayInputStream("".getBytes());
            } else if (command.get(0).equals("jobs")) {
                jobs(command);
                in = new ByteArrayInputStream("".getBytes());
            } else if (command.get(0).equals("fg")) {
                fg(command);
                in = new ByteArrayInputStream("".getBytes());
            } else if (i == commands.size() - 1) {
                in = createProcess(command, in);
                pipe = createPipe(in, System.out);
                pipe.start();
                while (pipe.isAlive());
            } else {
                in = createProcess(command, in);
                pipe = createPipe(in, out);
                pipe.start();
                while (pipe.isAlive());
                in = new ByteArrayInputStream(out.toByteArray());

                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                out = new ByteArrayOutputStream();
            } 
        }
    }

    /**
     * Create jobs
     *
     * @param commands the list of commands.
     */
    public static void createJobs(List< String > commands) {
        List< String > command = new LinkedList< String >(Arrays.asList(commands.get(0).split("\\s+")));

        if (command.get(0).equals("cd")) {
            cd(command);
        }
        else if (command.get(0).equals("jobs")) {
            jobs(command);
        }
        else if (command.get(0).equals("fg")) {
            fg(command);
        } else {
            createProcess(command, null);
        }
    }
}
