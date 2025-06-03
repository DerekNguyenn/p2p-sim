package com.example.p2pSim;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class SimulationGUI extends Application {

    public void start(Stage primaryStage) {
        // Input panel
        VBox inputPanel = new VBox(10);
        inputPanel.setPadding(new Insets(10));
        inputPanel.setStyle("-fx-background-color: #333333;");

        Label peersLabel = new Label("Initial Peers:");
        TextField peersField = new TextField("10");

        Label chunksLabel = new Label("Total Chunks:");
        TextField chunksField = new TextField("10");

        Button startButton = new Button("Start Simulation");

        inputPanel.getChildren().addAll(peersLabel, peersField, chunksLabel, chunksField, startButton);

        // Simulation canvas
        SimulationView simulationView = new SimulationView();

        // Split layout
        BorderPane layout = new BorderPane();
        layout.setLeft(inputPanel);
        layout.setCenter(simulationView);

        Scene scene = new Scene(layout, 1000, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("P2P File Sharing Simulation");
        primaryStage.show();

        startButton.setOnAction(e -> {
            try {
                int peers = Integer.parseInt(peersField.getText());
                int chunks = Integer.parseInt(chunksField.getText());
                simulationView.start(peers, chunks);
            } catch (NumberFormatException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Please enter valid integers.");
                alert.showAndWait();
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}


