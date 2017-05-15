package de.dynamicfiles.projects.javafxwithsqlite;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Please note that this is not a clean design, it is not intended to be clean, it is intended to show
 * an example project filled with SQLite, JavaFX and Maven.
 *
 * @author FibreFoX
 */
public class Launcher extends Application {

    public static final String JDBC_SQLITE_CLASSNAME = "org.sqlite.JDBC";
    public static final String JDBC_CONNECTION_PREFIX = "jdbc:sqlite:";
    public static final String PREMADE_DATABASE_FILENAME = "javafxwithsqlite.db";

    public static void main(String args[]) {
        // when the application is executed, this is a nice way to determine the location of the main-jar
        // this means that the database will be present inside "app"-folder from native bundle
        Path currentWorkingFolder = Paths.get("").toAbsolutePath();
        Path pathToTheDatabaseFile = currentWorkingFolder.resolve("javafxwithsqlite.db");

        String databaseConnectionURL = JDBC_CONNECTION_PREFIX + pathToTheDatabaseFile.toUri().toString();

        boolean loadedDriver = loadJDBCDriver();
        if( loadedDriver ){
            // check if database exists
            if( !isDatabaseAlreadyExisting(pathToTheDatabaseFile) ){
                try{
                    // if not existing, copy a prepared one from jar-resources
                    checkoutPremadeDatabase(pathToTheDatabaseFile);
                } catch(IOException ex){
                    Logger.getLogger(Launcher.class.getName()).log(Level.SEVERE, null, ex);
                    // there is some real problem, quit doing anything
                    System.exit(-1);
                }
            }
            // open database
            try(Connection openDatabaseConnection = openDatabaseConnection(databaseConnectionURL)){
                // DO SOME STUFF
            } catch(SQLException ex){
                Logger.getLogger(Launcher.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

//        Launcher.launch(args);
        System.exit(0);
    }

    private static void checkoutPremadeDatabase(Path pathToTheDatabaseFile) throws IOException {
        // that file was made with this statement: CREATE TABLE peoples (firstname, lastname)
        InputStream premadeDatabaseStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(Launcher.class.getPackage().getName().replace(".", "/") + "/" + PREMADE_DATABASE_FILENAME);
        Files.copy(premadeDatabaseStream, pathToTheDatabaseFile);
    }

    private static boolean isDatabaseAlreadyExisting(Path pathToTheDatabaseFile) {
        // maybe some additional write-permissions checks might be added
        return Files.exists(pathToTheDatabaseFile);
    }

    private static boolean loadJDBCDriver() {
        try{
            Class.forName(JDBC_SQLITE_CLASSNAME);
            return true;
        } catch(ClassNotFoundException ex){
            Logger.getLogger(Launcher.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public static Connection openDatabaseConnection(String databasePath) throws SQLException {
        return DriverManager.getConnection(databasePath);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.show();
    }

}
