/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simula;

import java.util.Objects;

/**
 *
 * @author nimda
 */
public class Stock {

    public String date;
    public Double data;

    public Stock(String date, Double data) {
        this.date = date;
        this.data = data;
    }

    Stock(Stock el) {
        this.date = el.date;
        this.data = el.data;
    }

    @Override
    public String toString() {
        return this.date + " " + this.data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Stock stock = (Stock) o;
        return date.equals(stock.date)
                && Objects.equals(data, stock.data);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + Objects.hashCode(this.date);
        hash = 37 * hash + Objects.hashCode(this.data);
        return hash;
    }

}
