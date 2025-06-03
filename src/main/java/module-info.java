module com.example.p2pSim {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.example.p2pSim to javafx.fxml;
    exports com.example.p2pSim;
}