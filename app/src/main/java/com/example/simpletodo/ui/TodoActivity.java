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
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.example.simpletodo.R;
import com.example.simpletodo.data.TodoItem;
import com.example.simpletodo.utils.DividerItemDecoration;

import java.util.List;

public class TodoActivity extends AppCompatActivity {

    private static int DIVIDER_PADDING_DP = 16;

    private RecyclerView mRecyclerView;
    private TodoRecyclerViewAdapter mTodoRecyclerViewAdapter;
    private FloatingActionButton mFloatingActionButton;
    private TodoRecyclerViewAdapter.OnItemClickListener mClickListener;
    private AlertDialog mDialog;
    private FrameLayout mEditDeleteItem;
    private int mSelectedPosition = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);

        mRecyclerView = (RecyclerView) findViewById(R.id.tasks_recyclerview);
        mTodoRecyclerViewAdapter = new TodoRecyclerViewAdapter();

        mRecyclerView.setAdapter(mTodoRecyclerViewAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        RecyclerView.ItemDecoration itemDecoration =
                new DividerItemDecoration(this, R.drawable.divider, DIVIDER_PADDING_DP);
        mRecyclerView.addItemDecoration(itemDecoration);

        if (TodoItem.count(TodoItem.class) > 0) {
            List<TodoItem> tasks = TodoItem.listAll(TodoItem.class);
            if (tasks != null) {
                mTodoRecyclerViewAdapter.updateAdapter(tasks);
            }
        }

        final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        mEditDeleteItem = (FrameLayout) findViewById(R.id.flEditDelete);
        final LinearLayout deleteTask = (LinearLayout) findViewById(R.id.ivDelete);
        final LinearLayout editTask = (LinearLayout) findViewById(R.id.ivEdit);
        final LinearLayout cancelTask = (LinearLayout) findViewById(R.id.ivCancel);

        mClickListener = new TodoRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final TodoItem item, final int position, final View parent) {

                if (mSelectedPosition != position) {
                    mFloatingActionButton.setVisibility(View.GONE);
                    mEditDeleteItem.setVisibility(View.VISIBLE);

                    mSelectedPosition = position;

                    deleteTask.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final TodoItem contact = item;
                            mTodoRecyclerViewAdapter.removeTodoItem(contact);
                            final String todoItem = contact.getTask();
                            final int priority = contact.getPriority();

                            mFloatingActionButton.setVisibility(View.VISIBLE);
                            mEditDeleteItem.setVisibility(View.GONE);

                            Snackbar snackbar = Snackbar
                                    .make(coordinatorLayout, getString(R.string.task_deleted), Snackbar.LENGTH_LONG)
                                    .setAction(R.string.undo, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            mTodoRecyclerViewAdapter.addTodoItemAtPosition(todoItem, priority, position);
                                        }
                                    });

                            snackbar.show();

                            parent.setSelected(false);
                            mTodoRecyclerViewAdapter.deleteSelectedItem(position);

                            mSelectedPosition = -1;
                        }


                    });

                    editTask.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            EditItem(v, item.getTask(), item.getPriority(), position);
                            mFloatingActionButton.setVisibility(View.VISIBLE);
                            mEditDeleteItem.setVisibility(View.GONE);

                            parent.setSelected(false);
                            mTodoRecyclerViewAdapter.deleteSelectedItem(position);

                            mSelectedPosition = -1;
                        }
                    });

                    cancelTask.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mFloatingActionButton.setVisibility(View.VISIBLE);
                            mEditDeleteItem.setVisibility(View.GONE);

                            parent.setSelected(false);
                            mTodoRecyclerViewAdapter.deleteSelectedItem(position);

                            mSelectedPosition = -1;
                        }
                    });
                } else {
                    mFloatingActionButton.setVisibility(View.VISIBLE);
                    mEditDeleteItem.setVisibility(View.GONE);

                    mSelectedPosition = -1;
                }
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
     *
     * @param v View
     */
    private void AddItem(View v) {
        EditItem(v, null, 1, 0);
    }

    /**
     * Helper function to Edit the Task
     *
     * @param v        View
     * @param text     Text of the existing task
     * @param position Position of the existing task in the list
     */
    private void EditItem(View v, final String text, int priority, final int position) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        builder.setIcon(android.R.drawable.ic_dialog_info);

        if (text == null) {
            builder.setTitle(getString(R.string.add_item));
            builder.setMessage(getString(R.string.add_item_message));
        } else {
            builder.setTitle(getString(R.string.edit_item));
            builder.setMessage(getString(R.string.edit_item_message));
        }

        View view = (View) LayoutInflater.from(v.getContext()).inflate(R.layout.dialog_todo, null);
        final EditText input = (EditText) view.findViewById(R.id.editText);
        final Spinner spinner = (Spinner) view.findViewById(R.id.spinner);

        if (text != null) {
            input.setText(text);
            input.setSelection(text.length());
            spinner.setSelection(priority - 1);
        }

        builder.setView(view);
        builder.setPositiveButton(getString(R.string.ok_button), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (text != null) {
                    mTodoRecyclerViewAdapter.updateItem(input.getText().toString(), spinner.getSelectedItem().toString(), position);
                } else {
                    mTodoRecyclerViewAdapter.addTodoItem(input.getText().toString(), spinner.getSelectedItem().toString());
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
