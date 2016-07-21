package edu.kaist.mrlab.templator.question;

import java.io.UnsupportedEncodingException;

import org.json.JSONArray;
import org.json.JSONObject;

import edu.kaist.mrlab.templator.srdf.tools.KoreanEL;

public class Entitizer {

	private KoreanEL kel = new KoreanEL();

	public String callKEL(String input) {

		JSONObject params = new JSONObject();
		params.put("text", input);

		String postResult = kel.post("http://143.248.135.150:2221/entity_linking", params);

		return postResult;

	}
	
	public String decode(String unicode) throws UnsupportedEncodingException {
		StringBuffer str = new StringBuffer();

		char ch = 0;
		for (int i = unicode.indexOf("\\u"); i > -1; i = unicode.indexOf("\\u")) {
			ch = (char) Integer.parseInt(unicode.substring(i + 2, i + 6), 16);
			str.append(unicode.substring(0, i));
			str.append(String.valueOf(ch));
			unicode = unicode.substring(i + 6);
		}
		str.append(unicode);

		return str.toString();
	}

	public String parser(String input) throws UnsupportedEncodingException {
	
		System.out.println(input);
		JSONArray jEntities = new JSONArray(input);
		for(int i = 0; i < jEntities.length(); i++){
			
			JSONObject jEntity = jEntities.getJSONObject(i);
			System.out.println(jEntity);
			
		}

		String decoded = this.decode("\uc774\uc21c\uc2e0");

		return decoded;
	}

	public static void main(String[] ar) throws Exception {

		Entitizer e = new Entitizer();
		String json = e.callKEL("이순신 장군이 1597년에 명량해협에서 지휘한 해전은 무엇인가?");
		String entities = e.parser(json);
		System.out.println(entities);

	}
}
