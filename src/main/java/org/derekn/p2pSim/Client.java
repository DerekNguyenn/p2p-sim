package org.derekn.p2pSim;

public class Client extends Leecher {
    public Client(int id, double x, double y, int totalChunks) {
        super(id, x, y, totalChunks);
    }

    @Override
    public String getNodeType() {
        return "Client";
    }
}
