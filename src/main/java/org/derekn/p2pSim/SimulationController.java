package org.derekn.p2pSim;

import java.util.*;

/**
 * Controls the overall simulation of the P2P file-sharing network.
 * Manages peer creation, chunk transfers, and simulation ticks.
 */
public class SimulationController {
    private List<PeerNode> allPeers; // All peers in the simulation
    private int totalChunks; // Total number of chunks to be downloaded
    private PeerNode downloadTarget; // The peer we are tracking for completion
    private boolean simulationRunning; // Flag to control simulation state
    private int tickCount; // Number of ticks since simulation start
    private int ticksSinceLastProgress = 0; // Ticks since last successful download
    private int lastChunkCount = 0; // Last known chunk count for stall detection
    public final int stallThreshold; // Number of idle ticks before detecting stall

    /**
     * Constructs a SimulationController and initializes peers.
     * @param initialPeers Number of peers to start with
     * @param totalChunks Total number of file chunks in simulation
     */
    public SimulationController(int initialPeers, int totalChunks) {
        this.totalChunks = totalChunks;
        this.stallThreshold = Math.max(10, totalChunks / 4); // Define stalling condition
        this.allPeers = new ArrayList<>();
        this.simulationRunning = false;
        this.tickCount = 0;

        createInitialPeers(initialPeers);
    }

    /**
     * Initializes a swarm of peers including a download target and seeders.
     */
    private void createInitialPeers(int count) {
        for (int i = 0; i < count; i++) {
            double x = randomX();
            double y = randomY();

            PeerNode peer;

            if (i == 0) {
                // Designate the first node as the Client (download target)
                peer = new Client(i, x, y, totalChunks);
                this.downloadTarget = peer;
            } else if (i == 1) {
                // Ensure at least one Seeder exists
                peer = new Seeder(i, x, y, totalChunks);
            } else {
                double r = Math.random();
                if (r < 0.2) {
                    peer = new Supernode(i, x, y, totalChunks);
                } else if (r < 0.5) {
                    peer = new Seeder(i, x, y, totalChunks);
                } else {
                    peer = new Leecher(i, x, y, totalChunks);
                }
            }

            System.out.printf("Created Peer %d: %s\n", peer.getId(), peer.getNodeType());

            allPeers.add(peer);
        }

        connectPeersRandomly(); // Establish initial connections
    }

    /**
     * Randomly creates bidirectional connections between peers.
     */
    private void connectPeersRandomly() {
        for (PeerNode a : allPeers) {
            for (PeerNode b : allPeers) {
                if (a != b && Math.random() < 0.2) {
                    a.connectTo(b);
                }
            }
        }
    }

    /**
     * Simulates one tick of activity: churn, transfers, progress updates.
     */
    public void tick() {
        if (!simulationRunning) return;

        tickCount++;

        simulateChurn();
        simulateChunkTransfers();

        // Debug: print current missing chunks for target
        System.out.println("Target missing: " + getDownloadTarget().getMissingChunks());

        // Debug: print target's connections and their chunks
        for (NetworkNode conn : getDownloadTarget().getConnections()) {
            if (conn instanceof PeerNode p) {
                System.out.print("Connected to Peer " + p.getId() + " with chunks: ");
                System.out.println(p.getOwnedChunks().isEmpty() ? "None" : p.getOwnedChunks());
            }
        }

        // If file is fully downloaded, end the simulation
        if (downloadTarget.hasCompleteFile()) {
            simulationRunning = false;
            System.out.println("File download complete at tick " + tickCount);
        }
    }

    /**
     * Facilitates chunk transfers from neighbors to leechers.
     */
    private void simulateChunkTransfers() {
        for (PeerNode node : allPeers) {
            node.clearTransfers(); // Reset transfer logs for tick

            if (node instanceof Leecher leecher) {
                for (NetworkNode neighbor : node.getConnections()) {
                    if (neighbor instanceof PeerNode otherPeer) {
                        if (leecher.downloadFrom(otherPeer)) {
                            leecher.addTransfer(new Transfer(otherPeer, leecher));

                            // Debug: Log successful transfer
                            System.out.printf("Tick %d: Peer %d received chunk from Peer %d%n",
                                    tickCount, leecher.getId(), otherPeer.getId());
                        }
                    }
                }
            }
        }
    }

    /**
     * Simulates peer churn: randomly adds or removes nodes from the network.
     */
    private void simulateChurn() {
        // Randomly remove a peer
        if (Math.random() < 0.05 && allPeers.size() > 3) {
            PeerNode toRemove = allPeers.get(new Random().nextInt(allPeers.size()));

            if (toRemove.canDisconnect()) {
                allPeers.remove(toRemove);
                for (PeerNode peer : allPeers) {
                    peer.disconnectFrom(toRemove);
                }
                System.out.println("Peer " + toRemove.getId() + " disconnected.");
            }
        }

        // Randomly add a new peer
        if (Math.random() < 0.1) {
            int id = allPeers.size();
            double x = randomX(), y = randomY();
            PeerNode newPeer = new Leecher(id, x, y, totalChunks);
            allPeers.add(newPeer);

            // Connect new peer to up to 3 random existing peers
            for (int i = 0; i < 3; i++) {
                PeerNode other = allPeers.get(new Random().nextInt(allPeers.size()));
                newPeer.connectTo(other);
            }
        }
    }

    public void startSimulation() {
        simulationRunning = true;
    }

    public void stopSimulation() {
        simulationRunning = false;
    }

    public boolean isRunning() {
        return simulationRunning;
    }

    public List<PeerNode> getPeers() {
        return allPeers;
    }

    public PeerNode getDownloadTarget() {
        return downloadTarget;
    }

    /**
     * Determines whether the download is stalled based on lack of progress.
     * @return true if stalled beyond threshold
     */
    public boolean downloadFailed() {
        PeerNode target = getDownloadTarget();
        int currentChunkCount = target.getOwnedChunks().size();

        if (currentChunkCount > lastChunkCount) {
            lastChunkCount = currentChunkCount;
            ticksSinceLastProgress = 0; // Reset timer on progress
        } else {
            ticksSinceLastProgress++;
        }

        return ticksSinceLastProgress >= stallThreshold;
    }

    public int getTickCount() {
        return tickCount;
    }

    // Generate random X coordinate for layout visualization
    private double randomX() {
        return 100 + Math.random() * 600;
    }

    // Generate random Y coordinate for layout visualization
    private double randomY() {
        return 100 + Math.random() * 400;
    }
}
