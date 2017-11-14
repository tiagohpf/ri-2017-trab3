package Utils;

import java.util.HashMap;
import java.util.Map;

public class Values {
    private Map<Integer, Integer> values;

    public Values() {
        values = new HashMap<>();
    }

    public Map<Integer, Integer> getValues() {
        return values;
    }

    public void addValue(int id, int frequency) {
        values.put(id, frequency);
    }

    public int getNumber(int id) {
        return values.get(id);
    }
    
    public void setValues(Map<Integer, Integer> values) {
        this.values = values;
    }
}
