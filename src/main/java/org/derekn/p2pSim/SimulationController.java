package org.derekn.p2pSim;

import java.util.*;

public class SimulationController {
    private List<PeerNode> allPeers;
    private int totalChunks;
    private PeerNode downloadTarget; // The peer we are tracking for completion
    private boolean simulationRunning;
    private int tickCount;

    // Constructor
    public SimulationController(int initialPeers, int totalChunks) {
        this.totalChunks = totalChunks;
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
                // First node is a seeder
                peer = new Seeder(i, x, y, totalChunks);
            } else if (Math.random() < 0.2) {
                peer = new Supernode(i, x, y, totalChunks);
            } else {
                peer = new Leecher(i, x, y, totalChunks);
            }

            System.out.printf("Created Peer %d: %s\n", peer.getId(), peer.getNodeType());

            allPeers.add(peer);
        }

        // Assign download target for tracking (e.g., last peer)
        downloadTarget = allPeers.getLast();

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

        // Stop condition
        if (downloadTarget.hasCompleteFile()) {
            simulationRunning = false;
            System.out.println("File download complete at tick " + tickCount);
        }
    }

    private void simulateChunkTransfers() {
        for (PeerNode node : allPeers) {
            if (node instanceof Leecher) {
                for (NetworkNode neighbor : node.getConnections()) {
                    if (neighbor instanceof PeerNode otherPeer) {
                        ((Leecher) node).downloadFrom(otherPeer);
                    }
                }
            }
        }
    }

    private void simulateChurn() {
        // Randomly remove a node
        if (Math.random() < 0.05 && allPeers.size() > 3) {
            PeerNode toRemove = allPeers.get(new Random().nextInt(allPeers.size()));
            if (!(toRemove instanceof Seeder)) {
                allPeers.remove(toRemove);
                for (PeerNode peer : allPeers) {
                    peer.disconnectFrom(toRemove);
                }
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

    // Random coordinate generators for layout
    private double randomX() {
        return 100 + Math.random() * 600;
    }

    private double randomY() {
        return 100 + Math.random() * 400;
    }
}
