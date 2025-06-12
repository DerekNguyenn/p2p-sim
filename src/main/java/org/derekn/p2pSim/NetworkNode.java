package org.derekn.p2pSim;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for all network nodes.
 * Manages node position and connectivity to other nodes.
 */
public abstract class NetworkNode {
    protected final int id;
    protected double x;
    protected double y;
    protected List<NetworkNode> connections;
    protected boolean canDisconnect = true;

    /**
     * Constructs a new NetworkNode.
     * @param id Unique identifier
     * @param x X-coordinate
     * @param y Y-coordinate
     */
    public NetworkNode(int id, double x, double y) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.connections = new ArrayList<NetworkNode>();
    }

    /**
     * Gets the node ID.
     * @return Integer ID
     */
    public int getId() {
        return id;
    }

    /**
     * Connects this node to another node.
     * @param otherNode Other node to connect to
     */
    public void connectTo(NetworkNode otherNode) {
        if (!connections.contains(otherNode)) {
            connections.add(otherNode);
        }
        if (!otherNode.connections.contains(this)) {
            otherNode.connections.add(this);
        }
    }

    /**
     * Checks if this node is allowed to disconnect.
     * @return true if can disconnect, false otherwise
     */
    public boolean canDisconnect() { return canDisconnect; }

    /**
     * Sets whether this node can disconnect.
     * @param canDisconnect Boolean flag
     */
    public void setCanDisconnect(boolean canDisconnect) { this.canDisconnect = canDisconnect; }

    /**
     * Disconnects this node from another node.
     * @param otherNode Node to disconnect from
     */
    public void disconnectFrom(NetworkNode otherNode) {
        connections.remove(otherNode);
        otherNode.connections.remove(this);
    }

    /**
     * Gets a list of connections to other nodes.
     * @return List of connected nodes
     */
    public List<NetworkNode> getConnections() {
        return connections;
    }

    public double getX() { return x; }
    public double getY() { return y; }

    /**
     * Gets the node type.
     * @return String representation of the node type
     */
    public abstract String getNodeType();
}
