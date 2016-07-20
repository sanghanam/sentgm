package edu.kaist.mrlab.templator.question;

import edu.kaist.mrlab.templator.srdf.KoSeCT;
import edu.kaist.mrlab.templator.srdf.SRDF;
import edu.kaist.mrlab.templator.srdf.modules.Preprocessor;
import edu.kaist.mrlab.templator.srdf.modules.SentenceSplitter;

/**
 * 
 * Query Triple Generator using SRDF
 * 
 * @author sangha
 *
 */
public class QTGenerator {
	
	
	private SRDF srdf = new SRDF();
	private KoSeCT kosect = new KoSeCT();
	private Preprocessor p = new Preprocessor();
	private SentenceSplitter ss = new SentenceSplitter();
	
	public String generateQT(String input){
		
		return srdf.doOneSentence(kosect, p, ss, input);
		
	}
	
	
	
	public static void main(String[] ar){
		
		QTGenerator qtg = new QTGenerator();
		qtg.generateQT("무엇는 이순신 장군이 1597년에 명량해협에서 지휘한 해전이다.");
		
	}
}
