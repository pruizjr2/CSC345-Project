# Fibonacci Heap Implementation in Java

This repository contains a Java implementation of a Fibonacci Heap data structure, along with various operations such as insertion, extraction of minimum element, and decrease/increase key operations.

## Overview

A Fibonacci Heap is a data structure that supports the operations of a priority queue. It consists of a collection of trees that are min-heap ordered. Each node in the heap has a key value and a reference to a child, parent, left sibling, right sibling, and degree. The heap is called Fibonacci because each tree in the collection obeys the heap property and the number of children of each node in the heap at level i is at most F(i), the ith Fibonacci number.

## Features

- Insertion: Inserts a new node into the heap.
- Minimum: Returns the minimum node in the heap without removing it.
- Extract Min: Removes and returns the minimum node from the heap.
- Decrease Key: Decreases the key value of a node to a specified new value.
- Increase Key: Increases the key value of a node to a specified new value.
- Delete: Deletes a specified node from the heap.
- Merge: Merges the current heap with another heap.

## Usage

To use this implementation, follow these steps:

1. Import the `FibonacciHeap` and `FibonacciHeapNode` classes into your Java project.
2. Create an instance of `FibonacciHeap` to work with.
3. Use the provided methods to perform operations on the heap.

Example:
```java
// Create a new Fibonacci Heap
FibonacciHeap heap = new FibonacciHeap();

// Insert nodes into the heap
FibonacciHeapNode node1 = new FibonacciHeapNode(10);
heap.insert(node1);

FibonacciHeapNode node2 = new FibonacciHeapNode(20);
heap.insert(node2);

// Extract the minimum node from the heap
FibonacciHeapNode minNode = heap.extractMin();
System.out.println("Minimum node: " + minNode.key);
