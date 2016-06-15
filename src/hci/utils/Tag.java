package hci.utils;

import java.util.ArrayList;
import hci.utils.*;

public class Tag {

	String label;
	ArrayList<Point> polygon;

	
	public Tag() {
		label = "";
		polygon = null;
	}
	
	public Tag(String label, ArrayList<Point> polygon) {
		this.label = label;
		this.polygon = polygon;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	

	public ArrayList<Point> getPolygon() {
		return polygon;
	}

	public void setPolygon(ArrayList<Point> polygon) {
		this.polygon = polygon;
	}
	
	
}
