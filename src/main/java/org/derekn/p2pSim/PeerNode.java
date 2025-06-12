package org.derekn.p2pSim;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class PeerNode extends NetworkNode {
    protected Set<Integer> ownedChunks;
    protected double uploadSpeed;
    protected double downloadSpeed;
    protected int totalChunks;
    private final List<Transfer> activeTransfers = new ArrayList<>();

    public PeerNode(int id, double x, double y, int totalChunks) {
        super(id, x, y);
        this.totalChunks = totalChunks;
        this.ownedChunks = new HashSet<Integer>();
        this.uploadSpeed = 50 + Math.random() * 100; // 50 - 150 KB/s
        this.downloadSpeed = 100 + Math.random() * 200; // 50 - 150 KB/s
    }

    public boolean hasChunk(int chunkIndex) {
        return ownedChunks.contains(chunkIndex);
    }

    public boolean hasCompleteFile() {
        return ownedChunks.size() == totalChunks;
    }

    public void receiveChunk(int chunkIndex) {
        ownedChunks.add(chunkIndex);
    }

    public Set<Integer> getOwnedChunks() {
        return ownedChunks;
    }

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

    public List<Transfer> getActiveTransfers() {
        return activeTransfers;
    }

    public void addTransfer(Transfer t) {
        activeTransfers.add(t);
    }

    public void clearTransfers() {
        activeTransfers.clear();
    }

    public abstract String getNodeType();
}
