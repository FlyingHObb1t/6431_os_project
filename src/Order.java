public class Order {
    private int nBurgers;
    private int nFries;
    private int nCokes;
    private String dinerIndex;
    private int insertTime;     // the time this order is placed into the pending list
    private int startTime;      // the time this order starts being processed.
    private int endTime;        // the time this order finishes being processed.
    public boolean finished;
    public Diner currentDiner;

    public Order(int nBurgers, int nFries, int nCokes, String dinerIndex) {
        this.nBurgers = nBurgers;
        this.nFries = nFries;
        this.nCokes = nCokes;
        this.insertTime = this.startTime = this.endTime = 0;
        this.dinerIndex = dinerIndex;
        this.finished = false;
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

    public int getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(int insertTime) {
        this.insertTime = insertTime;
    }

    public int getnBurgers() {
        return nBurgers;
    }

    public int getnFries() {
        return nFries;
    }

    public int getnCokes() {
        return nCokes;
    }

    public String getDinerIndex() {
        return dinerIndex;
    }
}
