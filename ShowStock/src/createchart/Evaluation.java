/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package createchart;

/**
 *
 * @author nimda
 */
public class Evaluation {
	public double[] coeff = null;
	public int day1;
	public double val1;
	public int day2;
	public double val2;
	public String phase;
        
        public Evaluation(Poly p) {
            	double a = 3 * p.coeff[3];
		double b = 2 * p.coeff[2];
		double c = p.coeff[1];
		double D = b * b - 4 * a * c;
		if (D < 0) {
			day1 = 1000000;
			val1 = 0;
			day2 = -1000000;
			val2 = 0;
			if (a < 0)
				phase = "G";
			else
				phase = "H";
		} else {
			day1 = (int) ((-b + Math.sqrt(D)) / (2.0 * a));
			val1 = p.value(day1);
			day2 = (int) ((-b - Math.sqrt(D)) / (2.0 * a));
			val2 = p.value(day2);
			if (a < 0) {
				if (day1 > 0 && day2 > 0)
					phase = "A";
				if (day1 < 0 && day2 > 0)
					phase = "B";
				if (day1 < 0 && day2 < 0)
					phase = "C";
			} else {
				if (day1 > 0 && day2 > 0)
					phase = "D";
				if (day1 > 0 && day2 < 0)
					phase = "E";
				if (day1 < 0 && day2 < 0)
					phase = "F";
			}
		}

        }
}
