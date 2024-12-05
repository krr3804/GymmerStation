module com.gymmer.gymmerstation {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.gymmer.gymmerstation to javafx.fxml;
    exports com.gymmer.gymmerstation;
    opens com.gymmer.gymmerstation.programManagement to javafx.fxml;
    exports com.gymmer.gymmerstation.programManagement;
    opens com.gymmer.gymmerstation.exerciseManagement to javafx.fxml;
    exports com.gymmer.gymmerstation.exerciseManagement;
    opens com.gymmer.gymmerstation.programOperation to javafx.fxml;
    exports com.gymmer.gymmerstation.programOperation;
    opens com.gymmer.gymmerstation.performanceArchive to javafx.fxml;
    exports com.gymmer.gymmerstation.performanceArchive;
    opens com.gymmer.gymmerstation.domain to javafx.fxml;
    exports com.gymmer.gymmerstation.domain;
    exports com.gymmer.gymmerstation.home;
    opens com.gymmer.gymmerstation.home to javafx.fxml;
}