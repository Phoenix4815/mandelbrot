package dev.lorenz.mandelbrot;

import dev.lorenz.mandelbrot.maths.Complex;
import dev.lorenz.mandelbrot.maths.Mandelbrot;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.*;

public final class MPanel extends JPanel implements Runnable {

	private static final long serialVersionUID = 1L;

	private Complex min, max;
	private volatile Image image;

	public MPanel() {
		resetBounds();

		Thread painterThread = new Thread(this);
		painterThread.setDaemon(true);
		painterThread.start();

		ComponentAdapter adapter = new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				super.componentResized(e);
				// Fix Component Bounds
				max = Utils.fixBounds(getWidth(), getHeight(), min, max.a);
			}
		};
		addComponentListener(adapter);

		MouseAdapter listener = new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {

				if (e.getButton() != MouseEvent.BUTTON1)
					return;

				double width = getWidth(), height = getHeight();

				Point p = e.getPoint();
				//p.setLocation(p.getX(), height - p.getY());

				Complex diff = max.sub(min); // Sichtfenster in komplexer Zahlenebene

				double ratioX = p.getX() / width, ratioY = p.getY() / height; // Verhältnis Punkt : Fensterbreite

				Complex c = min.add(diff.pmul(ratioX, ratioY)); // Punkt in Zahlenebene

				//TODO: Zoom verbessern.
				max = c.add(diff.mul(.4));
				min = c.sub(diff.mul(.4));

				repaint();
			}

		};
		addMouseListener(listener);

		JPopupMenu menu = new JPopupMenu();

		JMenuItem standard = new JMenuItem("Zoom zurücksetzen");
		standard.addActionListener(e -> {
			resetBounds();

			repaint();
		});
		menu.add(standard);

		JMenuItem goto0 = new JMenuItem("Ausschnitt festlegen");
		goto0.addActionListener(e -> {
			try {
				double minReal, minIm, maxReal;

				minReal = Double.parseDouble(JOptionPane.showInputDialog(this,
						"Bitte minimalen X-Wert eingeben:", min.a));
				minIm = Double.parseDouble(JOptionPane.showInputDialog(this,
						"Bitte minimalen Y-Wert eingeben:", min.b));
				maxReal = Double.parseDouble(JOptionPane.showInputDialog(this,
						"Bitte maximalen X-Wert eingeben:", max.a));

				min = new Complex(minReal, minIm);
				max = Utils.fixBounds(getWidth(), getHeight(), min, maxReal);

				repaint();
			} catch (NumberFormatException | NullPointerException ignored) {

			}
		});
		menu.add(goto0);

		menu.addSeparator();

		JMenuItem save = new JMenuItem("Bild speichern");
		save.addActionListener(e -> {
			try {
				// Auflösung auswählen
				int width = Integer.parseInt(JOptionPane.showInputDialog(this,
						"Breite des Bildes:", getWidth()));
				int height = (width * getHeight()) / getWidth();

				// Dateipfad auswählen
				JFileChooser chooser = new JFileChooser();

				if (chooser.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) {
					JOptionPane.showMessageDialog(this,
							"Kein Dateipfad ausgewählt");
					return;
				}

				File file = chooser.getSelectedFile();
				if(!file.getName().toLowerCase().endsWith(".png"));
				file = new File(file.getAbsoluteFile() + ".png");

				if (file.exists()) {
					JOptionPane.showMessageDialog(this,
							"Datei existiert bereits");
					return;
				}

				Utils.saveImage(width, height, file, min, max);
			} catch (NumberFormatException | NullPointerException ex) {
				ex.printStackTrace();
			}
		});
		menu.add(save);

		setComponentPopupMenu(menu);
		setInheritsPopupMenu(true);
	}

	private void resetBounds() {
		//-2, -1, 2
		min = new Complex(-2.1, -1.15);
		max = Utils.fixBounds(getWidth(), getHeight(), min, 2);
	}

	@Override
	public synchronized void paint(Graphics g) {
		super.paint(g);

		paintImage(g);
		g.setColor(Color.WHITE);
		g.setFont(new Font("Arial", Font.BOLD, 18));
		g.drawString("Lade...", 25, 25);

		// Neuzeichenprozess anstoßen
		notify();
	}

	private void paintImage(Graphics g) {
		// Anti-Aliasing
		Graphics2D g2 = (Graphics2D) getGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);

		g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
	}

	@Override
	public void run() {
		while(!Thread.currentThread().isInterrupted()) {
			synchronized (this) {
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				// Bild-Cache aktualisieren
				image = Mandelbrot.drawMandelbrot(getWidth(), getHeight(), min, max);

				paintImage(getGraphics());
			}
		}
	}
}
