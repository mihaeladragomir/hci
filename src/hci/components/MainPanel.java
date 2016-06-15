package hci.components;


import hci.database.ReadWriteDatabase;
import hci.utils.Tag;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import java.util.*;


public class MainPanel extends JPanel implements ListSelectionListener{
	private JPopupMenu popupMenu;
	private JList imagesList;
	private JList labelsList;
	private static DefaultListModel imageListModel;
	private static DefaultListModel labelListModel;


	private ImagePanel imagePanel;
	private JSplitPane labelsSplitPane;

	private ReadWriteDatabase dbs;

	public void updateImageList(String imageName) {
		imageListModel.addElement(imageName);

	}

	public void updateLabelsList(ArrayList<String> labelsList){
		for(int i = 0; i < labelsList.size(); i++)
			labelListModel.addElement(labelsList.get(i));
	}
	
	//class is nested
	class popupListener extends MouseAdapter
	{
		public void mousePressed(MouseEvent e)
		{  maybeShowPopup(e);  }

		public void mouseReleased(MouseEvent e)
		{  maybeShowPopup(e); }

		private void maybeShowPopup(MouseEvent e)
		{
			if(e.isPopupTrigger())
			{
				popupMenu=new JPopupMenu(); 
				//String label = JOptionPane.showInputDialog(this, "");
				//popupMenu.add(JOptionPane.showInputDialog(this, "new label name"));

				JMenuItem delete, edit;

				delete = new JMenuItem("delete");
				edit = new JMenuItem("edit");

				edit.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						//String label = JOptionPane.showInputDialog(null, this, "Please enter a name for the selected object", null, "Label name");
						String label = JOptionPane.showInputDialog("Please enter the name of the new label", "Label name");
						int index = labelsList.getSelectedIndex();
						String oldLabel = (String)labelListModel.getElementAt(index);
						System.out.println(index);
						if (label!=null){
							labelListModel.setElementAt(label, index);
							imagePanel.editLabelTag(index, label);
						}
					}
				});

				delete.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						int index = labelsList.getSelectedIndex();
						int answer = JOptionPane.showConfirmDialog(null,
								"Are you sure you want to delete this label? \n By deleting this label the corresponding marked object will be erased!", "Delete label", JOptionPane.YES_NO_OPTION);
						if (answer == JOptionPane.YES_OPTION){
							labelListModel.remove(index);
							imagePanel.deleteTag(index);
						}
					}
				});

				popupMenu.add(edit);
				popupMenu.add(delete);                      

				popupMenu.show(e.getComponent(),e.getX(),e.getY());
				int row=labelsList.locationToIndex(e.getPoint());
				labelsList.setSelectedIndex(row);
			}
		}
	}

	public MainPanel(ReadWriteDatabase dbs) {
		this.dbs = dbs;

		// Create and populate the list model for the image names list
		imageListModel = new DefaultListModel();
		imagesList = new JList (imageListModel);
		imagesList.setName("imagesList");
		imagesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		imagesList.setToolTipText("My collection of images.You can add an image by using the Upload button.");
		imagesList.setSelectedIndex(0);
		imagesList.addListSelectionListener(this); //add Selection listener to the imagesList

		JScrollPane imagesListScrollPane = new JScrollPane(imagesList); //makes the imagesList the client of the ScrollPane
		imagesListScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

		JPanel display = new JPanel(new BorderLayout());
		imagePanel = new ImagePanel();
		JScrollPane pictureScrollPane = new JScrollPane(imagePanel);

		display.add(pictureScrollPane, BorderLayout.PAGE_START);

		//Create a split pane with the two scroll panes in it.
		JSplitPane imagesSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,imagesListScrollPane, display);
		imagesSplitPane.setOneTouchExpandable(true);
		//imagesSplitPane.setPreferredSize(new Dimension(100,100));
		imagesSplitPane.setDividerLocation(150);

		//Create and populate the list model for the labels JList
		labelListModel = new DefaultListModel();
		ArrayList<String> labels = imagePanel.getLabelsList();
		for(String s: labels)
		{
			System.out.println(s);
			labelListModel.addElement(s);
		}

		//create the JList to display the labels and control its data using a DefaultListModel
		labelsList = new JList (labelListModel);
		labelsList.setName("labelsList");
		labelsList.setToolTipText("Current image labels list. You can edit and delete these.");
		labelsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		labelsList.setSelectedIndex(0);
		labelsList.addListSelectionListener(this);
		

		MouseListener popupListener=new popupListener();
		labelsList.addMouseListener(popupListener);

		JScrollPane labelsListScrollPane = new JScrollPane(labelsList); //makes the labelsList the client of the ScrollPane
		labelsListScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

		//add the labels JList to a panel
		JPanel labelsPanel = new JPanel();
		labelsPanel.add(labelsList);

		labelsSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,imagesSplitPane, labelsPanel);
		labelsSplitPane.setOneTouchExpandable(true);
		//labelsSplitPane.setPreferredSize(new Dimension(600,00));
		//labelsSplitPane.setDividerLocation(labelsSplitPane.getMaximumDividerLocation());
		labelsSplitPane.setDividerLocation(850);
		
	}

	//Listens to the two lists: imagesList and labelsList
	public void valueChanged(ListSelectionEvent evt) {
		final JList labelsList = (JList)evt.getSource();

		if (!evt.getValueIsAdjusting()&&(labelsList.getName().equals("imagesList"))) {
			System.out.print("Source: "+evt.getSource());

			String oldImageName = imagePanel.getImageName();
			ArrayList <Tag> oldTagsList = imagePanel.getTagsList();		

			dbs.modifyImageTags(oldImageName, oldTagsList);
			
			
			labelListModel.clear();

			// new image selected
			String newImageName = (String)imageListModel.getElementAt(labelsList.getSelectedIndex());

			System.out.println("Selected item: " + newImageName);

			String imagePath = "./src/hci/local/";
			imagePath = imagePath + newImageName; 

			ArrayList<String> labelsNames = dbs.getImageLabels(newImageName);
			ArrayList<Tag> tagsList = dbs.getImageTags(newImageName);
			for (String label: labelsNames)
			{
				System.out.println(label);
				labelListModel.addElement(label);
			}


			try{   
				imagePanel.updateImagePanel(imagePath, newImageName, tagsList);
				//imagePanel.updateTagsList(imageName);

			} catch (Exception excep) {
				excep.printStackTrace();
			}
		}
		if (!evt.getValueIsAdjusting()&&(labelsList.getName().equals("labelsList"))){
			labelsList.setSelectionForeground(Color.BLUE);

			//vreau highlight marked object
			//pop up in care pot sa editez/delete label
			//actualizeaza labelListModel&TagsList
			

		}
		
	}

	public JSplitPane getSplitPane(){
		return labelsSplitPane;
	}

	public ImagePanel getImagePanel(){
		return imagePanel;
	}

	public DefaultListModel getImageListModel() {
		return imageListModel;
	}

	public static DefaultListModel getLabelListModel() {
		return labelListModel;
	}

	public static void setLabelListModel(DefaultListModel labelListModel) {
		MainPanel.labelListModel = labelListModel;
	}


}
