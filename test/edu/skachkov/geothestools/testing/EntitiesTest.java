package edu.skachkov.geothestools.testing;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.google.code.geocoder.Geocoder;
import com.google.code.geocoder.model.GeocodeResponse;
import com.google.code.geocoder.model.GeocoderRequest;

import edu.skachkov.geothestools.integration.entities.Point;
import edu.skachkov.geothestools.integration.entities.Rectangle;

public class EntitiesTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void pointTest() {
		Point point = new Point(-110, 30);
		
		assertEquals("POINT(-110.0 30.0)", point.toString());
		assertEquals("ROW(ST_GeographyFromText('POINT(-110.0 30.0)'))", point.GetSQL());
	}
	
	@Test
	public void createPointFromGeocodeTest(){
		Geocoder geocoder = new Geocoder();
		
		GeocoderRequest geocoderRequest = new GeocoderRequest("Озеро Байкал", "ru");
		GeocodeResponse geocodeResponse = geocoder.geocode(geocoderRequest);
		
		Point p = new Point(geocodeResponse);
		
		assertEquals("POINT(107.6625 53.1736)", p.toString());
	}
	
	@Test
	public void rectangleTest(){
		Rectangle rectangle = new Rectangle(new Point(10, 10), new Point(20, 20));
		
		assertEquals("POINT(10.0 10.0),POINT(20.0 20.0)", rectangle.toString());
		assertEquals("ROW(ST_GeographyFromText('POINT(10.0 10.0)'),ST_GeographyFromText('POINT(20.0 20.0)'))", 
				rectangle.GetSQL());
	}
	

}
