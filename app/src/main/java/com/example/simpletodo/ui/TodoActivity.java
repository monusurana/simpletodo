package com.example.simpletodo.ui;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.example.simpletodo.R;
import com.example.simpletodo.data.TodoItem;

import java.util.List;

public class TodoActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private TodoRecyclerViewAdapter mTodoRecyclerViewAdapter;
    private FloatingActionButton mFloatingActionButton;
    private TodoRecyclerViewAdapter.OnItemClickListener mClickListener;
    private AlertDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);

        mRecyclerView = (RecyclerView) findViewById(R.id.tasks_recyclerview);
        mTodoRecyclerViewAdapter = new TodoRecyclerViewAdapter();

        mRecyclerView.setAdapter(mTodoRecyclerViewAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (TodoItem.count(TodoItem.class) > 0 ) {
            List<TodoItem> tasks = TodoItem.listAll(TodoItem.class);
            if (tasks != null) {
                mTodoRecyclerViewAdapter.updateAdapter(tasks);
            }
        }

        final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        mClickListener = new TodoRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemEdit(List<TodoItem> tasks, int position, View parent) {
                EditItem(parent, tasks.get(position).getTask(), position);
            }

            @Override
            public void onItemDelete(TodoItem task, final int position, View parent) {
                final String todoItem = task.getTask();

                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, getString(R.string.task_deleted), Snackbar.LENGTH_LONG)
                        .setAction(R.string.undo, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mTodoRecyclerViewAdapter.addTodoItemAtPosition(todoItem, position);
                            }
                        });

                snackbar.show();
            }
        };
        mTodoRecyclerViewAdapter.setOnItemClickListener(mClickListener);

        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.add_task);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AddItem(v);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mDialog != null)
            mDialog.dismiss();
    }

    /**
     * Helper function to add item
     * @param v View
     */
    private void AddItem(View v) {
        EditItem(v, null, 0);
    }

    /**
     * Helper function to Edit the Task
     * @param v View
     * @param text Text of the existing task
     * @param position Position of the existing task in the list
     */
    private void EditItem(View v, final String text, final int position) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        builder.setIcon(android.R.drawable.ic_dialog_info);

        if (text == null) {
            builder.setTitle(getString(R.string.add_item));
            builder.setMessage(getString(R.string.add_item_message));
        } else {
            builder.setTitle(getString(R.string.edit_item));
            builder.setMessage(getString(R.string.edit_item_message));
        }

        final EditText input = new EditText(v.getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        if (text != null) {
            input.setText(text);
            input.setSelection(text.length());
        }

        builder.setView(input, 50, 0, 50, 0);
        builder.setPositiveButton(getString(R.string.ok_button), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (text != null) {
                    mTodoRecyclerViewAdapter.updateItem(input.getText().toString(), position);
                } else {
                    mTodoRecyclerViewAdapter.addTodoItem(input.getText().toString());

                }
            }
        });

        builder.setNegativeButton(getString(R.string.cancel_button), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        mDialog = builder.create();

        input.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0)
                    mDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                else
                    mDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            mDialog.setOnShowListener(new DialogInterface.OnShowListener() {

                @Override
                public void onShow(DialogInterface dialog) {
                    if (text != null)
                        ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                    else
                        ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                }
            });
        }

        mDialog.show();
    }
}
