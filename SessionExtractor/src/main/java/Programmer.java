
import java.util.List;

/**
 * Representation of a programmer
 */
public class Programmer {
    
    private final int id;
    private List<Session> sessionList;

    public Programmer(int id, List<Session> sessionList) {
        this.id = id;
        this.sessionList = sessionList;
    }
  
    int getProgrammerId(){
        return id;
    }
    
    public List<Session> getProgrammerSession(){
        return sessionList;
    }
    
}
