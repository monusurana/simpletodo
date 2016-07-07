package com.example.simpletodo.data;

import com.orm.SugarRecord;

/**
 * Class to represent a Todo task
 */
public class TodoItem extends SugarRecord  {
    String task;
    int priority;

    public TodoItem() {
    }

    public TodoItem(String task, int priority) {
        String finalstr = task.substring(0, 1).toUpperCase() + task.substring(1);
        this.task = finalstr;
        this.priority = priority;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
