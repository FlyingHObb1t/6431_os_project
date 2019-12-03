import java.util.ArrayList;

public class Restaurant {
    private int nTables;
    private int nCooks;
    private ArrayList<Table> availableTables;
    private Machine burgerMachine;
    private Machine friesMachine;
    private Machine cokeMachine;
    private ArrayList<Order> pendingOrders;
    private ArrayList<Order> finishedOrders;
    private ArrayList<Thread> cookThreads;

    public Restaurant(int nCooks, int nTables) {
        this.nCooks = nCooks;
        this.nTables = nTables;
        availableTables = new ArrayList<>();
        burgerMachine = Machine.burgerMachine;
        friesMachine = Machine.friesMachine;
        cokeMachine = Machine.cokeMachine;
        pendingOrders = new ArrayList<>();
        finishedOrders = new ArrayList<>();
        cookThreads = new ArrayList<>();
    }

    // Init the tables and cook threads.
    public void Run() {
        for (int i = 0; i < nTables; i++) {
            Table table = new Table(i + 1);
            availableTables.add(table);
        }

        for (int i = 0; i < nCooks; i++) {
            Cook cook = new Cook(String.valueOf(i + 1), pendingOrders, finishedOrders);
            Thread cookThread = new Thread(cook);
            cookThreads.add(cookThread);
        }

        for (Thread td: cookThreads) {
            td.setDaemon(true);
            td.start();
        }
    }

    public ArrayList<Table> getAvailableTables() {
        return availableTables;
    }

    public ArrayList<Order> getPendingOrders() {
        return pendingOrders;
    }
}
