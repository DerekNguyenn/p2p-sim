package org.derekn.p2pSim;

public class Supernode extends PeerNode {
    public Supernode(int id, double x, double y, int totalChunks) {
        super(id, x, y, totalChunks);
        this.uploadSpeed *= 2;
        this.downloadSpeed *= 1.5;
    }

    public String getNodeType() {
        return "Supernode";
    }

    public boolean canAcceptMoreConnections() {
        return this.connections.size() < 12;
    }
}
