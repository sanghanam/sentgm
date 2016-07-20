package edu.kaist.mrlab.templator.question;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

import edu.kaist.mrlab.templator.srdf.KoSeCT;
import edu.kaist.mrlab.templator.srdf.SRDF;
import edu.kaist.mrlab.templator.srdf.modules.Preprocessor;
import edu.kaist.mrlab.templator.srdf.modules.SentenceSplitter;

/**
 * 
 * Query Triple Generator using SRDF and transform srdf triples to TGM style.
 * 
 * @author sangha
 *
 */
public class QTGenerator {
	
	private ArrayList<String> josa = new ArrayList<String>();
	private BufferedReader br;

	private NLQ2DS c = new NLQ2DS();
	private SRDF srdf = new SRDF();
	private KoSeCT kosect = new KoSeCT();
	private Preprocessor p = new Preprocessor();
	private SentenceSplitter ss = new SentenceSplitter();

	public ArrayList<String> generateQT(String input) {

		return transformToTGM(srdf.doOneSentence(kosect, p, ss, input));

	}

	private ArrayList<String> transformToTGM(String input) {

		ArrayList<String> srdfTriples = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(input, "\n");

		while (st.hasMoreTokens()) {

			String triple = st.nextToken();

			srdfTriples.add(triple);

		}

		ArrayList<String> tgmTriples = transform(srdfTriples);

		return tgmTriples;

	}

	private ArrayList<String> transform(ArrayList<String> input) {

		ArrayList<String> tgmTriples = new ArrayList<String>();
		String subject = null;
		String predicate = null;
		String object = null;
		
		boolean doubleReified = false;
		String fixedPredicate = null;

		for (int i = 0; i < input.size(); i++) {

			String sTriple = input.get(i);

			StringTokenizer st = new StringTokenizer(sTriple, "\t");

			String sbj = st.nextToken();
			String pred = st.nextToken();
			String obj = st.nextToken();

			String result;

			if (pred.contains("이다")) {

				object = pred.replace("이다", "");
				predicate = "type";

				if (c.getVarList().contains(sbj)) {

					subject = "var";

				}

				result = subject + "\t" + predicate + "\t" + object;
				tgmTriples.add(result);

			} else if (!obj.equals("ANONYMOUS") && josa.contains(pred)) {
				
				if(pred.equals("의")){
					
					subject = obj;
					predicate = sbj.replace("이다", "");
					object = "var";
					
				} else{

					subject = "var";
					if(doubleReified){
						
						predicate = fixedPredicate;
						
					} else{
						
						predicate = sbj.replace("이다", "");
						
					}
					
					object = obj;
					
				}
				
				result = subject + "\t" + predicate + "\t" + object;
				tgmTriples.add(result);

			} else if (!obj.equals("ANONYMOUS")) {

				subject = "var";
				fixedPredicate = predicate = pred;
				object = obj;
				
				doubleReified = true;
				
				result = subject + "\t" + predicate + "\t" + object;
				tgmTriples.add(result);

			}

		}

		return tgmTriples;
	}
	
	public void fillJosaArr(){
		
		try {
			br = new BufferedReader(new FileReader("data/dic/josa.dic"));
			
			String line;
			while((line = br.readLine()) != null){
				
				josa.add(line);
				
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	
	public QTGenerator(){
		
		fillJosaArr();
		
	}

	public static void main(String[] ar) {

		QTGenerator qtg = new QTGenerator();
		qtg.generateQT("무엇은 와인 생산지로 알려진 나라이다.");

	}
}
