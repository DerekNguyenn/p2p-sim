package org.derekn.p2pSim;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class representing a node in a network.
 * Nodes can be connected to each other and contain positional data.
 * Useful for representing peers in a P2P network simulation.
 */
public abstract class NetworkNode {
    protected final int id; // Unique ID assigned to each node
    protected double x;     // X-coordinate of the node in 2D space
    protected double y;     // Y-coordinate of the node in 2D space
    protected List<NetworkNode> connections; // List of nodes this node is connected to
    protected boolean canDisconnect = true;  // Flag to indicate if the node can be disconnected

    /**
     * Constructs a new NetworkNode with specified ID and position.
     *
     * @param id Unique identifier for the node
     * @param x  X-coordinate
     * @param y  Y-coordinate
     */
    public NetworkNode(int id, double x, double y) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.connections = new ArrayList<>(); // Initialize connections list
    }

    /**
     * Retrieves the unique identifier of the node.
     *
     * @return Node ID
     */
    public int getId() {
        return id;
    }

    /**
     * Establishes a bidirectional connection between this node and another.
     * Ensures both nodes list each other in their connection lists.
     *
     * @param otherNode The node to connect to
     */
    public void connectTo(NetworkNode otherNode) {
        if (!connections.contains(otherNode)) {
            connections.add(otherNode); // Add connection if not already present
        }
        if (!otherNode.connections.contains(this)) {
            otherNode.connections.add(this); // Ensure mutual connection
        }
    }

    /**
     * Indicates if the node is currently allowed to disconnect from others.
     *
     * @return true if disconnection is permitted; false otherwise
     */
    public boolean canDisconnect() {
        return canDisconnect;
    }

    /**
     * Updates the disconnection permission flag.
     *
     * @param canDisconnect Whether the node can disconnect
     */
    public void setCanDisconnect(boolean canDisconnect) {
        this.canDisconnect = canDisconnect;
    }

    /**
     * Removes a bidirectional connection between this node and another.
     *
     * @param otherNode The node to disconnect from
     */
    public void disconnectFrom(NetworkNode otherNode) {
        connections.remove(otherNode); // Remove connection from this node
        otherNode.connections.remove(this); // Remove connection from the other node
    }

    /**
     * Retrieves all nodes this node is connected to.
     *
     * @return List of connected nodes
     */
    public List<NetworkNode> getConnections() {
        return connections;
    }

    /**
     * @return X-coordinate of the node
     */
    public double getX() {
        return x;
    }

    /**
     * @return Y-coordinate of the node
     */
    public double getY() {
        return y;
    }

    /**
     * Return a string representing the type of node.
     * Used for distinguishing node roles (e.g., seed, peer).
     *
     * @return Node type description
     */
    public abstract String getNodeType();
}
