import java.io.*;
import java.util.ArrayList;

/*
    The main class of the project.
    Define the service of the restaurant

    Logic time and physical time are the main issue.
    Measurement should be logic time (minutes in the description)
    However, machine time can't be accurate (e.g. currentMillies)
    We set the start time and end time of some objects to mark the event time (logic time)
    And we use physical time (propotional to the logical time) to emulate the time passing.
    This means the printing information will be based on the event time but the lock will be held for the clock time.

    We use a standard start time as the base for all objects measuring the clock time.

    Resources that need to be coordinated: availableTables, 3 Machines, pendingOrders, finishedOrders.

    The output should be ordered chronologically.
 */
public class DiningService {
    public static ArrayList<Object> dinerCVs = new ArrayList<>();
    public static Integer lastLeavingTime = Integer.MIN_VALUE;

    public static void main(String[] args) throws FileNotFoundException {
        String inputFileName;
        File inputFile;
        String outputFileName;
        File outputFile = null;
        // Simple command line parsing and processing.
        int argc = args.length;
        if (argc == 0) {
            throw new IllegalArgumentException("Missing required command line arguments");
        } else {
            // Take the first argument as the input file name
            inputFileName = args[0];
            inputFile = new File(inputFileName);
        }
        // If 2 arguments are fed, take the second as the output file name.
        if (argc == 2) {
            outputFileName = args[1];
            outputFile = new File(outputFileName);
        } else if (argc > 2) {
            throw new IllegalArgumentException("Excessive arguments are given");
        }

        // Redirect stdout to the file if defined.
        if (outputFile != null) {
            System.setOut(new PrintStream(outputFile));
        }

        // Parse the input file and set up the configuration.
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile)));
        int nDiners = 0;
        int nTables = 0;
        int nCooks = 0;

        // The first three lines.
        try {
            nDiners = Integer.valueOf(br.readLine());
            nTables = Integer.valueOf(br.readLine());
            nCooks = Integer.valueOf(br.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Since the arrive time of the customers are sorted in ascending order, a sequential container is enough.
        ArrayList<Diner> diners = new ArrayList<>();
        ArrayList<Thread> dinerThreads = new ArrayList<>();
        Restaurant restaurant = new Restaurant(nCooks, nTables);
        ArrayList<Table> availableTables = restaurant.getAvailableTables();
        ArrayList<Order> pendingOrders = restaurant.getPendingOrders();
        for (int i = 0; i < nDiners; i++)
            dinerCVs.add(new Object());


        // Then, read till EOF (or nDiners lines) to establish the order.
        try {
            for (int i = 0; i < nDiners; i++) {
                String orderString = br.readLine();
                String[] items = orderString.split(",");
                int arriveTime = Integer.valueOf(items[0]);
                int nBurgers = Integer.valueOf(items[1]);
                int nFries = Integer.valueOf(items[2]);
                int nCokes = Integer.valueOf(items[3]);
                Order order = new Order(nBurgers, nFries, nCokes, String.valueOf(i + 1));
                Diner diner = new Diner(String.valueOf(i + 1), arriveTime, order, availableTables, pendingOrders);
                order.currentDiner = diner;
                diners.add(diner);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Run the restaurant
        restaurant.Run();

        // After the restaurant has started. Run the diner thread here.
        // Two options here to emulate the arrive time of the clock.
        // First, start the thread according to the arrival time.
        // Second, start the thread right away and sleep till the arrival time.
        // Use the second method since it's easier.
        for (int i = 0; i < nDiners; i++) {
            Thread dinerThread = new Thread(diners.get(i));
            dinerThreads.add(dinerThread);
        }

        for (Thread td: dinerThreads) {
            td.start();
        }

        try {
            for (Thread td : dinerThreads) {
                td.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Utils.insertMessage(lastLeavingTime, Utils.formatTime(lastLeavingTime, "The last diner leaves the restaurant."));
        Utils.printMessages();
    }

}
