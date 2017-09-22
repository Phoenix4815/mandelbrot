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

				Complex diff = max.sub(min); // Viewport in complex number plane

				double ratioX = p.getX() / width, ratioY = p.getY() / height; // Ratio Point : Window width

				Complex c = min.add(diff.pmul(ratioX, ratioY)); // Point in number plane

				//TODO: Improve zoom.
				max = c.add(diff.mul(.4));
				min = c.sub(diff.mul(.4));

				repaint();
			}

		};
		addMouseListener(listener);

		JPopupMenu menu = new JPopupMenu();

		JMenuItem standard = new JMenuItem("Reset Zoom");
		standard.addActionListener(e -> {
			resetBounds();

			repaint();
		});
		menu.add(standard);

		JMenuItem goto0 = new JMenuItem("Set Bounds");
		goto0.addActionListener(e -> {
			try {
				double minReal, minIm, maxReal;

				minReal = Double.parseDouble(JOptionPane.showInputDialog(this,
						"Please enter the minimal X-Value:", min.a));
				minIm = Double.parseDouble(JOptionPane.showInputDialog(this,
						"Please enter the minimal Y-Value:", min.b));
				maxReal = Double.parseDouble(JOptionPane.showInputDialog(this,
						"Please enter the maximal X-Value:", max.a));

				min = new Complex(minReal, minIm);
				max = Utils.fixBounds(getWidth(), getHeight(), min, maxReal);

				repaint();
			} catch (NumberFormatException | NullPointerException ignored) {

			}
		});
		menu.add(goto0);

		menu.addSeparator();

		JMenuItem save = new JMenuItem("Save Screenshot");
		save.addActionListener(e -> {
			try {
				// Ask for resolution
				int width = Integer.parseInt(JOptionPane.showInputDialog(this,
						"Width of the image:", getWidth()));
				int height = (width * getHeight()) / getWidth();

				// Ask for file path
				JFileChooser chooser = new JFileChooser();

				if (chooser.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) {
					JOptionPane.showMessageDialog(this,
							"No path given!");
					return;
				}

				File file = chooser.getSelectedFile();
				if(!file.getName().toLowerCase().endsWith(".png"));
				file = new File(file.getAbsoluteFile() + ".png");

				if (file.exists()) {
					JOptionPane.showMessageDialog(this,
							"File already exists!");
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
		g.drawString("Loading...", 25, 25);

		// trigger redrawing
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

				// update image cache
				image = Mandelbrot.drawMandelbrot(getWidth(), getHeight(), min, max);

				paintImage(getGraphics());
			}
		}
	}
}
