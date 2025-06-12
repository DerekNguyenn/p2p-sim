package org.derekn.p2pSim;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Abstract class representing a peer node in the network.
 * Handles chunk ownership, file completeness check, and transfer tracking.
 */
public abstract class PeerNode extends NetworkNode {
    protected Set<Integer> ownedChunks;
    protected double uploadSpeed;
    protected double downloadSpeed;
    protected int totalChunks;
    private final List<Transfer> activeTransfers = new ArrayList<>();

    /**
     * Constructs a new PeerNode.
     * @param id Unique identifier
     * @param x X-coordinate
     * @param y Y-coordinate
     * @param totalChunks Total number of file chunks
     */
    public PeerNode(int id, double x, double y, int totalChunks) {
        super(id, x, y);
        this.totalChunks = totalChunks;
        this.ownedChunks = new HashSet<Integer>();
        this.uploadSpeed = 50 + Math.random() * 100; // 50 - 150 KB/s
        this.downloadSpeed = 100 + Math.random() * 200; // 50 - 150 KB/s
    }

    /**
     * Checks if this peer has a specific chunk.
     * @param chunkIndex Index of the chunk
     * @return true if the chunk is present
     */
    public boolean hasChunk(int chunkIndex) {
        return ownedChunks.contains(chunkIndex);
    }

    /**
     * Checks if the peer has downloaded the complete file.
     * @return true if all chunks are owned
     */
    public boolean hasCompleteFile() {
        return ownedChunks.size() == totalChunks;
    }

    /**
     * Adds a chunk to the set of owned chunks.
     * @param chunkIndex Index of the chunk to receive
     */
    public void receiveChunk(int chunkIndex) {
        ownedChunks.add(chunkIndex);
    }

    /**
     * Gets the set of owned chunks.
     * @return Set of integers representing owned chunk indices
     */
    public Set<Integer> getOwnedChunks() {
        return ownedChunks;
    }

    /**
     * Gets the set of missing chunks.
     * @return Set of chunk indices not yet downloaded
     */
    public Set<Integer> getMissingChunks() {
        Set<Integer> missing = new HashSet<>();
        for (int i = 0; i < totalChunks; i++) {
            if (!ownedChunks.contains(i)) {
                missing.add(i);
            }
        }
        return missing;
    }

    public double getUploadSpeed() {
        return uploadSpeed;
    }

    public double getDownloadSpeed() {
        return downloadSpeed;
    }

    /**
     * Gets the list of active transfers this peer is participating in.
     * @return List of active transfers
     */
    public List<Transfer> getActiveTransfers() {
        return activeTransfers;
    }

    /**
     * Adds a transfer to the list of active transfers.
     * @param t Transfer instance to add
     */
    public void addTransfer(Transfer t) {
        activeTransfers.add(t);
    }

    /**
     * Clears all active transfers.
     */
    public void clearTransfers() {
        activeTransfers.clear();
    }

    /**
     * Gets the node type.
     * @return String representing the node type
     */
    public abstract String getNodeType();
}
