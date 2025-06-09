package org.derekn.p2pSim;

public class Leecher extends PeerNode {
    public Leecher(int id, double x, double y, int totalChunks) {
        super(id, x, y, totalChunks);
    }

    public String getNodeType() {
        return "Leecher";
    }

    public void downloadFrom(PeerNode peer) {
        for (int chunk : peer.getOwnedChunks()) {
            if (!this.ownedChunks.contains(chunk)) {
                this.receiveChunk(chunk);
                break; // One chunk per tick
            }
        }
    }
}
