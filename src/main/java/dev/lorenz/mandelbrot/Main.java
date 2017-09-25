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
		super("Mandelbrotmenge");
		setSize(1000, 600);
		setContentPane(new MPanel());

		// Fenstericon setzen... genius :D
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
				"Willkommen im Zeichenprogramm zur Mandelbrotmenge von Lorenz Jetter!\n" +
						"> Zum Vergrößern eines Bereichs, klicken.\n" +
						"> Rechtsklick für weitere Optionen.\n" +
						"HINWEIS: Das Zeichnen der einzelnen Bilder kann u.U. mehrere Sekunden dauern.");
	}

}
