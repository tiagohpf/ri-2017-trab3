package Utils;

/**
 * IR, October 2017
 *
 * Assignment 2 
 *
 * @author Tiago Faria, 73714, tiagohpf@ua.pt
 * @author David dos Santos Ferreira, 72219, davidsantosferreira@ua.pt
 * 
 */

// Class that represents a key of HashMap
public class Key{
    private final int firstValue;
    private final int secondValue;

    /**
     * Constructor
     * @param firstValue
     * @param secondValue 
     */
    public Key(int firstValue, int secondValue) {
        this.firstValue = firstValue;
        this.secondValue = secondValue;
    }
    
    /**
     * Get first value
     * @return first value
     */
    public int getFirstValue() {
        return firstValue;
    }
    
    /**
     * Get second value
     * @return first value
     */
    public int getSecondValue() {
        return secondValue;
    }

    /**
     * Check if two objects are equal
     * @param o
     * @return 
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Key)) return false;
        Key key = (Key) o;
        return firstValue == key.firstValue && secondValue == key.secondValue;
    }

    /**
     * Get hashCode of object
     * @return hashCode
     */
    @Override
    public int hashCode() {
        int result = firstValue;
        result = 1400 * result + secondValue;
        return result;
    }
    
    /**
     * Print object
     * @return string
     */
    @Override
    public String toString() {
        return "(" + firstValue + "," + secondValue + ")";
    }
}