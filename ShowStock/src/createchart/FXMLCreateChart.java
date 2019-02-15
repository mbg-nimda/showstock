/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package createchart;

import console.Console;
import static createchart.CreateChart.console;
import databasemanager.DatabaseManager;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import static java.lang.String.format;
import populate.Populate;
import yahoo_finance.Yahoo_Finance;

/**
 *
 * @author nimda
 */
public class FXMLCreateChart implements Initializable {

    @FXML
    public ComboBox tickers;
    public String ticker;
    public DatePicker enddate;
    public TextField window;
    public LineChart linechart;
    public NumberAxis yaxis;
    public Label tickname;
    public TextField incr;
    public Label delta;
    public Label phase;
    public Label day1;
    public Label val1;
    public Label day2;
    public Label val2;
    public Label close;
    public Label high;
    public Label polyv;
    public Label low;
    public Label start;
    public TextField day;
    public Label date;

    public double[] values;
    public String[] dats;
    private static Connection conn;
    public static Statement stm;
    private static ResultSet rs;
    private String edate;
    private Poly poly;
    private boolean NormalView = true;

    public FXMLCreateChart() {
        this.values = new double[10000];
        this.dats = new String[10000];
    }

    @FXML
    private void calcdate(ActionEvent event) {
        String choice = day.getText();
        int i = Integer.parseInt(choice);
        date.setText(dats[i]);
        double del = Math.sqrt(poly.delta);
        close.setText(Double.toString(values[i]));
        high.setText(Double.toString(poly.value(i) + del));
        polyv.setText(Double.toString(poly.value(i)));
        low.setText(Double.toString(poly.value(i) - del));
    }

    @FXML
    private void addTicker(ActionEvent event) {        
        DatabaseManager.manageTickers(console);
    }

    @FXML
    private void populate(ActionEvent event) {
        Populate.populate(console);
    }

    @FXML
    private void download(ActionEvent event) {
        Yahoo_Finance.main(null);
    }

    @FXML
    private void predict(ActionEvent event) {
        try {
            PrintWriter out = new PrintWriter(String.format("E:/Bolero/%s.csv", ticker));
            for (int i = 0; i < 512; i++) {
                out.println(values[i] - poly.value(i));
            }
            out.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FXMLCreateChart.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void alarms(ActionEvent event) {
        String[] args = {ticker, window.getText(), enddate.getValue().toString()};
    }

    @FXML
    private void report(ActionEvent event) {
        try {
            String repdate = enddate.getValue().toString();
            try (PrintWriter out = new PrintWriter(String.format("%s/report.csv", 
                    CreateChart.home))) {
                ObservableList items = tickers.getItems();
                String maxs = window.getText();
                int max = Integer.parseInt(maxs);
                Consumer<String> prt = (tck) -> {
                    ticker = tck;
                    enddate.setValue(LocalDate.parse(repdate));
                    getdata(event);
                    poly = new Poly(values, max);
                    Evaluation ev = new Evaluation(poly);
                    out.println(format("%s | %s | %s | %d | %.2f | %d | %.2f | %.4f",
                            enddate.getValue().toString(),
                            ticker, ev.phase,
                            ev.day1, ev.val1,
                            ev.day2, ev.val2,
                            Math.sqrt(poly.delta)));
                };
                items.forEach(prt);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FXMLCreateChart.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void displaydeviation(ActionEvent event) {
        NormalView = false;
        displaydata(event);
    }

    @FXML
    private void displaynormal(ActionEvent event) {
        NormalView = true;
        displaydata(event);
    }

    @FXML
    private void setwindow(ActionEvent event) {
        System.out.println(event.toString());
    }

    @FXML
    private void nextday(ActionEvent event) {
        try {
            String cmd = format("select min(dat) m from stocks s, tickers t where "
                    + "s.tick = t._id and t.symb like \"%s\" and dat > \"%s\";", ticker, edate);
            rs = stm.executeQuery(cmd);
            rs.next();
            enddate.setValue(LocalDate.parse(rs.getString("m")));
            getdata(event);
            displaydata(event);
        } catch (SQLException ex) {
            Logger.getLogger(FXMLCreateChart.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void prevday(ActionEvent event) {
        try {
            String cmd = format("select max(dat) m from stocks s, tickers t where "
                    + "s.tick = t._id and t.symb like \"%s\" and dat < \"%s\";", ticker, edate);
            rs = stm.executeQuery(cmd);
            rs.next();
            enddate.setValue(LocalDate.parse(rs.getString("m")));
            getdata(event);
            displaydata(event);
        } catch (SQLException ex) {
            Logger.getLogger(FXMLCreateChart.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

//    @FXML
//    private void minimise(ActionEvent event) {
//        double odelta;
//        odelta = poly.delta;
//        int incri = 1;
//        String wins = window.getText();
//        int win = Integer.parseInt(wins);
//        win += incri;
//        window.setText(Integer.toString(win));
//        displaydata(event);
//        incri = (poly.delta < odelta) ? 1 : -1;
//        if (incri == -1) {
//            win += incri;
//            window.setText(Integer.toString(win));
//            displaydata(event);
//        }
//        System.out.println(format("--%d--", incri));
//        while (true) {
//            odelta = poly.delta;
//            win += incri;
//            window.setText(Integer.toString(win));
//            displaydata(event);
//            System.out.println(format("%d  %.4f %.4f", win, poly.delta, odelta));
//            if (poly.delta > odelta) {
//                break;
//            }
//        }
//        win -= incri;
//        window.setText(Integer.toString(win));
//        displaydata(event);
//    }

    @FXML
    private void incrwindow(ActionEvent event) {
        String wins = window.getText();
        int win = Integer.parseInt(wins);
        String incs = incr.getText();
        int inc = Integer.parseInt(incs);
        win += inc;
        window.setText(Integer.toString(win));
        displaydata(event);
    }

    @FXML
    private void decrwindow(ActionEvent event) {
        String wins = window.getText();
        int win = Integer.parseInt(wins);
        String incs = incr.getText();
        int inc = Integer.parseInt(incs);
        win -= inc;
        window.setText(Integer.toString(win));
        displaydata(event);
    }

    @FXML
    private void getticker(ActionEvent event) {
        try {
            Object value = tickers.getValue();
            ticker = value.toString();
            String cmd = format("select name from tickers t where "
                    + "t.symb like \"%s\" ", ticker);
            //System.out.println(cmd);
            rs = stm.executeQuery(cmd);
            rs.next();
            tickname.setText(rs.getString(1));
            enddate.setValue(LocalDate.now());
        } catch (SQLException ex) {
            Logger.getLogger(FXMLCreateChart.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void getdate(ActionEvent event) {
        Object value = enddate.getValue();
        edate = value.toString();
    }

    @FXML
    private void displaydata(ActionEvent event) {
        linechart.getData().clear();
        XYChart.Series seriesv = new XYChart.Series();
        XYChart.Series seriesp = new XYChart.Series();
        XYChart.Series seriesm = new XYChart.Series();
        XYChart.Series seriese = new XYChart.Series();
        String maxs = window.getText();
        int max = Integer.parseInt(maxs);
        poly = new Poly(values, max);
        double del = Math.sqrt(poly.delta);
        double min = 1e6;
        double sup = -1e6;
        if (NormalView) {
            for (int i = 0; i < max; i++) {
                if (values[i] <= min) {
                    min = values[i];
                }
                if (values[i] >= sup) {
                    sup = values[i];
                }
                seriesv.getData().add(new XYChart.Data(-i, values[i]));
                seriese.getData().add(new XYChart.Data(-i, poly.value(i)));
                seriesp.getData().add(new XYChart.Data(-i, poly.value(i) + del));
                seriesm.getData().add(new XYChart.Data(-i, poly.value(i) - del));
            }
            linechart.getData().add(seriese);
        } else {
            for (int i = 0; i < max; i++) {
                seriesv.getData().add(new XYChart.Data(-i, values[i] - poly.value(i)));
                seriesp.getData().add(new XYChart.Data(-i, del));
                seriesm.getData().add(new XYChart.Data(-i, -del));
            }
        }
        linechart.setHorizontalZeroLineVisible(!NormalView);
        linechart.getData().add(seriesv);
        linechart.getData().add(seriesp);
        linechart.getData().add(seriesm);
        yaxis.setAutoRanging(!NormalView);
        if (NormalView) {
            yaxis.setLowerBound(min);
            yaxis.setUpperBound(sup);
        }
        LocalDate loda = LocalDate.parse(dats[max - 1]);
        String dates = loda.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        start.setText(dates);
        String deltas = Double.toString(Math.sqrt(poly.delta));
        delta.setText(deltas);
        Evaluation ev = new Evaluation(poly);
        String phases = ev.phase;
        phase.setText(phases);
        String day1s = Integer.toString(ev.day1);
        String val1s = format("%.2f", ev.val1);
        String day2s = Integer.toString(ev.day2);
        String val2s = format("%.2f", ev.val2);
        day1.setText(day1s);
        val1.setText(val1s);
        day2.setText(day2s);
        val2.setText(val2s);
    }

    @FXML
    private void getdata(ActionEvent event) {
        int j = 0;
        try {
            String cmd = format("select close, dat from stocks s, tickers t where "
                    + "s.tick = t._id and t.symb like \"%s\" and dat <= \"%s\" order by dat desc;", ticker,
                    edate);
            //System.out.println(cmd);
            rs = stm.executeQuery(cmd);
            while (rs.next()) {
                if (rs.getString("close") == null) {
                    continue;
                }
                values[j] = Double.valueOf(rs.getString("close"));
                dats[j] = rs.getString("dat");
                if (j == 0) {
                    enddate.setValue(LocalDate.parse(dats[0]));
                    close.setText(Double.toString(values[0]));
                }
//                System.out.println(dats[j] + " " + values[j]);
                j++;
            }
        } catch (SQLException | NullPointerException ex) {
            System.out.println("error at :" + j);
            Logger.getLogger(FXMLCreateChart.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ObservableList<String> data;
        data = FXCollections.observableArrayList();
        try {
            console = new Console();
            Class.forName("org.sqlite.JDBC");
            String cnnct = String.format("jdbc:sqlite:%s/stocks.db", CreateChart.home);
            conn = DriverManager.getConnection(cnnct);
            stm = conn.createStatement();
            rs = stm.executeQuery("select symb from tickers order by symb;");
            int j = 0;
            while (rs.next()) {
                data.add(rs.getString("symb"));
                j++;
            }
            tickers.setItems(data);
            enddate.setValue(LocalDate.now());
            edate = enddate.getValue().toString();
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(FXMLCreateChart.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
