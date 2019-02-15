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
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nimda
 */
public class Simula_old {

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

    public static void main_old(String[] args) {

        try {
            tick = args[0];
            out = new PrintWriter(String.format("%s.csv", tick));
            stocks = new ArrayList<>();
            window = Integer.valueOf(args[1]);
            today = args[2];
            init();
            for (firstid = window; firstid < stocks.size(); firstid++) {
                Batch batch = new Batch(stocks.subList(firstid - window, firstid));
                coefficients(batch);
            }
            alerts();
            double[][] mat = {
                {Maa, Mab, Mac, Mad},
                {Mab, Mbb, Mbc, Mbd},
                {Mac, Mbc, Mcc, Mcd},
                {Mad, Mcd, Mcd, Mdd}
            };
            String query;
            query = "select sum(sell*a) from trades";
            rs = stm.executeQuery(query);
            Sa = rs.getDouble(1);
            query = "select sum(sell*b) from trades";
            rs = stm.executeQuery(query);
            Sb = rs.getDouble(1);
            query = "select sum(sell*c) from trades";
            rs = stm.executeQuery(query);
            Sc = rs.getDouble(1);
            query = "select sum(sell*dev) from trades";
            rs = stm.executeQuery(query);
            Sd = rs.getDouble(1);
            query = "select sum(hold*a) from trades";
            rs = stm.executeQuery(query);
            Ha = rs.getDouble(1);
            query = "select sum(hold*b) from trades";
            rs = stm.executeQuery(query);
            Hb = rs.getDouble(1);
            query = "select sum(hold*c) from trades";
            rs = stm.executeQuery(query);
            Hc = rs.getDouble(1);
            query = "select sum(hold*dev) from trades";
            rs = stm.executeQuery(query);
            Hd = rs.getDouble(1);
            query = "select sum(buy *a) from trades";
            rs = stm.executeQuery(query);
            Ba = rs.getDouble(1);
            query = "select sum(buy *b) from trades";
            rs = stm.executeQuery(query);
            Bb = rs.getDouble(1);
            query = "select sum(buy *c) from trades";
            rs = stm.executeQuery(query);
            Bc = rs.getDouble(1);
            query = "select sum(buy *dev) from trades";
            rs = stm.executeQuery(query);
            Bd = rs.getDouble(1);
            double[][] res_sell = {{Sa}, {Sb}, {Sc}, {Sd}};
            double[][] res_hold = {{Ha}, {Hb}, {Hc}, {Hd}};
            double[][] res_buy = {{Ba}, {Bb}, {Bc}, {Bd}};
            double[][] weights_sell = Matrix.multiply(Matrix.inverse(mat), res_sell);
            double[][] weights_hold = Matrix.multiply(Matrix.inverse(mat), res_hold);
            double[][] weights_buy = Matrix.multiply(Matrix.inverse(mat), res_buy);
            Matrix.print(out, weights_sell);
            Matrix.print(out, weights_hold);
            Matrix.print(out, weights_buy);
            query = String.format(Locale.ENGLISH, "update nn_linear set tick = %d, type = 'S', a = %f, b = %f, "
                    + "c = %f, d = %f",
                    32, weights_sell[0][0], weights_sell[1][0], weights_sell[2][0], weights_sell[3][0]);
            stm.executeUpdate(query);
            query = String.format(Locale.ENGLISH, "update nn_linear set tick = %d, type ='H', a = %f, b = %f, "
                    + "c = %f, d = %f",
                    32, weights_hold[0][0], weights_hold[1][0], weights_hold[2][0], weights_hold[3][0]);
            stm.executeUpdate(query);
            query = String.format(Locale.ENGLISH, "update nn_linear set tick = %d, type = 'B', a = %f, b = %f, "
                    + "c = %f, d = %f",
                    32, weights_buy[0][0], weights_buy[1][0], weights_buy[2][0], weights_buy[3][0]);
            stm.executeUpdate(query);
            out.close();
            System.out.println(query);
        } catch (SQLException | FileNotFoundException ex) {
            Logger.getLogger(Simula_old.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    static void init() {
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:E:/Math/polyreg/polyreg.db");
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
            query = "delete from trades";
            stm.executeUpdate(query);
            query = String.format("insert into trades(dat, tick, data, buy, hold, sell) "
                    + "select dat, tick, close, 0, 1, 0 from stocks where tick = 32 "
                    + "and dat < \"%s\" order by dat desc", today);
            stm.executeUpdate(query);
            System.out.println(stocks.size());
            Maa = Mab = Mac = Mad = Mbb = Mbc = Mbd = Mcc = Mcd = Mdd = 0;
            firstid = stocks.size();
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(Simula_old.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void coefficients(Batch batch) {
        try {
            double a = batch.poly.coeff[3];
            double b = batch.poly.coeff[2];
            double c = batch.poly.coeff[1];
            double d = batch.poly.coeff[0];
            a /= d;
            b /= d;
            c /= d;
            Maa += a * a;
            Mab += a * b;
            Mac += a * c;
            Mad += a * batch.dev;
            Mbb += b * b;
            Mbc += b * c;
            Mbd += b * batch.dev;
            Mcc += c * c;
            Mcd += c * batch.dev;
            Mdd += batch.dev * batch.dev;
            String query = String.format(Locale.ENGLISH, " update trades set "
                    + "a = %f, "
                    + "b = %f, "
                    + "c = %f, "
                    + "dev = %f, "
                    + "dat = \"%s\", "
                    + "tick =  (select _id from tickers where symb = \"%s\"), "
                    + "data = %f",
                    a, b, c, batch.dev,
                    batch.day,
                    tick,
                    batch.data
            );
            System.out.println(query);
            stm.executeUpdate(query);
        } catch (SQLException ex) {
            Logger.getLogger(Simula_old.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void coefficientsbak(Batch batch) {
        double a = batch.poly.coeff[3];
        double b = batch.poly.coeff[2];
        double c = batch.poly.coeff[1];
        double d = batch.poly.coeff[0];
        a /= d;
        b /= d;
        c /= d;
        Maa += a * a;
        Mab += a * b;
        Mac += a * c;
        Mad += a * batch.dev;
        Mbb += b * b;
        Mbc += b * c;
        Mbd += b * batch.dev;
        Mcc += c * c;
        Mcd += c * batch.dev;
        Mdd += batch.dev * batch.dev;
    }

    private static void alerts() {
        boolean buy = true;
        for (firstid = stocks.size() - 1; firstid >= window/4;) {
            List<Stock> subList = stocks.subList(firstid - window/4, firstid);
            Stock[] extrema = extrema(subList);
            Stock stock = buy ? extrema[1] : extrema[0];
            firstid = stocks.indexOf(stock);
            System.out.println((buy ? "buy   : " : " sell : ") + stock);
            String query = String.format("update trades set dat = \"%s\", tick = 32, sell = %d, hold = 0,"
                    + "buy = %d ", stock.date, buy ? 0 : 1, buy ? 1 : 0);
            try {
                stm.executeUpdate(query);
            } catch (SQLException ex) {
                Logger.getLogger(Simula_old.class.getName()).log(Level.SEVERE, null, ex);
            }
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
}
