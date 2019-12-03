import java.util.ArrayList;

public class Diner implements Runnable {
    private String dinerIndex;
    private int arriveTime;
    private Order order;
    private int tableNumber;
    private int cookNumber;
    private int startTime;  // the time this diner gets the table
    private int endTime;    // the time this diner releases the table
    private boolean isBusy;
    private Table table;
    private ArrayList<Table> availableTables;
    private ArrayList<Order> pendingOrders;

    public Diner(String dinerIndex, int arriveTime, Order order, ArrayList<Table> availableTables, ArrayList<Order> pendingOrders) {
        this.dinerIndex = dinerIndex;
        this.arriveTime = arriveTime;
        this.order = order;
        this.tableNumber = -1;   // not served
        this.cookNumber = -1;
        this.isBusy = false;
        this.availableTables = availableTables;
        this.pendingOrders = pendingOrders;
    }

    @Override
    public void run() {
        // Actions of Diners：
        // sleep till the arrive time
        // Try to grab a table from the availableTables list
        // Once it has the table. Add the order to the pendingOrder list
        // Wait for the order to complete (how? by wait and notify)
        // spend 30 mins to finish the order (end the thread)
        try {
            Thread.sleep(arriveTime * Utils.TIME_UNIT);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        Utils.insertMessage(arriveTime, Utils.formatTime(arriveTime, "Diner " + dinerIndex + " arrives."));

        // get the table and record the time.
        synchronized (availableTables) {
            while (availableTables.size() == 0) {
                try {
                    availableTables.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // Two possible conditions: table idle for some time (set to arrive time) or diner idle for some time (set to table end time)
            table = availableTables.remove(0);
            startTime = Math.max(table.getEndTime(), arriveTime);
            table.setStartTime(startTime);
            Utils.insertMessage(startTime, Utils.formatTime(startTime, "Diner " + dinerIndex + " is seated at table " + table.getIndex() + "."));
            availableTables.notifyAll();
        }

        // put the order in the pendingOrders list
        synchronized (pendingOrders) {
            order.setInsertTime(startTime);
            pendingOrders.add(order);
            pendingOrders.notifyAll();
        }

        synchronized (this) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // eat the food.
        try {
            Thread.sleep(30 * Utils.TIME_UNIT);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // release the table and set end time
        synchronized (availableTables) {
            availableTables.add(table);
            endTime = order.getEndTime() + 30;
            table.setEndTime(endTime);
            availableTables.notifyAll();
        }
        Utils.insertMessage(endTime, Utils.formatTime(endTime, "Diner " + dinerIndex + " finishes. Diner " + dinerIndex + " leaves the restaurant."));
        synchronized (DiningService.lastLeavingTime) {
            DiningService.lastLeavingTime = Math.max(DiningService.lastLeavingTime, endTime);
        }
    }
}
