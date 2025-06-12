module org.derekn.p2pSim {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;

    opens org.derekn.p2pSim to javafx.fxml;
    exports org.derekn.p2pSim;
}