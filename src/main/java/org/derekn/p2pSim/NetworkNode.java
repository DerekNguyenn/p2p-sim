package org.derekn.p2pSim;

import java.util.ArrayList;
import java.util.List;

public abstract class NetworkNode {
    protected final int id;
    protected double x;
    protected double y;
    protected List<NetworkNode> connections;
    protected boolean canDisconnect = true;

    public NetworkNode(int id, double x, double y) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.connections = new ArrayList<NetworkNode>();
    }

    public int getId() {
        return id;
    }

    public void connectTo(NetworkNode otherNode) {
        if (!connections.contains(otherNode)) {
            connections.add(otherNode);
        }
        if (!otherNode.connections.contains(this)) {
            otherNode.connections.add(this);
        }
    }

    public boolean canDisconnect() { return canDisconnect; }

    public void setCanDisconnect(boolean canDisconnect) { this.canDisconnect = canDisconnect; }

    public void disconnectFrom(NetworkNode otherNode) {
        connections.remove(otherNode);
        otherNode.connections.remove(this);
    }

    public List<NetworkNode> getConnections() {
        return connections;
    }

    public double getX() { return x; }
    public double getY() { return y; }

    public abstract String getNodeType();
}
