package org.derekn.p2pSim;

/**
 * Represents a client node in the P2P network.
 * Inherits behavior from Leecher class but cannot disconnect during simulation.
 */
public class Client extends Leecher {
    /**
     * Constructs a new Client node.
     * @param id Unique identifier
     * @param x x-coordinate
     * @param y y-coordinate
     * @param totalChunks Total number of chunks in the file
     */
    public Client(int id, double x, double y, int totalChunks) {
        super(id, x, y, totalChunks);
        this.canDisconnect = false;
    }

    /**
     * Gets the node type.
     * @return String representing the node type
     */
    @Override
    public String getNodeType() {
        return "Client";
    }
}
