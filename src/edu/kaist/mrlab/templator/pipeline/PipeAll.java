package edu.kaist.mrlab.templator.pipeline;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import org.json.JSONObject;

import edu.kaist.mrlab.templator.question.InputParser;
import edu.kaist.mrlab.templator.question.NLQ2DS;
import edu.kaist.mrlab.templator.question.QT2JSON;
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

	private static BufferedReader fbr;

	private static InputParser ip;
	private static NLQ2DS conv;
	private static QTGenerator qtg;
	private static QT2JSON po;

	public String run(String input) throws Exception {

		String question = ip.getQuestion(input);
		String sentence = conv.getDS(question);
		System.out.println(sentence);
		ArrayList<String> qTriples = qtg.generateQT(sentence);
		JSONObject output = po.printOutput(question, qTriples);
		System.out.println(output);

		return transformArrToStr(qTriples);
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

	public static void main(String[] ar) throws Exception {

		ip = new InputParser();
		conv = new NLQ2DS();
		qtg = new QTGenerator();
		po = new QT2JSON();

		PipeAll pa = new PipeAll();

		fbr = new BufferedReader(new FileReader("data/input.txt"));
		String question;
		while ((question = fbr.readLine()) != null) {
			System.out.println(question);
			String result = pa.run(question);
			System.out.println(result);
			System.out.println();
		}

	}
}
