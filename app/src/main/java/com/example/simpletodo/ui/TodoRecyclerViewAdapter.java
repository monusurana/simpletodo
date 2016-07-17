package com.example.simpletodo.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.simpletodo.R;
import com.example.simpletodo.data.TodoItem;

import java.util.ArrayList;
import java.util.List;

public class TodoRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private List<TodoItem> mItems;
    private OnItemClickListener mListener;

    private SparseBooleanArray selectedItems;

    /**
     * Constructor
     */
    public TodoRecyclerViewAdapter() {
        mItems = new ArrayList<>();
        selectedItems = new SparseBooleanArray();
    }

    /**
     * Interface to get Edit and Delete events in the activity
     */
    public interface OnItemClickListener {
        void onItemClick(TodoItem item, int position, View parent);
    }

    /**
     * Function to set the listener for Edit and Delete events
     *
     * @param listener Listener for the events
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        if (viewType == TYPE_HEADER) {
            View headerView = inflater.inflate(R.layout.list_item_header, parent, false);
            return new ViewHolderHeader(headerView);
        } else if (viewType == TYPE_ITEM) {
            View itemView = inflater.inflate(R.layout.tasks_list_item, parent, false);


            return new ViewHolderItem(itemView);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ViewHolderHeader) {
            ((ViewHolderHeader) holder).mTitle.setText(R.string.all_todos);
            ((ViewHolderHeader) holder).mCount.setText(String.valueOf(mItems.size()));

        } else if (holder instanceof ViewHolderItem) {
            TodoItem contact = mItems.get(position - 1);
            TextView editTaskView = ((ViewHolderItem) holder).editTaskView;
            editTaskView.setText(contact.getTask());

            ImageView priority = ((ViewHolderItem) holder).priorityView;
            priority.setBackground(ContextCompat.getDrawable(priority.getContext(), R.drawable.priority));
            ((GradientDrawable) priority.getBackground()).setColor(getPriorityColor(contact.getPriority()));

            TextView priorityText = ((ViewHolderItem) holder).priorityTextView;
            priorityText.setText(getPriorityString(contact.getPriority()));

            holder.itemView.setSelected(selectedItems.get(position - 1, false));
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        } else {
            return TYPE_ITEM;
        }
    }

    /**
     * Function to update the list of TodoItems
     *
     * @param tasks List of TodoItems as an input
     */
    public void updateAdapter(List<TodoItem> tasks) {
        mItems = null;
        mItems = tasks;
    }

    /**
     * Update a TodoItem at a specific position
     *
     * @param task     Updated task string
     * @param position Position of the task to be updated
     */
    public void updateItem(String task, String priority, int position) {
        final TodoItem contact = mItems.get(position);
        contact.setTask(task);
        contact.setPriority(getPriority(priority));

        contact.save();

        notifyDataSetChanged();
    }

    /**
     * Add Todo item to the list
     *
     * @param task Task to be added to the list
     */
    public void addTodoItem(String task, String priority) {
        TodoItem item = new TodoItem(task, getPriority(priority));
        mItems.add(mItems.size(), item);

        item.save();

        notifyItemInserted(mItems.size());
        notifyItemChanged(0);
    }

    /**
     * Add Todo item at a specific position
     *
     * @param task     Task to be added to the list
     * @param position Position where the task should be added
     */
    public void addTodoItemAtPosition(String task, int priority, int position) {
        TodoItem item = new TodoItem(task, priority);
        mItems.add(position, item);

        item.save();

        notifyItemInserted(position + 1);
        notifyItemChanged(0);
    }

    /**
     * Remove a TodoItem from the list
     *
     * @param item Todoitem to be removed
     */
    public void removeTodoItem(TodoItem item) {
        int position = mItems.indexOf(item);
        mItems.remove(position);

        item.delete();

        notifyItemRemoved(position + 1);
        notifyItemChanged(0);
    }

    /**
     * View Holder for Recycler View Item
     */
    public class ViewHolderItem extends RecyclerView.ViewHolder {
        private ImageView priorityView;
        private TextView editTaskView;
        private TextView priorityTextView;

        public ViewHolderItem(final View itemView) {
            super(itemView);

            priorityView = (ImageView) itemView.findViewById(R.id.priority);
            editTaskView = (TextView) itemView.findViewById(R.id.tvTask);
            priorityTextView = (TextView) itemView.findViewById(R.id.tvPriority);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null)
                        mListener.onItemClick(mItems.get(getLayoutPosition() - 1), getLayoutPosition() - 1, v);

                    if (!selectedItems.get(getLayoutPosition() - 1, false)) {
                        selectedItems.clear();
                        selectedItems.put(getLayoutPosition() - 1, true);
                        v.setSelected(true);
                        notifyDataSetChanged();
                    } else {
                        selectedItems.delete(getLayoutPosition() - 1);
                        v.setSelected(false);
                        notifyDataSetChanged();
                    }
                }
            });
        }
    }


    /**
     * Delete selected item
     *
     * @param position
     */
    public void deleteSelectedItem(int position) {
        selectedItems.delete(position);
        notifyItemChanged(position + 1);
    }

    /**
     * View Holder for Recycler View Header
     */
    public static class ViewHolderHeader extends RecyclerView.ViewHolder {
        public TextView mTitle;
        private TextView mCount;

        public ViewHolderHeader(View itemView) {
            super(itemView);

            mTitle = (TextView) itemView.findViewById(R.id.header);
            mCount = (TextView) itemView.findViewById(R.id.count);
        }
    }

    /**
     * Helper method to return priority based on String input
     *
     * @param priority
     * @return integer value of priority
     * TODO: Use getResources() to use strings directly from xml instead of hard-coded values
     */
    private int getPriority(String priority) {
        switch (priority) {
            case "Low":
                return 1;
            case "Medium":
                return 2;
            case "High":
                return 3;
        }

        return 1;
    }

    /**
     * Helper method to return priority based on String input
     *
     * @param priority
     * @return integer value of priority
     * TODO: Use getResources() to use strings directly from xml instead of hard-coded values
     */
    private String getPriorityString(int priority) {
        switch (priority) {
            case 1:
                return "Low";
            case 2:
                return "Medium";
            case 3:
                return "High";
        }

        return "Low";
    }

    /**
     * Helper function to return color based on the priority
     *
     * @param priority
     * @return returns the integer value of the color
     * <p/>
     * TODO: Use getResources().getColor() to use color directly from xml
     */
    private int getPriorityColor(int priority) {
        String GREEN = "#009688";
        String RED = "#F44336";
        String YELLOW = "#FDD835";

        switch (priority) {
            case 1:
                return Color.parseColor(GREEN);
            case 2:
                return Color.parseColor(YELLOW);
            case 3:
                return Color.parseColor(RED);
        }

        return Color.parseColor(GREEN);
    }
}