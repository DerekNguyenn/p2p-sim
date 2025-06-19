package org.derekn.p2pSim;

/**
 * Represents a Supernode in the P2P network, a high-capacity peer with enhanced
 * upload and download capabilities. Supernodes are designed to handle more connections
 * and serve as efficient hubs for data distribution.
 */
public class Supernode extends PeerNode {

    /**
     * Constructs a Supernode with enhanced bandwidth characteristics.
     * Upload and download speeds are boosted compared to standard peers.
     *
     * @param id           Unique identifier for the node
     * @param x            X-coordinate for visual placement
     * @param y            Y-coordinate for visual placement
     * @param totalChunks  Total number of chunks in the file
     */
    public Supernode(int id, double x, double y, int totalChunks) {
        super(id, x, y, totalChunks);

        // Supernodes have double upload and 1.5x download speed
        this.uploadSpeed *= 2;
        this.downloadSpeed *= 1.5;
    }

    /**
     * Returns the string identifier for this node type.
     * Used in visualization and logging.
     *
     * @return "Supernode"
     */
    @Override
    public String getNodeType() {
        return "Supernode";
    }

    /**
     * Determines if the Supernode can accept more peer connections.
     * This limit is higher than regular peers, supporting up to 12 connections.
     *
     * @return true if connections are below 12; false otherwise
     */
    public boolean canAcceptMoreConnections() {
        return this.connections.size() < 12;
    }
}
