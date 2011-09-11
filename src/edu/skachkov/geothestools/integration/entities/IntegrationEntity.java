package edu.skachkov.geothestools.integration.entities;

public abstract class IntegrationEntity {

	protected static final int SRID = 4326;
	
	protected static final String COMPOSITE_GEO_TYPE_INSERT_FORMAT = 
			"ROW(ST_GeographyFromText('SRID=%s;%s'))";
	
	protected static final String GEO_FROM_TEXT_FORMAT = "ST_GeographyFromText('%s')";
	
	protected static final String ROW_FORMAT = "ROW(%s)"; 
	
	public abstract String GetSQL();
}
