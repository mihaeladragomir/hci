package hci.utils;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.*;

/**
 * Class for scaling an image.
 * @author M&M
 *
 */
public class ImageScaling {
	
	private  int w /*IMG_WIDTH;*/;
    private  int h /*IMG_HEIGHT*/;
 
	
	/*
	 * Image scaling
	 * */
	public void scale(String imageName){

	    try{
	 
	        BufferedImage originalImage = ImageIO.read(new File(imageName));
	        int type = originalImage.getType() == 0? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
	        w=originalImage.getWidth()/4; 
	        h=originalImage.getHeight()/4;
	 
	        BufferedImage resizedImageJpg = resizeImage(originalImage, type);
	        ImageIO.write(resizedImageJpg, "jpg", new File(imageName)); 
	 
	        
	    }catch(IOException e){
	        System.out.println(e.getMessage());
	  }
    }
	 
	
	/*
	 * 
	 * */
	    private BufferedImage resizeImage(BufferedImage originalImage, int type){
		    BufferedImage resizedImage = new BufferedImage(/*IMG_WIDTH, IMG_HEIGHT,*/w, h, type);
		    Graphics2D g = resizedImage.createGraphics();
		    g.drawImage(originalImage, 0, 0, /*IMG_WIDTH, IMG_HEIGHT,*/w, h, null);
		    g.dispose();
		 
		 return resizedImage;
	    }
	    
	 
	    /*
		 * 
		 * */
	    private BufferedImage resizeImageWithHint(BufferedImage originalImage, int type){
	 
		    BufferedImage resizedImage = new BufferedImage(/*IMG_WIDTH, IMG_HEIGHT,*/w, h, type);
		    Graphics2D g = resizedImage.createGraphics();
		    g.drawImage(originalImage, 0, 0, /*IMG_WIDTH, IMG_HEIGHT,*/ w, h, null);
		    g.dispose();    
		    g.setComposite(AlphaComposite.Src);
		 
		    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
		    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		    g.setRenderingHint(RenderingHints.KEY_RENDERING,
		    RenderingHints.VALUE_RENDER_QUALITY);
		    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		    RenderingHints.VALUE_ANTIALIAS_ON);
	 
	    return resizedImage;
	    }    

}
