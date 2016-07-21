package edu.kaist.mrlab.temlator.test;

import java.io.BufferedReader;
import java.io.FileReader;

import edu.kaist.mrlab.templator.pipeline.PipeAll;

public class Tester {
	
	public static BufferedReader fbr;
	
	public static void main(String[] ar) throws Exception {

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
