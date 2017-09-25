package dev.lorenz.mandelbrot;

import dev.lorenz.mandelbrot.maths.Complex;
import dev.lorenz.mandelbrot.maths.Mandelbrot;

import javax.swing.*;

/**
 * @author Lorenz
 *
 */
public final class Main extends JFrame {

	private static final long serialVersionUID = 1L;

	public Main() {
		super("Mandelbrot Fractal");
		setSize(1000, 600);
		setContentPane(new MPanel());

		// Set window icon
		final Complex min = new Complex(-1.59, -0.98);
		setIconImage(Mandelbrot.drawMandelbrot(100, 100,
				min, Utils.fixBounds(100, 100, min, .51)));
	}
	
	public static void main(String[] args) {
		Main main = new Main();
//		main.setAlwaysOnTop(true);
		main.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		main.setLocationByPlatform(true);
		main.setVisible(true);

		JOptionPane.showMessageDialog(main,
				"Welcome to the Mandelbrot Plotter of Phoenix4815!\n" +
						"> Left click to zoom in.\n" +
						"> Right click for more options.");
	}

}
