package org.derekn.p2pSim;

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

        Label fileSizeLabel = new Label("File Size (bytes):");
        fileSizeLabel.setTextFill(Color.WHITE);
        TextField fileSizeField = new TextField("10485760"); // 10 MB default

        Label chunkSizeLabel = new Label("Chunk Size (bytes):");
        chunkSizeLabel.setTextFill(Color.WHITE);
        TextField chunkSizeField = new TextField("1048576"); // 1 MB default

        Button startButton = new Button("Start Simulation");

        inputPanel.getChildren().addAll(
                peersLabel, peersField,
                fileSizeLabel, fileSizeField,
                chunkSizeLabel, chunkSizeField,
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
                long fileSize = Long.parseLong(fileSizeField.getText());
                int chunkSize = Integer.parseInt(chunkSizeField.getText());

                if (fileSize <= 0 || chunkSize <= 0) {
                    throw new NumberFormatException();
                }

                int totalChunks = (int) Math.ceil((double) fileSize / chunkSize);
                simulationView.start(peers, totalChunks, chunkSize, fileSize);

                Timeline progressUpdater = new Timeline(new KeyFrame(Duration.millis(500), evt -> {
                    progressBar.setProgress(simulationView.getDownloadProgress());
                }));
                progressUpdater.setCycleCount(Timeline.INDEFINITE);
                progressUpdater.play();

            } catch (NumberFormatException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Please enter valid positive numbers.");
                alert.showAndWait();
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
