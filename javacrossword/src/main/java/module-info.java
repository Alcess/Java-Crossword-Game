module crosswordjava {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;

    opens crosswordjava to javafx.fxml;

    exports crosswordjava;
}
