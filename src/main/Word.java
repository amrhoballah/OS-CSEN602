package main;

public class Word {
    String key;
    Object value;
    
    public Word(String key, Object value) {
        this.key = key;
        this.value = value;
    }
    
    public String toString(){
        return "Key: "+this.key+"\nValue: "+this.value;
    }
}
