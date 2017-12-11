/**
 * Copyright 2016 University of Zurich
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */


import java.io.File;
import java.util.Set;

import cc.kave.commons.model.events.IDEEvent;
import cc.kave.commons.utils.io.IReadingArchive;
import cc.kave.commons.utils.io.ReadingArchive;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.DateTimeException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Extracts event frequency data with optional tf-idf to a csv file
 */
public class ExtractEvents {

    private String eventsDir;
    private int userProfCount;
    private int sessionCount;
    private HashSet<String> buildTypes;
    private EventMatcher eventMatcher;
    private int dateExceptions = 0;

    public ExtractEvents(String eventsDir) {
            this.eventsDir = eventsDir;
    }

    /**
     * Extracts session data for all programmers and saves to a defined csv file
     */
    public void runEventExtraction() {
        boolean wantTFIDF = false;              // true if tf-idf to be applied
        String outputFileName = "output.csv";   // name of csv to output to
        EventGranularity requiredEG = EventGranularity.HIGH; // granularity of events
        buildTypes = new HashSet<>();
        eventMatcher = new EventMatcher();     
        Set<String> eventTypes = new HashSet<>();
        List<Programmer> programmers;

        if(requiredEG == EventGranularity.LOW){
            eventTypes = loadEventTypesSet("basicEventTypes.txt");
        } else if(requiredEG == EventGranularity.MEDIUM){
            eventTypes = loadEventTypesSet("mediumEventTypes.txt");
        } else {
            eventTypes = loadEventTypesSet("expandedCommandEventSet.txt");
        }
        programmers = 
                extractProgrammerSessionEventFrequency(eventTypes, requiredEG);

        if(wantTFIDF){
            convertSessionEventFrequencyToTFIDF(programmers, eventTypes);
        }
        writeToCsv(programmers, eventTypes, outputFileName, wantTFIDF);

    }
    
    // Load a set of event names representing a event grularity
    private Set<String> loadEventTypesSet(String fileName){
        String line;
        Set<String> events = new HashSet<>();
        try {
            FileReader fr = new FileReader(fileName);
            BufferedReader br = new BufferedReader(fr);
            line = br.readLine();
            String[] eventStrings = line.split(",");
            for(String eventString: eventStrings){
                events.add(eventString);
            }    
            br.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ExtractEvents.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ExtractEvents.class.getName()).log(Level.SEVERE, null, ex);
        }
        return events;
    }
    
    // Write programmer session to csv
    private void writeToCsv(List<Programmer> progList, 
            Set<String> eventNames, String fileName, boolean wantTFIDF){
        try {
            // write to csv
            PrintWriter pw = new PrintWriter(new File(fileName));
            StringBuilder sb = new StringBuilder();
            sb.append("prog_id,session_id,");
            Iterator itr = eventNames.iterator();
            while(itr.hasNext()){
                sb.append(itr.next() + ",");
            }
            sb.deleteCharAt(sb.length()-1);
            sb.append("\n");
            pw.write(sb.toString());
            for(Programmer p: progList){
                for(Session s: p.getProgrammerSession()){
                    sb = new StringBuilder();
                    sb.append(p.getProgrammerId() + "," + s.getSessionId() + ",");
                    HashMap<String, Double> sessionTFIDF = null;
                    HashMap<String, Integer> sessionCount = null;
                    if(wantTFIDF){
                        sessionTFIDF = s.getTFIDF();
                    } else{
                        sessionCount = s.getEventCounts();
                    }

                    itr = eventNames.iterator();
                    while(itr.hasNext()){
                        if(wantTFIDF){
                            sb.append(sessionTFIDF.get(itr.next()) + ",");
                        } else {
                            sb.append(sessionCount.get(itr.next()) + ",");
                        }
                    }
                    sb.deleteCharAt(sb.length()-1);
                    sb.append("\n");
                    pw.write(sb.toString());
                }
            }
            pw.close();
        } catch (FileNotFoundException ex) {
            System.out.println("file not found");
            Logger.getLogger(ExtractEvents.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }

    // Extract session data for all programmers for granularity specified
    private List<Programmer> extractProgrammerSessionEventFrequency(
            Set<String> eventTypes, EventGranularity eg) {
        int progId = 0;
        ExecutorService executor = Executors.newFixedThreadPool(4);
        List<Programmer> progList = new ArrayList<>(); 
        Set<String> userZips = IoHelper.findAllZips(eventsDir);
        List<LoadFrequencyData> threadList = new ArrayList<>();
        
        // Set up thread in pool
        for (String userZip : userZips) {
            Set<String> eventInstance = new HashSet<>(eventTypes);
            threadList.add(new LoadFrequencyData(userZip, eventInstance, progId, eg));
            progId++;
        }
        for(LoadFrequencyData thread: threadList){
            executor.submit(thread);
        }
        executor.shutdown();
        
        // Start thread pool
        try {
            executor.awaitTermination(1, TimeUnit.DAYS);
            // Threads finished or timeout
            for(LoadFrequencyData thread: threadList){
                Programmer prog = thread.getProg();
                if(prog != null){
                    progList.add(thread.getProg());               
                }

            }
        } catch (InterruptedException ex) {
            Logger.getLogger(ExtractEvents.class.getName()).log(Level.SEVERE, null, ex);
        }
        return progList;
    }
    
    // Applies tf-idf to all programmers sessions
    public void convertSessionEventFrequencyToTFIDF(List<Programmer> progList, 
            Set<String> eventTypes){
        // process f to tf
        int totalSessionCount = 0;
        for(Programmer p: progList){
            for(Session s: p.getProgrammerSession()){
                s.generateEventFrequency();
                totalSessionCount++;
            }
        }
        
        // process f to idf
        HashMap<String, Double> idf = convertFrequencyToIDF(progList, 
                eventTypes, totalSessionCount);
        System.out.println("idf: " + idf);
        // apply tf-idf
        for(Programmer p: progList){
            for(Session s: p.getProgrammerSession()){
                s.calculateTfIDF(idf);
            }
        }
    }
    
    // Generates idf values
    private HashMap<String, Double> convertFrequencyToIDF(List<Programmer> progList, 
            Set<String> eventTypes, int totalSessionCount){
        HashMap<String, Integer> tempIDF = new HashMap<>();
        HashMap<String, Double> idf = new HashMap<>();
        for(String event: eventTypes){
            tempIDF.put(event, 0);
        }
        
        for(String event: eventTypes){
            for(Programmer p: progList){
                for(Session s: p.getProgrammerSession()){
                    if(s.getEventCount(event) > 0){
                        tempIDF.put(event, tempIDF.get(event)+1);
                    }
                }
            }
        }
        
        for(String key: tempIDF.keySet()){
            idf.put(key, Math.log10((float) totalSessionCount / (tempIDF.get(key) + 1))); 
        }
        return idf;
    }

    /**
     * Thread to extract session data on a single programmer
     */
    class LoadFrequencyData implements Runnable{
        
        private String userZip;
        private Set<String> eventTypes;
        private int progId;
        private Programmer prog;
        private EventGranularity eg;
        private EventMatcher em;
        
        private LoadFrequencyData(String userZip, Set<String> eventTypes, int id, 
                EventGranularity eg) {
            this.userZip = userZip;
            this.eventTypes = eventTypes;
            this.progId = id;
            this.eg = eg;
            if(!(eg == EventGranularity.LOW)){
                em = new EventMatcher();
            }
        }
        
        Programmer getProg(){
            return prog;
        }
        
        @Override
        public void run() {
            System.out.printf("\n#### processing user zip: %s #####\n", userZip);
            HashMap<String, HashMap<String, Integer>> sessionEventData = new HashMap<>();
            try (IReadingArchive ra = new ReadingArchive(new File(eventsDir, userZip))) {
                while(ra.hasNext()){
                    try{
                        IDEEvent e = ra.getNext(IDEEvent.class);
                        String sessionId = e.IDESessionUUID;

                        if(!sessionEventData.containsKey(sessionId)){
                            sessionEventData.put(sessionId, createEventMap(eventTypes));    
                        }
                        String eventType;
                        if(eg == EventGranularity.LOW){
                            eventType = e.getClass().getSimpleName();
                        } else {
                            eventType = em.matchEventToString(e, eg);
                        }
                        if(eventTypes.contains(eventType)){
                            HashMap<String, Integer> sessionMap = 
                                    sessionEventData.get(sessionId);
                            sessionMap.put(eventType, sessionMap.get(eventType)+1);
                        }
                    } catch(DateTimeException e){
                        // ignore date exception
                    }
                }
                List<Session> sessionList = new ArrayList<>();
                for(String sessionId: sessionEventData.keySet()){
                    Session session = new Session(sessionId, sessionEventData.get(sessionId));
                    if(!(session.size() < 2000)){
                        sessionList.add(session);
                    }
                }
                if(sessionList.size() > 0){
                    prog = new Programmer(progId, sessionList);
                    System.out.printf("\n#### finished user zip: %s #####\n", userZip);
                } else {
                    System.out.printf("\n#### failed user zip: %s #####\n", userZip);
                }
            } catch(Exception e){
                System.out.println("error " + e);
            }
        }
        
        // Create hashmap with events as keys
        private HashMap<String, Integer> createEventMap(Set<String> eventTypes){
            HashMap<String, Integer> eventMap = new HashMap<>();
            for(String event: eventTypes){
                eventMap.put(event, 0);
            }
            return eventMap;
        }
    
    }
    
    /* Used to save a set of event names to file
    private void saveEventTypesSet(Set<String> setOfEventsToSave, String fileName){
        try {
            PrintWriter pw = new PrintWriter(new File(fileName));
            StringBuilder sb = new StringBuilder();
            Iterator itr = setOfEventsToSave.iterator();
            while(itr.hasNext()){
                sb.append(itr.next());
                sb.append(",");
            }
            sb.deleteCharAt(sb.length()-1);
            sb.append("\n");
            pw.write(sb.toString());
            pw.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ExtractEvents.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    */
    
    /* Used to extract a set of event types
    private void extractEventGranularity(String userZip, Set<String> eventSet, 
        EventGranularity reqGranularity) {
        try (IReadingArchive ra = new ReadingArchive(new File(eventsDir, userZip))) {
            while(ra.hasNext()){
                try{
                    IDEEvent e = ra.getNext(IDEEvent.class);
                    String id = e.IDESessionUUID;
                    if(reqGranularity == EventGranularity.LOW){
                        eventSet.add(e.getClass().getSimpleName());
                    } else {
                        String event = eventMatcher.matchEventToString(e, reqGranularity);
                        if(event.length() > 0){
                            eventSet.add(event);
                        }
                    }
                } catch(DateTimeException e){
                    dateExceptions++;
                }
            }
        }
    }
    
    
    private Set<String> getListOfAllEvents(EventGranularity reqGranularity) {
        int userCount = 0;
        Set<String> eventSet = new HashSet<>();
        Set<String> userZips = IoHelper.findAllZips(eventsDir);
        for (String userZip : userZips) {
                userProfCount = 0;
                sessionCount = 0;
                System.out.printf("\n#### processing user zip: %s #####\n", userZip);
                extractEventGranularity(userZip, eventSet, reqGranularity);
                userCount++;
        }
        System.out.println(eventMatcher.commandTypes);
        System.out.println(eventMatcher.commandTypes.size());
        return eventSet;
    }
    */
}