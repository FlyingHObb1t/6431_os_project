import java.util.ArrayList;

public class Cook implements Runnable {
    private String cookIndex;
    private ArrayList<Order> pendingOrders;
    private ArrayList<Order> finishedOrders;
    private Order currentOrder;
    private int startTime;      // the time this cook starts processing the current (last) order
    private int endTime;        // the time this cook finishes processing the current (last) order
    private boolean busy;
    private Machine burgerMachine;
    private Machine friesMachine;
    private Machine cokeMachine;
    private Object machineCV;

    public Cook(String cookIndex, ArrayList<Order> pendingOrders, ArrayList<Order> finishedOrders) {
        this.cookIndex = cookIndex;
        this.pendingOrders = pendingOrders;
        this.finishedOrders = finishedOrders;
        this.startTime = this.endTime = 0;
        this.burgerMachine = Machine.burgerMachine;
        this.friesMachine = Machine.friesMachine;
        this.cokeMachine = Machine.cokeMachine;
        this.machineCV = Machine.machineCV;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }


    private boolean cookBurger(int nBurgers) {
        int time = 0;
        boolean owned = false;
        for (int i = 0; i < 100; i++) {
            synchronized (burgerMachine) {
                if (!burgerMachine.isBusy()) {
                    // starts to process this order.
//                    System.out.println(cookIndex + " currentOrder endTime" + currentOrder.getEndTime());
//                    System.out.println(cookIndex + " burgerMachine endTime" + burgerMachine.getEndTime());
                    time = Math.max(currentOrder.getEndTime(), burgerMachine.getEndTime());
                    burgerMachine.setStartTime(time);
                    burgerMachine.setEndTime(time + nBurgers * burgerMachine.getProcessingTime());
                    currentOrder.setEndTime(time + nBurgers * burgerMachine.getProcessingTime());
                    burgerMachine.setBusy(true);
                    owned = true;
                    break;
                }
            }
        }
        if (!owned) {
//            System.out.println("owned by others");
            return false;
        }
//        System.out.println("cook " + cookIndex + ": process burger at " + time);
        for (int i = 0; i < nBurgers; i++) {
            Utils.insertMessage(time + i * burgerMachine.getProcessingTime(),
                    Utils.formatTime(time + i * burgerMachine.getProcessingTime(), "Cook " + cookIndex + " uses the burger machine."));
        }
        try {
            Thread.sleep(burgerMachine.getProcessingTime() * nBurgers * Utils.TIME_UNIT / 2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        synchronized (burgerMachine) {
//            System.out.println("release burger machine");
            burgerMachine.setBusy(false);
        }
        synchronized (machineCV) {
//            System.out.println("cook " + cookIndex + " release the burger machine");
            machineCV.notifyAll();
        }
        return true;
    }

    private boolean cookFries(int nFries) {
        int time;
        synchronized (friesMachine) {
            if (!friesMachine.isBusy()) {
                // starts to process this order.
                time = Math.max(currentOrder.getEndTime(), friesMachine.getEndTime());
                friesMachine.setStartTime(time);
                friesMachine.setEndTime(time + nFries * friesMachine.getProcessingTime());
                currentOrder.setEndTime(time + nFries * friesMachine.getProcessingTime());
                friesMachine.setBusy(true);
            } else {
                return false;
            }
        }
        for (int i = 0; i < nFries; i++) {
            Utils.insertMessage(time + i * friesMachine.getProcessingTime(), Utils.formatTime(time + i * friesMachine.getProcessingTime(), "Cook " + cookIndex + " uses the fries machine."));
        }
        try {
            Thread.sleep(friesMachine.getProcessingTime() * nFries * Utils.TIME_UNIT);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        synchronized (friesMachine) {
            friesMachine.setBusy(false);
        }
        synchronized (machineCV) {
            machineCV.notifyAll();
        }
        return true;
    }

    private boolean cookCoke(int nCokes) {
        synchronized (cokeMachine) {
            if (!cokeMachine.isBusy()) {
                // starts to process this order.
                int time = Math.max(currentOrder.getEndTime(), cokeMachine.getEndTime());
                cokeMachine.setStartTime(time);
                cokeMachine.setEndTime(time + nCokes * cokeMachine.getProcessingTime());
                currentOrder.setEndTime(time + nCokes * cokeMachine.getProcessingTime());
                cokeMachine.setBusy(true);
                Utils.insertMessage(time, Utils.formatTime(time, "Cook " + cookIndex + " uses the coke machine."));
            } else {
                return false;
            }
        }
        try {
            Thread.sleep(cokeMachine.getProcessingTime() * nCokes * Utils.TIME_UNIT);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        synchronized (cokeMachine) {
            cokeMachine.setBusy(false);
        }
        synchronized (machineCV) {
            machineCV.notifyAll();
        }
        return true;
    }

    @Override
    public void run() {
        // List of actions:
        // A cook is trying to get the order from the pendingOrders list in a while loop.
        // Once the order is fetched. The cook will set the start time of the order.
        // The cook will try to get the machine to process the food in a loop until all items have been processed.
        // The cook shouldn't be put to sleep if one resource is not available. Instead it should go to another resource.
        // When all the items are finished. The cook should set the endTime of the order and put it into the finishedOrders list.
        // Another option: if all machines are occupied, the cook could be put to sleep.
        while (true) {
            int currentDinerIndex = 0;
            synchronized (pendingOrders) {
                while (pendingOrders.size() == 0) {
                    try {
                        pendingOrders.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                currentOrder = pendingOrders.remove(0);
                startTime = Math.max(currentOrder.getInsertTime(), endTime);
                currentOrder.setStartTime(startTime);
                // the endTime of an Order will change with the process. It represents the current time of the order
                currentOrder.setEndTime(startTime);
//                System.out.println("in cook " + currentOrder.getEndTime());
                Utils.insertMessage(startTime, Utils.formatTime(startTime, "Cook " + cookIndex + " processes Diner " + currentOrder.getDinerIndex() + "'s order."));
            }

            int nBurgers = currentOrder.getnBurgers();
            int nFries = currentOrder.getnFries();
            int nCokes = currentOrder.getnCokes();
            Diner currentDiner = currentOrder.currentDiner;

            while (nBurgers > 0 || nFries > 0 || nCokes > 0) {
                if (nBurgers > 0) {
//                    System.out.println(currentOrder.getEndTime());
                    boolean burgerFinished = cookBurger(nBurgers);
                    if (burgerFinished)
                        nBurgers = 0;
                }
                if (nFries > 0) {
                    boolean friesFinished = cookFries(nFries);
                    if (friesFinished)
                        nFries = 0;
                }
                if (nCokes > 0) {
                    boolean cokeFinished = cookCoke(nCokes);
                    if (cokeFinished)
                        nCokes = 0;
                }
                if (nBurgers == 0 && nFries == 0 && nCokes == 0)
                    break;
                synchronized (machineCV) {
                    try {
//                        System.out.println("cook " + cookIndex + " waits for machine");
                        machineCV.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            synchronized (finishedOrders) {
                endTime = currentOrder.getEndTime();
                finishedOrders.add(currentOrder);
            }
            Utils.insertMessage(endTime, Utils.formatTime(endTime, "Diner " + currentOrder.getDinerIndex() + "'s order is ready. Diner " + currentOrder.getDinerIndex() + " starts eating."));
//            synchronized (DiningService.dinerCVs.get(Integer.valueOf(currentDinerIndex - 1))) {
//                DiningService.dinerCVs.get(Integer.valueOf(currentDinerIndex - 1)).notifyAll();
//            }
            synchronized (currentDiner) {
                currentDiner.notifyAll();
            }
        }
    }
}
