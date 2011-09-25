package edu.skachkov.geothestools.testing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.junit.BeforeClass;
import org.junit.Test;

public class MorphAnalyzerTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		
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
