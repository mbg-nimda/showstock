/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simula;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nimda
 */
public class Simula {

    static public List<Stock> stocks;
    static public int window;
    static public String tick;
    static public String today;
    private static Connection conn;
    public static Statement stm;
    private static ResultSet rs;
    private static int firstid;
    public static PrintWriter out;
    private static double Maa, Mab, Mac, Mad, Mbb, Mbc, Mbd, Mcc, Mcd, Mdd;
    private static double Sa, Sb, Sc, Sd, Ha, Hb, Hc, Hd, Ba, Bb, Bc, Bd;
    private static int tick_id;

    public static void main(String[] args) {
        try {
            tick = args[0];
            out = new PrintWriter(String.format("%s.csv", tick));
            stocks = new ArrayList<>();
            window = Integer.valueOf(args[1]);
            today = args[2];
            init();
            for (firstid = window; firstid < stocks.size(); firstid++) {
                Batch batch = new Batch(stocks.subList(firstid - window, firstid));
                phases(batch);
            }
            alerts();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Simula.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    static void init() {
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:E:/Bolero/stocks.db");
            stm = conn.createStatement();
            String query
                    = String.format("select close, dat from stocks s, tickers t where symb = \"%s\" "
                            + "and s.tick = t._id and dat <= \"%s\" order by dat desc;",
                            tick, today);
            rs = stm.executeQuery(query);
            while (rs.next()) {
                Stock stock = new Stock(rs.getString("dat"),
                        rs.getDouble("close"));
                stocks.add(stock);
            }
            query = String.format(Locale.ENGLISH, "select _id from tickers where symb = \"%s\"", tick);
            rs = stm.executeQuery(query);
            tick_id = rs.getInt("_id");
            query = "delete from trades";
            stm.executeUpdate(query);
              query = String.format("insert into trades(dat, tick, data) "
                    + "select dat, tick, close from stocks where tick = %d "
                    + "and dat < \"%s\" order by dat desc", tick_id, today);
            stm.executeUpdate(query);
            System.out.println(stocks.size());
            Maa = Mab = Mac = Mad = Mbb = Mbc = Mbd = Mcc = Mcd = Mdd = 0;
            firstid = stocks.size();
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(Simula.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void alerts() {
        boolean buy = true;
        Stock last = null;
        List<Stock> subList = stocks.subList(stocks.size() - 1 - window / 8, stocks.size());
        Stock[] extrema = extrema(subList);
        firstid = stocks.indexOf(extrema[1]);
        last = extrema[1];
        while (firstid >= window / 8) {
            subList = stocks.subList(firstid - window / 8, firstid + 1);
            extrema = extrema(subList);
            Stock stock = buy ? extrema[0] : extrema[1];
            firstid = stocks.indexOf(stock);           
            String query;
            if (buy) {
                query = String.format("update trades set est = "
                        + "(select (julianday(dat)-julianday(\"%s\")) /"
                        + "(julianday(\"%s\")-julianday(\"%s\"))) "
                        + "where tick = %d and dat between \"%s\" and \"%s\"",
                        last.date, stock.date, last.date,
                        tick_id, last.date, stock.date);
            } else {
                query = String.format("update trades set est = "
                        + "(select (julianday(\"%s\")-julianday(dat)) /"
                        + "(julianday(\"%s\")-julianday(\"%s\"))) "
                        + "where tick = %d and dat between \"%s\" and \"%s\"",
                        stock.date, stock.date, last.date,
                        tick_id, last.date, stock.date);
            }
            try {
                stm.executeUpdate(query);
            } catch (SQLException ex) {
                Logger.getLogger(Simula.class.getName()).log(Level.SEVERE, null, ex);
            }
            last = stock;
            buy = !buy;
        }
    }

    private static Stock[] extrema(List<Stock> subList) {
        Stock max = new Stock("", 0d);
        Stock min = new Stock("", 1e6);
        Stock[] ret = new Stock[2];
        for (Stock el : subList) {
            if (el.data > max.data) {
                max = new Stock(el);
            }
            if (el.data < min.data) {
                min = new Stock(el);
            }
        }
        ret[0] = max;
        ret[1] = min;
        return ret;
        //return({max, min});        
    }

    private static void phases(Batch batch) {
        try {
            char phase = batch.phase.charAt(0);
            char subphase = batch.subphase.charAt(0);
            String query = String.format(Locale.ENGLISH, "update trades set tick = %d, "
                    + "dat = \"%s\", phase = \"%s\", subphase = \"%s\", dev = %f, subdev = %f",
                    tick_id, batch.day, phase, subphase, batch.dev, batch.subdev);
            System.out.println(query);
            stm.executeUpdate(query);
        } catch (SQLException ex) {
            Logger.getLogger(Simula.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
