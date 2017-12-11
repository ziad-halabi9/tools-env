
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class TFIDFJUnitTest {
   /* 
  {"NavigationEvent", "ActivityEvent", 
                "CompletionEvent", "FindEvent", "DocumentEvent", "SystemEvent", 
                "UserProfileEvent", "SolutionEvent", "BuildEvent", 
                "IDEStateEvent", "WindowEvent", "TestRunEvent", "CommandEvent", 
                "VersionControlEvent", "EditEvent", "DebuggerEvent", 
                "InfoEvent", "ErrorEvent"};
*/
    private List<Programmer> progList;
    private String[] eventNames ={"CommandEvent", "BuildEvent", "TestRunEvent", 
        "EditEvent"};
    
    @Before
    public void setUp() {
        HashMap<String, Integer> s1 = new HashMap<>();
        HashMap<String, Integer> s2 = new HashMap<>();
        HashMap<String, Integer> s3 = new HashMap<>();
        s1.put("CommandEvent", 6);
        s1.put("BuildEvent", 0);
        s1.put("TestRunEvent", 1);
        s1.put("EditEvent", 3);
        s2.put("CommandEvent", 0);
        s2.put("BuildEvent", 0);
        s2.put("TestRunEvent", 4);
        s2.put("EditEvent", 0);
        s3.put("CommandEvent", 4);
        s3.put("BuildEvent", 2);
        s3.put("TestRunEvent", 0);
        s3.put("EditEvent", 7);
        List<Session> prog1session = new ArrayList<>();
        prog1session.add(new Session("0", s1));
        prog1session.add(new Session("1", s2));
        prog1session.add(new Session("2", s3));
        Programmer progOne = new Programmer(1, prog1session);
        s1 = new HashMap<>();
        s2 = new HashMap<>();
        s1.put("CommandEvent", 0);
        s1.put("BuildEvent", 0);
        s1.put("TestRunEvent", 7);
        s1.put("EditEvent", 14);
        s2.put("CommandEvent", 32);
        s2.put("BuildEvent", 11);
        s2.put("TestRunEvent", 0);
        s2.put("EditEvent", 0);
        List<Session> prog2session = new ArrayList<>();
        prog2session.add(new Session("0", s1));
        prog2session.add(new Session("1", s2));
        Programmer progTwo = new Programmer(2, prog2session);
        progList = new ArrayList<>();
        progList.add(progOne);
        progList.add(progTwo);
    }
    
    // test not fully implmented, currently using visual check on output
    @Test
    public void testTFIDF(){
        ExtractEvents testInst = new ExtractEvents("");
        Set<String> eventNamesSet = new HashSet<>();
        for(String s: eventNames){
            eventNamesSet.add(s);
        }
        testInst.convertSessionEventFrequencyToTFIDF(progList, eventNamesSet);
        for(Programmer p: progList){
            for(Session s: p.getProgrammerSession()){
                System.out.println("" + s.getTFIDF());
            }
        }
        // correct answers = [com = 0.058146 build = 0, test = 0.009691 edit = 0.029073]
        // [com = 0 build = 0 test = 0.09691 edit = 0]
        // [com = 0.029818462 build = 0.034129231 test = 0 edit = 0.052182308]
        // [com = 0 build = 0 test = 0.032303333 edit = 0.064606667]
        // [com = 0.07211907 build = 0.056749767 test = 0 edit = 0]
        assertTrue(true);
        
    
    }
    
}
