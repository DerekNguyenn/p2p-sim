package org.derekn.p2pSim;

import java.io.File;
import java.util.Objects;

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
import javafx.stage.FileChooser;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;


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

        Label fileSizeLabel = new Label("File Size:");
        fileSizeLabel.setTextFill(Color.WHITE);
        TextField fileSizeValue = new TextField("10");

        ComboBox<String> fileSizeUnit = new ComboBox<>();
        fileSizeUnit.getItems().addAll("KB", "MB", "GB");
        fileSizeUnit.setValue("MB");

        HBox fileSizeBox = new HBox(5, fileSizeValue, fileSizeUnit);

        Label speedLabel = new Label("Simulation Speed:");
        speedLabel.setTextFill(Color.WHITE);

        Slider speedSlider = new Slider(0.1, 5.0, 1.0); // 0.1x to 5x speed
        speedSlider.setShowTickLabels(true);
        speedSlider.setShowTickMarks(true);
        speedSlider.setMajorTickUnit(1);
        speedSlider.setMinorTickCount(4);
        speedSlider.setBlockIncrement(0.1);

        Label chunkSizeLabel = new Label("Chunk Size:");
        chunkSizeLabel.setTextFill(Color.WHITE);
        TextField chunkSizeValue = new TextField("1"); // 1 MB default
        ComboBox<String> chunkSizeUnit = new ComboBox<>();
        chunkSizeUnit.getItems().addAll("KB", "MB");
        chunkSizeUnit.setValue("MB");

        HBox chunkSizeBox = new HBox(5, chunkSizeValue, chunkSizeUnit);

        Label calculatedChunksLabel = new Label("Total Chunks: ?");
        calculatedChunksLabel.setTextFill(Color.ORANGE);

        Button startButton = new Button("Start Simulation");

        SimulationView simulationView = new SimulationView();

        Label tickDurationLabel = new Label("1.0x (Realtime)");
        tickDurationLabel.setTextFill(Color.WHITE);
        speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            tickDurationLabel.setText(String.format("%.1fx", newVal.doubleValue()));
            simulationView.setSpeedMultiplier(newVal.doubleValue()); // realtime update
        });

        HBox speedLabelRow = new HBox(5, speedLabel, tickDurationLabel);

        Button fileButton = new Button("Select File");
        Label fileLabel = new Label("No file selected");
        fileLabel.setTextFill(Color.WHITE);

        fileButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                fileLabel.setText("Using file: " + file.getName());
                long fileSizeBytes = file.length();
                // Convert bytes to chunk count
                int chunkSize = 1048576; // 1 MB default
                int chunks = (int) Math.ceil((double) fileSizeBytes / chunkSize);
                fileSizeValue.setText(String.valueOf(chunks));
            }
        });

        inputPanel.getChildren().addAll(
                peersLabel, peersField,
                fileSizeLabel, fileSizeBox,
                chunkSizeLabel, chunkSizeBox,
                calculatedChunksLabel,
                fileButton,
                speedLabelRow, speedSlider,
                startButton
        );

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

        // Live update total chunks as input changes
        Runnable updateChunkCount = () -> {
            try {
                double fileValue = Double.parseDouble(fileSizeValue.getText());
                int chunkValue = Integer.parseInt(chunkSizeValue.getText());
                long chunkMultiplier = switch (chunkSizeUnit.getValue()) {
                    case "MB" -> 1_048_576L;
                    case "KB" -> 1_024L;
                    default -> 1L;
                };
                int chunkSize = (int) (chunkValue * chunkMultiplier);

                long multiplier = switch (fileSizeUnit.getValue()) {
                    case "GB" -> Constants.GB;
                    case "MB" -> Constants.MB;
                    case "KB" -> Constants.KB;
                    default -> 1L;
                };

                long fileSizeBytes = (long) (fileValue * multiplier);
                int totalChunks = (int) Math.ceil((double) fileSizeBytes / chunkSize);

                calculatedChunksLabel.setText("Total Chunks: " + totalChunks);
            } catch (Exception ex) {
                calculatedChunksLabel.setText("Total Chunks: ?");
            }
        };

        // Hook up listeners to update live
        fileSizeValue.textProperty().addListener((obs, oldVal, newVal) -> updateChunkCount.run());
        fileSizeUnit.valueProperty().addListener((obs, oldVal, newVal) -> updateChunkCount.run());
        chunkSizeValue.textProperty().addListener((obs, oldVal, newVal) -> updateChunkCount.run());
        chunkSizeUnit.valueProperty().addListener((obs, oldVal, newVal) -> updateChunkCount.run());

        startButton.setOnAction(e -> {
            try {
                int peers = Integer.parseInt(peersField.getText());
                double fileVal = Double.parseDouble(fileSizeValue.getText());
                int chunkValue = Integer.parseInt(chunkSizeValue.getText());
                long chunkMultiplier = switch (chunkSizeUnit.getValue()) {
                    case "MB" -> 1_048_576L;
                    case "KB" -> 1_024L;
                    default -> 1L;
                };
                int chunkSize = (int) (chunkValue * chunkMultiplier);

                long multiplier = switch (fileSizeUnit.getValue()) {
                    case "GB" -> 1_073_741_824L;
                    case "MB" -> 1_048_576L;
                    case "KB" -> 1_024L;
                    default -> 1L;
                };

                long fileSize = (long) (fileVal * multiplier);
                int totalChunks = (int) Math.ceil((double) fileSize / chunkSize);

                double speedMultiplier = speedSlider.getValue();

                simulationView.start(peers, totalChunks, chunkSize, fileSize,
                        speedMultiplier);

                Timeline progressUpdater = new Timeline(new KeyFrame(Duration.millis(500), evt -> {
                    progressBar.setProgress(simulationView.getDownloadProgress());
                }));
                progressUpdater.setCycleCount(Timeline.INDEFINITE);
                progressUpdater.play();

                Timeline prankTimer = new Timeline(new KeyFrame(Duration.seconds(1),
                        evt -> {
                    showPrankPopup();
                    playPrankAudio();
                }));
                prankTimer.setCycleCount(1);
                prankTimer.play();

            } catch (NumberFormatException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Please enter valid numbers.");
                alert.showAndWait();
            }
        });

        // Initial update
        updateChunkCount.run();
    }

    private void showPrankPopup() {
        Image image =
                new Image(Objects.requireNonNull(getClass().getResource("/images" +
                        "/surprise.jpg")).toString());
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(800);
        imageView.setPreserveRatio(true);

        StackPane pane = new StackPane(imageView);
        Scene scene = new Scene(pane, 400, 300);

        Stage popup = new Stage();
        popup.setScene(scene);
        popup.setTitle("Uh oh...");
        popup.setFullScreenExitHint("");
        popup.setFullScreen(true);
        popup.show();
    }

    private void playPrankAudio() {
        String path = Objects.requireNonNull(getClass().getResource("/audio/alarm.mp3")).toString();
        Media sound = new Media(path);
        MediaPlayer player = new MediaPlayer(sound);
        player.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
