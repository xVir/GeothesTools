package edu.skachkov.geothestools.testing;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianAnalyzer;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.junit.BeforeClass;
import org.junit.Test;

public class MorphAnalyzerTest {

	private static RussianAnalyzer analyzer;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		analyzer = new RussianAnalyzer();
		
	}

	@Test
	public void testWord() throws IOException {
		
		 LuceneMorphology luceneMorph = new RussianLuceneMorphology();
		 List<String> normalForms = luceneMorph.getNormalForms("байкальского");
		 
		 assertNotNull(normalForms);
		 assertTrue(normalForms.size() > 0);
		 assertEquals("байкальский", normalForms.get(0));
		
		
	}

}
