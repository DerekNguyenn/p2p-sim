package com.example.p2pSim;

public class consoleTest {

    public static void main(String[] args) {
        int totalChunks = 10;
        int initialPeers = 6;

        SimulationController controller = new SimulationController(initialPeers, totalChunks);

        System.out.println("Starting P2P simulation with " + initialPeers + " peers.");
        controller.startSimulation();

        int maxTicks = 100;
        for (int tick = 0; tick < maxTicks && controller.isRunning(); tick++) {
            controller.tick();

            PeerNode target = controller.getDownloadTarget();
            System.out.printf("Tick %d: Download progress: %d/%d chunks\n",
                    tick, target.getOwnedChunks().size(), totalChunks);

            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Simulation ended.");
        printFinalReport(controller);
    }

    private static void printFinalReport(SimulationController controller) {
        System.out.println("==== Final Network Snapshot ====");
        for (PeerNode peer : controller.getPeers()) {
            System.out.printf("Peer %d [%s]: %d chunks, %d connections\n",
                    peer.getId(),
                    peer.getNodeType(),
                    peer.getOwnedChunks().size(),
                    peer.getConnections().size());
        }

        PeerNode target = controller.getDownloadTarget();
        System.out.printf("Download target: Peer %d - COMPLETE? %s\n",
                target.getId(),
                target.hasCompleteFile() ? "YES ✅" : "NO ❌");
    }
}
