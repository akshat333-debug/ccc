import java.util.ArrayDeque;
import java.util.Deque;

class MinMaxStack {

    // Each node stores its value, and the min/max up to that node
    private static class Node {
        int val;
        int currentMin;
        int currentMax;

        Node(int val, int currentMin, int currentMax) {
            this.val = val;
            this.currentMin = currentMin;
            this.currentMax = currentMax;
        }
    }

    private Deque<Node> stack;

    public MinMaxStack() {
        stack = new ArrayDeque<>();
    }

    
    //  Push an element x onto the stack in O(1) time.
     
    public void push(int x) {
        if (stack.isEmpty()) {
            // First element sets its own min and max
            stack.push(new Node(x, x, x));
        } else {
            // Compare x with the current top's min and max
            Node topNode = stack.peek();
            int newMin = Math.min(x, topNode.currentMin);
            int newMax = Math.max(x, topNode.currentMax);
            stack.push(new Node(x, newMin, newMax));
        }
    }

    
    //  Removes and returns the top element of the stack in O(1) time.
     
    public int pop() {
        if (stack.isEmpty()) {
            throw new IllegalStateException("Stack is empty");
        }
        return stack.pop().val;
    }

    
    // Returns the top element without removing it in O(1) time.
     
    public int top() {
        if (stack.isEmpty()) {
            throw new IllegalStateException("Stack is empty");
        }
        return stack.peek().val;
    }

    
    // Returns the minimum element in the stack in O(1) time.

    public int getMin() {
        if (stack.isEmpty()) {
            throw new IllegalStateException("Stack is empty");
        }
        return stack.peek().currentMin;
    }

    
     // Returns the maximum element in the stack in O(1) time.
     
    public int getMax() {
        if (stack.isEmpty()) {
            throw new IllegalStateException("Stack is empty");
        }
        return stack.peek().currentMax;
    }

    
    // Demonstration
    
    public static void main(String[] args) {
        MinMaxStack stack = new MinMaxStack();
        stack.push(5);
        stack.push(3);
        stack.push(7);
        stack.push(2);

        System.out.println("Top element: " + stack.top());     // 2
        System.out.println("Min element: " + stack.getMin());  // 2
        System.out.println("Max element: " + stack.getMax());  // 7

        stack.pop(); // removes 2
        System.out.println("Top after pop: " + stack.top());   // 7
        System.out.println("Min: " + stack.getMin());          // 3
        System.out.println("Max: " + stack.getMax());          // 7
    }
}
