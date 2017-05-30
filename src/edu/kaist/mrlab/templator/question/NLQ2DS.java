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

		System.out.println(varPosition);

		if (varPosition == -1 && (input.contains("은?") || input.contains("는?"))) {

			declStc = "무엇은 " + input.substring(0, input.length() - 2);
			// return "This question cannot be processed";

		} else if (varPosition == 0) {
		
			// 어떤 강이 서울을 흐르는가?
			// 어떤는 서울을 흐르는 강이다.

			if(subject.equals("어떤")){
			
				input = input.substring(0, input.length() - 2);
				ArrayList<String> inputToken = new ArrayList<String>();
				StringTokenizer st = new StringTokenizer(input, " ");
				while(st.hasMoreTokens()){
					inputToken.add(st.nextToken());
				}
				
				declStc = subject + "는 ";
				
				for(int i = 2; i < inputToken.size(); i++){
					declStc += inputToken.get(i) + " ";
				}
				
				String lastWord = inputToken.get(1).substring(0, inputToken.get(1).length() - 1);
				
				declStc += lastWord;
				
			}
			
		} else {

			if (subject.contains("몇 개")) {

				declStc = input.substring(0, varPosition + 3);

			} else {

				if(varPosition < 2){
					declStc = "";
				}
				
				declStc = subject + "는 " + input.substring(0, varPosition - 2);

			}

		}

		declStc = declStc + "이다.";

		System.out.println(declStc);

		return declStc;
	}

	public static void main(String[] ar) throws Exception {

		NLQ2DS convt = new NLQ2DS();

		convt.loadVarDic();

		String question = "김자옥의 출신 국가는 어디인가?";
		String declStc = convt.getDS(question);
		System.out.println("NLQ:\t" + question);
		System.out.println("DS:\t" + declStc);
		System.out.println();

	}
}
