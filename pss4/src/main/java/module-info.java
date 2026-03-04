module org.example.pss4 {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.pss4 to javafx.fxml;
    exports org.example.pss4;
}