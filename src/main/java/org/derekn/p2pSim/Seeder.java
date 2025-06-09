package org.derekn.p2pSim;

public class Seeder extends PeerNode {
    public Seeder(int id, double x, double y, int totalChunks) {
        super(id, x, y, totalChunks);

        for (int i = 0; i < totalChunks; i++) {
            ownedChunks.add(i);
        }
    }

    public String getNodeType() {
        return "Seeder";
    }
}
