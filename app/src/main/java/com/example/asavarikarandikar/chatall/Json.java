package com.example.asavarikarandikar.chatall;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import android.util.Log;


public class Json implements Serializable{
	/**
	 *
	 */
	private static final long serialVersionUID = 6325138424036049257L;


	HashMap<String,String> jsonList = new HashMap<String,String>();

	Json() {

	}

	Json(String jsonStyle) {
		String jsonStrippedbracket = jsonStyle.replaceAll("\\{", "");
		jsonStrippedbracket = jsonStrippedbracket.replaceAll("\\}", "");
		jsonStrippedbracket = jsonStrippedbracket.replaceAll("\"", "");
		Log.d("JsON" , jsonStyle);
		String documentData[] = jsonStrippedbracket.split(",");
		for(String doc : documentData) {
			String keyValue[] = doc.split(":");
			System.out.println(keyValue[0] +" "+ keyValue[1]);
			jsonList.put(keyValue[0], keyValue[1]);
		}
	}

	public void put (String key,String value) {
		jsonList.put(key, value);
	}

	public void put(String key,ArrayList<String> value) {
		for(int i=0;i<value.size();i++) {
			jsonList.put(key+i, value.get(i));
		}
	}

	public String getJsonString(){
		String json = "{";
		Iterator it = jsonList.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry)it.next();
			//System.out.println(pair.getKey() + " = " + pair.getValue());
			json = json + "\""+pair.getKey()+ "\":\""+pair.getValue()+"\",";
			//it.remove(); // avoids a ConcurrentModificationException
		}
		if(json.charAt(json.length()-1)==','){
			json = json.substring(0,json.length()-1);
		}
		json = json + "}";
		return json;
	}
	public ArrayList<String> getMulVal(String key){
		int i=0;
		ArrayList<String> messages = new ArrayList<String>();
		while(jsonList.containsKey(key + i)) {
			messages.add(jsonList.get(key+i));
			i++;
		}
		return messages;

	}
	public String getValue(String key) {
		if(jsonList.containsKey(key)){
			return jsonList.get(key);
		}
		else {
			return null;
		}
	}

	public String toString(){
		return getJsonString();
	}

}
