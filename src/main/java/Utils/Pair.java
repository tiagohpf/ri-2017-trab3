package Utils;

/**
 * IR, October 2017
 *
 * Assignment 2 
 *
 * @author Tiago Faria, 73714, tiagohpf@ua.pt
 * @author David dos Santos Ferreira, 72219, davidsantosferreira@ua.pt
 * @param <K>
 * @param <V>
 * 
 */

/*
* Pair.
* Structure to manage pairs.
*/

public class Pair<K, V> {
    private K key;
    private V value;

    /**
     * Constructor that receives a key and a value.
     * @param key
     * @param value
     */
    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Get the key of pair.
     * @return key
     */
    public K getKey() {
        return key;
    }

    /**
     * Get the value of pair.
     * @return value
     */
    public V getValue() {
        return value;
    }

    /**
     * Set a new key in pair.
     * @param key
     */
    public void setKey(K key) {
        this.key = key;
    }

    /**
     * Set a new value in pair.
     * @param value
     */
    public void setValue(V value) {
        this.value = value;
    }

    /**
     * Get the string of pair.
     * @return string
     */
    @Override
    public String toString() {
        return "(" + key + ", " + value + ")";
    }
}