package edu.skachkov.geothestools.integration.entities;

import com.google.code.geocoder.model.GeocodeResponse;
import com.google.code.geocoder.model.GeocoderGeometry;
import com.google.code.geocoder.model.GeocoderResult;

public class Point extends IntegrationEntity {

	private final static String POINT_FORMAT = "POINT(%s %s)";

	private double longitude;
	private double latitude;
	
	

	public Point(double longitude, double latitude) {
		super();
		this.longitude = longitude;
		this.latitude = latitude;
	}
	
	public Point(GeocodeResponse response){
		super();
		
		if (response.getResults().size() > 0) {
			GeocoderResult result = response.getResults().get(0);
			
			GeocoderGeometry geom = result.getGeometry();
			
			longitude = geom.getLocation().getLng().doubleValue();
			latitude = geom.getLocation().getLat().doubleValue();
		}
	}

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
