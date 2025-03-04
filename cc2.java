import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

class cc2 {
    // Key = interval start, Value = interval end
    private final TreeMap<Integer, Integer> intervals;

    public cc2() {
        intervals = new TreeMap<>();
    }

    
    // Adds a new interval [start, end] and merges with any overlapping intervals.
     
    public void addInterval(int start, int end) {
        // Ensure start <= end
        if (start > end) {
            int temp = start;
            start = end;
            end = temp;
        }

        boolean changed = true;
        while (changed) {
            changed = false;

            // 1) Check if there's an interval that overlaps on the left side via floorEntry(end).
            //    floorEntry(end) gives us the interval with the greatest start <= end.
            Map.Entry<Integer, Integer> floor = intervals.floorEntry(end);
            if (floor != null) {
                int existingStart = floor.getKey();
                int existingEnd   = floor.getValue();
                // If [existingStart, existingEnd] overlaps with [start, end], merge them
                if (existingEnd >= start && existingStart <= end) {
                    start = Math.min(start, existingStart);
                    end   = Math.max(end, existingEnd);
                    intervals.remove(existingStart); // Remove the old interval
                    changed = true;
                }
            }

            // 2) Check for intervals whose start lies in [start, end].
            //    These intervals can overlap as well.
            NavigableMap<Integer, Integer> sub = intervals.subMap(start, true, end, true);
            if (!sub.isEmpty()) {
                changed = true;
                // Collect keys to remove them safely after iteration
                List<Integer> keysToRemove = new ArrayList<>(sub.keySet());
                for (int s : keysToRemove) {
                    int e = intervals.remove(s);
                    start = Math.min(start, s);
                    end   = Math.max(end, e);
                }
            }
        }

        // Finally, insert the merged interval
        intervals.put(start, end);
    }

    
     // Returns the current list of merged, non-overlapping intervals in ascending order.
     
    public List<int[]> getIntervals() {
        List<int[]> result = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : intervals.entrySet()) {
            result.add(new int[] {entry.getKey(), entry.getValue()});
        }
        return result;
    }

    
    // Demonstration
    
    public static void main(String[] args) {
        cc2 im = new cc2();
        im.addInterval(1, 5);
        im.addInterval(6, 8);
        im.addInterval(2, 7);

        // Expect a single merged interval [1, 8]
        List<int[]> current = im.getIntervals();
        for (int[] interval : current) {
            System.out.println("[" + interval[0] + ", " + interval[1] + "]");
        }
    }
}
