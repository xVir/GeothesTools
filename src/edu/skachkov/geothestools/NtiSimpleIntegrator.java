package edu.skachkov.geothestools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.google.code.geocoder.Geocoder;
import com.google.code.geocoder.model.GeocodeResponse;
import com.google.code.geocoder.model.GeocoderGeometry;
import com.google.code.geocoder.model.GeocoderRequest;
import com.google.code.geocoder.model.GeocoderResult;

public class NtiSimpleIntegrator {

	private static Connection connection;

	/**
	 * Integrates geodata into NTI database
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			connection = DriverManager.getConnection(
					"jdbc:postgresql://127.0.0.1:5432/geothes1", "postgres",
					"postgres");
			
			Geocoder geocoder = new Geocoder();
			
			GeocoderRequest geocoderRequest = new GeocoderRequest("Озеро Байкал", "ru");
			GeocodeResponse geocodeResponse = geocoder.geocode(geocoderRequest);
			
			
			
			for (GeocoderResult result : geocodeResponse.getResults()){
				System.out.println(result);
				
				GeocoderGeometry geometry = result.getGeometry();
				System.out.println(geometry);
				
				double longitude = geometry.getLocation().getLng().doubleValue();
				double latitude = geometry.getLocation().getLat().doubleValue();
				
				System.out.println(longitude);
				System.out.println(latitude);
				
			}
			
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	
	

}
