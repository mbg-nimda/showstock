/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databasemanager;

import console.Console;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nimda
 */
public class DatabaseManager {

    private static Console myconsole;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
    }
    private static String home;
    private static Connection c;
    private static Statement stm;

    public static void manageTickers(Console console) {
        try {
            myconsole = console;
            home = home();
            Class.forName("org.sqlite.JDBC");
            c = DriverManager
                    .getConnection(String.format("jdbc:sqlite:%s/stocks.db", home));
            stm = c.createStatement();
            Tickermngmnt tm = new Tickermngmnt(console, home, stm);
            tm.setVisible(true);
       } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static String home() {
        File here = new File(".");
        return here.getAbsolutePath();
    }
}
