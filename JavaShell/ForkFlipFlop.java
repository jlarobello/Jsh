import java.io.IOException;
import java.io.PrintStream;
import java.io.File;

import java.lang.Process;
import java.lang.ProcessBuilder;

/**
 * Test program for PipeThread.
 *
 * @author Jonathan Robello
 */
public class ForkFlipFlop {
    
    final static String working_dir = System.getProperty("user.dir");

    public static void main(String[] args) {
        int error;
        String command = "java FlipFlop", x = "0", y = "0";
        ProcessBuilder pb = null;
        Process process   = null;
        PipeThread pipeOut = null, pipeErr = null;

        if (args.length >= 2) {
            x = args[0];
            y = args[1];
        }

        command += "x" + " " + "y";
        try {
            pb = new ProcessBuilder("java", "FlipFlop", x, y);
            pb.directory(new File(working_dir));
            process = pb.start();
            
            pipeOut = new PipeThread(process.getInputStream(), new PrintStream(System.out));
            pipeErr = new PipeThread(process.getErrorStream(), new PrintStream(System.err));
            pipeOut.start();
            pipeErr.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

