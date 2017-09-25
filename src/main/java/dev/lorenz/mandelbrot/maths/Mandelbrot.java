package dev.lorenz.mandelbrot.maths;

import dev.lorenz.mandelbrot.Utils;

import java.awt.*;
import java.awt.image.BufferedImage;

public final class Mandelbrot {
	private static final int MAX = 800;
	private static final double LOG_TWO = Math.log(2);
	private static final double INVERSE_LOG_TWO = 1 / LOG_TWO;

	private Mandelbrot() {
	}

	/**
	 * Returns the color of a point C
	 *
	 * @param c The complex number c
	 * @return The color of the point c
	 */
	private static int getColor(final Complex c) {
		double a = c.a, b = c.b;
		double a2, b2;

		int n;
		for (n = 0; n < MAX; n++) {
			a2 = a * a;
			b2 = b * b;

			if (a2 + b2 >= 4)
				break;

			b = a * b;
			b += b + c.b; // b = (2 * a * b) + c.b
			a = a2 - b2 + c.a;

			//cpy = cpy.mul(cpy).add(c); // Too inefficient
		}

		final double gradient = n + LOG_TWO - Math.log(Math.log(a * a + b * b)) * INVERSE_LOG_TWO;
		return Color.HSBtoRGB((float) (.7 + .01 * gradient), .9f, 1 - n / MAX);
	}

	public static BufferedImage drawMandelbrot(int width, int height, Complex min, Complex max) {

		final int[] pixels = new int[width * height];
		final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		// "step" between each point in the coordinates
		Complex step = max.sub(min).pmul(1.0 / width, 1.0 / height);

		Utils.pointStream(0, 0, width, height)
				.parallel()
				.forEach(p -> {
					final int c = getColor(step.pmul(p.getX(), p.getY()).add(min));
					pixels[(int) p.getY() * width + (int) p.getX()] = c;
				});

		image.setRGB(0, 0, width, height, pixels, 0, width);
		return image;
	}
}
