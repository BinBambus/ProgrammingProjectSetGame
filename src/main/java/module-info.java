/**
 module org.example.setgame {
 requires javafx.controls;
 requires javafx.fxml;
 requires javafx.web;

 requires org.controlsfx.controls;
 requires com.dlsc.formsfx;
 requires net.synedra.validatorfx;
 requires org.kordamp.ikonli.javafx;
 requires org.kordamp.bootstrapfx.core;
 requires eu.hansolo.tilesfx;
 requires com.almasb.fxgl.all;
 requires jakarta.persistence;
 requires org.hibernate.orm.core;
 requires java.naming;

 opens org.example.setgame to javafx.fxml;
 exports org.example.setgame;
 }*/
//CHATGPT
module org.example.setgame {
    requires org.hibernate.orm.core;
    requires jakarta.persistence; // Or jakarta.persistence if using Jakarta EE
    requires java.sql; // If you are using JDBC
    requires javafx.controls;
    requires javafx.fxml;
    requires org.postgresql.jdbc; // If you are using PostgreSQL
    requires java.naming;


    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;

    // Open the package to Hibernate
    opens org.example.setgame to org.hibernate.orm.core, javafx.fxml;
    exports org.example.setgame;
    // If you have other packages with entities, open them too
    // opens org.example.setgame.entities to org.hibernate.orm.core;
}