package hci.components;

import hci.database.ReadWriteDatabase;
import hci.utils.Tag;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.awt.GridLayout;
import java.awt.Panel;

/**
 * Main class of the program - handles display of the main window
 * @author Michal
 *
 */
public class ImageLabeller extends JFrame {

	private static final long serialVersionUID = 1L;

	//the panel that has the menu bar
	MenuBarPanel menuBarPanel = null;
	//the panel that has all the main buttons
	ToolBoxPanel toolBoxPanel = null;
	//the panel that displays the image lists, the current image and the corresponding tags
	MainPanel mainPanel = null;

	ReadWriteDatabase dbs = null;


	/**
	 * sets up application window
	 * @param imageFilename image to be loaded for editing
	 * @throws Exception
	 */
	public void setupGUI() throws Exception {
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent event) {
				//here we exit the program (maybe we should ask if the user really wants to do it?)
				//maybe we also want to store the polygons somewhere? and read them next time
				
				JOptionPane.showMessageDialog(null, "The last changes to the image have been saved", "Info", JOptionPane.INFORMATION_MESSAGE);
				

				String oldImageName = mainPanel.getImagePanel().getImageName();
				ArrayList <Tag> oldTagsList =  mainPanel.getImagePanel().getTagsList();		

				dbs.modifyImageTags(oldImageName, oldTagsList);
				
				System.exit(0);
				
				
			}
		});		

		dbs = new ReadWriteDatabase();

		ArrayList<String> imagesNames = dbs.getImagesNames();


		menuBarPanel = new MenuBarPanel();
		this.setJMenuBar(menuBarPanel);


		//setup main window panel
		JPanel appPanel = new JPanel(new BorderLayout());

		this.setContentPane(appPanel);

		// main Panel
		mainPanel = new MainPanel(dbs);

		for(String s:imagesNames)
			mainPanel.updateImageList(s);
		appPanel.add(mainPanel.getSplitPane(),BorderLayout.CENTER);
		mainPanel.setVisible(true);

		//create a tool box panel
		toolBoxPanel = new ToolBoxPanel(mainPanel, dbs);
		appPanel.add(toolBoxPanel,BorderLayout.PAGE_START);
		toolBoxPanel.setVisible(true);

		//display all the stuff
		this.pack();
		this.setVisible(true);
	}

	public ImagePanel getImagePanel() {
		return mainPanel.getImagePanel();
	}

	public JFrame getFrame() {
		return this;
	}
}
