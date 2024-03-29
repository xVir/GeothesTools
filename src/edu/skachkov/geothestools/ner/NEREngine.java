package edu.skachkov.geothestools.ner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.WrongCharaterException;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;

/**
 * Named entity recognizer for Russian language
 * 
 * @author Danil
 * 
 */
public class NEREngine {

	private static final String EXCESS_CHARS = ".,;:(){}\'\"";

	private static final String IRKUTSK_FILE_NAME = "data/irkutskaya_obl.txt";

	private LuceneMorphology russianMorph;

	private GeoObjectsDictionary geoObjectsDictionary;

	public NEREngine() {
		try {
			russianMorph = new RussianLuceneMorphology();

			geoObjectsDictionary = new GeoObjectsDictionary(IRKUTSK_FILE_NAME);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public List<String> FindNamesInText(String inputText) {

		// get first word or word, what begins from upper case

		String text = normalizeText(inputText);

		String[] tokens = StringUtils.split(text);

		List<String> normalizedTokens = new ArrayList<String>();
		for (String token : tokens) {

			try {
				List<String> normalForms = russianMorph.getNormalForms(token);

				if (normalForms.size() > 0) {
					normalizedTokens.add(normalForms.get(0));
				} else {
					normalizedTokens.add(token);
				}

			} catch (WrongCharaterException wrongCharaterException) {
				wrongCharaterException.printStackTrace();
				System.out.println("-------------> In token: " + token);
			}
		}

		List<String> results = new ArrayList<String>();

		for (String token : normalizedTokens) {
			String geoObject = geoObjectsDictionary.getObjectIfExists(token);
			if (StringUtils.isNotBlank(geoObject)) {
				results.add(geoObject);
			}
		}

		return results;
	}

	private String normalizeText(String text) {
		if (StringUtils.isBlank(text)) {
			return "";
		}

		String result = StringUtils.replaceChars(text.toLowerCase(),
				EXCESS_CHARS, null);

		result = StringUtils.normalizeSpace(result);

		return result;

	}

	public List<String> FindNamesInAbstract(String abstr) {
		// get all words, which starts with Upper case letter

		StringBuilder namesBuilder = new StringBuilder();

		String textToProcess = StringUtils.replaceChars(abstr, EXCESS_CHARS,
				null);

		String[] wordItems = StringUtils.split(textToProcess);

		for (String word : wordItems) {
			if (!word.equals(StringUtils.uncapitalize(word))) {
				namesBuilder.append(word + " ");
			}
		}

		return FindNamesInText(namesBuilder.toString());
	}

}
