package org.derekn.p2pSim;

import javafx.animation.KeyFrame;
import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Visualization component for rendering the peer-to-peer simulation.
 * Uses JavaFX to display network nodes, connections, and live transfer animations.
 */
public class SimulationView extends Pane {
    private SimulationController controller;
    private Timeline timeline;
    private final Map<Integer, Circle> nodeCircles;
    private int totalChunks;
    private boolean downloadComplete = false;
    private boolean downloadFailed = false;
    private long startTimeMs;
    private int lastChunkCount = 0;
    private int ticksSinceLastChunk = 0;
    private long tickDurationMs = 500;
    private String summaryReport;

    public SimulationView() {
        this.setStyle("-fx-background-color: #000000;");
        this.nodeCircles = new HashMap<>();
    }

    /**
     * Starts the simulation and initializes rendering loop.
     */
    public void start(int initialPeers, int totalChunks, int chunkSizeBytes,
                      long fileSizeBytes, double speedMultiplier) {
        this.startTimeMs = System.currentTimeMillis();

        if (timeline != null) timeline.stop();

        this.controller = new SimulationController(initialPeers, totalChunks);
        this.totalChunks = totalChunks;
        controller.startSimulation();

        this.tickDurationMs = (long)(Constants.DEFAULT_TICK_DUR_MS / speedMultiplier);

        // Set up the rendering and simulation update loop
        timeline = new Timeline(new KeyFrame(Duration.millis(tickDurationMs), e -> {
            controller.tick();

            PeerNode target = controller.getDownloadTarget();
            int currentChunkCount = target.getOwnedChunks().size();

            // Track progress by chunk count
            if (currentChunkCount > lastChunkCount) {
                ticksSinceLastChunk = 0;
                lastChunkCount = currentChunkCount;
            } else {
                ticksSinceLastChunk++;
            }

            // Check if download has completed
            if (!downloadComplete && target.hasCompleteFile()) {
                downloadComplete = true;
                buildReportSummary();
                timeline.stop();
                drawNetwork();
            }

            // Check if download has failed due to stalling
            if (!downloadComplete && !downloadFailed && ticksSinceLastChunk >= controller.stallThreshold) {
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

    /**
     * Draws the current state of the network: nodes, connections, and transfers.
     */
    private void drawNetwork() {
        this.getChildren().clear();
        nodeCircles.clear();

        List<PeerNode> peers = controller.getPeers();

        // Draw static peer-to-peer connections
        for (PeerNode node : peers) {
            for (NetworkNode conn : node.getConnections()) {
                if (conn.getId() > node.getId()) { // Avoid drawing duplicate lines
                    Line edge = new Line(node.getX(), node.getY(), conn.getX(), conn.getY());
                    edge.setStroke(Color.web("#777777"));
                    edge.setStrokeWidth(1.0);
                    this.getChildren().add(edge);
                }
            }
        }

        // Draw dynamic transfer arrows only if simulation is running
        if (!downloadComplete && !downloadFailed ) {
            for (PeerNode peer : peers) {
                for (Transfer transfer : peer.getActiveTransfers()) {
                    PeerNode from = transfer.getSender();
                    PeerNode to = transfer.getReceiver();

                    double dx = to.getX() - from.getX();
                    double dy = to.getY() - from.getY();
                    double angle = Math.toDegrees(Math.atan2(dy, dx));

                    Polygon arrow = new Polygon();
                    arrow.getPoints().addAll(0.0, -5.0, 10.0, 0.0, 0.0, 5.0);
                    arrow.setFill(Color.LIMEGREEN);
                    arrow.setStroke(Color.BLACK);
                    arrow.setStrokeWidth(0.5);
                    arrow.setTranslateX(from.getX());
                    arrow.setTranslateY(from.getY());
                    arrow.setRotate(angle);
                    this.getChildren().add(arrow);

                    Path path = new Path();
                    path.getElements().add(new MoveTo(from.getX(), from.getY()));
                    path.getElements().add(new LineTo(to.getX(), to.getY()));

                    PathTransition move = new PathTransition();
                    move.setNode(arrow);
                    move.setPath(path);
                    move.setDuration(Duration.millis(tickDurationMs * 0.95));
                    move.setCycleCount(1);
                    move.setOnFinished(evt -> this.getChildren().remove(arrow));
                    move.play();
                }
            }
        }

        // Draw each node as a colored circle with a chunk count label
        for (PeerNode peer : peers) {
            Color fillColor = getColorForType(peer);
            double radius = getRadiusForType(peer);

            Circle circle = new Circle(peer.getX(), peer.getY(), radius);
            circle.setFill(fillColor);
            circle.setStroke(Color.WHITE);
            circle.setStrokeWidth(1.0);
            this.getChildren().add(circle);
            nodeCircles.put(peer.getId(), circle);

            Text label = new Text(String.valueOf(peer.getOwnedChunks().size()));
            label.setStyle("-fx-font-size: 10;");
            label.setFill(Color.BLACK);

            double textWidth = label.getLayoutBounds().getWidth();
            double textHeight = label.getLayoutBounds().getHeight();
            label.setX(peer.getX() - textWidth / 2);
            label.setY(peer.getY() + textHeight / 4);

            this.getChildren().add(label);
        }

        // Render final report if simulation ends
        if (downloadComplete || downloadFailed) {
            Text summary = new Text(20, 40, summaryReport);
            summary.setFill(Color.WHITE);
            summary.setStyle("-fx-font-size: 14; -fx-font-family: monospace;");
            this.getChildren().add(summary);
        }
    }

    public void setSpeedMultiplier(double speedMultiplier) {
        if (timeline != null) {
            timeline.stop();
            this.tickDurationMs = (long)(500 / speedMultiplier);
            timeline.getKeyFrames().setAll(new KeyFrame(Duration.millis(this.tickDurationMs), e -> {
                controller.tick();
                drawNetwork();

                if (!downloadComplete && controller.getDownloadTarget().hasCompleteFile()) {
                    downloadComplete = true;
                    buildReportSummary();
                    drawNetwork();
                }

                if (!downloadComplete && controller.downloadFailed()) {
                    downloadFailed = true;
                    buildReportSummary();
                    drawNetwork();
                }
            }));
            timeline.play();
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

    /**
     * Builds the simulation report shown after download completes or fails.
     */
    private void buildReportSummary() {
        long timeElapsed = (System.currentTimeMillis() - startTimeMs) / 1000;
        PeerNode target = controller.getDownloadTarget();
        int downloaded = target.getOwnedChunks().size();
        int missing = totalChunks - downloaded;

        long simulatedTimeMs = controller.getTickCount() * Constants.DEFAULT_TICK_DUR_MS;
        long simulatedSeconds = simulatedTimeMs / 1000;

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
            Time Elapsed (simulated): %d seconds
            Time Elapsed (actual): %d seconds
            Chunks Downloaded: %d/%d
            Chunks Missing: %d
            Active Peers: %d
            Final Seeders: %d
            Supernodes: %d
            Total Connections: %d%s
            """,
                status,
                timeElapsed,
                simulatedSeconds,
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