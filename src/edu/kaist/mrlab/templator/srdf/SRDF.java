package edu.kaist.mrlab.templator.srdf;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.NoSuchElementException;
import java.util.Scanner;

import edu.kaist.mrlab.templator.srdf.data.Triple;
import edu.kaist.mrlab.templator.srdf.modules.Chunker;
import edu.kaist.mrlab.templator.srdf.modules.Preprocessor;
import edu.kaist.mrlab.templator.srdf.modules.SentenceSplitter;
import edu.kaist.mrlab.templator.srdf.modules.TripleGenerator;

public class SRDF {

	private Scanner scan;
	private static BufferedWriter filebw;

	private static int readedSTC = 0;
	private static int generatedTriples = 0;

	public String inputSentence() {

		String sentence;

		scan = new Scanner(System.in);

		System.out.println("문장을 입력하세요:");

		sentence = scan.nextLine();

		return sentence;
	}

	public static void writeTriplesToFile(ArrayList<Triple> triples, Chunker c) throws Exception {

		filebw.write("STC : " + c.getText().get("text") + "\n");
		System.out.println("STC : " + c.getText().get("text"));
		readedSTC++;

		for (int j = 0; j < triples.size(); j++) {
			Triple t = triples.get(j);
			filebw.write(t.getSubject() + "\t" + t.getPredicate() + "\t" + t.getObject() + "\n");
		}
		generatedTriples += triples.size();

		filebw.write("\n\n");

	}

	public String formatTime(long lTime) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(lTime);
		return (c.get(Calendar.HOUR_OF_DAY) + "시 " + c.get(Calendar.MINUTE) + "분 " + c.get(Calendar.SECOND) + "."
				+ c.get(Calendar.MILLISECOND) + "초");
	}

	public String doOneSentence(KoSeCT kosect, Preprocessor p, SentenceSplitter ss, String input) {
		String qTriples = "";
		try {

			ArrayList<String> splittedSTC = ss.splitSentence(input);

			for (int j = 0; j < splittedSTC.size(); j++) {

				// System.out.println(splittedSTC.get(j));

				input = kosect.changeSymbol(splittedSTC.get(j));
				input = kosect.removeUTF8BOM(input);
				input = p.removeBracket(input);

				ArrayList<Chunker> chunkers = kosect.doPreprocessWithoutSplitting(input);

				if (!p.isValidChunk(chunkers) || chunkers.isEmpty()) {
					continue;
				}

				for (Chunker c : chunkers) {
					TripleGenerator tg = new TripleGenerator(c.getNPChunks(), c.getVPChunks());
					tg.generate();
					ArrayList<Triple> triples = tg.getTriples();
					for (int k = 0; k < triples.size(); k++) {
						Triple t = triples.get(k);
//						System.out.print(t.getSubject() + "\t" + t.getPredicate() + "\t" + t.getObject() + "\n");
						qTriples += t.getSubject() + "\t" + t.getPredicate() + "\t" + t.getObject() + "\n";
					}
				}
			}

		} catch (Exception e) {

		}

		return qTriples;
	}


	public static void main(String[] ar) throws NoSuchElementException {

		long startTime = System.currentTimeMillis();

		KoSeCT kosect = new KoSeCT();
		Preprocessor p = new Preprocessor();
		SentenceSplitter ss = new SentenceSplitter();
		SRDF srdf = new SRDF();

		String input = srdf.inputSentence();

		srdf.doOneSentence(kosect, p, ss, input);

		// 종료 시간
		long endTime = System.currentTimeMillis();
		// 시간 출력
		System.out.println("##  시작시간 : " + new SRDF().formatTime(startTime));
		System.out.println("##  종료시간 : " + new SRDF().formatTime(endTime));
		System.out.println("##  소요시간(초.0f) : " + (endTime - startTime) / 1000.0f + "초");

		System.out.println("## 입력 문장 수 : " + readedSTC);
		System.out.println("## 출력 트리플 수 : " + generatedTriples);

	}
}
