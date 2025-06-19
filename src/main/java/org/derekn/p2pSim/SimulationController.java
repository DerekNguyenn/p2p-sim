package org.derekn.p2pSim;

import java.util.*;

/**
 * Controls the overall simulation of the P2P file-sharing network.
 * Manages peer creation, chunk transfers, and simulation ticks.
 */
public class SimulationController {
    private List<PeerNode> allPeers;
    private int totalChunks;
    private PeerNode downloadTarget; // The peer we are tracking for completion
    private boolean simulationRunning;
    private int tickCount;
    private int ticksSinceLastProgress = 0;
    private int lastChunkCount = 0;
    public final int stallThreshold;

    /**
     * Constructs a SimulationController and initializes peers.
     * @param initialPeers Number of peers to start with
     * @param totalChunks Total number of file chunks in simulation
     */
    public SimulationController(int initialPeers, int totalChunks) {
        this.totalChunks = totalChunks;
        this.stallThreshold = Math.max(10, totalChunks / 4);
        this.allPeers = new ArrayList<PeerNode>();
        this.simulationRunning = false;
        this.tickCount = 0;

        // Initialize the initial swarm
        createInitialPeers(initialPeers);
    }

    private void createInitialPeers(int count) {
        for (int i = 0; i < count; i++) {
            double x = randomX();
            double y = randomY();

            PeerNode peer;

            if (i == 0) {
                // First node is always the Client (download target)
                peer = new Client(i, x, y, totalChunks);
                this.downloadTarget = peer;
            } else if (i == 1) {
                // Guarantee at least one Seeder exists
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

        // Randomly connect peers
        connectPeersRandomly();
    }

    private void connectPeersRandomly() {
        for (PeerNode a : allPeers) {
            for (PeerNode b : allPeers) {
                if (a != b && Math.random() < 0.2) {
                    a.connectTo(b);
                }
            }
        }
    }

    // Simulate one tick (one time step)
    public void tick() {
        if (!simulationRunning) return;

        tickCount++;

        simulateChurn();
        simulateChunkTransfers();

        // Debug logging prints
        System.out.println("Target missing: " + getDownloadTarget().getMissingChunks());
        for (NetworkNode conn : getDownloadTarget().getConnections()) {
            if (conn instanceof PeerNode p) {
                System.out.print("Connected to Peer " + p.getId() + " with chunks: ");
                if (p.getOwnedChunks().isEmpty()) {
                    System.out.print("None");
                } else {
                    System.out.print(p.getOwnedChunks());
                }
                System.out.println();
            }
        }

        // Stop condition
        if (downloadTarget.hasCompleteFile()) {
            simulationRunning = false;
            System.out.println("File download complete at tick " + tickCount);
        }
    }

    private void simulateChunkTransfers() {
        for (PeerNode node : allPeers) {
            node.clearTransfers();

            if (node instanceof Leecher leecher) {
                for (NetworkNode neighbor : node.getConnections()) {
                    if (neighbor instanceof PeerNode otherPeer) {
                        if (leecher.downloadFrom(otherPeer)) {
                            leecher.addTransfer(new Transfer(otherPeer, leecher));

                            // Debug logging print
                            System.out.printf("Tick %d: Peer %d received chunk from Peer %d%n",
                                    tickCount, leecher.getId(), otherPeer.getId());
                        }
                    }
                }
            }
        }
    }

    private void simulateChurn() {
        // Randomly remove a node
        if (Math.random() < 0.05 && allPeers.size() > 3) {
            PeerNode toRemove = allPeers.get(new Random().nextInt(allPeers.size()));

            if (toRemove.canDisconnect()) {
                allPeers.remove(toRemove);
                for (PeerNode peer : allPeers) {
                    peer.disconnectFrom(toRemove);
                }
                // Debug logging print
                System.out.println("Peer " + toRemove.getId() + " disconnected.");
            }
        }

        // Randomly add a new peer
        if (Math.random() < 0.1) {
            int id = allPeers.size();
            double x = randomX(), y = randomY();
            PeerNode newPeer = new Leecher(id, x, y, totalChunks);
            allPeers.add(newPeer);

            // Connect it to a few peers
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

    public boolean downloadFailed() {
        PeerNode target = getDownloadTarget();
        int currentChunkCount = target.getOwnedChunks().size();

        if (currentChunkCount > lastChunkCount) {
            lastChunkCount = currentChunkCount;
            ticksSinceLastProgress = 0; // Reset if progress made
        } else {
            ticksSinceLastProgress++;
        }

        return ticksSinceLastProgress >= stallThreshold;
    }

    public int getTickCount() {
        return tickCount;
    }

    // Random coordinate generators for layout
    private double randomX() {
        return 100 + Math.random() * 600;
    }

    private double randomY() {
        return 100 + Math.random() * 400;
    }
}
