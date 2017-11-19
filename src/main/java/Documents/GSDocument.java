package Documents;

import Utils.Values;
import java.util.HashMap;
import java.util.Map;

public class GSDocument extends Document{
    private Map<Integer, Values> relevances;
    /**
     * Class that extends Document abstract class
     * Class that represents Gold Standard Files
     * Constructor.
     */
    public GSDocument() {
        relevances = new HashMap<>();
    }
    
    public GSDocument(int id, Map<Integer, Values> relevances) {
        super(id);
        this.relevances = relevances;
    }
    
    public Map<Integer, Values> getRelevances() {
        return relevances;
    }
    
    public void setRelevances(Map<Integer, Values> relevances) {
        this.relevances = relevances;
    }
    
    /**
     * Print object
     * @return string 
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Integer, Values> line : relevances.entrySet()) {
            Values values = line.getValue();
            for (Map.Entry<Integer, Double> value : values.getValues().entrySet())
                sb.append(String.format("{%d, %d, %d}", line.getKey(), value.getKey(), value.getValue().intValue()));
        }
        return sb.toString();
    }
}
