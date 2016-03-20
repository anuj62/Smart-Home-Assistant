package hackathon.stamford.smarthomestatus2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;




import org.apache.http.client.ClientProtocolException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;



class GetFlowThingsIO {
	private Map <String, String> inputMap;
	private Map <String, String> wordMap;
	private Mappings mapping;
	private static HashSet <String> validKeys;
	private static HashSet <String> validRooms;
	public GetFlowThingsIO (String[] validRoomsArray) {
		validRooms = new HashSet<String>();
		for (int i = 0; i < validRoomsArray.length; ++i) {
			validRooms.add(validRoomsArray[i]);
		}
		initValidKeys();
		mapping = new Mappings();
	}
	public GetFlowThingsIO () {
		validRooms = new HashSet<String>();
		initValidKeys();
		mapping = new Mappings();
	}

	public String getOutput(FlowInputTypes flowInputType, String inputParam) {
		if (flowInputType == FlowInputTypes.FLOWDATA) {
			String url = "https://api.flowthings.io/v0.1/ply2016/flow/"+inputParam;
			return getHTTPOutput(url);
		} else if (flowInputType == FlowInputTypes.FLOWIDS) {
			String url = "https://api.flowthings.io/v0.1/ply2016/flow/";
			return getHTTPOutput(url);
		}
		return null;
	}

	private void initValidKeys () {
		validKeys = new HashSet<String>();
		validKeys.add("capacity");
		validKeys.add("temperature");
		validKeys.add("humidity");
		validKeys.add("luminosity");
	}

	//Only does capacity points currently.
	public String getParsedOutput (String input) {
		String result = null;
		JSONObject json;
		System.out.println(input);
		try {
			json = (JSONObject) new JSONParser().parse(input);
	    } catch (ParseException e) {
		  // TODO Auto-generated catch block
		  e.printStackTrace();
		  return null;
	    }
	    JSONArray body = (JSONArray) json.get("body");
	    if (body == null) {
		  return result;
	    }
	    HashMap<String, String> roomCapacity = new HashMap<String, String>();
	    for (int i = 0; i < body.size(); ++i) {
		  getCapacityForElement((JSONObject) body.get(i), roomCapacity);
	    }
	    return getHashOutputInString(roomCapacity);
    }

	private String getHashOutputInString(HashMap<String, String> roomCapacity) {
		String result = "";
		Iterator it = roomCapacity.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry)it.next();
			String resultRow = "Capacity of " + pair.getKey() + " is " + pair.getValue() + "\n";
			result = result+"\n"+resultRow;
		}
		return result;
	}

	public String getMeasuresFor(List<String> roomList, List<String> measureList)
	{
		String measureURLFormat = "https://api.flowthings.io/v0.1/ply2016/drop/%1$s?filter=EXISTS %2$s&limit=1";
		StringBuilder responseBuilder = new StringBuilder();
		for (String roomName : roomList)
		{
			System.out.println("In room loop: " + roomName);
			if (!Mappings.roomAliases.containsKey(roomName)) {
				continue;
			}
			String flowID = Mappings.roomToFlowID.get(roomName);
			String resultPrefix = "In "+roomName+" ";
			StringBuilder responseBuilderSub = new StringBuilder();
			responseBuilderSub.append(resultPrefix);
			boolean validOutput = false;
			for (String measure : measureList)
			{
				System.out.println("In measure loop: " + measure);
				if (!Mappings.measureAliases.containsKey(measure)) {
					continue;
				}				
				String measureValue = getMeasuresFor(roomName, measure);
				if (measureValue != null && !measureValue.trim().isEmpty() ) {
                  responseBuilderSub.append(measureValue);
				}
				validOutput = true;
			}
			if (validOutput == true) {
				responseBuilder.append(responseBuilderSub).append(".");
			}
		}

		return responseBuilder.toString();
	}

	public String getMeasuresFor(String roomName, String measure)
	{
		String measureURLFormat = "https://api.flowthings.io/v0.1/ply2016/drop/%1$s?filter=EXISTS %2$s&limit=1";
		return getMeasuresFor(roomName, measure, measureURLFormat);
	}

	private String getMeasuresFor(String roomName, String measure, String measureURLFormat)
	{
		StringBuilder responseBuilder = new StringBuilder();

		System.out.println(roomName +" "+ measure);
		
		String roomID = Mappings.roomAliases.get(roomName);
		String flowID = Mappings.roomToFlowID.get(roomID);
		String measure_identifier = Mappings.measureAliases.get(measure);
		if (measure_identifier == null || measure_identifier.isEmpty()) {
			return "";
		}
		String measureURL = String.format(measureURLFormat, flowID, measure_identifier);
		measureURL = measureURL.replaceAll(" ", "%20");
		System.out.println(roomID +" "+ flowID +" "+ measure_identifier);
		System.out.println(measureURL);
		String httpOutput = getHTTPOutput(measureURL);
		if (httpOutput == null) {
			return "";
		}
		JSONObject responseObj  = (JSONObject) JSONValue.parse(httpOutput);
		if (responseObj == null) {
			return "";
		}
		JSONArray bodyObj = (JSONArray) responseObj.get("body");
		System.out.println(bodyObj.size());
		String measureValue = getValueFor((JSONObject) bodyObj.get(0), measure_identifier);
		if (measureValue != null && !measureValue.isEmpty()) {
		  System.out.println(measureValue);
		  responseBuilder.append(", the " + measure + " is " + measureValue+" ");
		} else {
		  return "";
		}
		
		return responseBuilder.toString();
	}
	
	private String getValueFor(JSONObject bodyelement, String measure_identifier)
	{
		String result = null;
		if (bodyelement.containsKey("elems")) {
		  System.out.println("elems");
		  JSONObject elems = (JSONObject) bodyelement.get("elems");
		  if (elems.containsKey(measure_identifier)) {
			  elems = (JSONObject) elems.get(measure_identifier);
			  System.out.println("outer value");
			  if (elems.containsKey("value")) {
				  JSONObject valueObj = (JSONObject) elems.get("value");
			    if (valueObj.containsKey("value")) {
				  System.out.println("inner value");
				  JSONObject value = (JSONObject) valueObj.get("value");
				  if (value.containsKey("value")) {
				    System.out.println("Innermost value");
				    String valueString = mapping.measureUnits.get(measure_identifier);
				    if (valueString.trim().isEmpty()) {
				    	System.out.println("Empty value");
				    	return null;
				    }
				    return value.get("value").toString() + " " + mapping.measureUnits.get(measure_identifier);
				  } else {
					return null;
				  }
			    } else {
				  return null;
			    }
			  } else {
				return null;
			  }
		  } else {
			  return null;
		  }
		} return result;
	}

	private void getCapacityForElement (JSONObject bodyElement, HashMap<String, String> roomCapacity) {
		Iterator<String> keys = validKeys.iterator();
		while (keys.hasNext()) {
			String key = keys.next();
			if (bodyElement.containsKey(key)) {
				if (key.equals("capacity")) {
					String capacity = bodyElement.get("capacity").toString();
					if (capacity == null) {
						return;
					}
					if (bodyElement.containsKey("path")) {
						String path = getShortPath(bodyElement.get("path").toString());
						if (path != null && validRooms.contains(path)) {
						  roomCapacity.put(path, capacity);
						}
					}
				}
			}
		}
	}
	
	private String getShortPath (String path) {
		String[] pathSubDivisions = path.split("/");
		int length = pathSubDivisions.length;
		if (length > 0) {
			return pathSubDivisions[length - 1].toLowerCase();
		}
		return "";
	}
	private String getHTTPOutput (String url) {
		try {
			return new HTTPStuff().getFlowThingsHTTPOutput(url);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public void addMeasuresIfEmpty (List<String> measures){
		boolean containsStatus = false;
		for (String measure: measures) {
			if (measure.equals("status")) {
				containsStatus = true;
				break;
			}
		}
		if (containsStatus == true) {
			measures.clear();
		}
        if (measures.size() == 0) {
			Iterator<String> it = Mappings.measureAliases.keySet().iterator();
			while (it.hasNext()) {
				measures.add(it.next());
			}
		}
        
	}

}
