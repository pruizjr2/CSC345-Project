package org.example.fibonachyheap.project;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

    public class FibonacciHeapGUI extends Application {

        private final int WINDOW_WIDTH = 800;
        private final int WINDOW_HEIGHT = 600;

        private FibonacciHeap heap;
        private Map<FibonacciHeapNode, Circle> nodeCircleMap;
        private Circle selectedCircle;

        private Pane headPane;

        @Override
        public void start(Stage primaryStage) {
            // Create the root pane
            BorderPane root = new BorderPane();
            root.setPadding(new Insets(10));

            // Create the heap visualization pane
            headPane = new Pane();
            headPane.setPrefSize(600, 400);
            headPane.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID,
                    CornerRadii.EMPTY, BorderWidths.DEFAULT)));

            // Create a label for displaying information
            Label infoLabel = new Label("Fibonacci Heap Visualization");
            infoLabel.setStyle("-fx-font-weight: bold;");

            // Create a TextField for entering the value of the point
            TextField valueField = new TextField();
            valueField.setPromptText("Enter value");

            // Create buttons for heap operations
            Button insertButton = new Button("Insert");
            Button extractMinButton = new Button("Extract Minimum");
            Button decreaseKeyButton = new Button("Decrease Key");
            Button increaseKeyButton = new Button("Increase Key");
            Button deleteButton = new Button("Delete");
            Button clearButton = new Button("Clear");

            // Set button actions
            insertButton.setOnAction(e -> insertOperation(Integer.parseInt(valueField.getText())));
            extractMinButton.setOnAction(e -> extractMinOperation());
            decreaseKeyButton.setOnAction(e -> decreaseKeyOperation());
            increaseKeyButton.setOnAction(e -> increaseKeyOperation());
            deleteButton.setOnAction(e -> deleteOperation());
            clearButton.setOnAction(e -> clearOperation());

            // Create a VBox for buttons and value input field
            VBox buttonBox = new VBox(10, insertButton, valueField, extractMinButton, decreaseKeyButton, increaseKeyButton, deleteButton,  clearButton);
            buttonBox.setPadding(new Insets(10));
            buttonBox.setStyle("-fx-background-color: #f0f0f0;");

            // Add components to the root pane
            root.setTop(infoLabel);
            root.setCenter(headPane);
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

            // Add event listener to select a node on click
            headPane.setOnMouseClicked(this::handleNodeSelection);
        }

        private void insertOperation(int value) {
            // Create a new node with the given key and value
            FibonacciHeapNode newNode = new FibonacciHeapNode(value);

            // Insert a new node into the heap
            heap.insert(newNode);

            // Render the inserted node
            Circle circle = new Circle(20);
            circle.setFill(Color.WHITE);
            circle.setStroke(Color.BLACK);
            circle.setStrokeWidth(2);

            headPane.getChildren().add(circle);
            nodeCircleMap.put(newNode, circle);

            // Update the visualization
            updateVisualization();
        }

        private void extractMinOperation() {
            removeAllLines();

            FibonacciHeapNode minNode = heap.extractMin();
            Circle minCircle = nodeCircleMap.get(minNode);
            headPane.getChildren().remove(minCircle);
            nodeCircleMap.remove(minNode);
            updateVisualization();

        }

        private void decreaseKeyOperation() {
            // Decrease the key of the selected node
            if (selectedCircle != null) {
                for (Map.Entry<FibonacciHeapNode, Circle> entry : nodeCircleMap.entrySet()) {
                    if (entry.getValue() == selectedCircle) {
                        heap.decreaseKey(entry.getKey());
                        updateVisualization();
                        break;
                    }
                }
            }
        }

        private void increaseKeyOperation() {
            // Increase the key of the selected node
            if (selectedCircle != null) {
                for (Map.Entry<FibonacciHeapNode, Circle> entry : nodeCircleMap.entrySet()) {
                    if (entry.getValue() == selectedCircle) {
                        heap.increaseKey(entry.getKey());
                        updateVisualization();
                        break;
                    }
                }
            }
        }

        private void deleteOperation() {
            // Delete the selected node
            if (selectedCircle != null) {
                for (Map.Entry<FibonacciHeapNode, Circle> entry : nodeCircleMap.entrySet()) {
                    if (entry.getValue() == selectedCircle) {
                        heap.delete(entry.getKey());
                        headPane.getChildren().remove(selectedCircle);
                        nodeCircleMap.remove(entry.getKey());

                        removeAllLines();
                        updateVisualization();

                        selectedCircle = null;
                        break;
                    }
                    updateVisualization();
                }
            }
            updateVisualization();
        }

        private void clearOperation(){
            heap.clearHeap();
            nodeCircleMap.clear();
            headPane.getChildren().clear();
            updateVisualization();
        }

        private void handleNodeSelection(MouseEvent event) {
            // Reset the previous selection
            clearNodeSelection();

            // Check if the click falls into any circle
            for (Map.Entry<FibonacciHeapNode, Circle> entry : nodeCircleMap.entrySet()) {
                Circle circle = entry.getValue();
                if (isPointInsideCircle(event.getX(), event.getY(), circle)) {
                    // Если клик попал в круг, выделяем его
                    circle.setFill(Color.color(0, 1, 0, 0.5)); // Semi-transparent green fill
                    selectedCircle = circle;
                    break;
                }
            }
        }

        private void clearNodeSelection() {
            // Reset the selection of all circles
            for (Circle circle : nodeCircleMap.values()) {
                circle.setFill(Color.WHITE); // Reset the circle fill to white
            }
        }

        private boolean isPointInsideCircle(double x, double y, Circle circle) {
            double centerX = circle.getCenterX();
            double centerY = circle.getCenterY();
            double radius = circle.getRadius();
            return Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2) <= Math.pow(radius, 2);
        }

        private void updateVisualization() {
            List<Node> toRemove = new ArrayList<>();
            for (Node node : headPane.getChildren()) {
                if (node instanceof Text) {
                    toRemove.add(node);
                }
            }
            headPane.getChildren().removeAll(toRemove);
            double radius = 20; // Node radius

            int numNodes = nodeCircleMap.size(); // Total number of nodes
            int numCols = (int) Math.ceil(Math.sqrt(numNodes)); // Number of columns
            int numRows = (int) Math.ceil((double) numNodes / numCols); // Number of lines

            double cellWidth = headPane.getPrefWidth() / numCols; // Cell width
            double cellHeight = headPane.getPrefHeight() / numRows; // Cell height

            int i = 0; // Node index

            // We go through all the nodes and update their position on the panel
            for (Map.Entry<FibonacciHeapNode, Circle> entry : nodeCircleMap.entrySet()) {
                FibonacciHeapNode node = entry.getKey();
                Circle circle = entry.getValue();

                // Calculate the coordinates of the node according to its position in the grid
                int col = i % numCols;
                int row = i / numCols;

                double centerX = cellWidth * (col + 0.5);
                double centerY = cellHeight * (row + 0.5);

                circle.setCenterX(centerX);
                circle.setCenterY(centerY);

                // Add text with the value inside the circle
                Text text = new Text(String.valueOf(node.key));
                text.setX(centerX - text.getLayoutBounds().getWidth() / 2);
                text.setY(centerY + text.getLayoutBounds().getHeight() / 4);
                headPane.getChildren().add(text);

                // If the node is minimal, outline it in red
                if (node == heap.minimum()) {
                    circle.setStroke(Color.RED);
                } else {
                    circle.setStroke(Color.BLACK);
                }

                i++;
            }

            // We go through all the nodes and update the connections between them
            for (Map.Entry<FibonacciHeapNode, Circle> entry : nodeCircleMap.entrySet()) {
                FibonacciHeapNode node = entry.getKey();
                Circle circle = entry.getValue();
                Circle parentCircle = nodeCircleMap.get(node.parent);
                // Визуализируем связь между узлом и его родителем (если есть)
                if (parentCircle != null && node.parent != null) {
                    Line line = new Line();
                    line.setStartX(parentCircle.getCenterX());
                    line.setStartY(parentCircle.getCenterY());
                    line.setEndX(circle.getCenterX());
                    line.setEndY(circle.getCenterY());
                    headPane.getChildren().add(line);
                }

                // Визуализируем связи между узлом и его детьми (если есть)
                FibonacciHeapNode child = node.child;
                while (child != null) {
                    Circle childCircle = nodeCircleMap.get(child);
                    if (childCircle != null) {
                        Line line = new Line();
                        line.setStartX(circle.getCenterX());
                        line.setStartY(circle.getCenterY());
                        line.setEndX(childCircle.getCenterX());
                        line.setEndY(childCircle.getCenterY());
                        headPane.getChildren().add(line);
                    }
                    child = child.right;
                    if (child == node.child) {
                        break;
                    }
                }
            }
        }

        private void removeAllLines() {
            List<Node> linesToRemove = new ArrayList<>();
            for (Node node : headPane.getChildren()) {
                if (node instanceof Line) {
                    linesToRemove.add(node);
                }
            }
            headPane.getChildren().removeAll(linesToRemove);
        }


    public static void main(String[] args) {
        launch(args);
    }
}