package hci.components;

import hci.database.ReadWriteDatabase;
import hci.utils.Tag;

import java.awt.Color;
import java.awt.Dimension;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.text.Document;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.ArrayList;



public class ToolBoxPanel extends JPanel {

	private ReadWriteDatabase dbs;
	private JButton uploadButton,saveButton, deleteImgButton, markButton, zoomInButton,
	zoomOutButton, undoButton, redoButton, helpButton, addLabelButton, editLabelButton,
	deleteLabelButton, deleteMarkButton, editMarkButton, markObjectButton= null;
	private MainPanel mainPanel = null;



	public void copyFile(File srcFile, File destFile) throws IOException {
		InputStream inStream = null;
		OutputStream outStream = null;

		try {
			inStream  = new FileInputStream(srcFile);
			outStream = new FileOutputStream(destFile);

			byte[] buffer = new byte[1024];

			int length;
			//copy the file content in bytes 
			while ((length = inStream.read(buffer)) > 0) {
				outStream.write(buffer, 0, length);
			}

			inStream.close();
			outStream.close();

			System.out.println("File copied successful from" + " " + srcFile.getPath() + " to " + destFile.getPath());

		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	public File uploadFile (JPanel imgPanel){
		File file = null ;
		String filePath, fileName="";
		JFileChooser fc = new JFileChooser();
		int returnVal = fc.showOpenDialog(imgPanel);

		if (returnVal == JFileChooser.APPROVE_OPTION) { 
			file = fc.getSelectedFile();
		}
		return file;
	}


	public void addActionListenerLabel(JButton labelButton) {
		labelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mainPanel.getImagePanel().addTag();
			}
		});
	}

	public void addActionListenerSave(JButton saveButton) {
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String oldImageName = mainPanel.getImagePanel().getImageName();
				ArrayList <Tag> oldTagsList =  mainPanel.getImagePanel().getTagsList();		

				dbs.modifyImageTags(oldImageName, oldTagsList);

				JOptionPane.showMessageDialog(null, "You have succesfully saved the modifications for all images", "Info", JOptionPane.INFORMATION_MESSAGE);

			}
		});
	}

	public void addActionListenerUpload(JButton uploadButton) {
		uploadButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String text = (String)e.getActionCommand().trim();
				if (text.equals("Upload")){

					File imageFile = uploadFile(mainPanel.getImagePanel());
					if (imageFile!=null){
						try {

							String imageName = imageFile.getName();
							String imagePath = "./src/hci/local/"+imageName; 
							if (!dbs.imageInDbs(imageName))
							{
								File imageFileLocal = new File(imagePath);
								copyFile(imageFile, imageFileLocal);

								dbs.addNewImage(imageName, imagePath);
								mainPanel.updateImageList(imageName);
								ArrayList<Tag> tagsList = new ArrayList <Tag> ();
								mainPanel.getImagePanel().updateImagePanel(imagePath, imageName, tagsList );
								mainPanel.getLabelListModel().clear();
							}

							else
							{
								System.out.println("ds");
								JOptionPane.showMessageDialog(null, "The image you try to upload is already in the collection!", "Info", JOptionPane.INFORMATION_MESSAGE);
								// let the user know that he has uploaded that image and that 
								// it appears in the left side.
							}


						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}						
					}
				}
			}});
	}


	public ToolBoxPanel(MainPanel mainPanel, ReadWriteDatabase dbs) {
		this.dbs = dbs;
		this.mainPanel = mainPanel;
		Color myColor = new Color(238, 235, 255);

		//Undo button
		ImageIcon undo = new ImageIcon("./images/undo.jpg");
		undoButton = new JButton("Undo",undo);
		undoButton.setVerticalTextPosition(SwingConstants.BOTTOM);
		undoButton.setHorizontalTextPosition(SwingConstants.CENTER);
		undoButton.setVisible(true);
		undoButton.setBackground(myColor);
		undoButton.setSize(50, 20);
		undoButton.setEnabled(true);
		undoButton.setToolTipText("Click to undo last action");
		undoButton.setFocusPainted(false);
		this.addActionListenerUpload(undoButton);

		//Redo button
		ImageIcon redo = new ImageIcon("./images/redo.jpg");
		redoButton = new JButton("Redo",redo);
		redoButton.setVerticalTextPosition(SwingConstants.BOTTOM);
		redoButton.setHorizontalTextPosition(SwingConstants.CENTER);
		redoButton.setVisible(true);
		redoButton.setBackground(myColor);
		redoButton.setSize(50, 20);
		redoButton.setEnabled(true);
		redoButton.setToolTipText("Click to redo last action");
		redoButton.setFocusPainted(false);
		this.addActionListenerUpload(redoButton);

		//Upload button
		ImageIcon upload = new ImageIcon("./images/upload.jpg");
		uploadButton = new JButton("Upload",upload);
		uploadButton.setVerticalTextPosition(SwingConstants.BOTTOM);
		uploadButton.setHorizontalTextPosition(SwingConstants.CENTER);
		uploadButton.setMnemonic(KeyEvent.VK_U);
		uploadButton.setFocusPainted(false);
		uploadButton.setVisible(true);
		uploadButton.setBackground(myColor);
		uploadButton.setSize(50, 20);
		uploadButton.setEnabled(true);
		uploadButton.setToolTipText("Click to upload a new image");
		this.addActionListenerUpload(uploadButton);


		//Delete image button
		ImageIcon deleteImg = new ImageIcon("./images/deletePic.jpg");
		deleteImgButton = new JButton("Delete",deleteImg);
		deleteImgButton.setFocusPainted(false);
		deleteImgButton.setVerticalTextPosition(SwingConstants.BOTTOM);
		deleteImgButton.setHorizontalTextPosition(SwingConstants.CENTER);
		deleteImgButton.setBackground(myColor);
		deleteImgButton.setVisible(true);
		deleteImgButton.setSize(50, 20);
		deleteImgButton.setEnabled(true);
		deleteImgButton.setToolTipText("Click to delete the current image");
		this.addActionListenerUpload(deleteImgButton);

		//ZoomIn button
		ImageIcon zoomIn = new ImageIcon("./images/zoomIn.jpg");
		zoomInButton = new JButton("Zoom In",zoomIn);
		zoomInButton.setFocusPainted(false);
		zoomInButton.setVerticalTextPosition(SwingConstants.BOTTOM);
		zoomInButton.setHorizontalTextPosition(SwingConstants.CENTER);
		zoomInButton.setBackground(myColor);
		zoomInButton.setVisible(true);
		zoomInButton.setMnemonic(KeyEvent.VK_PLUS);
		zoomInButton.setSize(50, 20);
		zoomInButton.setEnabled(true);
		zoomInButton.setToolTipText("Click to zoom in.");
		this.addActionListenerUpload(zoomInButton);

		//ZoomIn button
		ImageIcon zoomOut = new ImageIcon("./images/zoomOut.jpg");
		zoomOutButton = new JButton("Zoom Out",zoomOut);
		zoomOutButton.setFocusPainted(false);
		zoomOutButton.setVerticalTextPosition(SwingConstants.BOTTOM);
		zoomOutButton.setHorizontalTextPosition(SwingConstants.CENTER);
		zoomOutButton.setBackground(myColor);
		zoomInButton.setMnemonic(KeyEvent.VK_MINUS);
		zoomOutButton.setVisible(true);
		zoomOutButton.setSize(50, 20);
		zoomOutButton.setEnabled(true);
		zoomOutButton.setToolTipText("Click to zoom out.");
		this.addActionListenerUpload(zoomOutButton);


		//Save changes(made to the picture) button
		ImageIcon save = new ImageIcon("./images/saveme.jpg");
		saveButton = new JButton("  Save   ", save);
		saveButton.setBackground(myColor);
		saveButton.setVerticalTextPosition(SwingConstants.BOTTOM);
		saveButton.setHorizontalTextPosition(SwingConstants.CENTER);
		saveButton.setMnemonic(KeyEvent.VK_S);
		saveButton.setSize(20, 10);
		saveButton.setVisible(true);
		saveButton.setFocusPainted(false);
		saveButton.setToolTipText("Click to save your changes.");
		this.addActionListenerSave(saveButton);

		// create a button for marking an object 
		ImageIcon mark = new ImageIcon("./images/draw.jpg");
		markButton = new JButton("Annotate object", mark);//, mark);
		markButton.setVerticalTextPosition(SwingConstants.BOTTOM);
		markButton.setHorizontalTextPosition(SwingConstants.CENTER);
		markButton.setBackground(myColor);
		markButton.setSize(20, 10);
		markButton.setVisible(true);
		markButton.setFocusPainted(false);
		markButton.setEnabled(true);
		markButton.setToolTipText("Click to mark a new object");
		//this.addActionListenerLabel(markButton);

		//Adjust object's contour Button
		ImageIcon editMark = new ImageIcon("./images/editMark.jpg");
		editMarkButton = new JButton("Edit Mark", editMark);
		editMarkButton.setVerticalTextPosition(SwingConstants.BOTTOM);
		editMarkButton.setHorizontalTextPosition(SwingConstants.CENTER);
		editMarkButton.setBackground(myColor);
		editMarkButton.setSize(20, 10);
		editMarkButton.setVisible(true);
		editMarkButton.setFocusPainted(false);
		editMarkButton.setEnabled(true);
		editMarkButton.setToolTipText("Click to adjust an object's contour");
		this.addActionListenerLabel(editMarkButton);


		//Delete object's contour Button
		ImageIcon deleteMark = new ImageIcon("./images/deleteMark.jpg");
		deleteMarkButton = new JButton("Delete Mark", deleteMark);
		deleteMarkButton.setVerticalTextPosition(SwingConstants.BOTTOM);
		deleteMarkButton.setHorizontalTextPosition(SwingConstants.CENTER);
		deleteMarkButton.setBackground(myColor);
		deleteMarkButton.setSize(20, 10);
		deleteMarkButton.setVisible(true);
		deleteMarkButton.setFocusPainted(false);
		deleteMarkButton.setEnabled(true);
		deleteMarkButton.setToolTipText("Click to delete an object's contour");
		this.addActionListenerLabel(deleteMarkButton);


		//Add new Label Button
		ImageIcon addLabel = new ImageIcon("./images/addLabel.jpg");
		addLabelButton = new JButton("Add label", addLabel);
		addLabelButton.setMnemonic(KeyEvent.VK_N);
		addLabelButton.setVerticalTextPosition(SwingConstants.BOTTOM);
		addLabelButton.setHorizontalTextPosition(SwingConstants.CENTER);
		addLabelButton.setBackground(myColor);
		addLabelButton.setSize(50, 20);
		addLabelButton.setEnabled(true);
		addLabelButton.setToolTipText("Click to add a new label");
		this.addActionListenerLabel(addLabelButton);		

		//Edit a Label Button
		ImageIcon editLabel = new ImageIcon("./images/editLabel.jpg");
		editLabelButton = new JButton("Edit \n label", editLabel);
		editLabelButton.setMnemonic(KeyEvent.VK_N);
		editLabelButton.setVerticalTextPosition(SwingConstants.BOTTOM);
		editLabelButton.setHorizontalTextPosition(SwingConstants.CENTER);
		editLabelButton.setBackground(myColor);
		editLabelButton.setSize(50, 20);
		editLabelButton.setEnabled(true);
		editLabelButton.setToolTipText("Click to edit a label");
		this.addActionListenerLabel(editLabelButton);

		//Delete a Label Button
		ImageIcon deleteLabel = new ImageIcon("./images/deleteLabel.jpg");
		deleteLabelButton = new JButton("Delete label", deleteLabel);
		deleteLabelButton.setMnemonic(KeyEvent.VK_N);
		deleteLabelButton.setVerticalTextPosition(SwingConstants.BOTTOM);
		deleteLabelButton.setHorizontalTextPosition(SwingConstants.CENTER);
		deleteLabelButton.setBackground(myColor);
		deleteLabelButton.setSize(50, 20);
		deleteLabelButton.setEnabled(true);
		deleteLabelButton.setToolTipText("Click to delete a label");
		this.addActionListenerLabel(deleteLabelButton);

		//Help Button
		ImageIcon help = new ImageIcon("./images/help.jpg");
		helpButton = new JButton("Help", help);
		helpButton.setVerticalTextPosition(SwingConstants.BOTTOM);
		helpButton.setHorizontalTextPosition(SwingConstants.CENTER);
		helpButton.setBackground(myColor);
		helpButton.setSize(20, 10);
		helpButton.setVisible(true);
		helpButton.setFocusPainted(false);
		helpButton.setEnabled(true);
		helpButton.setToolTipText("Help");
		this.addActionListenerLabel(helpButton);

		this.add(saveButton);
		this.add(undoButton);
		this.add(redoButton);
		
		this.add(uploadButton);
		this.add(deleteImgButton);
		this.add(zoomInButton);
		this.add(zoomOutButton);
		this.add(markButton);
	/*	this.add(editMarkButton);
		this.add(deleteMarkButton);
		this.add(addLabelButton);
		this.add(editLabelButton);
		this.add(deleteLabelButton);
		this.add(helpButton);*/




	}

}
