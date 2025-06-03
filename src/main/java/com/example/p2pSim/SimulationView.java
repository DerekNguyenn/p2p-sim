package com.example.p2pSim;

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
    private static final int INITIAL_PEERS = 10;
    private static final int TOTAL_CHUNKS = 10;

    private SimulationController controller;
    private Timeline timeline;
    private Map<Integer, Circle> nodeCircles;

    public SimulationView() {
        this.controller = new SimulationController(INITIAL_PEERS, TOTAL_CHUNKS);
        this.nodeCircles = new HashMap<>();
        this.setStyle("-fx-background-color: #1e1e1e;");
    }

    public void start() {
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

        // Draw connections (lines)
        for (PeerNode node : peers) {
            for (NetworkNode conn : node.getConnections()) {
                if (conn.getId() > node.getId()) { // draw once per pair
                    Line edge = new Line(node.getX(), node.getY(), conn.getX(), conn.getY());
                    edge.setStroke(Color.GRAY);
                    edge.setStrokeWidth(1.5);
                    this.getChildren().add(edge);
                }
            }
        }

        // Draw nodes
        for (PeerNode peer : peers) {
            Color fillColor = getColorForType(peer);
            Circle circle = new Circle(peer.getX(), peer.getY(), getRadiusForType(peer));
            circle.setFill(fillColor);
            circle.setStroke(Color.WHITE);
            circle.setStrokeWidth(1.0);

            nodeCircles.put(peer.getId(), circle);
            this.getChildren().add(circle);

            // Add chunk progress text
            Text label = new Text(peer.getX() - 10, peer.getY() + 4,
                    String.valueOf(peer.getOwnedChunks().size()));
            label.setFill(Color.WHITE);
            label.setStyle("-fx-font-size: 10;");
            this.getChildren().add(label);
        }
    }

    private Color getColorForType(PeerNode peer) {
        return switch (peer.getNodeType()) {
            case "Seeder" -> Color.LIMEGREEN;
            case "Leecher" -> Color.CORNFLOWERBLUE;
            case "Supernode" -> Color.ORANGE;
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

