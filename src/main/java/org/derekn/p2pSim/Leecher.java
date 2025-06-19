package org.derekn.p2pSim;

/**
 * Represents a peer in the network that downloads files but doesn't initially possess
 * all chunks.
 */
public class Leecher extends PeerNode {
    /**
     * Constructs a new Leecher.
     * @param id Unique identifier
     * @param x x-coordinate
     * @param y y-coordinate
     * @param totalChunks Total number of chunks in the file
     */
    public Leecher(int id, double x, double y, int totalChunks) {
        super(id, x, y, totalChunks);
    }

    /**
     * @return String representing the node type
     */
    public String getNodeType() {
        return "Leecher";
    }

    /**
     * Downloads one missing chunk from the provided peer if available.
     * @param peer Source peer node
     * @return true if a chunk was downloaded, false otherwise
     */
    public boolean downloadFrom(PeerNode peer) {
        for (int chunk : peer.getOwnedChunks()) {
            if (!this.ownedChunks.contains(chunk)) {
                this.receiveChunk(chunk);
                return true; // One chunk per tick
            }
        }
        return false;
    }
}
