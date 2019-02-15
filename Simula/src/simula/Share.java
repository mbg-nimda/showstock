/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simula;

import java.time.Instant;
import java.util.Date;
import java.util.Locale;

/**
 *
 * @author nimda
 */
public class Share {
    public boolean isBuy;
    public String date;
    public double price;

    public Share(boolean isBuy, String date, double price) {
        this.isBuy = isBuy;
        this.date = date;
        this.price = price;
    }    
    @Override
    public String toString() {
        return String.format(Locale.UK, "%s on %s at %f €", this.isBuy?"Buy":"Sell", this.date, this.price);
    }
}
