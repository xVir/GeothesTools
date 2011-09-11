package edu.skachkov.geothestools.integration.entities;

public class Point extends IntegrationEntity {

	private final static String POINT_FORMAT = "POINT(%s %s)";

	private double longitude;
	private double latitude;

	@Override
	public String GetSQL() {

		String pointDescription = String.format(GEO_FROM_TEXT_FORMAT, toString());

		return String.format(ROW_FORMAT, 
				pointDescription);

	}

	@Override
	public String toString() {
		return String.format(POINT_FORMAT, longitude, latitude);
	}

}
