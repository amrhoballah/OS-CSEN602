package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class OS implements Kernel {
    Word[] memory;
    int pointer;
    int processCount;
    int ticks;
    PCB currentPCB;
    Queue<Integer> processQueue;

    public OS() {
        memory = new Word[35];
        processQueue = new LinkedList<Integer>();
        ticks = 0;
        processCount = 0;
        pointer = 0;
    }

    @Override
    public String readFile(String fileName) throws IOException {
        String path;
        Object var = readMemo(fileName);
        if (var == null)
            path = "src/resources/" + fileName + ".txt";
        else
            path = "src/resources/" + var.toString() + ".txt";
        File file = new File(path);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String str;
        String s = br.readLine();
        while ((str = br.readLine()) != null) {
            s = s + "\n" + str;
        }
        br.close();
        return s;
    }

    @Override
    public void writeFile(String fileName, String data) {
        try {
            String path = "src/resources/" + readMemo(fileName) + ".txt";
            FileWriter f = new FileWriter(path);
            if (readMemo(data) == null)
                f.write(data);
            else
                f.write(readMemo(data).toString());
            f.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    }

    @Override
    public void print(String data) {
        Object var = readMemo(data);
        if (var == null)
            System.out.println(data);
        else
            System.out.println(var.toString());
    }

    @Override
    public String input() {
        return new Scanner(System.in).nextLine();
    }

    @Override
    public Object readMemo(String varName) {
        switch (varName) {
            case "startBoundary":
                int j = 0;
                for (Word word : memory) {
                    if (word != null && word.key.equals("processId") && (Integer) word.value == currentPCB.processId) {
                        return memory[j + 3].value;
                    }
                    j++;
                }
                break;
            case "processState":
                return memory[currentPCB.startBoundary + 1].value;
            case "programCounter":
                return memory[currentPCB.startBoundary + 2].value;
            case "endBoundary":
                return memory[currentPCB.startBoundary + 4].value;
            case "instruction":
                return memory[currentPCB.startBoundary + 5 + currentPCB.programCounter].value;
            default:
                for (int i = currentPCB.startBoundary + 5; i <= currentPCB.endBoundary; i++) {
                    if (memory[i] != null && memory[i].key.equals(varName)) {
                        System.out.println("Memory Read - Index: "+i+"  Key: "+memory[i].key+"  Value: "+memory[i].value);
                        return memory[i].value;
                    }
                }
        }
        return null;
    }

    @Override
    public void writeMemo(String varName, Object data) {
        if (pointer < 35) {
            memory[pointer++] = new Word(varName, data);
            return;
        }
        switch (varName) {
            case "processState":
                memory[currentPCB.startBoundary + 1].value = data;
                break;
            case "programCounter":
                memory[currentPCB.startBoundary + 2].value = data;
                break;
            default:
                for (int i = currentPCB.startBoundary + 5; i <= currentPCB.endBoundary; i++) {
                    if (memory[i] == null) {
                        memory[i] = new Word(varName, data);
                        System.out.println("Memory Write - Index: "+i+"  Key: "+varName+"  Value: "+data);
                        return;
                    }
                    if (memory[i].key.equals(varName)) {
                        memory[i].value = data;
                        System.out.println("Memory Write - Index: "+i+"  Key: "+varName+"  Value: "+data);
                        return;
                    }
                }
        }
    }

    @Override
    public void add(String var1, String var2) {
        int a = Integer.parseInt(readMemo(var1).toString());
        int b = Integer.parseInt(readMemo(var2).toString());
        writeMemo(var1, a + b);
    }

    @Override
    public void parser(String programName) throws IOException {
        processCount++;
        String path = "src/resources/" + programName + ".txt";
        File file = new File(path);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String[] s = new String[(int) br.lines().count()];
        br.close();
        br = new BufferedReader(new FileReader(file));
        int i = 0;
        String str;
        while ((str = br.readLine()) != null) {
            s[i++] = str;
        }
        br.close();
        PCB pcb = new PCB(processCount, State.NOT_RUNNING, pointer, pointer + s.length + 6);
        System.out.println(pcb);
        processQueue.add(pcb.processId);
        writeMemo("processId", pcb.processId);
        writeMemo("processState", pcb.processState);
        writeMemo("programCounter", pcb.programCounter);
        writeMemo("startBoundary", pcb.startBoundary);
        writeMemo("endBoundary", pcb.endBoundary);
        System.out.println("\nInstructions:");
        for (String call : s) {
            System.out.println(call);
            writeMemo("instruction", call);
        }
        System.out.println("\n");
        pointer += 2;
    }

    @Override
    public String execute(String systemCall) throws IOException {
        String[] array = systemCall.split(" ");
        String call = array[0];
        String var1 = array.length > 1 ? array[1] : null;
        String var2 = array.length == 3 && array[2].equals("input") ? execute("input") : array.length == 3 ? var2 = array[2] : null;
        if (array.length > 3) {
            String s = "";
            for (int i = 2; i < array.length; i++) {
                if (i > 2) {
                    s += (" ");
                }
                s += (array[i]);
            }
            var2 = execute(s);
        }
        switch (call) {
            case "print":
                print(var1);
                return "";
            case "input":
                return input();
            case "readFile":
                return readFile(var1);
            case "writeFile":
                writeFile(var1, var2);
                return "";
            case "add":
                add(var1, var2);
                return "";
            case "assign":
                writeMemo(var1, var2);
                return "";
        }
        return "";
    }

    @Override
    public void scheduler() throws IOException {
        while (!processQueue.isEmpty()) {
            currentPCB = new PCB();
            currentPCB.processId = processQueue.peek();
            System.out.println("Chosen Program: " + currentPCB.processId);
            currentPCB.startBoundary = (Integer) readMemo("startBoundary");
            currentPCB.processState = currentPCB.fromStringState(readMemo("processState").toString());
            currentPCB.programCounter = (Integer) readMemo("programCounter");
            currentPCB.endBoundary = (Integer) readMemo("endBoundary");
            currentPCB.processState = State.RUNNING;
            writeMemo("processState", currentPCB.processState);
            while (ticks < 3 && currentPCB.processState == State.RUNNING) {
                execute(readMemo("instruction").toString());
                currentPCB.programCounter++;
                if (currentPCB.programCounter + currentPCB.startBoundary + 5 == currentPCB.endBoundary - 1) {
                    currentPCB.processState = State.NOT_RUNNING;
                    writeMemo("processState", currentPCB.processState);
                }
                ticks++;
            }
            currentPCB.processState = currentPCB.fromStringState(readMemo("processState").toString());
            if (currentPCB.processState == State.RUNNING) {
                processQueue.add(processQueue.remove());
                currentPCB.processState = State.NOT_RUNNING;
                writeMemo("processState", currentPCB.processState);
            } else {
                processQueue.remove();
            }
            writeMemo("programCounter", currentPCB.programCounter);
            System.out.println("Program " + currentPCB.processId + " Ran for " + ticks + " Time Slice(s)\n");
            ticks = 0;
        }
    }

    public static void main(String[] args) {
        OS os = new OS();
        try {
            //for(int i=1; i<4; i++)
            os.parser("Program 3");
            os.parser("Program 2");
            os.parser("Program 1");
            os.scheduler();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
