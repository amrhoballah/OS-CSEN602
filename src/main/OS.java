package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Scanner;

public class OS implements Kernel{
    Hashtable<String, Object> memoryHashtable;
    String[] dictionary = {"print", "input", "readFile", "writeFile", "add", "assign"};

    public OS(){
        memoryHashtable = new Hashtable<>();
    }
    

    @Override
    public String readFile(String fileName) throws IOException {
        String path ="src/resources/"+readMemo(fileName)+".txt";
        File file=new File(path);
        BufferedReader br=new BufferedReader(new FileReader(file));
        String str;
        String s=br.readLine();
        while((str=br.readLine())!=null){
            s=s+"\n"+str;
        }
        br.close();
        return s;
    }

    @Override
    public void writeFile(String fileName, String data) {
        try{
            String path="src/resources/"+readMemo(fileName)+".txt";
            FileWriter f = new FileWriter(path);
            System.out.println(data);
            f.write(readMemo(data));
            f.close();
            System.out.println("Successfully wrote to the file.");
        }
        catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        
    }

    @Override
    public void print(String data) {
        if(readMemo(data) == null)
            System.out.println(data);
        else
            System.out.println(readMemo(data));        
    }

    @Override
    public String input() {
        Scanner in = new Scanner(System.in);
        return in.nextLine();
    }

    @Override
    public String readMemo(String varName) {
        String s = (String) memoryHashtable.get(varName);
        return s;
    }

    @Override
    public String writeMemo(String varName, String data) {
        memoryHashtable.put(varName, data);
        return varName;
    }

    @Override
    public void add(String var1, String var2) {
        try{
            int a = Integer.parseInt(readMemo(var1));
            int b= Integer.parseInt(readMemo(var2));
            int sum = a+b;
            String s=sum+"";
            writeMemo(var1,s);
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
        
    }

    @Override
    public void parser(String programName) throws IOException {
        String path ="src/resources/" + programName + ".txt";
        File file=new File(path);
        BufferedReader br=new BufferedReader(new FileReader(file));
        String[] s = new String[(int) br.lines().count()];
        br.close();
        br = new BufferedReader(new FileReader(file));
        int i = 0;
        String str;
        while((str=br.readLine())!=null){
            s[i++] = str;
        }
        br.close();
        for (String call : s) {
            System.out.println(call);
            execute(call);
        }
    }

    @Override
    public String execute(String systemCall) throws IOException{
        String[] array = systemCall.split(" "); 
        String call = array[0];
        String var1 = array.length > 1 ? array[1] : null;
        String var2 = array.length == 3 && array[2].equals("input") ? execute("input") : array.length == 3 ? var2 = array[2] : null;
        if(array.length > 3){
            String s = "";
            for (int i = 2; i < array.length; i++) {
                if (i > 2) {
                   s+=(" ");
                 }
                s+=(array[i]);
            }
            var2 = execute(s);
        }
        switch(call){
            case "print": print(var1); return "";
            case "input": return input();
            case "readFile": return readFile(var1);
            case "writeFile": writeFile(var1, var2); return "";
            case "add": add(var1, var2); return "";
            case "assign": writeMemo(var1, var2); return "";
        }
        return "";
    }
    
    public static void main(String[] args) {
        OS os = new OS();
        try {
            os.parser("Program 1");
            os.parser("Program 2");
            os.parser("Program 3");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
