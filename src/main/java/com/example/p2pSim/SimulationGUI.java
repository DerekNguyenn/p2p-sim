package com.example.p2pSim;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SimulationGUI extends Application {

    @Override
    public void start(Stage primaryStage) {
        SimulationView view = new SimulationView();

        Scene scene = new Scene(view, 800, 600);
        primaryStage.setTitle("P2P File Sharing Simulation");
        primaryStage.setScene(scene);
        primaryStage.show();

        view.start(); // Begin simulation loop
    }

    public static void main(String[] args) {
        launch(args);
    }
}

