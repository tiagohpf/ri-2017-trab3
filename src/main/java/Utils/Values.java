package Utils;

import java.util.HashMap;
import java.util.Map;

public class Values {
    private Map<Integer, Double> values;

    public Values() {
        values = new HashMap<>();
    }
    
    public Values(Map<Integer, Double> values)  {
        this.values = values;
    }

    public Map<Integer, Double> getValues() {
        return values;
    }

    public void addValue(int id, double frequency) {
        values.put(id, frequency);
    }

    public double getNumber(int id) {
        return values.get(id);
    }
    
    public void setValues(Map<Integer, Double> values) {
        this.values = values;
    }
}
