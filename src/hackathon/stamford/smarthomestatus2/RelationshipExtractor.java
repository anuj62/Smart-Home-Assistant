package hackathon.stamford.smarthomestatus2;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import android.speech.tts.TextToSpeech;

public class RelationshipExtractor {

	public RelationshipExtractor(String command, TextToSpeech tts) {
		// TODO Auto-generated constructor stub

		
		try {
			URL url = new URL("https://gateway.watsonplatform.net/relationship-extraction-beta/api");
			HttpsURLConnection conn = (HttpsURLConnection)url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("content-type", "application/x-www-form-urlencoded");
//			conn.setRequestProperty("content-type", "application/json");
			Authenticator.setDefault (new Authenticator() {
			    protected PasswordAuthentication getPasswordAuthentication() {
			        return new PasswordAuthentication ("16615385-439c-484d-81b3-370d306af04d", "bOOVesvQOK1k".toCharArray());
			    }
			});
			conn.setDoOutput(true);
			String params = "txt=" + command + "&" +
					"sid=ie-en-news&" +
					"rt=json";
			DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
			dos.writeBytes(params);
			dos.flush();
			dos.close();
			InputStream in;
			int status = conn.getResponseCode();
			if(status >= HttpStatus.SC_BAD_REQUEST){
				System.out.println("Error! status code - " + status);
				in = conn.getErrorStream();
			} else {
				in = conn.getInputStream();
			}
			BufferedReader rd = new BufferedReader(new InputStreamReader(in));
			String line;
			StringBuilder result = new StringBuilder();
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			rd.close();
			line = result.toString();
//			System.out.println(line);
			
			HashSet<String> things = new HashSet<String>();
			JSONObject outJson = new JSONObject(line);

			String text = outJson.getJSONObject("doc").getJSONObject("sents").getJSONArray("sent").getJSONObject(0).getJSONObject("parse").getString("text");
			Pattern pattern = Pattern.compile("\\[NP [^\\[](.*?)[^\\[] NP\\]");
			Matcher matcher = pattern.matcher(text);
			while(matcher.find()){
				String thing = matcher.group();
				String[] nouns = thing.split(" ");
				thing = "";
				for(int i = 0; i < nouns.length; i++){
					String[] words = nouns[i].split("_");
					if(words.length > 1 && words[1].equals("CC")){
						things.add(thing.toLowerCase(Locale.getDefault()).trim());
						System.out.println("regex thing: " + thing);
						thing = "";
					}
					if(!thing.equals("")){
						thing += " ";
					}
					if(words.length > 1 && (words[1].equals("NN") || words[1].equals("JJ") || words[1].equals("NNP") || words[1].equals("CD"))){
						thing += words[0];
					}
				}
				System.out.println("regex thing: " + thing);
				things.add(thing.toLowerCase(Locale.getDefault()).trim());
			}

			GetFlowThingsIO io = new GetFlowThingsIO();
			String output;
			
			ArrayList<String> thingsList = new ArrayList<String>();
			thingsList.addAll(things);
			ArrayList<String> measures = new ArrayList<String>(thingsList);
			io.addMeasuresIfEmpty(measures);
			output = io.getMeasuresFor(thingsList , measures).trim();
			
			if (output == null) {
				output = "";
			}
			System.out.println("Output: " + output + ".");
			
			if(tts.isSpeaking()){
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(!output.equals("")){
				tts.speak(output, TextToSpeech.QUEUE_FLUSH, null);
			}
			else{
				tts.speak("Sorry. I could not find an answer to that question.", TextToSpeech.QUEUE_FLUSH, null);
			}
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
