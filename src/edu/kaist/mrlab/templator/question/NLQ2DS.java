package edu.kaist.mrlab.templator.question;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * 
 * Converter for natural language question to declarative sentence. Simple
 * Rule-based using 'var.dic' file First, it select specific word in NLQ using
 * var.dic Second, change it to variable "주어" (subject in declarative sentence)
 * 
 * @author sangha
 *
 */
public class NLQ2DS {

	private HashMap<String, ArrayList<String>> varDic = new HashMap<String, ArrayList<String>>();
	private ArrayList<String> varList = new ArrayList<String>();
	private BufferedReader br;
	private static BufferedReader fbr;

	public ArrayList<String> getVarList() {
		return varList;
	}

	public NLQ2DS() {
		this.loadVarDic();
	}

	/**
	 * Load variable dictionary file 'var.dic' for Korean.
	 * 
	 * @throws IOException
	 */
	public void loadVarDic() {

		try {
			br = new BufferedReader(new FileReader("data/dic/var.dic"));

			String line = null;
			String key = null;
			ArrayList<String> varArr;
			boolean firstCol;
			StringTokenizer st;
			String value;

			while ((line = br.readLine()) != null) {

				varArr = new ArrayList<String>();
				firstCol = true;
				st = new StringTokenizer(line, "\t");

				while (st.hasMoreTokens()) {

					if (firstCol) {

						key = st.nextToken();
						firstCol = false;

					} else {

						value = st.nextToken();
						varArr.add(value);
						varList.add(value);

					}
				}

				varDic.put(key, varArr);
				firstCol = true;

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getDS(String input) {

		String declStc = null;
		String subject = null;
		int varPosition = -1;

		for (int i = 0; i < varList.size(); i++) {

			varPosition = input.indexOf(varList.get(i));

			if (varPosition != -1) {

				subject = varList.get(i);
				break;

			}
		}

		if (varPosition == -1) {

			return "This question cannot be processed";

		}

		if (subject.contains("몇 개")) {

			declStc = input.substring(0, varPosition + 3);

		} else {

			declStc = subject + "는 " + input.substring(0, varPosition - 2);

		}

		declStc = declStc + "이다.";

		return declStc;
	}

	public static void main(String[] ar) throws Exception {

		NLQ2DS convt = new NLQ2DS();

		convt.loadVarDic();

		fbr = new BufferedReader(new FileReader("data/questions.txt"));
		String question;
		while ((question = fbr.readLine()) != null) {
			String declStc = convt.getDS(question);
			System.out.println("NLQ:\t" + question);
			System.out.println("DS:\t" + declStc);
			System.out.println();
		}

	}
}
