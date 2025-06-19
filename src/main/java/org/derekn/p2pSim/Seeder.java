package org.derekn.p2pSim;

/**
 * Represents a peer node that starts with the full file and only uploads chunks.
 */
public class Seeder extends PeerNode {
    /**
     * Constructs a new Seeder.
     * @param id Unique identifier
     * @param x X-coordinate
     * @param y Y-coordinate
     * @param totalChunks Total number of file chunks
     */
    public Seeder(int id, double x, double y, int totalChunks) {
        super(id, x, y, totalChunks);

        for (int i = 0; i < totalChunks; i++) {
            ownedChunks.add(i);
        }
    }

    /**
     * @return String representing the node type
     */
    public String getNodeType() {
        return "Seeder";
    }
}
