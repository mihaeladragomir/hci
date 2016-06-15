package hci.components;

import java.awt.Color;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class MenuBarPanel extends JMenuBar {

	public MenuBarPanel() {
		
		JMenu fileMenu = new JMenu("File");
		JMenu editMenu = new JMenu("Edit");
		JMenu viewMenu = new JMenu("View");
		JMenu helpMenu = new JMenu("Help");
		JMenuItem fileMenuItem, editMenuItem, viewMenuItem, helpMenuItem;
		
		fileMenuItem  = new JMenuItem("Upload");
		fileMenu.add(fileMenuItem);
		/*fileMenuItem  = new JMenuItem("Delete");
		fileMenu.add(fileMenuItem);*/
		fileMenu.addSeparator();
		fileMenuItem  = new JMenuItem("Save");
		fileMenu.add(fileMenuItem);
		fileMenu.addSeparator();
		fileMenuItem  = new JMenuItem("Exit");
		fileMenu.add(fileMenuItem);
		
		editMenuItem  = new JMenuItem("Undo");
		editMenu.add(editMenuItem);
		editMenuItem  = new JMenuItem("Redo");
		editMenu.add(editMenuItem);
		editMenu.addSeparator();
		editMenuItem  = new JMenuItem("Select object");
		editMenu.add(editMenuItem);
		editMenuItem  = new JMenuItem("Edit Selected object");
		editMenu.add(editMenuItem);
		editMenuItem  = new JMenuItem("Delete Selected object");
		editMenu.add(editMenuItem);
		editMenu.addSeparator();
		editMenuItem  = new JMenuItem("Add new label");
		editMenu.add(editMenuItem);
		editMenuItem  = new JMenuItem("Edit label");
		editMenu.add(editMenuItem);
		editMenuItem  = new JMenuItem("Delete label");
		editMenu.add(editMenuItem);
		
		viewMenuItem  = new JMenuItem("Zoom In");
		viewMenu.add(viewMenuItem);
		viewMenuItem  = new JMenuItem("Zoom Out");
		viewMenu.add(viewMenuItem);
		
		helpMenuItem = new JMenuItem("Help Content");
		helpMenu.add(helpMenuItem);
		
		this.add(fileMenu);
		this.add(editMenu);
		this.add(viewMenu);
		this.add(helpMenu);
	}
}
