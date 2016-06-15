package hci.database;
import hci.utils.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*import javax.xml.parsers.DocumentBuilder;
	import javax.xml.parsers.DocumentBuilderFactory;*/

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class ReadWriteDatabase {	
	private static Document parsedDoc;
	private static Point currentPoint;
	private static String file="database.xml";
	private static Tag tag;

	//returns a parsed document
	public static Document parseDocument(String filePath){
		try{
			SAXBuilder builder = new SAXBuilder();
			File xmlFile = new File(filePath);
			parsedDoc = (Document) builder.build(xmlFile);   		      		     			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return parsedDoc;
	}


	//checks whether an image is in the database or not
	public static  boolean imageInDbs(String imageName){
		boolean inDbs = false;
		List<Element> images = getImageElements(parsedDoc);
		for (Element img:images){
			if (img.getChild("name").getText().equals(imageName)){
				inDbs=true;
			}
		}
		return inDbs;
	}

	//returns a list of all image elements from a parsed document
	public static List<Element> getImageElements(Document parsedDoc){
		Element root = parseDocument(file).getRootElement();
		List<Element> images = root.getChildren("image");
		return images;
	}

	//returns a list of all the image names in the XML file
	public ArrayList<String> getImagesNames(){
		ArrayList<String> allImageNames = new ArrayList<String>();
		List<Element> imagesList = getImageElements(parsedDoc);
		for (Element img:imagesList){
			allImageNames.add(img.getChild("name").getText());
		}
		return allImageNames;
	}


	//returns a list of all the labels for a image
	public  ArrayList<String> getImageLabels(String imageName){
		ArrayList<String> labelsList= new ArrayList<String>();			
		List<Element> images = getImageElements(parsedDoc);
		for (int i=0; i<images.size();i++){
			Element image = images.get(i);
			if (image.getChild("name").getValue().equals(imageName)){
				List<Element> tags = image.getChildren("tag");
				for (int j=0; j<tags.size(); j++){
					Element tag = tags.get(j);
					String label = tag.getChildText("tname");
					labelsList.add(label);

				}
			}
		}
		return labelsList;
	}

	//returns a list of all the points corresponding to a particular tag from an image
	public  ArrayList<Point> getTagPoints(String imageName, String label){
		ArrayList<Point> pointsList= new ArrayList<Point>();
		Element root = parseDocument(file).getRootElement(); 

		List<Element> images = root.getChildren("image");
		for (int i=0; i<images.size();i++){
			Element image = images.get(i);
			
			if (image.getChild("name").getValue().equals(imageName)){
				List<Element> tags = image.getChildren("tag");
				for (int j=0; j<tags.size(); j++){
					Element tag = tags.get(j);
					String currentLabel = tag.getChildText("tname");
					
					if (tag.getChild("tname").getValue().equals(label)){
						List<Element> points = tag.getChildren("point");
						for (int k=0; k<points.size(); k++){
							Element point = points.get(k);
							int xcoord =Integer.parseInt(point.getChildText("xcoord"));
							int ycoord = Integer.parseInt(point.getChildText("ycoord"));
							currentPoint = new Point(xcoord,ycoord);
							pointsList.add(currentPoint);
						}
					}
				}
			}
		}
		return pointsList;
	}

	//return a list of Tags for a particular image
	public ArrayList <Tag> getImageTags(String imageName){
		ArrayList<Tag> tagsList=new ArrayList <Tag>();
		ArrayList <String> labels=getImageLabels(imageName);
		for (int i=0; i<labels.size();i++){
			Tag tag=new Tag(labels.get(i),getTagPoints(imageName, labels.get(i)));
			tagsList.add(tag);
		}

		return tagsList;
	}



	//adds an new image element (without any labelling done)
	public static  void addNewImage( String imageName, String imagePath){
		if (imageInDbs(imageName)==false){
			Element image = new Element("image");
			image.addContent(new Element("name").setText(imageName));
			image.addContent(new Element("path").setText(imagePath));
			parsedDoc.getRootElement().addContent(image);
			writeModifications();
		}
		else System.out.println("The image is already in the database");
	}

	public static void addNewTag(String imageName, String label, ArrayList<Point> pointsList){
		for (Element image: getImageElements(parsedDoc)){
			if (image.getChild("name").getText().equals(imageName)){
				Element tag = new Element("tag");
				tag.addContent(new Element("tname").setText(label));
				for (int i=0; i<pointsList.size(); i++){
					Element point = new Element("point");
					point.addContent(new Element("xcoord").setText(String.valueOf(pointsList.get(i).getX())));
					point.addContent(new Element("ycoord").setText(String.valueOf(pointsList.get(i).getY())));
					tag.addContent(point);
				}
				image.addContent(tag);
				writeModifications();
			}
		}

	}
	
	//??adds a new tag (label and corresponding polygon coordinates) to a given image
	public void storeNewTag(String imageName, String label, ArrayList<Point> pointsList){
		for (Element image: getImageElements(parsedDoc)){
			if (image.getChild("name").getText().equals(imageName)){
				Element tag = new Element("tag");
				tag.addContent(new Element("tname").setText(label));
				for (int i=0; i<=pointsList.size(); i++){
					Element point = new Element("point");
					point.addContent(new Element("xcoord").setText(""+pointsList.get(i).getX()));
					point.addContent(new Element("ycoord").setText(""+pointsList.get(i).getY()));
					tag.addContent(point);
				}
				image.addContent(tag);

			}
		}

	}


	public void modifyImageTags(String imageName, ArrayList<Tag> tagsList){
		for (Element image: getImageElements(parsedDoc)){
			if (image.getChild("name").getText().equals(imageName)){
				deleteElemImageTags(image);
				for (Tag t:tagsList){
					
					addNewTag(imageName, t.getLabel(), t.getPolygon());

				}
			}

		}

	}

	public void modifyTagLabel(String imageName, String label, ArrayList<Point> pointsList){
		for (Element image: getImageElements(parsedDoc)){
			if (image.getChild("name").getText().equals(imageName)){
				List<Element> imageTags = image.getChildren("tag"); 
				for (Element currentTag:imageTags){
					if (currentTag.getChild("tname").getText().equals(label)){
						currentTag.getChild("tname").setText(label);
						writeModifications();
					}
				}
			}
		}

	}

	//modifies existing tag coordinates
	public void modifyTagCoord(){

	}
	
	//TODO Este gresita!! nu o folosii!
	//deletes an entire image element ( with all it's children)
	public static void deleteImage(String imageName){
		Document doc = parseDocument(file);
		Element root = doc.getRootElement();
		String sr = root.getName();
		List<Element> imagesList= root.getChildren("image");
		for (Element image:imagesList){
			if (image.getChild("name").getText().equals(imageName)){
				root.removeContent(image);
				writeModifications();
			}
		}
	}


	public static void deleteImageTags(String imageName){
		for (Element image: getImageElements(parsedDoc)){
			if (image.getChild("name").getText().equals(imageName)){
				image.removeChildren("tag");
				writeModifications();
			}
		}
	}

	public void deleteElemImageTags(Element image){
		image.removeChildren("tag");
		writeModifications();

	}


	//writes any modifications made in the XML document
	public static void writeModifications(){
		try{
			XMLOutputter xmlOutput = new XMLOutputter();

			// display nice nice
			xmlOutput.setFormat(Format.getPrettyFormat());
			xmlOutput.output(parsedDoc, new FileWriter(file));

			System.out.println("File updated!");

		} catch (IOException io) {

			io.printStackTrace();
		}
	}



/*
	public static void main(String[] args) {
		deleteImage("20121006_084743.jpg");
		deleteImage("image4.jpg");
		deleteImage("image6.jpg");
		System.out.println("image added");
		deleteImage("image7.jpg");
		deleteImage("image8.jpg");
		deleteImage("image11.jpg");
		deleteImage("image12.jpg");
		
		
		
		ArrayList<Point> pointsList = new ArrayList<Point>();
		pointsList.add(new Point(2,3));
		//addNewTag("image4", "dude", pointsList);
		addNewTag("image4", "bou", pointsList );
		deleteImageTags("image4");
		 
		try {

			SAXBuilder builder = new SAXBuilder();
			File xmlFile = new File("database.xml");

			Document doc = (Document) builder.build(xmlFile);
			Element root = doc.getRootElement();




			XMLOutputter xmlOutput = new XMLOutputter();

			// display nice nice
			xmlOutput.setFormat(Format.getPrettyFormat());
			xmlOutput.output(doc, new FileWriter(file));

			System.out.println("File updated!");

		  } catch (IOException io) {

			io.printStackTrace();
		  } catch (JDOMException e) {

			e.printStackTrace();
		  }
	}*/
}
