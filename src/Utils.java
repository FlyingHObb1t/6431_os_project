import java.util.ArrayList;
import java.util.Comparator;

public class Utils {
    public static final int TIME_UNIT = 50;
    private static ArrayList<Pair<Integer, String>> messages = new ArrayList<>();

    /*
        return a string with the formatted time at the beginning (the string itself shouldn't have the time)
     */
    public static String formatTime(int minutes, String msg) {
        // convert the string
        int hours = minutes / 60;
        int minutesINHour = minutes % 60;
        StringBuilder sb = new StringBuilder();
        if (hours < 10)
            sb.append("0");
        sb.append(hours);
        sb.append(":");
        if (minutesINHour < 10)
            sb.append("0");
        sb.append(minutesINHour);
        sb.append(" - ");
        sb.append(msg);
        return sb.toString();
    }

    public static synchronized void insertMessage(int timeIndex, String msg) {
        messages.add(new Pair<>(timeIndex, msg));
    }

    public static void printMessages() {
        // sort the list first and print the information
        messages.sort(new Comparator<Pair<Integer, String>>() {
            @Override
            public int compare(Pair<Integer, String> o1, Pair<Integer, String> o2) {
                return o1.getFirst() - o2.getFirst();
            }
        });
        for (Pair pair: messages) {
            System.out.println(pair.getSecond());
        }
    }
}

/*
    Made implicitly a pair of int and string.
 */
class Pair<K, V> {
    private K first;
    private V second;

    public Pair(K first, V second) {
        this.first = first;
        this.second = second;
    }

    public K getFirst() {
        return first;
    }

    public V getSecond() {
        return second;
    }
}
