package edu.skachkov.geothestools.testing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import edu.skachkov.geothestools.ner.NEREngine;

@RunWith(Parameterized.class)
public class NEREngineTest {

	private NEREngine engine;

	private String text;
	private String geoEntity;
	
	public NEREngineTest(String text, String geoEntity) {
		super();
		this.text = text;
		this.geoEntity = geoEntity;
	}
	
	
	@Parameters
	public static Collection GetTexts(){
		return Arrays.asList(new String[][]{
				{"Временные тренды стойких органических загрязнителей (СОЗ) экосистемы озера Байкал","байкал"},
				{"К экологической ситуации на Байкале","байкал"},
				{"Заказники Бурятии","бурятия"},
				{"Исследование элементного состава воды и донных отложений озера Таватуй методом масс-спектрометрии с индуктивно связанной плазмой (ИСП-МС)", "таватуй"},
				
		});
	}
	
	
	@Before
	public void setUp(){
		engine = new NEREngine();
	}
	
	@Test()
	public void testFindOneNameInText() {

		List<String> result = engine.FindNamesInText(text);
		assertNotNull(result);
		assertTrue(result.size() > 0);
		
		String firstName = result.get(0);
		assertEquals(geoEntity, firstName);
		
	}

}
