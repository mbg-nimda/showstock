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
public class Matrix {
	public static double[][] M;

	public double[] multiply(double[] x) {
		double[][] A = M;
		int m = A.length;
		int n = A[0].length;
		if (x.length != n)
			throw new RuntimeException("Illegal matrix dimensions.");
		double[] y = new double[m];
		for (int i = 0; i < m; i++) {
			y[i] = 0;
			for (int j = 0; j < n; j++)
				y[i] += (A[i][j] * x[j]);
		}
		return y;
	}

	public Matrix(double n) {
		M = new double[][] {
				{
						(16 * pow(n, 3) + 24 * pow(n, 2) + 56 * n + 24)
								/ (pow(n, 4) - 6 * pow(n, 3) + 11 * pow(n, 2) - 6 * n),
						(-120 * pow(n, 2) - 120 * n - 100)
								/ (pow(n, 4) - 6 * pow(n, 3) + 11 * pow(n, 2) - 6 * n),
						(240 * n + 120)
								/ (pow(n, 4) - 6 * pow(n, 3) + 11 * pow(n, 2) - 6 * n),
						(-140)
								/ (pow(n, 4) - 6 * pow(n, 3) + 11 * pow(n, 2) - 6 * n) },
				{
						(-120 * pow(n, 2) - 120 * n - 100)
								/ (pow(n, 4) - 6 * pow(n, 3) + 11 * pow(n, 2) - 6 * n),
						(1200 * pow(n, 4) + 5400 * pow(n, 3) + 8400 * pow(n, 2)
								+ 6000 * n + 2200)
								/ (pow(n, 7) - 14 * pow(n, 5) + 49 * pow(n, 3) - 36 * n),
						(-2700 * pow(n, 2) - 6300 * n - 3000)
								/ (pow(n, 6) - pow(n, 5) - 13 * pow(n, 4) + 13
										* pow(n, 3) + 36 * pow(n, 2) - 36 * n),
						(1680 * pow(n, 2) + 4200 * n + 3080)
								/ (pow(n, 7) - 14 * pow(n, 5) + 49 * pow(n, 3) - 36 * n) },
				{
						(240 * n + 120)
								/ (pow(n, 4) - 6 * pow(n, 3) + 11 * pow(n, 2) - 6 * n),
						(-2700 * pow(n, 2) - 6300 * n - 3000)
								/ (pow(n, 6) - pow(n, 5) - 13 * pow(n, 4) + 13
										* pow(n, 3) + 36 * pow(n, 2) - 36 * n),
						(6480 * pow(n, 2) + 12600 * n + 4680)
								/ (pow(n, 7) - 14 * pow(n, 5) + 49 * pow(n, 3) - 36 * n),
						(-4200)
								/ (pow(n, 6) - pow(n, 5) - 13 * pow(n, 4) + 13
										* pow(n, 3) + 36 * pow(n, 2) - 36 * n) },
				{
						(-140)
								/ (pow(n, 4) - 6 * pow(n, 3) + 11 * pow(n, 2) - 6 * n),
						(1680 * pow(n, 2) + 4200 * n + 3080)
								/ (pow(n, 7) - 14 * pow(n, 5) + 49 * pow(n, 3) - 36 * n),
						(-4200)
								/ (pow(n, 6) - pow(n, 5) - 13 * pow(n, 4) + 13
										* pow(n, 3) + 36 * pow(n, 2) - 36 * n),
						(2800) / (pow(n, 7) - 14 * pow(n, 5) + 49 * pow(n, 3) - 36 * n) } };
	}

	private double pow(double n, int i) {
		double ret = 1;
		for (int k = 0; k < i; k++)
			ret *= n;
		return ret;
	}
}
