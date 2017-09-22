package dev.lorenz.mandelbrot;

import dev.lorenz.mandelbrot.maths.Complex;
import dev.lorenz.mandelbrot.maths.Mandelbrot;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class Utils {
	private Utils() {
	}

	public static Complex fixBounds(int width, int height, Complex min, double maxReal) {
		return new Complex(maxReal, min.b + (maxReal - min.a) * ((double) height) / width);
	}

	public static void saveImage(int width, int height, File file,
			Complex min, Complex max) {
		BufferedImage image = Mandelbrot.drawMandelbrot(width, height, min, max);
		
		try {
			ImageIO.write(image, "png", file);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Couldn't save image!",
					"Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			return;
		}

		JOptionPane.showMessageDialog(null, "Saving successful!");
	}

	public static Stream<Point> pointStream(int startX, int startY, int endX, int endY) {
		return IntStream.range(startX, endX)
				.mapToObj(x -> IntStream.range(startY, endY)
						.mapToObj(y -> new Point(x, y)))
				.flatMap(s -> s);
	}
}
