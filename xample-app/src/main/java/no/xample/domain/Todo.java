package no.xample.domain;

import no.bols.katana.server.KatanaJSONObject;

public class Todo extends KatanaJSONObject{
    private String task;
    private boolean done;

    public Todo(String id) {
        this.setId(id);
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}
