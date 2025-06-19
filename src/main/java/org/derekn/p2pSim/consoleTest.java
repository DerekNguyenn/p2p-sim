package org.derekn.p2pSim;

/**
 * A simple console test class to run and observe the P2P simulation.
 * This class initializes a simulation with a set number of peers and chunks,
 * and then runs it for a specified number of ticks.
 */
public class consoleTest {
    public static void main(String[] args) {
        int totalChunks = 10; // Total number of chunks to be distributed in the network
        int initialPeers = 6; // Number of peers present in the simulation initially

        // Create a controller to manage simulation logic
        SimulationController controller = new SimulationController(initialPeers, totalChunks);

        System.out.println("Starting P2P simulation with " + initialPeers + " peers.");
        controller.startSimulation(); // Begin the simulation

        int maxTicks = 100; // Maximum number of ticks to simulate

        // Iterate over simulation ticks until maximum or simulation ends
        for (int tick = 0; tick < maxTicks && controller.isRunning(); tick++) {
            controller.tick(); // Progress the simulation by one tick

            PeerNode target = controller.getDownloadTarget(); // Get the main download target peer

            // Display current progress of download target peer
            System.out.printf("Tick %d: Download progress: %d/%d chunks\n",
                    tick, target.getOwnedChunks().size(), totalChunks);

            try {
                Thread.sleep(20); // Delay to simulate real-time progression
            } catch (InterruptedException e) {
                e.printStackTrace(); // Print exception stack trace if interrupted
            }
        }

        System.out.println("Simulation ended.");
        printFinalReport(controller); // Show a snapshot of the final network state
    }

    /**
     * Outputs a detailed report of the final simulation state,
     * including owned chunks and connections of each peer.
     *
     * @param controller the simulation controller managing the state
     */
    private static void printFinalReport(SimulationController controller) {
        System.out.println("==== Final Network Snapshot ====");

        // Iterate through each peer and print their chunk and connection info
        for (PeerNode peer : controller.getPeers()) {
            System.out.printf("Peer %d [%s]: %d chunks, %d connections\n",
                    peer.getId(),
                    peer.getNodeType(),
                    peer.getOwnedChunks().size(),
                    peer.getConnections().size());
        }

        // Display whether the download results
        PeerNode target = controller.getDownloadTarget();
        System.out.printf("Download target: Peer %d - COMPLETE? %s\n",
                target.getId(),
                target.hasCompleteFile() ? "YES ✅" : "NO ❌");
    }
}
