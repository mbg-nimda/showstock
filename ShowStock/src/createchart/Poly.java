/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package createchart;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import static java.lang.String.format;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nimda
 */
public class Poly {
	public double[] coeff;        
	public double delta = 0;
	public Integer days = 0;

	public Poly(double[] y, Integer d) {
		coeff = getCoefficients(y, d);
		days = d;
		for (int i = 0; i < days; i++) {
			double v = y[i] - value(i);
			delta += v * v;
		}
		delta /= days;
	}

	private static double[] getCoefficients(double[] yy, Integer d) {
		Matrix Mat = new Matrix(d);
		double[] z = new double[4];
		z[0] = 0;
		for (int i = 1; i <= d; i++)
			z[0] += yy[i - 1];
		z[1] = 0;
		for (int i = 1; i <= d; i++)
			z[1] += yy[i - 1] * i;
		z[2] = 0;
		for (int i = 1; i <= d; i++)
			z[2] += yy[i - 1] * i * i;
		z[3] = 0;
		for (int i = 1; i <= d; i++)
			z[3] += yy[i - 1] * i * i * i;
		return Mat.multiply(z);
	}

        public double value(double d) {
		double ret = 0;
		for (int i = 3; i >= 0; i--)
			ret = ret * d + coeff[i];
		return ret;
	}

        void print(String fname) {
            PrintWriter out = null;
            try {
                out = new PrintWriter(fname);
                out.println(format("%f, %f, %f, %f ", coeff[0], coeff[1], coeff[2], coeff[3]));
                out.close();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Poly.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                out.close();
            }
        }
}

