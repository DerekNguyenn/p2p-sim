module com.example.p2pSim {
    requires javafx.controls;
    requires javafx.fxml;

    opens org.derekn.p2pSim to javafx.fxml;
    exports org.derekn.p2pSim;
}