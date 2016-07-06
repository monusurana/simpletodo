package com.example.simpletodo.data;

import com.orm.SugarRecord;

/**
 * Class to represent a Todo task
 */
public class TodoItem extends SugarRecord  {
    private String task;

    public TodoItem() {
    }

    public TodoItem(String task) {
        String finalstr = task.substring(0, 1).toUpperCase() + task.substring(1);
        this.task = finalstr;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }
}
