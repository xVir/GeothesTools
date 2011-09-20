package edu.skachkov.geothestools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.google.code.geocoder.Geocoder;
import com.google.code.geocoder.model.GeocodeResponse;
import com.google.code.geocoder.model.GeocoderGeometry;
import com.google.code.geocoder.model.GeocoderRequest;
import com.google.code.geocoder.model.GeocoderResult;

import edu.skachkov.geothestools.integration.constants.GeoobjectTypes;
import edu.skachkov.geothestools.integration.constants.GeoreferenceTypes;
import edu.skachkov.geothestools.integration.entities.Point;

public class NtiSimpleIntegrator {

	private static boolean emulate = true;
	
	private static Connection connection;

	private static Map<String,String> knownPublicationPlaces = new HashMap<String, String>();

	private static Geocoder geocoder = new Geocoder();
	
	static{
		
		knownPublicationPlaces.put("�.", "������");
		knownPublicationPlaces.put("���", "�����-���������");
		
		knownPublicationPlaces.put("�. �.", "");
		knownPublicationPlaces.put("Irkutsk", "�������");
		
		knownPublicationPlaces.put("Ulaanbaatar", "����-�����");
		knownPublicationPlaces.put("[Oslo]", "����");
		
	}
	
	/**
	 * Integrates geodata into NTI database
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			connection = DriverManager.getConnection(
					"jdbc:postgresql://127.0.0.1:5432/geothes1", "postgres",
					"postgres");
			
			clearGeoobjectTypesTable(connection);
			fillGeoobjectTypesTable(connection);
			
			clearGeoreferenceTypesTable(connection);
			fillGeoreferenceTypesTable(connection);
			
			clearReferencestable(connection);
			
			makePublicationPlacesReferences(connection);
			
			connection.close();
			
			System.out.println("Done!");
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	
	

	}

	private static void makePublicationPlacesReferences(Connection connection2) throws SQLException {
		final String selectRecordsSQL = "select rec_cod, pub_place from records";
		
		Statement st = connection2.createStatement();
		ResultSet recordsSet = st.executeQuery(selectRecordsSQL);
		
		while (recordsSet.next()) {
			
			String recCod = recordsSet.getString(1);
			String pubPlace = recordsSet.getString(2);
		
			if (StringUtils.isNotBlank(pubPlace) && knownPublicationPlaces.containsKey(pubPlace)) {
				pubPlace = knownPublicationPlaces.get(pubPlace);//fix abbreviated location name
			}
			
			if (StringUtils.isNotBlank(pubPlace)) {
				
				Point pubPoint = getPointForPlace(pubPlace);
				
				int objectType = GeoobjectTypes.POINT_TYPE;
				int referenceType = GeoreferenceTypes.PUBPLACE_REF_ID;
				
				final String insertSQLFormat = 
						"insert into geo_references (rec_cod, type_id, object_type_id, point_data) values ('%s',%s,%s,%s)";
				
				final String insertCommand = String.format(insertSQLFormat, recCod,referenceType,objectType,pubPoint.GetSQL());
				
				if (emulate) {
					System.out.println(insertCommand);
				}
				else {
					Statement insertStatement = connection2.createStatement();
					insertStatement.execute(insertCommand);	
				}
				
				
			}
			
		}
		
	}

	private static Point getPointForPlace(String pubPlace) {
		
		GeocoderRequest geocoderRequest = new GeocoderRequest(pubPlace, "ru");
		GeocodeResponse geocodeResponse = geocoder.geocode(geocoderRequest);
		
		Point result = new Point(geocodeResponse);
		return result;
	}

	private static void clearReferencestable(Connection connection2) throws SQLException {
		final String sql = "truncate table geo_references";
		executeSql(connection2,sql);
	}

	private static void executeSql(Connection connection2, String sql) throws SQLException {
		Statement st = connection2.createStatement();
		st.execute(sql);
	}

	private static void clearGeoreferenceTypesTable(Connection connection2) throws SQLException {
		final String sql = "truncate table georeference_types CASCADE";
		executeSql(connection2, sql);
	}

	private static void clearGeoobjectTypesTable(Connection connection2) throws SQLException {
		final String sql = "truncate table geoobject_types CASCADE";
		executeSql(connection2, sql);
	}

	private static void fillGeoreferenceTypesTable(Connection connection2) throws SQLException {
		final String insertSQLFormat = "insert into georeference_types (type_id, descr, column_name) values (?, ?, ?)";
		
		PreparedStatement st = connection2.prepareStatement(insertSQLFormat);
		st.setInt(1, GeoreferenceTypes.TITLE_REF_ID);
		st.setString(2, GeoreferenceTypes.TITLE_REF_DESCR);
		st.setString(3, GeoreferenceTypes.TITLE_REF_COLUMN);
		st.executeUpdate();
		
		st.setInt(1, GeoreferenceTypes.PUBPLACE_REF_ID);
		st.setString(2, GeoreferenceTypes.PUBPLACE_REF_DESCR);
		st.setString(3, GeoreferenceTypes.PUBPLACE_REF_COLUMN);
		st.executeUpdate();
	}

	private static void fillGeoobjectTypesTable(Connection connection2) throws SQLException {
		final String insertSQLFormat = "insert into geoobject_types (object_type_id, descr) values (?,?)";
		
		PreparedStatement st = connection2.prepareStatement(insertSQLFormat);
		st.setInt(1, GeoobjectTypes.POINT_TYPE);
		st.setString(2, GeoobjectTypes.POINT_DESCR);
		st.executeUpdate();
		
		st.setInt(1, GeoobjectTypes.RECTANGLE_TYPE);
		st.setString(2, GeoobjectTypes.RECTANGLE_DESCR);
		st.executeUpdate();
		
	}
	
	

}
