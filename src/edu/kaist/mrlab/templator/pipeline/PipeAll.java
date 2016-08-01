package edu.kaist.mrlab.templator.pipeline;

import java.util.ArrayList;

import org.json.JSONArray;

import edu.kaist.mrlab.templator.question.InputParser;
import edu.kaist.mrlab.templator.question.NLQ2DS;
import edu.kaist.mrlab.templator.question.QT2Template;
import edu.kaist.mrlab.templator.question.QTGenerator;

/**
 * 
 * Pipeline of Korean Template Generation Module This is the main class of
 * SenTGM
 * 
 * @author sangha
 *
 */
public class PipeAll {

	private InputParser ip = new InputParser();
	private NLQ2DS nlq2ds = new NLQ2DS();
	private QTGenerator qtg = new QTGenerator();
	private QT2Template po = new QT2Template();

	public String run(String input) throws Exception {

		String question = ip.getQuestion(input);
		String sentence = nlq2ds.getDS(question);
		System.out.println(sentence);
		ArrayList<String> qTriples = qtg.generateQT(sentence);
		System.out.println(qTriples);
		JSONArray output = po.printOutput(question, qTriples);

		return output.toString();
	}

	public String transformArrToStr(ArrayList<String> input) {

		String result = "";

		if (input.size() == 0) {
			
			return null;
		
		} else {
			
			for (int i = 0; i < input.size(); i++) {

				result += input.get(i) + "\n";

			}

			return result;
		}

	}
	
}
