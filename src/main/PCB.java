package main;

public class PCB {
    int processId;
    State processState;
    int programCounter;
    int startBoundary;
    int endBoundary;

    public PCB(int processId, State processState, int startBoundary, int endBoundary) {
        this.processId = processId;
        this.processState = processState;
        this.programCounter = 0;
        this.startBoundary = startBoundary;
        this.endBoundary = endBoundary;
    }

    public PCB() {
        this.processId = 0;
        this.processState = State.NOT_RUNNING;
        this.programCounter = 0;
        this.startBoundary = 0;
        this.endBoundary = 0;
    }

    public String toString(){
        return "PCB:\nProcess ID "+this.processId+"\nProcess State "+this.processState
        +"\nProgram Counter "+this.programCounter+"\nMemory Boundary "
        +this.startBoundary+" to "+this.endBoundary;
    }
    
    public State fromStringState(String string) {
        if (string.equals("RUNNING"))
            return State.RUNNING;
        return State.NOT_RUNNING;
    }
}

enum State {
    NOT_RUNNING, RUNNING;
}