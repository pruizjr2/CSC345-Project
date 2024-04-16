import java.util.ArrayList;
import java.util.List;

// Node class for Fibonacci Heap
class FibonacciHeapNode {
    int key; // Key value of the node
    int value; // Optional value associated with the node
    FibonacciHeapNode parent; // Reference to the parent node
    FibonacciHeapNode child; // Reference to the child node
    FibonacciHeapNode left; // Reference to the left sibling node
    FibonacciHeapNode right; // Reference to the right sibling node
    int degree; // Degree of the node (number of children)
    boolean mark; // Flag to indicate whether the node has lost a child since it became a child of another node

    // Constructor to initialize the node with a given value
    public FibonacciHeapNode(int value) {
        this.key = value; // Key and value are set to the same value initially
        this.value = value;
        this.parent = null; // Parent, child, left, and right references are set to null initially
        this.child = null;
        this.left = this; // Left and right references are set to itself initially to create a circular doubly linked list
        this.right = this;
        this.degree = 0; // Degree is initialized to 0
        this.mark = false; // Mark flag is set to false initially
    }

    // Method to provide a string representation of the node
    @Override
    public String toString() {
        // Convert parent, child, left, and right references to their key values or "null" if null
        String parentKey = (parent != null) ? String.valueOf(parent.key) : "null";
        String childKey = (child != null) ? String.valueOf(child.key) : "null";
        String leftKey = (left != null) ? String.valueOf(left.key) : "null";
        String rightKey = (right != null) ? String.valueOf(right.key) : "null";

        // Return a string representing the node with its key, value, parent, child, left, right, degree, and mark
        return "FibonacciHeapNode{" +
                "key=" + key +
                ", value=" + value +
                ", parent=" + parentKey +
                ", child=" + childKey +
                ", left=" + leftKey +
                ", right=" + rightKey +
                ", degree=" + degree +
                ", mark=" + mark +
                '}';
    }
}

// Class representing a Fibonacci Heap
public class FibonacciHeap {
    private FibonacciHeapNode minNode; // Reference to the minimum node in the heap
    private int numNodes; // Number of nodes in the heap

    // Constructor to initialize an empty Fibonacci Heap
    public FibonacciHeap() {
        this.minNode = null; // Initialize the minimum node reference to null
        this.numNodes = 0; // Initialize the number of nodes to 0
    }

    // Method to clear the heap by resetting the minimum node reference and the number of nodes
    public void clearHeap(){
        minNode = null; // Reset the minimum node reference to null
        numNodes = 0; // Reset the number of nodes to 0
    }

    // Method to insert a new node into the heap
    public void insert(FibonacciHeapNode node) {
        if (minNode == null) { // If the heap is empty
            minNode = node; // Set the new node as the minimum node
        } else { // If the heap is not empty
            // Add the new node to the list of roots
            node.left = minNode;
            node.right = minNode.right;
            minNode.right = node;
            node.right.left = node;

            // Update the minimum node reference if necessary
            if (node.key < minNode.key) {
                minNode = node;
            }
        }
        numNodes++; // Increment the number of nodes
    }

    // Method to return the minimum node in the heap
    public FibonacciHeapNode minimum() {
        return minNode; // Return the minimum node reference
    }

    // Method to extract the minimum node from the heap
    public FibonacciHeapNode extractMin() {
        FibonacciHeapNode min = this.minNode; // Store the current minimum node
        if (min != null) { // If the heap is not empty
            // Extract the children of the minimum node and insert them into the root list
            FibonacciHeapNode c = min.child;
            FibonacciHeapNode k = c, p;
            if (c != null) {
                do {
                    p = c.right;
                    insert(c);
                    c.parent = null;
                    c = p;
                } while (c != null && c != k);
            }

            // Remove the minimum node from the root list
            min.left.right = min.right;
            min.right.left = min.left;
            min.child = null;

            // Update the minimum node reference and consolidate the heap
            if (min == min.right)
                this.minNode = null;
            else {
                this.minNode = min.right;
                this.consolidate();
            }

            this.numNodes -= 1; // Decrement the number of nodes
            return min; // Return the extracted minimum node
        }
        return null; // Return null if the heap is empty
    }

    // Method to consolidate the heap to ensure optimal structure
    public void consolidate() {
        double goldenRatio = (1 + Math.sqrt(5)) / 2; // Calculate the golden ratio
        int maxDegree = (int) (Math.log(this.numNodes) / Math.log(goldenRatio)); // Calculate the maximum degree
        FibonacciHeapNode[] degreeArray = new FibonacciHeapNode[maxDegree + 1]; // Create an array to store nodes of each degree
        for (int i = 0; i <= maxDegree; ++i)
            degreeArray[i] = null; // Initialize the degree array with null values
        FibonacciHeapNode currentNode = minNode; // Start with the minimum node
        if (currentNode != null) { // If the heap is not empty
            FibonacciHeapNode currentCheck = minNode;
            do {
                FibonacciHeapNode tempNode = currentNode;
                int degree = tempNode.degree;
                while (degreeArray[degree] != null) { // If there is another node with the same degree
                    FibonacciHeapNode otherNode = degreeArray[degree];
                    if (tempNode.key > otherNode.key) {
                        FibonacciHeapNode temp = tempNode;
                        tempNode = otherNode;
                        otherNode = temp;
                        currentNode = tempNode;
                    }
                    link(otherNode, tempNode); // Link the nodes
                    currentCheck = tempNode;
                    degreeArray[degree] = null; // Reset the degree array
                    degree += 1; // Increment the degree
                }
                degreeArray[degree] = tempNode; // Store the node in the degree array
                currentNode = currentNode.right; // Move to the next node in the root list
            } while (currentNode != null && currentNode != currentCheck); // Continue until all nodes are processed
            this.minNode = null; // Reset the minimum node reference
            for (int i = 0; i <= maxDegree; ++i) { // Iterate through the degree array
                if (degreeArray[i] != null) { // If a node is present for the degree
                    insert(degreeArray[i]); // Insert the node into the heap
                }
            }
        }
    }

    // Method to cut a node from its parent during certain operations
    public void cut(FibonacciHeapNode node, FibonacciHeapNode parent) {
        if (node.parent != null) { // If the node has a parent
            if (node.right == node) { // If the node is the only child of its parent
                parent.child = null; // Set the child reference of the parent to null
            } else { // If the node has siblings
                // Remove the node from the list of children of its parent
                node.left.right = node.right;
                node.right.left = node.left;
                if (parent.child == node) {
                    parent.child = node.right;
                }
            }

            parent.degree--; // Decrease the degree of the parent

            // Add the node to the list of root nodes
            node.left = minNode;
            node.right = minNode.right;
            minNode.right = node;
            node.right.left = node;

            // Update the minimum node reference if necessary
            if (node.key < minNode.key) {
                minNode = node;
            }

            node.parent = null; // Set the parent reference of the node to null
            node.mark = false; // Reset the mark flag of the node
        }
    }

    // Method to perform cascading cuts to maintain the heap's properties after a node is cut
    private void cascadingCut(FibonacciHeapNode node) {
        FibonacciHeapNode parent = node.parent; // Get the parent of the node
        if (parent != null) { // If the node has a parent
            if (!node.mark) { // If the node is not marked
                node.mark = true; // Mark the node
            } else { // If the node is marked
                cut(node, parent); // Cut the node from its parent
                cascadingCut(parent); // Perform cascading cuts on the parent
            }
        }
    }

    // Method to decrease the key of a node
    public void decreaseKey(FibonacciHeapNode node, int newKey) {
        if (node != null && newKey < node.key) { // If the new key is less than the current key
            node.key = newKey; // Update the key of the node
            FibonacciHeapNode parent = node.parent; // Get the parent of the node

            if (parent != null && node.key < parent.key) { // If the node violates the heap property
                cut(node, parent); // Cut the node from its parent
                cascadingCut(parent); // Perform cascading cuts on the parent
            }

            if (node.key < minNode.key) { // Update the minimum node reference if necessary
                minNode = node;
            }
        }
    }

    // Method to increase the key of a node
    public void increaseKey(FibonacciHeapNode node, int newKey) {
        if (node != null && newKey > node.key) { // If the new key is greater than the current key
            node.key = newKey; // Update the key of the node
            FibonacciHeapNode child = node.child; // Get the child of the node

            if (child != null) { // If the node has children
                do {
                    if (child.key > node.key) { // If a child's key is greater than the node's key
                        cut(child, node); // Cut the child from the node
                        cascadingCut(node); // Perform cascading cuts on the node
                    }
                    child = child.right; // Move to the next child
                } while (child != node.child); // Continue until all children are processed
            }

            if (node.key > minNode.key) { // If the node's key is greater than the minimum node's key
                if (node == minNode) { // If the node is the minimum node
                    // Find the next minimum node
                    FibonacciHeapNode nextMin = minNode.right;
                    while (nextMin != minNode) {
                        if (nextMin.key < minNode.key) {
                            minNode = nextMin;
                        }
                        nextMin = nextMin.right;
                    }
                }
            }
        }
    }

    // Method to delete a node from the heap
    public void delete(FibonacciHeapNode nodeToDelete) {
        if (nodeToDelete == null)
            return; // Return if the node to delete is null

        if (nodeToDelete == minNode) { // If the node to delete is the minimum node
            extractMin(); // Extract the minimum node
        } else { // If the node to delete is not the minimum node
            if (nodeToDelete.parent != null) { // If the node has a parent
                // Remove the node from its parent's child list
                if (nodeToDelete.parent.child == nodeToDelete) {
                    nodeToDelete.parent.child = nodeToDelete.right;
                }
                if (nodeToDelete.right != null) {
                    nodeToDelete.right.left = nodeToDelete.left;
                }
                if (nodeToDelete.left != null) {
                    nodeToDelete.left.right = nodeToDelete.right;
                }
            }

            // Update the child references of the deleted node's children
            if (nodeToDelete.child != null) {
                FibonacciHeapNode child = nodeToDelete.child;
                do {
                    child.parent = null; // Remove parent reference
                    child = child.right; // Move to the next child
                } while (child != null && child != nodeToDelete.child); // Continue until all children are processed
            }

            numNodes--; // Decrease the number of nodes in the heap
        }
    }

    // Method to merge the current heap with another heap
    public void merge(FibonacciHeap heap) {
        if (heap == null) { // If the other heap is null
            return; // Do nothing and return
        }

        if (minNode == null) { // If the current heap is empty
            minNode = heap.minNode; // Copy the root nodes from the other heap
            numNodes = heap.numNodes; // Update the number of nodes
            return;
        }

        if (heap.minNode != null) { // If both heaps are not empty
            // Connect the root lists of the two heaps
            FibonacciHeapNode thisLast = minNode.left;
            FibonacciHeapNode otherLast = heap.minNode.left;

            thisLast.right = heap.minNode;
            heap.minNode.left = thisLast;

            minNode.left = otherLast;
            otherLast.right = minNode;

            // Update the minimum node reference if necessary
            if (heap.minNode.key < minNode.key) {
                minNode = heap.minNode;
            }

            numNodes += heap.numNodes; // Update the number of nodes
        }
    }

    // Method to link two trees of the same degree by making one the child of the other
    private void link(FibonacciHeapNode smaller, FibonacciHeapNode larger) {
        // Remove smaller from the root list
        smaller.left.right = smaller.right;
        smaller.right.left = smaller.left;

        // Make smaller a child of larger
        smaller.parent = larger;
        if (larger.child == null) { // If larger has no children
            // Set smaller as the only child of larger
            larger.child = smaller;
            smaller.left = smaller;
            smaller.right = smaller;
        } else { // If larger already has children
            // Insert smaller into the list of children of larger
            smaller.left = larger.child;
            smaller.right = larger.child.right;
            larger.child.right = smaller;
            smaller.right.left = smaller;
        }

        // Set smaller as a non-root node
        smaller.mark = false;

        // Increase the degree of larger by 1
        larger.degree++;
    }
}
