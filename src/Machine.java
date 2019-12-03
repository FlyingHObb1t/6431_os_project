public class Machine {
    public static Machine burgerMachine = new Machine("burger machine", 5);
    public static Machine friesMachine = new Machine("fries machine", 3);
    public static Machine cokeMachine = new Machine("coke machine", 1);
    public static Object machineCV;

    private int processingTime;
    private String machineName;
    private int startTime;
    private int endTime;
    private boolean busy;


    private Machine(String machineName, int processingTime) {
        this.processingTime = processingTime;
        this.machineName = machineName;
        this.busy = false;
        machineCV = new Object();
    }

    public synchronized boolean isBusy() {
        return busy;
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

    public synchronized void setBusy(boolean busy) {
        this.busy = busy;
    }

    public int getProcessingTime() {
        return processingTime;
    }

    public String getMachineName() {
        return machineName;
    }
}
