package Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    public void addValue(int id, double score) {
        values.put(id, score);
    }

    public double getNumber(int id) {
        return values.get(id);
    }
    
    public void setValues(Map<Integer, Double> values) {
        this.values = values;
    }
    
    public Map<Integer, Double> sortValues() {
        List<Map.Entry<Integer,Double>> entries = new ArrayList<>(values.entrySet());
        Collections.sort(entries, (Map.Entry<Integer, Double> o1, Map.Entry<Integer, Double> o2) 
                -> o1.getValue().compareTo(o2.getValue()) * -1);
        Collections.sort(entries, (Map.Entry<Integer, Double> o1, Map.Entry<Integer, Double> o2) -> {
            int res = 0;
            if (o1.getValue() == o2.getValue())
                res = o1.getKey().compareTo(o2.getKey());
            return res;
        });
        // Create new map with sorted results
        Map<Integer, Double> result = new LinkedHashMap<>();
        for (Map.Entry<Integer, Double> entry: entries)
            result.put(entry.getKey(), entry.getValue());
        return result;
    }
}
