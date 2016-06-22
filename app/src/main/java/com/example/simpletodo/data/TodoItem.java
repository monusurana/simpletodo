package com.example.simpletodo.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class to represent a Todo task
 */
public class TodoItem implements Parcelable {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.task);
    }

    protected TodoItem(Parcel in) {
        this.task = in.readString();
    }

    public static final Creator<TodoItem> CREATOR = new Creator<TodoItem>() {
        @Override
        public TodoItem createFromParcel(Parcel source) {
            return new TodoItem(source);
        }

        @Override
        public TodoItem[] newArray(int size) {
            return new TodoItem[size];
        }
    };
}
