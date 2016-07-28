package edu.kaist.mrlab.templator.question;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class QT2Template {

	public JSONObject printOutput(String question, ArrayList<String> input) {

		JSONObject result = new JSONObject();
		JSONArray slotJArr = new JSONArray();

		String order = "ORDER BY DESC ";
		String orderVar = null;
		String query = "SELECT ?v1 WHERE { ";
		String[][] sparqlVars = new String[input.size()][3];
		String[][] sparqlVerbs = new String[input.size()][3];

		ArrayList<Slot> slotList = new ArrayList<Slot>();
		
		boolean isEst = false;
		
		/**
		 * below is for query
		 */
		String[] vars;
		for (int i = 0; i < input.size(); i++) {

			vars = input.get(i).split("\t");
			sparqlVerbs[i] = vars;

		}

		boolean isSecondVar = false;
		int vCount = 1;
		Slot varSlot = null;
		for (int i = 0; i < sparqlVerbs.length; i++) {
			for (int j = 0; j < sparqlVerbs[0].length; j++) {

				if (sparqlVerbs[i][j].equals("var")) {
					
					if(!isSecondVar){
						varSlot = new Slot(sparqlVerbs[i][j], "v" + vCount);
						slotList.add(varSlot);
						sparqlVars[i][j] = "v" + vCount;
						vCount++;
						isSecondVar = true;
						
					} else{
						sparqlVars[i][j] = varSlot.getVar();
					}
					
				} else{
					
					slotList.add(new Slot(sparqlVerbs[i][j], "v" + vCount));
					sparqlVars[i][j] = "v" + vCount;
					vCount++;
					
				}
				

				query += "?" + sparqlVars[i][j] + " ";
			}

			query += ". ";
		}

		query += "} ";

		/**
		 * below is for slots
		 */

		for (int i = 0; i < sparqlVerbs.length; i++) {
			for (int j = 0; j < sparqlVerbs[0].length; j++) {

				String verb = sparqlVerbs[i][j];

				if (verb.equals("var")) {
					continue;
				}

				JSONObject tempJObj = new JSONObject();
				
				String var = null;
				for(int k = 0; k < slotList.size(); k++){
					
					if(slotList.get(k).getVerb().equals(verb)){
						var = slotList.get(k).getVar();
						slotList.remove(k);
						break;
					}
					
				}
				
				if(var == null){
					
					System.exit(0);
					
				}
				
				tempJObj.put("s", var);

				if (verb.equals("type")) {

					tempJObj.put("p", "is");
					tempJObj.put("o", "<http://lodqa.org/vocabulary/sort_of>");
					slotJArr.put(tempJObj);

				}

				// j-1 indicate that property.
				else if (j > 1 && sparqlVerbs[i][j - 1].equals("type")) {

					tempJObj.put("p", "is");
					tempJObj.put("o", "rdf:Class");
					slotJArr.put(tempJObj);

					tempJObj = new JSONObject();
					tempJObj.put("s", var);
					tempJObj.put("p", "verbalization");
					tempJObj.put("o", verb);
					slotJArr.put(tempJObj);

				}
				
				// special case '-est'
				else if (j == 1 && sparqlVerbs[i][j].contains("가장")) {

					tempJObj.put("p", "is");
					tempJObj.put("o", "rdf:Property");
					slotJArr.put(tempJObj);

					tempJObj = new JSONObject();
					tempJObj.put("s", var);
					tempJObj.put("p", "verbalization");
					tempJObj.put("o", verb.replace("가장", "").trim());
					slotJArr.put(tempJObj);
					
					orderVar = sparqlVars[i][j+1];
					isEst = true;

				}

				// it means now it is property.
				else if (j == 1) {

					tempJObj.put("p", "is");
					tempJObj.put("o", "rdf:Property");
					slotJArr.put(tempJObj);

					tempJObj = new JSONObject();
					tempJObj.put("s", var);
					tempJObj.put("p", "verbalization");
					tempJObj.put("o", verb);
					slotJArr.put(tempJObj);

				}

				// other all cases.
				else {

					tempJObj.put("p", "is");
					if(j == 0){
						tempJObj.put("o", "rdf:Resource");
					} else{
						tempJObj.put("o", "rdf:Resource|rdfs:Literal");
					}
					slotJArr.put(tempJObj);

					tempJObj = new JSONObject();
					tempJObj.put("s", var);
					tempJObj.put("p", "verbalization");
					tempJObj.put("o", verb);
					slotJArr.put(tempJObj);

				}

			}
		}
		
		if(isEst){
			
			query += order + "(?" + orderVar + ") LIMIT 1";
			
			for(int i = 0; i < slotJArr.length(); i++){
			
				JSONObject slot = (JSONObject)slotJArr.get(i);
				String strS = (String) slot.get("s");
				if(strS.equals(orderVar)){

					slotJArr.remove(i);
					i--;
					
				}
				
			}
			
			
		}

		result.put("question", question);
		result.put("query", query);
		result.put("slots", slotJArr);
		result.put("score", 1.0);

		return result;
	}

}
