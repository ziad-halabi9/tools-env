/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.HashMap;

/**
 * Class to represent a programmers session
 */
public class Session {
    private final String sessionId;
    private HashMap<String, Integer> eventCounts;
    private HashMap<String, Float> eventFrequency;
    private HashMap<String, Double> eventTFIDF;
    
    public Session(String sessionId, HashMap<String, Integer> eventCounts){
        this.sessionId = sessionId;
        this.eventCounts = eventCounts;
    }
    
    public int getEventCount(String event){
        return eventCounts.get(event);
    }
    
   HashMap<String, Integer> getEventCounts(){
       return eventCounts;
   }
    
    public String getSessionId(){
        return sessionId;
    }

    void generateEventFrequency(){
        eventFrequency = new HashMap<>();
        // find total number of events
        int totalSessionEvents = 0;        
        for(String key: eventCounts.keySet()){
            totalSessionEvents += eventCounts.get(key);
        }
        
        for(String key: eventCounts.keySet()){
            eventFrequency.put(key, (float) eventCounts.get(key) / totalSessionEvents);
        }
        
    }

    void calculateTfIDF(HashMap<String, Double> idf) {
        eventTFIDF = new HashMap<>();
        for(String key: eventFrequency.keySet()){
            eventTFIDF.put(key, eventFrequency.get(key) * idf.get(key));
        }
    }

    public HashMap<String, Double> getTFIDF() {
        return eventTFIDF;
    }

    int size() {
        int size = 0;
        for(String key: eventCounts.keySet()){
            size += eventCounts.get(key);
        }
        return size;
    }
    
}
