package dev.lorenz.mandelbrot.maths;

/**
 * Created by lorenz on 08.02.17.
 */
public final class Complex implements Cloneable {
	public final double a;
	public final double b;

	public Complex(double a, double b) {
		this.a = a;
		this.b = b;
	}

	public Complex add(Complex c) {
		return new Complex(a + c.a, b + c.b);
	}

//	public Complex add(double r) {
//		return new Complex(a + r, b);
//	}

//	public Complex padd(double x, double y) {
//		return new Complex(a + x, b + y);
//	}


	public Complex sub(Complex c) {
		return new Complex(a - c.a, b - c.b);
	}

//	public Complex sub(double r) {
//		return new Complex(a - r, b);
//	}

//	public Complex mul(Complex c) {
//		// (a_1 + i b_1)(a_2 + i b_2)
//		// = a_1 (a_2 + i b_2) + i b_1 (a_2 + i b_2)
//		// = a_1 a_2 +  a_1 i b_2 + i b_1 a_2 + i b_1 i b_2
//		// = a_1 a_2 - b_1 b_2 +  i (a_1 b_2 + b_1 a_2)
//		return new Complex(a * c.a - b * c.b, a * c.b + b * c.a);
//	}

	public Complex mul(double s) {
		return new Complex(s * a, s * b);
	}

	public Complex pmul(double x, double y) {
		return new Complex(a * x, b * y);
	}

//	public Complex neq() {
//		return new Complex(a, -b);
//	}

//	public double len2() {
//		return a * a + b * b;
//	}

	public Complex copy() {
		return new Complex(a, b);
	}

	@Override
	public Complex clone() {
		return copy();
	}

	@Override
	public String toString() {
		return String.format("%.4f + %.4fi", a, b);
	}
}
