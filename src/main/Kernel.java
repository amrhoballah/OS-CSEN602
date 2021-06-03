package main;

import java.io.IOException;

public interface Kernel {
    String readFile(String fileName) throws IOException;
    void writeFile(String fileName, String data);
    void print(String data);
    String input();
    String readMemo(String varName);
    String writeMemo(String varName, String data); 
    void add(String var1, String var2);
    void parser(String programName) throws IOException;
    String execute(String systemCall) throws IOException;
}
