package edu.kaist.mrlab.templator.srdf.tools;

import org.apache.http.HttpHeaders;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

/**
 * This is java-rest-client post to Korean Entity Linking Module. 
 * It use 'Apache HttpClient' and 'java-json' library.
 * 
 * @author Sangha Nam
 */
@SuppressWarnings("deprecation")
public class KoreanEL {

	private HttpClient client;

	private String post(String url, JSONObject params, String encoding) {

		client = new DefaultHttpClient();

		try {
			HttpPost post = new HttpPost(url);
			/**
			 * Set header something else.
			 */
			post.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
			System.out.println("POST : " + post.getURI());

			post.setEntity(new StringEntity(params.toString(), encoding));

			ResponseHandler<String> rh = new BasicResponseHandler();

			String postResult = client.execute(post, rh);

			return postResult;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			client.getConnectionManager().shutdown();
		}

		return "error";

	}

	public String post(String url, JSONObject params) {
		/**
		 * Change encoding something else.
		 */
		return post(url, params, "UTF-8");
	}

	public static void main(String[] args) throws Exception {

		KoreanEL rct = new KoreanEL();

		JSONObject params = new JSONObject();
		params.put("text", "고려군은 함주에서 장벽을 쌓아 오랑캐의 침입을 막았다.");

		String postResult = rct.post("http://143.248.135.150:2221/entity_linking", params);

		System.out.println(postResult);

	}
}