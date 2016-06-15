package hci;


import hci.components.ImageLabeller;

import java.awt.Frame;

public class Engine {


	/**
	 * Runs the program
	 * @param argv path to an image
	 */
	public static void main(String argv[]) {
		try {
			//create a window and display the image
			ImageLabeller window = new ImageLabeller();
			//window.setExtendedState(Frame.MAXIMIZED_BOTH);
			window.pack();
			window.setupGUI();
		} catch (Exception e) {
			System.err.println("Image: ");
			e.printStackTrace();
		}
	}
}
