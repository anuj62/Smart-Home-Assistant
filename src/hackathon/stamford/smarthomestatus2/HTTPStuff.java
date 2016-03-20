package hackathon.stamford.smarthomestatus2;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;

import java.io.*;
class HTTPStuff {

	  public String getFlowThingsHTTPOutputSimple (String url) {
		System.out.println(url);
		try {
			return getFlowThingsHTTPOutput(url);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	  }
	  
	  public String getFlowThingsHTTPOutput(String url) throws ClientProtocolException, IOException {
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("X-Auth-token", "12PjnZk3HMzzfWg4LUSYtIvMZUzcBXzq");
		headers.put("Content-Type", "application/json");
        //return getHTTPOutput(url, headers);
        return getHTTPOutputSimple(url, headers);
	  }
	  
	  
	  private String getHTTPOutputSimple (String url, Map<String, String> headers) throws ClientProtocolException, IOException {
		    URL url_ = new URL(url);
		    HttpURLConnection connection = (HttpURLConnection) url_.openConnection();
		    connection.setRequestMethod("GET");
			for (String key : headers.keySet()) {
			    String value = headers.get(key);
			    connection.setRequestProperty(key, value);
			}
	        int responseCode = connection.getResponseCode();
	        System.out.println("GET Response Code :: " + responseCode);
	        if (responseCode == HttpURLConnection.HTTP_OK) { // success
	            BufferedReader in = new BufferedReader(new InputStreamReader(
	                    connection.getInputStream()));
	            String inputLine;
	            StringBuffer response = new StringBuffer();
	 
	            while ((inputLine = in.readLine()) != null) {
	                response.append(inputLine);
	            }
	            in.close();
	 
	            // print result
	            return response.toString();
	        } else {
	            return "";
	        }
	  }
	  
}

