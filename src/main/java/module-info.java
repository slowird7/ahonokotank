module com.app.ahonokotank {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires jna;


    opens com.app.ahonokotank to javafx.fxml;
    exports com.app.ahonokotank;
}