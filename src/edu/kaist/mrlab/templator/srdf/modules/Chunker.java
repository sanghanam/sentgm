package edu.kaist.mrlab.templator.srdf.modules;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import edu.kaist.mrlab.templator.srdf.data.Chunk;
import edu.kaist.mrlab.templator.srdf.tools.KoreanAnalyzer;
import edu.kaist.mrlab.templator.srdf.tools.StringEdit;

public class Chunker {

	ArrayList<Chunk> NPChunks = new ArrayList<Chunk>();
	ArrayList<Chunk> VPChunks = new ArrayList<Chunk>();

	JSONArray morpArr = null;
	JSONArray depenArr = null;
	JSONArray wordArr = null;

	String depenText = null;

	JSONObject text = null;
	
	public JSONObject getText(){
		return text;
	}

	public ArrayList<Chunk> getNPChunks() {
		return NPChunks;
	}

	public ArrayList<Chunk> getVPChunks() {
		return VPChunks;
	}

	@SuppressWarnings("unchecked")
	public void chunk(String json) {
		try {

			JSONParser jsonParser = new JSONParser();
			JSONObject reader = (JSONObject) jsonParser.parse(json);

			JSONArray stn = (JSONArray) reader.get("sentence");

			Iterator<Object> s = stn.iterator();

			while (s.hasNext()) {

				text = (JSONObject) s.next();
				JSONObject morp = (JSONObject) s.next();
				JSONObject depen = (JSONObject) s.next();
				JSONObject word = (JSONObject) s.next();

				morpArr = (JSONArray) morp.get("morp");
				depenArr = (JSONArray) depen.get("dependency");
				wordArr = (JSONArray) word.get("word");

				// head가 -1인 VP를 찾는다.

				Iterator<Object> d = depenArr.iterator();

				ArrayList<Integer> tempMod = new ArrayList<Integer>();
				ArrayList<Integer> VPMod = new ArrayList<Integer>();

				while (d.hasNext()) {

					JSONObject innerDepen = (JSONObject) d.next();
					int depenHead = (int) (long) innerDepen.get("head");
					int depenID = (int) (long) innerDepen.get("id");
					String depenLabel = (String) innerDepen.get("label");

					if (depenHead == -1) {

						// 찾은 VP의 mod를 본다.
						JSONArray depenMod = (JSONArray) innerDepen.get("mod");
						depenText = (String) innerDepen.get("text");
						String VPChunk = depenText;
						String temp = getPostposition(depenID, depenLabel);
						String[] ar = temp.split("/");
						if (ar.length >= 1) {
							VPChunk = ar[0];
						}
						String etc = "";
						if (ar.length > 1) {
							etc = ar[1];
						}
						StringEdit.replaceLast(VPChunk, etc, "");
						// VPChunk = VPChunk.replace(etc, "");

						Iterator<Object> m = depenMod.iterator();
						while (m.hasNext()) {
							int ti = (int) (long) m.next();
							tempMod.add(ti);
							VPMod.add(ti);
						}
						VPChunks.add(new Chunk(VPChunk, etc, depenID, "VP", tempMod));
					}
				}

				// mod를 하나씩 depenID로 따라간다.

				for (int i = 0; i < VPMod.size(); i++) {
					Iterator<Object> d2 = depenArr.iterator();
					for (int j = 0; j < VPMod.get(i); j++) {
						d2.next();
					}
					JSONObject target = (JSONObject) d2.next();

					recur(target);

				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public void recur(JSONObject target) {

		ArrayList<Integer> APID = new ArrayList<Integer>();
		ArrayList<Integer> tempMod = new ArrayList<Integer>();
		int targetID = (int) (long) target.get("id");
		int targetHead = (int) (long) target.get("head");
		String targetLabel = (String) target.get("label");
		String targetText = (String) target.get("text");
		JSONArray targetMod = (JSONArray) target.get("mod");
		Iterator<Object> m = targetMod.iterator();
		while (m.hasNext()) {
			tempMod.add((int) (long) m.next());
		}
		String targetChunk = targetText;
		// mod가 AP인 경우와 NP인 경우, 그리고 VP인 경우로 나눈다.
		// mod가 AP인 경우에는 VP와 결합하여 하나의 VP Chunk로 만든다.
		// 이때, word는 AP는 그대로 모두 사용하고 VP는
		// "NNG+XSV or VV or NNG+XSA or VCP" 등의 POS 표현만을 사용한다.
		// postposition은 나머지 뒷 부분 EF, SF 등이 해당

		if (targetLabel.contains("AP")) {
			Chunk tempCHK = null;

			for (int i = 0; i < VPChunks.size(); i++) {
				if ((VPChunks.get(i).getID() == targetHead)
						|| VPChunks.get(i).getAPID() != null && VPChunks.get(i).getAPID().contains(targetHead)) {
					tempCHK = VPChunks.get(i);
					break;
				}
			}

			if (tempCHK != null) {
				VPChunks.remove(tempCHK);
				String VPText = tempCHK.getChunk();
				String VPETC = tempCHK.getPostposition();
				int VPID = tempCHK.getID();
				ArrayList<Integer> VPMOD = tempCHK.getMod();
				VPMOD.remove((Object) targetID);
				String etc = getPostposition(targetID, targetLabel);
				StringEdit.replaceLast(targetChunk, etc, "");
				// targetChunk = targetChunk.replace(etc, "");
				Chunk tempC;
				VPChunks.add(tempC = new Chunk(targetChunk + " " + VPText, VPETC, VPID, "VP", VPMOD));
				APID.add(targetID);
				tempC.setAPID(APID);

				if (tempMod.size() != 0) {
					Iterator<Object> d2 = depenArr.iterator();
					for (int j = 0; j < tempMod.get(0); j++) {
						d2.next();
					}
					JSONObject nextTarget = (JSONObject) d2.next();
					recur(nextTarget);
				}
			}

		}
		// mod가 NP인 경우에는 해당 NP의 mod를 본다.
		// 해당 mod가 있으면 계속 따라가며 최장의 NP를 만들어서 하나의 NP Chunk로 만든다.
		else if (targetLabel.contains("NP")) {

			// System.out.println(target);

			// NP의 mod가 더이상 없을 때 까지 recursion
			String NPChunk = processMod(target, "") + targetText;
			String temp = getPostposition(targetID, targetLabel);
			String[] ar = temp.split("/");
//			String chunk = ar[0];
			String postposition = "";
			if (ar.length > 1) {
				postposition = ar[1];
			}
			NPChunk = NPChunk.substring(0, NPChunk.length() - postposition.length());
			// NPChunk = NPChunk.replace(postposition, "");
			NPChunks.add(new Chunk(NPChunk, postposition, targetID, targetLabel));

		}
		// mod가 VP인 경우에는 위 알고리즘을 다시 반복한다. (recursion?)
		else if (targetLabel.contains("VP")) {
			// tempCHK = VPChunks.get(VPChunks.size() - 1);
			String temp = getPostposition(targetID, targetLabel);
			String[] ar = temp.split("/");
			String chunk = ar[0];
			String etc = "";
			if (ar.length > 1) {
				etc = ar[1];
			}
			StringEdit.replaceLast(targetChunk, etc, "");
			// targetChunk = targetChunk.replace(etc, "");
			VPChunks.add(new Chunk(chunk, etc, targetID, "VP", tempMod));

			ArrayList<Integer> tempModArr = new ArrayList<Integer>();
			for (int i = 0; i < tempMod.size(); i++) {
				tempModArr.add(tempMod.get(i));
			}

			for (int i = 0; i < tempModArr.size(); i++) {
				Iterator<Object> d2 = depenArr.iterator();
				for (int j = 0; j < tempModArr.get(i); j++) {
					d2.next();
				}
				JSONObject nextTarget = (JSONObject) d2.next();

				recur(nextTarget);

			}
		}

	}

	// chunk와 postposition 분리 함수
	@SuppressWarnings("unchecked")
	public String getPostposition(int ID, String label) {

		String chunk = "";
		String postposition = "";

		// ID 값에 해당하는 word객체를 가지고 와서 begin과 end를 추출
		// 해당 begin과 end 사이에 위치한 POS-Tag 정보를 보면서
		// 조사 표현에 해당하는 부분을 떼어냄

		Iterator<Object> w = wordArr.iterator();

		for (int i = 0; i < ID; i++) {
			w.next();
		}

		JSONObject targetWord = (JSONObject) w.next();

		int wordBegin = (int) (long) targetWord.get("begin");
		int wordEnd = (int) (long) targetWord.get("end");

		// System.out.println(wordBegin);
		// System.out.println(wordEnd);

		Iterator<Object> m = morpArr.iterator();

		for (int j = 0; j < wordBegin; j++) {
			m.next();
		}
		boolean VPLabelFlag = false;
		for (int k = 0; k <= wordEnd - wordBegin; k++) {
			JSONObject innerMorp = (JSONObject) m.next();
			String morpType = (String) innerMorp.get("type");
			String morpLemma = (String) innerMorp.get("lemma");

			if (label.contains("NP")) {
				if (morpType.equals("JKS") || morpType.equals("JKC") || morpType.equals("JKG") || morpType.equals("JKO")
						|| morpType.equals("JKB") || morpType.equals("JKV") || morpType.equals("JKQ")
						|| morpType.equals("JX") || morpType.equals("JC") || morpType.equals("SF")
						|| morpType.equals("SP")) {

					postposition += morpLemma;

				} else {
					chunk += morpLemma;
				}
			} else if (label.contains("VP")) {

				if (VPLabelFlag) {
					postposition += morpLemma;
				} else {
					if (morpType.equals("EP") || morpType.equals("EF") || morpType.equals("EC")
							|| morpType.equals("ETN") || morpType.equals("ETM") || morpType.equals("SF")
							|| morpType.equals("SP") || morpType.equals("JX")) {

						postposition += morpLemma;
						VPLabelFlag = true;
					} else {
						chunk += morpLemma;
					}
				}

			}

		}

		// System.out.println(chunk + " " + postposition);
		return chunk + "/" + postposition;

	}

	public void printChunks2File(BufferedWriter filebw) {
		try {

			String result = "";

			result += "Sentence: " + text.get("text") + "\n";

			result += "==NPChunks==" + "\n";
			for (int i = 0; i < NPChunks.size(); i++) {
				result += NPChunks.get(i).print() + " / " + NPChunks.get(i).getID() + " / "
						+ NPChunks.get(i).getLabel() + "\n";
			}

			result += "==VPChunks==" + "\n";
			for (int i = 0; i < VPChunks.size(); i++) {
				result += VPChunks.get(i).print() + " / " + VPChunks.get(i).getID() + " / " + VPChunks.get(i).getMod() + "\n";
			}
			
			result += "\n";
			result += "\n";

			filebw.write(result);

		} catch (Exception e) {

		}

	}
	
	public void printSTC2Console() {
		System.out.println("Sentence: " + text.get("text"));
	}

	public void printChunks2Console() {
		System.out.println("Sentence: " + text.get("text"));

		System.out.println("==NPChunks==");
		for (int i = 0; i < NPChunks.size(); i++) {
			System.out.println(
					NPChunks.get(i).print() + " / " + NPChunks.get(i).getID() + " / " + NPChunks.get(i).getLabel());
		}

		System.out.println();
		System.out.println("==VPChunks==");
		for (int i = 0; i < VPChunks.size(); i++) {
			System.out.println(
					VPChunks.get(i).print() + " / " + VPChunks.get(i).getID() + " / " + VPChunks.get(i).getMod());
		}
		System.out.println();
		System.out.println();
	}

	@SuppressWarnings("unchecked")
	public String processMod(JSONObject target, String input) {

		String result = input;

		String NPDepenText = null;

		JSONArray NPMod = (JSONArray) target.get("mod");
//		String NPLabel = (String) target.get("label");

		// if (NPLabel.contains("NP")) {
		ArrayList<Integer> NPModArr = new ArrayList<Integer>();

		Iterator<Object> n = NPMod.iterator();

		while (n.hasNext()) {
			NPModArr.add((int) (long) n.next());
		}

		if (!NPModArr.isEmpty()) {
			for (int k = NPModArr.size() - 1; k >= 0; k--) {

				Iterator<Object> npd = depenArr.iterator();

				for (int l = 0; l < NPModArr.get(k); l++) {
					npd.next();
				}

				JSONObject NPDepen = (JSONObject) npd.next();
//				String NPDepenLabel = (String) NPDepen.get("label");
				// if (NPDepenLabel.contains("NP")
				// || NPDepenLabel.contains("AP")
				// || NPDepenLabel.contains("DP")) {
				NPDepenText = (String) NPDepen.get("text");

				String temp = NPDepenText + " " + result;

				result = processMod(NPDepen, temp);
				// }
				// }
			}
		}
		return result;
	}

	public static void main(String[] ar) {
		CoreExtractor parser = new CoreExtractor();
		KoreanAnalyzer ex = new KoreanAnalyzer();
		DPWDChanger dtc = new DPWDChanger();
		try {
			String output1 = ex.getResult("오디세우스는 그리스 신화상의 영웅으로, 이타카의 영주, 트로이 전쟁의 영웅, 트로이 목마의 고안자이다.");
			//교명은 요한, 아호는 우사, 죽적 등이다.
			//쾰른 대성당은 세계에서는 세 번째로 높은 로마네스크·고딕 양식 성당이다. 

			String output2 = parser.parse(output1);

			String result = dtc.change(output2);

			Chunker c = new Chunker();
			c.chunk(result);
			c.printChunks2Console();
			// BufferedWriter filebw = new BufferedWriter(new
			// OutputStreamWriter(
			// new FileOutputStream("data\\chunked\\test12.json"), "UTF8"));
			// filebw.write(result);
			// filebw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
