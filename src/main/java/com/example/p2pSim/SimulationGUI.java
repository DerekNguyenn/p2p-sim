package com.example.p2pSim;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;

public class SimulationGUI extends Application {

    public void start(Stage primaryStage) {
        ProgressBar progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(300);
        progressBar.setStyle("-fx-accent: lime;");

        // Input panel
        VBox inputPanel = new VBox(10);
        inputPanel.setPadding(new Insets(10));
        inputPanel.setStyle("-fx-background-color: #333333;");

        Label peersLabel = new Label("Initial Peers:");
        peersLabel.setTextFill(Color.WHITE);
        TextField peersField = new TextField("10");

        Label chunksLabel = new Label("Total Chunks:");
        chunksLabel.setTextFill(Color.WHITE);
        TextField chunksField = new TextField("10");

        Button startButton = new Button("Start Simulation");

        inputPanel.getChildren().addAll(
                peersLabel, peersField,
                chunksLabel, chunksField,
                startButton
        );

        SimulationView simulationView = new SimulationView();

        StackPane simulationPane = new StackPane(simulationView);
        simulationPane.setStyle("-fx-background-color: #000000;");

        VBox bottomBox = new VBox(progressBar);
        bottomBox.setPadding(new Insets(10));
        bottomBox.setStyle("-fx-background-color: #222222;");
        bottomBox.setAlignment(javafx.geometry.Pos.CENTER);

        BorderPane layout = new BorderPane();
        layout.setLeft(inputPanel);
        layout.setCenter(simulationPane);
        layout.setBottom(bottomBox);

        Scene scene = new Scene(layout, 1000, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("P2P File Sharing Simulation");
        primaryStage.show();

        startButton.setOnAction(e -> {
            try {
                int peers = Integer.parseInt(peersField.getText());
                int chunks = Integer.parseInt(chunksField.getText());
                simulationView.start(peers, chunks);

                // Use a Timeline to keep progress bar updated
                Timeline progressUpdater = new Timeline(new KeyFrame(Duration.millis(500), evt -> {
                    progressBar.setProgress(simulationView.getDownloadProgress());
                }));
                progressUpdater.setCycleCount(Timeline.INDEFINITE);
                progressUpdater.play();

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


