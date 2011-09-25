package edu.skachkov.geothestools.ner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

public class GeoObjectsDictionary {

	private List<String> objects = new ArrayList<String>();

	public GeoObjectsDictionary() {
	}

	public GeoObjectsDictionary(String fileName) throws IOException {
		// load objects from specified file

		List<String> lines = FileUtils.readLines(new File(fileName));

		for (String line : lines) {
			if (StringUtils.isNotBlank(line)) {
				if (!objects.contains(line.toLowerCase())) {
					objects.add(line.toLowerCase());
				}
			}
		}

	}
	
	public void addObject(String obj){
		objects.add(obj.toLowerCase());
	}
	
	/**
	 * 
	 * @param objectName
	 * @return object name if exist, null if object not exists
	 */
	public String getObjectIfExists(String objectName){
		if (objects.contains(objectName.toLowerCase())) {
			return objectName;
		}
		
		return null;
	}

}
