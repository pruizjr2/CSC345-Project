import java.util.ArrayList;
import java.util.List;

class FibonacciHeapNode {
    int key;
    int value;
    FibonacciHeapNode parent;
    FibonacciHeapNode child;
    FibonacciHeapNode left;
    FibonacciHeapNode right;
    int degree;
    boolean mark;

    public FibonacciHeapNode(int value) {
        this.key = value;
        this.value = value;
        this.parent = null;
        this.child = null;
        this.left = this;
        this.right = this;
        this.degree = 0;
        this.mark = false;
    }

    @Override
    public String toString() {
        String parentKey = (parent != null) ? String.valueOf(parent.key) : "null";
        String childKey = (child != null) ? String.valueOf(child.key) : "null";
        String leftKey = (left != null) ? String.valueOf(left.key) : "null";
        String rightKey = (right != null) ? String.valueOf(right.key) : "null";

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

public class FibonacciHeap {
    private FibonacciHeapNode minNode;
    private int numNodes;

    public FibonacciHeap() {
        this.minNode = null;
        this.numNodes = 0;
    }

    public void clearHeap(){
        minNode = null;
        numNodes = 0;
    }

    // Insert a new node into the heap
    public void insert(FibonacciHeapNode node) {
        if (minNode == null) {
            minNode = node;
        } else {
            // Add a new node to the list of roots
            node.left = minNode;
            node.right = minNode.right;
            minNode.right = node;
            node.right.left = node;

            // Check and update the minimum node reference if necessary
            if (node.key < minNode.key) {
                minNode = node;
            }
        }
        numNodes++;
    }

    // Return the minimum node in the heap
    public FibonacciHeapNode minimum() {
        return minNode;
    }

    public FibonacciHeapNode extractMin() {
        FibonacciHeapNode min = this.minNode;
        if (min != null) {
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
            min.left.right = min.right;
            min.right.left = min.left;
            min.child = null;
            if (min == min.right)
                this.minNode = null;
            else {
                this.minNode = min.right;
                this.consolidate();
            }
            this.numNodes -= 1;
            return min;
        }
        return null;
    }

    public void consolidate() {
        double goldenRatio = (1 + Math.sqrt(5)) / 2;
        int maxDegree = (int) (Math.log(this.numNodes) / Math.log(goldenRatio));
        FibonacciHeapNode[] degreeArray = new FibonacciHeapNode[maxDegree + 1];
        for (int i = 0; i <= maxDegree; ++i)
            degreeArray[i] = null;
        FibonacciHeapNode currentNode = minNode;
        if (currentNode != null) {
            FibonacciHeapNode currentCheck = minNode;
            do {
                FibonacciHeapNode tempNode = currentNode;
                int degree = tempNode.degree;
                while (degreeArray[degree] != null) {
                    FibonacciHeapNode otherNode = degreeArray[degree];
                    if (tempNode.key > otherNode.key) {
                        FibonacciHeapNode temp = tempNode;
                        tempNode = otherNode;
                        otherNode = temp;
                        currentNode = tempNode;
                    }
                    link(otherNode, tempNode);
                    currentCheck = tempNode;
                    degreeArray[degree] = null;
                    degree += 1;
                }
                degreeArray[degree] = tempNode;
                currentNode = currentNode.right;
            } while (currentNode != null && currentNode != currentCheck);
            this.minNode = null;
            for (int i = 0; i <= maxDegree; ++i) {
                if (degreeArray[i] != null) {
                    insert(degreeArray[i]);
                }
            }
        }
    }





    public void cut(FibonacciHeapNode node, FibonacciHeapNode parent) {
        // Check if the node is a parent
        if (node.parent != null) {
            // Remove a node from the list of children of its parent
            if (node.right == node) {
                parent.child = null;
            } else {
                node.left.right = node.right;
                node.right.left = node.left;
                if (parent.child == node) {
                    parent.child = node.right;
                }
            }

            // Decrease the degree of the parent
            parent.degree--;

            // Add a node to the list of root nodes
            node.left = minNode;
            node.right = minNode.right;
            minNode.right = node;
            node.right.left = node;

            // Update the link to the minimal node if necessary
            if (node.key < minNode.key) {
                minNode = node;
            }

            // Set a new parent for the node
            node.parent = null;

            // Set the cut node flag
            node.mark = false;
        }
    }

    private void cascadingCut(FibonacciHeapNode node) {
        FibonacciHeapNode parent = node.parent;
        if (parent != null) {
            if (!node.mark) {
                node.mark = true;
            } else {
                cut(node, parent);
                cascadingCut(parent);
            }
        }
    }

    public void decreaseKey(FibonacciHeapNode node) {
        decreaseKey(node, node.key - 1);
    }

    public void increaseKey(FibonacciHeapNode node) {
        increaseKey(node, node.key + 1);
    }

    public void decreaseKey(FibonacciHeapNode node, int newKey) {
        if (node != null && newKey < node.key) {
            node.key = newKey;
            FibonacciHeapNode parent = node.parent;

            if (parent != null && node.key < parent.key) {
                cut(node, parent);
                cascadingCut(parent);
            }

            if (node.key < minNode.key) {
                minNode = node;
            }
        }
    }

    public void increaseKey(FibonacciHeapNode node, int newKey) {
        if (node != null && newKey > node.key) {
            node.key = newKey;
            FibonacciHeapNode child = node.child;

            if (child != null) {
                do {
                    if (child.key > node.key) {
                        cut(child, node);
                        cascadingCut(node);
                    }
                    child = child.right;
                } while (child != node.child);
            }

            if (node.key > minNode.key) {
                if (node == minNode) {
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

    public void delete(FibonacciHeapNode nodeToDelete) {
        if (nodeToDelete == null)
            return;

        if (nodeToDelete == minNode) {
            extractMin();
        } else {
            // Remove the node from its parent's child list
            if (nodeToDelete.parent != null) {
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
                    child = child.right;
                } while (child != null && child != nodeToDelete.child);
            }

            // Decrease the number of nodes in the heap
            numNodes--;
        }
    }

    // Merge the current heap with another heap
    public void merge(FibonacciHeap heap) {
        if (heap == null) {
            return;
        }

        // If the current heap is empty, simply copy the root nodes from the other heap
        if (minNode == null) {
            minNode = heap.minNode;
            numNodes = heap.numNodes;
            return;
        }

        // If both heaps are not empty, merge their root lists
        if (heap.minNode != null) {
            // Connect the root lists of the two heaps
            FibonacciHeapNode thisLast = minNode.left;
            FibonacciHeapNode otherLast = heap.minNode.left;

            thisLast.right = heap.minNode;
            heap.minNode.left = thisLast;

            minNode.left = otherLast;
            otherLast.right = minNode;

            // Update the minimum node if necessary
            if (heap.minNode.key < minNode.key) {
                minNode = heap.minNode;
            }

            // Update the number of nodes
            numNodes += heap.numNodes;
        }
    }

    // Link two trees of the same degree by making one the child of the other
    private void link(FibonacciHeapNode smaller, FibonacciHeapNode larger) {
        // Remove smaller from the root list
        smaller.left.right = smaller.right;
        smaller.right.left = smaller.left;

        // Make smaller a child of larger
        smaller.parent = larger;
        if (larger.child == null) {
            larger.child = smaller;
            smaller.left = smaller;
            smaller.right = smaller;
        } else {
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
