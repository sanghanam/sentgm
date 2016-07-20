package edu.kaist.mrlab.templator.pipeline;

import java.io.BufferedReader;
import java.io.FileReader;

import edu.kaist.mrlab.templator.question.Converter;
import edu.kaist.mrlab.templator.question.InputParser;
import edu.kaist.mrlab.templator.question.QTGenerator;

/**
 * 
 * Pipeline of Korean Template Generation Module
 * 
 * @author sangha
 *
 */
public class PipeAll {

//	private static FileInputStream fis;
	private static BufferedReader fbr;

	private static InputParser ip;
	private static Converter conv;
	private static QTGenerator qtg;

	public String run(String input) throws Exception {

		String question = ip.getQuestion(input);
		String sentence = conv.getDS(question);
		System.out.println(sentence);
		String qTriples = qtg.generateQT(sentence);

		return qTriples;
	}

	public static void main(String[] ar) throws Exception {

		ip = new InputParser();
		conv = new Converter();
		qtg = new QTGenerator();

		PipeAll pa = new PipeAll();

		
		fbr = new BufferedReader(new FileReader("data/input.txt"));
		String question;
		while((question = fbr.readLine())!= null){
			System.out.println(question);
			String result = pa.run(question);
			System.out.println(result);
			System.out.println();
		}
		
//		File file = new File("data/input.txt");
//		fis = new FileInputStream(file);
//		byte[] data = new byte[(int) file.length()];
//		fis.read(data);
//		String input = new String(data, "UTF-8");
//
//		System.out.println(input);
//		String result = pa.run(input);
//		System.out.println(result);

	}
}
