package com.hit.gui;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class App {

	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				JFrame app = new MainFrame("Cache Books");
				app.setSize(700, 650);
				app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				app.setVisible(true);
			}
		});
	}

}
