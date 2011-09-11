package edu.skachkov.geothestools;

import java.io.File;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class NtiDbLoader {

	private static final String INSERT_FORMAT = "insert into records (rec_cod,x_mcf) values ('%s','%s')";

	private static XPathFactory factory;

	private static XPathExpression keyPrefixXPath;

	private static XPathExpression keyXPath;

	static {
		factory = XPathFactory.newInstance();
	}

	/**
	 * @param args
	 * @throws SQLException
	 *             if operation failed
	 */
	public static void main(String[] args) throws SQLException {

		Connection connection = null;
		
		//!!!
		boolean emulate = true;

		try {

			// creation xPath for parsing records
			keyPrefixXPath = factory.newXPath().compile(
					"datafield[@tag='390']/subfield[@code='N']/text()");
			keyXPath = factory.newXPath().compile(
					"datafield[@tag='390']/subfield[@code='A']/text()");

			connection = DriverManager.getConnection(
					"jdbc:postgresql://127.0.0.1:5432/geothes1", "postgres",
					"postgres");

			connection.setAutoCommit(true);

			if (!emulate) {
				clearDatabase(connection);	
			}
			

			File file = new File("input/all-viniti-db-baikal.xml");

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);
			// doc.getDocumentElement().normalize();

			NodeList nodeLst = doc.getElementsByTagName("record");

			int itemsCount = nodeLst.getLength();

			Statement statement = connection.createStatement();

			for (int i = 0; i < itemsCount; i++) {
				Node node = nodeLst.item(i).cloneNode(true);

				if (node.getNodeType() == Node.ELEMENT_NODE) {

					Element element = (Element) node;

					String recordKey = getRecordKey(element);
					String recordText = getRecordText(element);

					if (StringUtils.isBlank(recordKey)
							|| StringUtils.isBlank(recordText)) {
						throw new Exception("Illegal values!");
					}

					String insertCommand = String.format(INSERT_FORMAT,
							recordKey, recordText);

					// System.out.println(insertCommand);

					try {
						
						if (!emulate) {
							statement.execute(insertCommand);	
						}
						
						//System.out.println(insertCommand);
						System.out.println("Inserting " + recordKey
								+ " executed. " + i + " of " + itemsCount);
					} catch (Exception ex) {
						System.out.println("Inserting " + recordKey
								+ " failed.");
						ex.printStackTrace();
					}

				}

			}

			System.out.println("Completed!");

		} catch (SQLException sqlException) {

			sqlException.printStackTrace();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			if (connection != null) {
				connection.close();
			}

		}

	}

	private static void clearDatabase(Connection connection)
			throws SQLException {
		Statement statement = connection.createStatement();
		statement.execute("truncate table records cascade;");

	}

	private static String getRecordText(Element element) {
		String result = "";

		try {
			result = node2String(element);

			result = result.replace("'", "''");

		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}

		return result;
	}

	private static String node2String(Node node)
			throws TransformerFactoryConfigurationError, TransformerException {
		// you may prefer to use single instances of Transformer, and
		// StringWriter rather than create each time. That would be up to your
		// judgement and whether your app is single threaded etc
		StreamResult xmlOutput = new StreamResult(new StringWriter());
		Transformer transformer = TransformerFactory.newInstance()
				.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.transform(new DOMSource(node), xmlOutput);
		return xmlOutput.getWriter().toString();
	}

	private static String getRecordKey(Element element)
			throws XPathExpressionException {

		String keyPrefix = keyPrefixXPath.evaluate(element);
		String keyValue = keyXPath.evaluate(element);

		return keyPrefix + keyValue;

	}

}
