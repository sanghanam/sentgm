package edu.kaist.mrlab.templator.question;

import java.io.File;
import java.io.FileInputStream;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * 
 * Parser for Input of Template Generate Module
 * 
 * @author sangha
 *
 */
public class InputParser {

	private static FileInputStream fis;

	/**
	 * 
	 * @param input
	 * @return natural language question
	 * @throws ParseException
	 */
	public String getQuestion(String input) throws ParseException{
		
		JSONParser jsonParser = new JSONParser();
		JSONObject reader = (JSONObject) jsonParser.parse(input);

		return (String) reader.get("string");
	}
	
	public static void main(String[] ar) throws Exception {
		
		InputParser parser = new InputParser();
		
		File file = new File("data/input.txt");
		fis = new FileInputStream(file);
		byte[] data = new byte[(int) file.length()];
		fis.read(data);
		String input = new String(data, "UTF-8");
		
		String nlq = parser.getQuestion(input);
		System.out.println(nlq);
		
	}
}
