package hci.components;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.ImageIcon;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import hci.database.ReadWriteDatabase;
import hci.utils.*;


import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.ImageIcon;
import javax.swing.UIManager;
import javax.swing.SwingUtilities;
/**
 * Handles image editing panel
 * @author Michal
 *
 */

public class ImagePanel extends JPanel implements MouseListener {

	private static final long serialVersionUID = 1L;

	private BufferedImage image = null;
	private ArrayList<Point> currentPolygon = null;//currentPolygon=currentTag.getPolygon()--in !null
	private ArrayList<Tag> tagsList = null;

	private String imageName = "";

	public ImagePanel() {
		setBorder(BorderFactory.createLineBorder(Color.black));
		currentPolygon = new ArrayList<Point> ();
		tagsList = new ArrayList<Tag> ();	
		addMouseListener(this);
	}

	public Dimension getPreferredSize() {
		return new Dimension(800,600);
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		g.drawImage(image, 0, 0, null);

		Tag tag;

		for(int i = 0; i < tagsList.size(); i++)
		{
			tag = tagsList.get(i);
			ArrayList <Point> polygon = tag.getPolygon();

			if (polygon.size() > 0)
			{
				Point currentPoint = polygon.get(0);



				( (Graphics2D)g).setStroke(new BasicStroke(3));
				for(int j = 1; j < polygon.size(); j++)
				{

					( (Graphics2D)g).drawLine(currentPoint.getX(), currentPoint.getY(), polygon.get(j).getX(), polygon.get(j).getY());

					currentPoint = polygon.get(j);

				}
				( (Graphics2D)g).drawLine(currentPoint.getX(), currentPoint.getY(), polygon.get(0).getX(), polygon.get(0).getY());

			}

		}
	}


	public void updateImagePanel(String imagePath, String imageName, ArrayList<Tag> tagsList) throws Exception {
		currentPolygon.clear();
		this.tagsList.clear();
		this.tagsList = tagsList;		

		System.out.println("update image panel: " + imagePath);
		this.imageName = imageName;
		try {
			image = ImageIO.read(new File(imagePath));
			if (image.getWidth() > 800 || image.getHeight() > 600) {
				int newWidth = image.getWidth() > 800 ? 800 : (image.getWidth() * 600)/image.getHeight();
				int newHeight = image.getHeight() > 600 ? 600 : (image.getHeight() * 800)/image.getWidth();
				System.out.println("SCALING TO " + newWidth + "x" + newHeight );
				Image scaledImage = image.getScaledInstance(newWidth, newHeight, Image.SCALE_FAST);
				image = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
				image.getGraphics().drawImage(scaledImage, 0, 0, this);
			}
		} catch (Exception excep) {
			excep.printStackTrace();
		}

		repaint();
	}

	public void updateTagsList(){

	}

	public void addTag() {
		Graphics2D g = (Graphics2D)this.getGraphics();
		g.setColor(Color.MAGENTA);
		if (currentPolygon.size() > 1) {
			Point firstVertex = currentPolygon.get(0);
			Point lastVertex = currentPolygon.get(currentPolygon.size() - 1);

			g.setColor(Color.GREEN);
			g.drawLine(firstVertex.getX(), firstVertex.getY(), lastVertex.getX(), lastVertex.getY());
		}

		String label = JOptionPane.showInputDialog(this, "Please enter a name for the selected object", "Label name");
		tagsList.add(new Tag(label, currentPolygon));
	}


	@Override
	public void mouseClicked(MouseEvent e)  {
		int x = e.getX();
		int y = e.getY();

		//check if the cursor is within image area
		if(image == null)
			return;
		
			
		if (x > image.getWidth() || y > image.getHeight()) 
				//if not do nothing
			return;
		
		

		Graphics2D g = (Graphics2D)this.getGraphics();

		//if the left button than we will add a vertex to polygon
		if (e.getButton() == MouseEvent.BUTTON1) {
			g.setColor(Color.GREEN);
			if (currentPolygon.size() != 0) {
				Point lastVertex = currentPolygon.get(currentPolygon.size() - 1);
				g.drawLine(lastVertex.getX(), lastVertex.getY(), x, y);
			}
			g.fillOval(x-10,y-10,20,20);

			currentPolygon.add(new Point(x,y));
			System.out.println(x + " " + y);


			Point firstVertex=currentPolygon.get(0);

			// closing the polygon 	
			if (currentPolygon.size() >= 3  && 
					x > firstVertex.getX() - 20 &&
					x < firstVertex.getX() + 20 && 
					y > firstVertex.getY() - 20 &&
					y < firstVertex.getY()+20 ) {

				//String label = "label";
				int confirmLabel = JOptionPane.showConfirmDialog(null,
						"Do you want to save this object?", "Save", JOptionPane.YES_NO_OPTION);
				
				if(confirmLabel == JOptionPane.YES_OPTION) {
					String label = JOptionPane.showInputDialog(this, "Please enter a name for the selected object", "Label name");
					/*if(label == "\n")
						JOptionPane.showMessageDialog(null, "You must give a name to the object!", "Error", JOptionPane.ERROR_MESSAGE);
					*/
					MainPanel.getLabelListModel().addElement(label);
					tagsList.add(new Tag(label, currentPolygon));
					currentPolygon = new ArrayList<Point>();	
				}
				else {
					JOptionPane.showMessageDialog(null,
							"The underlying object will be deleted", "Info", JOptionPane.INFORMATION_MESSAGE);
					currentPolygon.clear();
					
				}
						

				this.repaint();				 	
				
			}

		} 
	}

	public void setTagsList(ArrayList<Tag> tagsList){

		this.tagsList = tagsList;	

	}

	public ArrayList<Tag> getTagsList(){
		return tagsList;		 
	}

	public void editLabelTag(int index, String labelName){
		Tag t = tagsList.get(index);
		t.setLabel(labelName);
	}

	public void deleteTag(int index){
		tagsList.remove(index);
		System.out.println("Size of tags list " + tagsList.size());
		this.repaint();
	}

	public String getImageName(){
		return imageName;
	}

	public ArrayList<String> getLabelsList() {
		System.out.println(tagsList.size());
		ArrayList<String> labels = new ArrayList<String> ();
		for(Tag t: tagsList)
			labels.add(t.getLabel());
		return labels;
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
	}

}


