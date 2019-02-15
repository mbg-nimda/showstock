/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simula;

import java.util.List;

/**
 *
 * @author nimda
 */
class Batch {

    public final String day;
    public final double data;
    public final Poly poly;
    public final Evaluation ev;
    public final Poly subpoly;
    public final Evaluation subev;
    public final String phase;
    public final String subphase;
    public final double delta;
    public final double dev;
    public final double subdelta;
    public final double subdev;

    public Batch(List<Stock> stockList) {
        day = stockList.get(0).date;
        data = stockList.get(0).data;
        //System.out.println(day + " " + stockList.size());
        poly = new Poly(stockList);        
        ev = new Evaluation(poly);
        subpoly = new Poly(stockList.subList(0, stockList.size() / 8));
        subev = new Evaluation(subpoly);
        phase = ev.phase;
        subphase = subev.phase;
        delta = Math.sqrt(poly.delta);
        dev = (stockList.get(0).data - poly.value(0)) / delta;
        subdelta = Math.sqrt(subpoly.delta);
        subdev = (stockList.get(0).data - subpoly.value(0)) / subdelta;
    }
}
