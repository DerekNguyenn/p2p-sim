package org.derekn.p2pSim;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimulationView extends Pane {
    private SimulationController controller;
    private Timeline timeline;
    private Map<Integer, Circle> nodeCircles;
    private int totalChunks;
    private boolean downloadComplete = false;
    private boolean downloadFailed = false;
    private long startTimeMs;
    private int lastChunkCount = 0;
    private int ticksSinceLastChunk = 0;
    private final int stallThreshold = 20;
    private String summaryReport;

    public SimulationView() {
        this.setStyle("-fx-background-color: #000000;");
        this.nodeCircles = new HashMap<>();
    }

    public void start(int initialPeers, int totalChunks) {
        this.startTimeMs = System.currentTimeMillis();

        if (timeline != null) timeline.stop();

        this.controller = new SimulationController(initialPeers, totalChunks);
        this.totalChunks = totalChunks;
        controller.startSimulation();

        timeline = new Timeline(new KeyFrame(Duration.millis(500), e -> {
            controller.tick();

            PeerNode target = controller.getDownloadTarget();
            int currentChunkCount = target.getOwnedChunks().size();

            // Check for progress
            if (currentChunkCount > lastChunkCount) {
                ticksSinceLastChunk = 0;
                lastChunkCount = currentChunkCount;
            } else {
                ticksSinceLastChunk++;
            }

            // Download complete
            if (!downloadComplete && target.hasCompleteFile()) {
                downloadComplete = true;
                buildReportSummary();
                timeline.stop();
                drawNetwork();
            }

            // Download failure
            if (!downloadComplete && !downloadFailed && ticksSinceLastChunk >= stallThreshold) {
                downloadFailed = true;
                buildReportSummary();
                timeline.stop();
                drawNetwork();
            }

            drawNetwork();
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void drawNetwork() {
        this.getChildren().clear();
        nodeCircles.clear();

        List<PeerNode> peers = controller.getPeers();

        // Draw connections first (lines)
        for (PeerNode node : peers) {
            for (NetworkNode conn : node.getConnections()) {
                if (conn.getId() > node.getId()) {
                    Line edge = new Line(node.getX(), node.getY(), conn.getX(), conn.getY());
                    edge.setStroke(Color.web("#777777"));
                    edge.setStrokeWidth(1.0);
                    this.getChildren().add(edge);
                }
            }
        }

        // Draw nodes
        for (PeerNode peer : peers) {
            Color fillColor = getColorForType(peer);
            double radius = getRadiusForType(peer);

            Circle circle = new Circle(peer.getX(), peer.getY(), radius);
            circle.setFill(fillColor);
            circle.setStroke(Color.WHITE);
            circle.setStrokeWidth(1.0);

            this.getChildren().add(circle);
            nodeCircles.put(peer.getId(), circle);

            // Chunk count label
            Text label = new Text(peer.getX() - 10, peer.getY() + 4,
                    String.valueOf(peer.getOwnedChunks().size()));
            label.setFill(Color.BLACK);
            label.setStyle("-fx-font-size: 10;");
            this.getChildren().add(label);
        }

        if (downloadComplete || downloadFailed) {
            Text summary = new Text(20, 40, summaryReport);
            summary.setFill(Color.WHITE);
            summary.setStyle("-fx-font-size: 14; -fx-font-family: monospace;");
            this.getChildren().add(summary);
        }
    }

    public double getDownloadProgress() {
        PeerNode target = controller.getDownloadTarget();
        return (double) target.getOwnedChunks().size() / totalChunks;
    }

    private Color getColorForType(PeerNode peer) {
        return switch (peer.getNodeType()) {
            case "Client" -> Color.PINK;
            case "Seeder" -> Color.LIME;
            case "Leecher" -> Color.ROYALBLUE;
            case "Supernode" -> Color.GOLD;
            default -> Color.GRAY;
        };
    }

    private double getRadiusForType(PeerNode peer) {
        return switch (peer.getNodeType()) {
            case "Client" -> 12;
            case "Seeder" -> 10;
            case "Leecher" -> 8;
            case "Supernode" -> 14;
            default -> 6;
        };
    }

    private void buildReportSummary() {
        long timeElapsed = (System.currentTimeMillis() - startTimeMs) / 1000;
        PeerNode target = controller.getDownloadTarget();
        int downloaded = target.getOwnedChunks().size();
        int missing = totalChunks - downloaded;

        int totalConnections = controller
                .getPeers()
                .stream()
                .mapToInt(p -> p.getConnections().size()).sum() / 2;

        long seederCount = controller.getPeers().stream()
                .filter(p -> p instanceof Seeder).count();

        long supernodeCount = controller.getPeers().stream()
                .filter(p -> p instanceof Supernode).count();

        String status = downloadComplete
                ? "✅ Download Complete"
                : "❌ Download Failed";

        String reason = downloadComplete
                ? ""
                : "\nDownload failed - required chunks unavailable.";

        summaryReport = String.format("""
            %s
            Time Elapsed: %d seconds
            Chunks Downloaded: %d/%d
            Chunks Missing: %d
            Active Peers: %d
            Final Seeders: %d
            Supernodes: %d
            Total Connections: %d%s
            """,
                status,
                timeElapsed,
                downloaded, this.totalChunks,
                missing,
                controller.getPeers().size(),
                seederCount,
                supernodeCount,
                totalConnections,
                reason
        );
    }
}

