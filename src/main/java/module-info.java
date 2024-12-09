module com.example.webhk {
    requires javafx.controls;
    requires javafx.fxml;
    requires jakarta.servlet;
    requires java.sql;
    requires org.json;
//    requires mysql.connector.j;
    //requires mssql.jdbc;


    opens com.example.webhk to javafx.fxml;
    exports com.example.webhk;
}