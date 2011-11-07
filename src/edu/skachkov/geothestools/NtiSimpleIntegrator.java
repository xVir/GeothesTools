package edu.skachkov.geothestools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.google.code.geocoder.Geocoder;
import com.google.code.geocoder.model.GeocodeResponse;
import com.google.code.geocoder.model.GeocoderRequest;

import edu.skachkov.geothestools.integration.constants.GeoobjectTypes;
import edu.skachkov.geothestools.integration.constants.GeoreferenceTypes;
import edu.skachkov.geothestools.integration.entities.Point;
import edu.skachkov.geothestools.ner.NEREngine;

public class NtiSimpleIntegrator {

	private static boolean emulate = true;

	private static Connection connection;

	private static Map<String, String> knownPublicationPlaces = new HashMap<String, String>();

	private static NEREngine nerEngine = new NEREngine();

	private static Geocoder geocoder = new Geocoder();

	static {

		knownPublicationPlaces.put("М.", "Москва");
		knownPublicationPlaces.put("СПб", "Санкт-Петербург");

		knownPublicationPlaces.put("Б. и.", "");
		knownPublicationPlaces.put("Irkutsk", "Иркутск");

		knownPublicationPlaces.put("Ulaanbaatar", "Улан-Батор");
		knownPublicationPlaces.put("[Oslo]", "Осло");

	}

	/**
	 * Integrates geodata into NTI database
	 * 
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

			// makePublicationPlacesReferences(connection);

			//makeTitleReferences(connection);
			makeAbstractreferences(connection);

			connection.close();

			System.out.println("Done!");

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	private static void makeAbstractreferences(Connection connection2)
			throws SQLException {
		final String selectRecordsSQL = "select rec_cod, abstr from abstract";
		Statement st = connection2.createStatement();
		ResultSet recordsSet = st.executeQuery(selectRecordsSQL);

		while (recordsSet.next()) {
			String recCod = recordsSet.getString(1);
			String abstr = recordsSet.getString(2);

			List<Point> abstrPoints = getPointsForAbstract(abstr);

			int objectType = GeoobjectTypes.POINT_TYPE;
			int referenceType = GeoreferenceTypes.ABSTRACT_REF_ID;

			final String insertSQLFormat = "insert into geo_references (rec_cod, type_id, object_type_id, point_data) values ('%s',%s,%s,%s)";

			for (Point p : abstrPoints) {

				final String insertCommand = String.format(insertSQLFormat,
						recCod, referenceType, objectType, p.GetSQL());

				if (emulate) {
					System.out.println(insertCommand);
				} else {
					Statement insertStatement = connection2.createStatement();
					insertStatement.execute(insertCommand);
				}
			}

		}
	}

	private static List<Point> getPointsForAbstract(String abstr) {
		List<Point> resultList = new ArrayList<Point>();

		System.out
				.println("------------------------------- Point for Abstract -------------------------------------");
		System.out.println(abstr);

		try {

			// trying determine title place

			List<String> namesInText = nerEngine.FindNamesInAbstract(abstr);

			for (String name : namesInText) {
				if (StringUtils.isNotBlank(name)) {

					System.out.println(name);

					Point p = getPointForPlace(name);

					System.out.println(p);

					resultList.add(p);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out
				.println("----------------------------End Point for Abstract -------------------------------------");

		return resultList;

	}

	private static void makeTitleReferences(Connection connection2)
			throws SQLException {
		final String selectRecordsSQL = "select rec_cod, title from records";
		Statement st = connection2.createStatement();
		ResultSet recordsSet = st.executeQuery(selectRecordsSQL);

		while (recordsSet.next()) {

			String recCod = recordsSet.getString(1);
			String title = recordsSet.getString(2);

			List<Point> titlePoints = getPointsForTitle(title);

			int objectType = GeoobjectTypes.POINT_TYPE;
			int referenceType = GeoreferenceTypes.TITLE_REF_ID;

			final String insertSQLFormat = "insert into geo_references (rec_cod, type_id, object_type_id, point_data) values ('%s',%s,%s,%s)";

			for (Point p : titlePoints) {

				final String insertCommand = String.format(insertSQLFormat,
						recCod, referenceType, objectType, p.GetSQL());

				if (emulate) {
					System.out.println(insertCommand);
				} else {
					Statement insertStatement = connection2.createStatement();
					insertStatement.execute(insertCommand);
				}
			}

		}

	}

	private static List<Point> getPointsForTitle(String title) {
		List<Point> resultList = new ArrayList<Point>();

		System.out
				.println("------------------------------- Point for Title -------------------------------------");
		System.out.println(title);

		try {

			// trying determine title place

			List<String> namesInText = nerEngine.FindNamesInText(title);

			for (String name : namesInText) {
				if (StringUtils.isNotBlank(name)) {

					System.out.println(name);

					Point p = getPointForPlace(name);

					System.out.println(p);

					resultList.add(p);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out
				.println("----------------------------End Point for Title -------------------------------------");

		return resultList;
	}

	private static void makePublicationPlacesReferences(Connection connection2)
			throws SQLException {
		final String selectRecordsSQL = "select rec_cod, pub_place from records";

		Statement st = connection2.createStatement();
		ResultSet recordsSet = st.executeQuery(selectRecordsSQL);

		while (recordsSet.next()) {

			String recCod = recordsSet.getString(1);
			String pubPlace = recordsSet.getString(2);

			if (StringUtils.isNotBlank(pubPlace)
					&& knownPublicationPlaces.containsKey(pubPlace)) {
				pubPlace = knownPublicationPlaces.get(pubPlace);// fix
																// abbreviated
																// location name
			}

			if (StringUtils.isNotBlank(pubPlace)) {

				Point pubPoint = getPointForPlace(pubPlace);

				int objectType = GeoobjectTypes.POINT_TYPE;
				int referenceType = GeoreferenceTypes.PUBPLACE_REF_ID;

				final String insertSQLFormat = "insert into geo_references (rec_cod, type_id, object_type_id, point_data) values ('%s',%s,%s,%s)";

				final String insertCommand = String.format(insertSQLFormat,
						recCod, referenceType, objectType, pubPoint.GetSQL());

				if (emulate) {
					System.out.println(insertCommand);
				} else {
					Statement insertStatement = connection2.createStatement();
					insertStatement.execute(insertCommand);
				}

			}

		}

	}

	private static Point getPointForPlace(String place) {

		GeocoderRequest geocoderRequest = new GeocoderRequest(place, "ru");
		GeocodeResponse geocodeResponse = geocoder.geocode(geocoderRequest);

		Point result = new Point(geocodeResponse);
		return result;
	}

	private static void clearReferencestable(Connection connection2)
			throws SQLException {
		final String sql = "truncate table geo_references";
		executeSql(connection2, sql);
	}

	private static void executeSql(Connection connection2, String sql)
			throws SQLException {

		if (emulate) {
			System.out.println("Emulating mode:" + sql);
		} else {
			Statement st = connection2.createStatement();
			st.execute(sql);
		}

	}

	private static void clearGeoreferenceTypesTable(Connection connection2)
			throws SQLException {
		final String sql = "truncate table georeference_types CASCADE";
		executeSql(connection2, sql);
	}

	private static void clearGeoobjectTypesTable(Connection connection2)
			throws SQLException {
		final String sql = "truncate table geoobject_types CASCADE";
		executeSql(connection2, sql);
	}

	private static void fillGeoreferenceTypesTable(Connection connection2)
			throws SQLException {
		final String insertSQLFormat = "insert into georeference_types (type_id, descr, column_name) values (?, ?, ?)";

		PreparedStatement st = connection2.prepareStatement(insertSQLFormat);
		st.setInt(1, GeoreferenceTypes.TITLE_REF_ID);
		st.setString(2, GeoreferenceTypes.TITLE_REF_DESCR);
		st.setString(3, GeoreferenceTypes.TITLE_REF_COLUMN);
		executeStatement(st);

		st.setInt(1, GeoreferenceTypes.PUBPLACE_REF_ID);
		st.setString(2, GeoreferenceTypes.PUBPLACE_REF_DESCR);
		st.setString(3, GeoreferenceTypes.PUBPLACE_REF_COLUMN);
		executeStatement(st);
	}

	private static void fillGeoobjectTypesTable(Connection connection2)
			throws SQLException {
		final String insertSQLFormat = "insert into geoobject_types (object_type_id, descr) values (?,?)";

		PreparedStatement st = connection2.prepareStatement(insertSQLFormat);
		st.setInt(1, GeoobjectTypes.POINT_TYPE);
		st.setString(2, GeoobjectTypes.POINT_DESCR);
		executeStatement(st);

		st.setInt(1, GeoobjectTypes.RECTANGLE_TYPE);
		st.setString(2, GeoobjectTypes.RECTANGLE_DESCR);
		executeStatement(st);

	}

	private static void executeStatement(PreparedStatement st)
			throws SQLException {

		if (emulate) {

		} else {
			st.executeUpdate();
		}

	}

}
