package edu.kaist.mrlab.templator.srdf;

import java.util.Scanner;

import edu.kaist.mrlab.templator.pipeline.PipeAll;

public class SenTGM {
	
	private Scanner scan;

	public String inputSentence() {

		String sentence;

		scan = new Scanner(System.in);

		System.out.println("질문을 입력하세요:");

		sentence = scan.nextLine();

		return sentence;
	}

	public static void main(String[] ar) throws Exception {
		
		SenTGM sentgm = new SenTGM();
		String input = sentgm.inputSentence();
		PipeAll pa = new PipeAll();
		input = "{ \"string\": \"" + input + "\", \"language\": \"ko\" }";  
		String result = pa.run(input);
		
		System.out.println(result);

	}
}
