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

    public SimulationView() {
        this.setStyle("-fx-background-color: #000000;");
        this.nodeCircles = new HashMap<>();
    }

    public void start(int initialPeers, int totalChunks) {
        if (timeline != null) timeline.stop();

        this.controller = new SimulationController(initialPeers, totalChunks);
        this.totalChunks = totalChunks;
        controller.startSimulation();

        timeline = new Timeline(new KeyFrame(Duration.millis(500), e -> {
            controller.tick();
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
    }

    public double getDownloadProgress() {
        PeerNode target = controller.getDownloadTarget();
        return (double) target.getOwnedChunks().size() / totalChunks;
    }

    private Color getColorForType(PeerNode peer) {
        return switch (peer.getNodeType()) {
            case "Seeder" -> Color.LIME;
            case "Leecher" -> Color.ROYALBLUE;
            case "Supernode" -> Color.GOLD;
            default -> Color.GRAY;
        };
    }

    private double getRadiusForType(PeerNode peer) {
        return switch (peer.getNodeType()) {
            case "Seeder" -> 10;
            case "Leecher" -> 8;
            case "Supernode" -> 14;
            default -> 6;
        };
    }
}

