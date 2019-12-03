public class Table {
    private int index;
    private int startTime;      // the starting time of the current (last) occupation
    private int endTime;        // the end time of the current (last) occupation
    private boolean busy;

    public Table(int index) {
        this.index = index;
        this.busy = false;
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

    public int getIndex() {
        return index;
    }
}
