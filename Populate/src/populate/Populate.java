package populate;

import console.Console;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Populate {

    private static Connection c;
    private static Statement stm;
    private static String home;
    private static Console myconsole;
    public static boolean wait = true;

    public static void main(String[] args) {
    }

    public static void populate(Console console) {
        try {
            myconsole = console;
            myconsole.setVisible(true);
            home = home();
            //console.
            DirFinder df = new DirFinder();
            df.setVisible(true);
            int i = 0;
            while (wait) {
                i++;
                wait = !df.done;
                String out = wait+" "+i;
//                System.out.println(out);
//                console.textArea.append(out);
            }
            File dir = df.dir;
            Class.forName("org.sqlite.JDBC");
            c = DriverManager
                    .getConnection(String.format("jdbc:sqlite:%s/stocks.db", home));
            stm = c.createStatement();
            String cmd = "select symb from tickers order by symb";
            ResultSet rS = stm.executeQuery(cmd);
            List<String> stocks = new ArrayList();
            while (rS.next()) {
                stocks.add(rS.getString("symb"));
            }
            String outtext = "";
            for (String stock : stocks) {
                String fname = String.format("%s/%s", dir.getPath(), stock);
                System.out.println("\n" + fname + "\n");
                loadFile(fname, stock);
                outtext = fname + " loaded.\n\n";
                myconsole.textArea.append(outtext);
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            myconsole.textArea.append(e.getMessage());
            //System.exit(0);
        }
    }

    private static void loadFile(String fname, String stock) {
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(String.format(
                    "%s.csv", fname)));
            String line;
            br.readLine();
            line = br.readLine();
            String cmd = String.format(
                    "select _id from tickers where symb = \"%s\"", stock);
            ResultSet rS = stm.executeQuery(cmd);
            int id = rS.getInt(1);
            while (line != null) {
                line = br.readLine();
                if (line == null) {
                    break;
                }
                String[] cols = line.split(",");
                cmd = "insert into stocks (dat, close, volume, tick) values(date(\""
                        + cols[0]
                        + "\"),"
                        + cols[4]
                        + ","
                        + cols[6]
                        + ","
                        + id
                        + ") ";
                myconsole.textArea.append(cmd + "\n");
                stm.execute(cmd);
            }
        } catch (IOException | SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            myconsole.textArea.append(e.getMessage() + "\n");
            myconsole.textArea.append("current working directory : " + home);
        }
    }
    private static String home() {
        File here = new File(".");
        return here.getAbsolutePath();
    }
}
