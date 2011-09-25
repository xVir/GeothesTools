package edu.skachkov.geothestools.ner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;

/**
 * Named entity recognizer for Russian language
 * @author Danil
 *
 */
public class NEREngine {
	
	private LuceneMorphology luceneMorph;

	public NEREngine() {
		 try {
			luceneMorph = new RussianLuceneMorphology();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public List<String> FindNamesInText(String text){
		
		//get first word or word, what begins from upper case
		
		text = text.toLowerCase();
		
		
		List<String> results = new ArrayList<String>();
		
		return results;
	}
	
}
