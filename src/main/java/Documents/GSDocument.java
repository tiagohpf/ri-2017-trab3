package Documents;

import Utils.Key;
import java.util.HashMap;
import java.util.Map;

public class GSDocument extends Document{
    private Map<Key, Integer> relevances;
    /**
     * Class that extends Document abstract class
     * Class that represents Gold Standard Files
     * Constructor.
     */
    public GSDocument() {
        relevances = new HashMap<>();
    }
    
    public GSDocument(int id, Map<Key, Integer> relevances) {
        super(id);
        this.relevances = relevances;
    }
    
    public Map<Key, Integer> getRelevances() {
        return relevances;
    }
    
    public void setRelevances(Map<Key, Integer> relevances) {
        this.relevances = relevances;
    }
    
    /**
     * Print object
     * @return string 
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Key, Integer> line : relevances.entrySet()) {
            sb.append(String.format("{%d, %d, %d}", 
                    line.getKey().getFirstValue(), line.getKey().getSecondValue(), line.getValue()));
        }
        return sb.toString();
    }
}
