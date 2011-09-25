package edu.skachkov.geothestools.testing;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.model.TestClass;

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
				{"��������� ������ ������� ������������ ������������� (���) ���������� ����� ������","����� ������"},
				{"� ������������� �������� �� �������","������"},
				{"�������� ����� ������ ���������� �������� ������������ �����������","����������� ����������"},
				{"��������� �������","�������"},
				{"������������ ����������� ������� ���� � ������ ��������� ����� ������� ������� ����-������������� � ���������� ��������� ������� (���-��)", "�������"}
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
