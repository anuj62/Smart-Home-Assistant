package hackathon.stamford.smarthomestatus2;

import java.util.HashMap;
import java.util.Map;


public class Mappings {
    static Map<String, String> roomToFlowID = new HashMap<String, String>();
    static Map<String, String> roomAliases = new HashMap<String, String>();
    static Map<String, String> measureAliases = new HashMap<String, String>();
    static Map<String, String> measureUnits = new HashMap<String, String>();


    Mappings()
    {
        roomToFlowID.put( "2flcowk","f56c4c7415bb7092d12e798a7");
        roomToFlowID.put( "aud","f56c4c76968056d7a7d42014e");
        roomToFlowID.put( "audlob","f56c4c7c968056d7a7d4202aa");
        roomToFlowID.put( "dav","f56c4c6c268056d7a7d41fee0");
        roomToFlowID.put( "wad","f56c4c83d68056d7a7d420452");

        roomToFlowID.put( "dr","f56c660e268056d4a318c41e6");
        roomToFlowID.put( "kt","f56c660c368056d4a318c4166");
        roomToFlowID.put( "nc","f56c660cb5bb70951649b4acb");

        roomAliases.put("2nd floor", "2flcowk");
        roomAliases.put("second floor", "2flcowk");
        roomAliases.put("coworking space", "2flcowk");
        roomAliases.put("auditorium", "aud");
        roomAliases.put("auditorium lobby", "audlob");
        roomAliases.put("lobby", "audlob");
        roomAliases.put("davenport room", "dav");
        roomAliases.put("davenport", "dav");
        roomAliases.put("wadel", "wad");
        roomAliases.put("room 101", "wad");
        roomAliases.put("101", "wad");

//        roomAliases.put("dining room", "dr");
//        roomAliases.put("kitchen", "kt");
//        roomAliases.put("server room", "nc");

        measureAliases.put("humidity","humidity_%_0");
//        measureAliases.put("luminiscence","luminiscence_lux_0");
        measureAliases.put("brightness","luminiscence_lux_0");
        measureAliases.put("temperature","temperature_degcc_0");

        measureUnits.put("humidity_%_0", "percent");
        measureUnits.put("luminiscence_lux_0", "lux");
        measureUnits.put("temperature_degcc_0", "degree celsius");
        measureUnits.put("temperature__0", "fahrenheit");
    }
}