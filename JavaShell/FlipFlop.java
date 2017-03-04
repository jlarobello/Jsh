import java.io.IOException;

import java.lang.Thread;

/**
 * FlipFlop java program that creates two
 * threads. Threads sleep for x/y
 * milliseconds.
 *
 * @author Jonathan Robello
 */
public class FlipFlop {
    
    public static void main(String[] args) {
        int x = 0, y = 0;
        
        try {
            if (args.length >= 2) {
                x = Integer.parseInt(args[0]);
                y = Integer.parseInt(args[1]);
            }
        } catch (NumberFormatException e) {
            System.out.println("Unable to parse input.");
        }

        Flip flip = new FlipFlop().new Flip(x);
        Flop flop = new FlipFlop().new Flop(y);
        
        flip.start();
        flop.start();
    }

    /**
     * The Flip thread.
     */
    private class Flip extends Thread {
        
        private int time;
        
        /**
         * Default Constructor
         */
        public Flip() {
            this.time = 0;
        }
        
        /**
         * Constructor that sets time.
         */
        public Flip(int time) {
            this.time = time;
        }

        /**
         * Executes the thread.
         */
        public void run() {
            for (int i = 0; i < 20; i++) {
                System.out.print("flip\n");
               
                try {
                    Thread.sleep(time);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * The Flop thread.
     */
    private class Flop extends Thread {

        private int time;

        /**
         * Default Constructor
         */
        public Flop() {
            this.time = 0;
        }

        /**
         * Constructor that sets time.
         */
        public Flop(int time) {
            this.time = time;
        }

        /**
         * Executes the thread.
         */
        public void run() {
            for (int i = 0; i < 20; i++) {
                System.err.print("flop\n");

                try {
                    Thread.sleep(time);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

