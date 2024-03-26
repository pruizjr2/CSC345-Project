import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class FibonacciHeapGUI extends Application {

    private final int WINDOW_WIDTH = 800;
    private final int WINDOW_HEIGHT = 600;

    private FibonacciHeap heap;
    private Map<FibonacciHeapNode, Circle> nodeCircleMap;

    @Override
    public void start(Stage primaryStage) {
        // Create the root pane
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        // Create the heap visualization pane
        Pane heapPane = new Pane();
        heapPane.setPrefSize(600, 400);
        heapPane.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        // Create a label for displaying information
        Label infoLabel = new Label("Fibonacci Heap Visualization");
        infoLabel.setStyle("-fx-font-weight: bold;");

        // Create buttons for heap operations
        Button insertButton = new Button("Insert");
        Button extractMinButton = new Button("Extract Min");
        Button decreaseKeyButton = new Button("Decrease Key");

        // Set button actions
        insertButton.setOnAction(e -> insertOperation());
        extractMinButton.setOnAction(e -> extractMinOperation());
        decreaseKeyButton.setOnAction(e -> decreaseKeyOperation());

        // Create a VBox for buttons
        VBox buttonBox = new VBox(10, insertButton, extractMinButton, decreaseKeyButton);
        buttonBox.setPadding(new Insets(10));
        buttonBox.setStyle("-fx-background-color: #f0f0f0;");

        // Add components to the root pane
        root.setTop(infoLabel);
        root.setCenter(heapPane);
        root.setRight(buttonBox);

        // Set up the scene
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);

        // Set up the stage
        primaryStage.setTitle("Fibonacci Heap Visualization");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Initialize the heap and node-circle map
        heap = new FibonacciHeap();
        nodeCircleMap = new HashMap<>();
    }

    private void insertOperation() {
        // Generate a random key and value for the new node
        int key = (int) (Math.random() * 100);
        int value = (int) (Math.random() * 100);

        // Insert the node into the heap
        FibonacciHeapNode node = heap.insert(key, value);

        // Visualize the inserted node
        Circle circle = new Circle(20);
        circle.setFill(Color.WHITE);
        circle.setStroke(Color.BLACK);
        circle.setStrokeWidth(2);
        circle.setCenterX(50 + Math.random() * 500);
        circle.setCenterY(50 + Math.random() * 300);
        heapPane.getChildren().add(circle);
        nodeCircleMap.put(node, circle);
    }

    private void extractMinOperation() {
        // Extract the minimum node from the heap
        FibonacciHeapNode minNode = heap.extractMin();

        // Remove the corresponding circle from the visualization
        Circle circle = nodeCircleMap.remove(minNode);
        heapPane.getChildren().remove(circle);
    }

    private void decreaseKeyOperation() {
        // Select a random node from the heap
        FibonacciHeapNode[] nodes = nodeCircleMap.keySet().toArray(new FibonacciHeapNode[0]);
        if (nodes.length == 0) {
            return;
        }
        FibonacciHeapNode node = nodes[(int) (Math.random() * nodes.length)];

        // Decrease the key of the selected node
        heap.decreaseKey(node, node.key - 10);

        // Update the visualization of the node
        Circle circle = nodeCircleMap.get(node);
        circle.setCenterX(circle.getCenterX() - 20);
        circle.setCenterY(circle.getCenterY() - 20);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
