package edu.skachkov.geothestools.integration.entities;

public class Rectangle extends IntegrationEntity {
	
	private static final String RECTANGLE_FORMAT = "%s,%s";
	
	private Point p1,p2;
	
	

	public Rectangle(Point p1, Point p2) {
		super();
		this.p1 = p1;
		this.p2 = p2;
	}

	@Override
	public String toString() {
		return String.format(RECTANGLE_FORMAT, p1.toString(),p2.toString());
	}

	@Override
	public String GetSQL() {
		
		String rectangleDescription = String.format(RECTANGLE_FORMAT, geoFromText(p1),geoFromText(p2));
		return String.format(ROW_FORMAT, rectangleDescription);
		
	}

	private String geoFromText(Point p) {
		return String.format(GEO_FROM_TEXT_FORMAT, p.toString());
	}
	
}
