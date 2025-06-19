package org.derekn.p2pSim;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Abstract class representing a peer node in the network.
 * Each peer can own file chunks and participate in data transfers.
 * Handles logic for chunk management and active transfer tracking.
 */
public abstract class PeerNode extends NetworkNode {
    protected Set<Integer> ownedChunks; // Chunks currently owned by this peer
    protected double uploadSpeed;       // Upload speed in KB/s
    protected double downloadSpeed;     // Download speed in KB/s
    protected int totalChunks;          // Total chunks needed to complete the file
    private final List<Transfer> activeTransfers = new ArrayList<>(); // Currently active transfers

    /**
     * Constructs a new PeerNode instance with specified properties.
     * Random upload and download speeds simulate heterogeneous peers.
     *
     * @param id Unique identifier for the peer
     * @param x X-coordinate of the peer
     * @param y Y-coordinate of the peer
     * @param totalChunks Total number of file chunks to be downloaded
     */
    public PeerNode(int id, double x, double y, int totalChunks) {
        super(id, x, y);
        this.totalChunks = totalChunks;
        this.ownedChunks = new HashSet<>();

        // Randomized upload/download speeds for simulation realism
        this.uploadSpeed = 50 + Math.random() * 100;     // Upload speed: 50–150 KB/s
        this.downloadSpeed = 100 + Math.random() * 200;  // Download speed: 100–300 KB/s
    }

    /**
     * Checks if the peer already owns a specific chunk.
     *
     * @param chunkIndex Index of the chunk
     * @return true if the chunk is present; false otherwise
     */
    public boolean hasChunk(int chunkIndex) {
        return ownedChunks.contains(chunkIndex);
    }

    /**
     * Determines whether the peer has completed downloading all file chunks.
     *
     * @return true if all chunks are owned; false otherwise
     */
    public boolean hasCompleteFile() {
        return ownedChunks.size() == totalChunks;
    }

    /**
     * Adds a chunk to the list of owned chunks.
     *
     * @param chunkIndex Index of the chunk to add
     */
    public void receiveChunk(int chunkIndex) {
        ownedChunks.add(chunkIndex);
    }

    /**
     * Retrieves the set of chunks this peer owns.
     *
     * @return Set of chunk indices
     */
    public Set<Integer> getOwnedChunks() {
        return ownedChunks;
    }

    /**
     * Identifies all file chunks that this peer still needs.
     *
     * @return Set of missing chunk indices
     */
    public Set<Integer> getMissingChunks() {
        Set<Integer> missing = new HashSet<>();
        for (int i = 0; i < totalChunks; i++) {
            if (!ownedChunks.contains(i)) {
                missing.add(i); // Add chunk if it's not owned
            }
        }
        return missing;
    }

    /**
     * Gets the upload speed of the peer.
     *
     * @return Upload speed in KB/s
     */
    public double getUploadSpeed() {
        return uploadSpeed;
    }

    /**
     * Gets the download speed of the peer.
     *
     * @return Download speed in KB/s
     */
    public double getDownloadSpeed() {
        return downloadSpeed;
    }

    /**
     * Returns a list of current active transfers involving this peer.
     *
     * @return List of active transfers
     */
    public List<Transfer> getActiveTransfers() {
        return activeTransfers;
    }

    /**
     * Adds a transfer to this peer's active transfer list.
     *
     * @param t Transfer to track
     */
    public void addTransfer(Transfer t) {
        activeTransfers.add(t);
    }

    /**
     * Clears all active transfer records from this peer.
     * Called at the end of a simulation tick.
     */
    public void clearTransfers() {
        activeTransfers.clear();
    }

    /**
     * @return Node type as a string
     */
    public abstract String getNodeType();
}
