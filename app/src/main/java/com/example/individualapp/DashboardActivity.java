package com.example.individualapp;

import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

public class DashboardActivity extends AppCompatActivity {
    ArrayList<ToDo> list;
    Boolean order = false;
    ImageView settings;
    DBHandler dbHandler;
    Toolbar dashboard_toolbar;
    RecyclerView rv_dashboard;
    DashboardActivity activity;
    FloatingActionButton fab_dashboard;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        dashboard_toolbar = findViewById(R.id.dashboard_toolbar);
        rv_dashboard = findViewById(R.id.rv_dashboard);
        fab_dashboard = findViewById(R.id.fab_dashboard);
        settings = findViewById(R.id.iv_settings);
        setSupportActionBar(dashboard_toolbar);
        setTitle("To do list");
        activity = this;
        dbHandler = new DBHandler(activity);
        rv_dashboard.setLayoutManager(new LinearLayoutManager(activity));
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity, SettingsActivity.class));
            }
        });

        fab_dashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
                dialog.setTitle("Add a new item");
                View view = getLayoutInflater().inflate(R.layout.dialog_dashboard, null);
                final EditText toDoName = view.findViewById(R.id.ev_todo);
                dialog.setView(view);

                dialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (toDoName.getText().toString().length() > 0) {
                            ToDo toDo = new ToDo();
                            toDo.setName(toDoName.getText().toString());
                            toDo.setCompleted(-1);
                            dbHandler.addToDo(toDo);
                            refreshList();
                        }
                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                dialog.show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else
            return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        order = sharedPreferences.getBoolean("switch", false);
        refreshList();
        super.onResume();
    }

    public void updateToDo(final ToDo toDo) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        dialog.setTitle("Update a item");
        View view = getLayoutInflater().inflate(R.layout.dialog_dashboard, null);
        final EditText toDoName = view.findViewById(R.id.ev_todo);
        toDoName.setText(toDo.getName());
        dialog.setView(view);
        dialog.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (toDoName.getText().toString().length() > 0) {
                    toDo.setName(toDoName.getText().toString());
                    toDo.setCompleted(-1);
                    dbHandler.updateToDo(toDo);
                    refreshList();
                }
            }
        });
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        dialog.show();
    }

    public void refreshList() {
        if (order == false)
            rv_dashboard.setAdapter(new DashboardAdapter(activity, dbHandler.getToDos()));
        else {
            ArrayList<ToDo> sortedList = dbHandler.getToDos();
            Collections.sort(sortedList, (ToDo one, ToDo second) -> {
               return one.getName().compareTo(second.getName());
            });
            rv_dashboard.setAdapter(new DashboardAdapter(activity, sortedList));
        }
    }

    public void nextPage(final ToDo item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final AlertDialog alert = builder.create();
        View view = getLayoutInflater().inflate(R.layout.rv_child_item, null);
        final TextView itemName = view.findViewById(R.id.tv_todoitem_name);
        ImageView editItem = view.findViewById(R.id.iv_edit);
        ImageView deleteItem = view.findViewById(R.id.iv_delete);
        itemName.setText(item.getName());
        alert.setView(view);
        alert.show();

        editItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.updateToDo(item);
                alert.dismiss();
            }
        });

        deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
                dialog.setTitle("Are you sure");
                dialog.setMessage("Do you want to delete this item ?");
                dialog.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int pos) {
                        activity.dbHandler.deleteToDo(item.getId());
                        activity.refreshList();
                        alert.dismiss();
                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alert.dismiss();
                    }
                });
                dialog.show();
            }
        });
    }

    class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.ViewHolder> {
        DashboardActivity activity;

        DashboardAdapter(DashboardActivity activity, ArrayList<ToDo> List) {
            list = List;
            this.activity = activity;
        }

        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new ViewHolder(LayoutInflater.from(activity).inflate(R.layout.rv_child_dashboard, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, final int i) {
            holder.toDoName.setText(list.get(i).getName());
            holder.toDoName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.nextPage(list.get(i));
                }
            });

            if (list.get(i).getCompleted() == -1)
                holder.toDoCompleted.setChecked(false);
            else
                holder.toDoCompleted.setChecked(true);
            holder.toDoCompleted.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (list.get(i).getCompleted() == -1)
                        list.get(i).setCompleted(1);
                    else
                        list.get(i).setCompleted(-1);
                    activity.dbHandler.updateToDo(list.get(i));
                }
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView toDoName;
            CheckBox toDoCompleted;

            ViewHolder(View v) {
                super(v);
                toDoName = v.findViewById(R.id.tv_todo_name);
                toDoCompleted = v.findViewById(R.id.cb_item);
            }
        }
    }
}
