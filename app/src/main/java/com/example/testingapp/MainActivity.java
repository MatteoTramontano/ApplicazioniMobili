package com.example.testingapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    DataBaseHelper myDb;
    String filtroSel = "";
    Task task;
    ListView listView;

    ArrayList <Task> taskArrayList;
    customListAdapter listAdapter;



    Cursor data;


    private ViewPager mViewPager;
    private PagerAdapter mAdapter;
    private DrawerLayout mDrawerLayout;
    private AppBarConfiguration mAppBarConfiguration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setLogo(R.drawable.ic_darth_vader);
        //getSupportActionBar().setIcon(R.drawable.ic_darth_vader);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher_foreground);

        loadActivity();
    }
    @Override
    protected void onResume(){
        super.onResume();
        loadActivity();
    }


        private void loadActivity () {
            final Spinner filtro = (Spinner) findViewById(R.id.spinner_filter_cat);
            myDb = new DataBaseHelper(this);

            taskArrayList = new ArrayList<>();

            listView = (ListView) findViewById(R.id.list_view);
            data = myDb.getListContent();
            taskArrayList.clear();
            showTaskList(data, taskArrayList, listAdapter);
            //tutta roba per lo spinner
            ArrayList<String> categorie = new ArrayList<>();
            categorie = myDb.getAllCat();
            categorie.add(0, "ALL");

            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, categorie);
            adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            filtro.setAdapter(adapter);

            filtro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    filtroSel = filtro.getSelectedItem().toString();
                    if (filtroSel.equalsIgnoreCase("ALL")) {
                        //do nothing
                        showTaskList(data, taskArrayList, listAdapter);
                    } else {
                        ArrayList<Task> filterList = onSelecetedSpinner(filtroSel);
                        showTaskList(data, filterList, listAdapter);
                    }
                    Toast.makeText(MainActivity.this, "Filter selected " + filtroSel, Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                    //filtroSel = "";
                    Toast.makeText(MainActivity.this, "Null Filter selected ", Toast.LENGTH_SHORT).show();
                    showTaskList(data, taskArrayList, listAdapter);
                }
            });


            // Apre il task che viene cliccato
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                    Task task = (Task) listView.getItemAtPosition(position);
                    String title = task.getName();
                    String cat = task.getCategory();
                    Intent intent = new Intent(MainActivity.this, editTask.class);
                    intent.putExtra("title", title);
                    intent.putExtra("cat", cat);
                    startActivity(intent);
                }
            });

            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                    Task task = (Task) listView.getItemAtPosition(position);
                    final String title = task.getName();
                    final String cat = task.getCategory();
                    final int pos = position;
                    new AlertDialog.Builder(MainActivity.this)
                            .setIcon(android.R.drawable.ic_delete)
                            .setTitle("DELETE")
                            .setMessage("Are you sure to delete this item?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Cursor dataMio = myDb.getEditContent(title,cat);
                                    dataMio.moveToFirst();
                                    String code = dataMio.getString(0);
                                    int requestCode = Integer.parseInt(code);


                                    Intent alarmIntent = new Intent (MainActivity.this, AlarmReceiver.class);
                                    PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, requestCode, alarmIntent, PendingIntent.FLAG_ONE_SHOT);
                                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                                    alarmManager.cancel(pendingIntent);

                                    Task deleted = taskArrayList.get(pos);
                                    taskArrayList.remove(pos);
                                    myDb.deleteTask(title,cat);
                                    showTaskList(data, taskArrayList, listAdapter);
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();

                return  true;
                }

            });

            //Il bottone rimanda all' add Task activity
            FloatingActionButton fab = findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent add_intent = new Intent(MainActivity.this, add_task.class);
                    startActivity(add_intent);

                }
            });
        }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == R.id.action_category){
            Intent add_intent = new Intent(MainActivity.this,editCategory.class);
            startActivity(add_intent);
        }
        if(id == R.id.action_calendar){
            Intent add_intent = new Intent(MainActivity.this,CalendarActivity.class);
            startActivity(add_intent);
        }
        if(id == R.id.action_graphic){
            Intent add_intent = new Intent(MainActivity.this, Chart.class);
            startActivity(add_intent);
        }

        return super.onOptionsItemSelected(item);
    }

    //funzione che una volta richiamata mostra nella listview principale i tasj estratti dal db
    public void showTaskList (Cursor dataC, ArrayList <Task> taskArrayList, customListAdapter listAdapter) {

        if (dataC.getCount() == 0) {

            Toast.makeText(MainActivity.this, "empty category db", Toast.LENGTH_LONG).show();
        }  else {
            while (dataC.moveToNext() ) {
                if (!dataC.getString(6).equals("CLOSE")) {
                    task = new Task(dataC.getString(1), dataC.getString(2), dataC.getString(3), dataC.getString(4),dataC.getString(6));
                    taskArrayList.add(task);

                }


            }
            listAdapter = new customListAdapter(this, R.layout.list_adapter_view, taskArrayList);
            listView = (ListView) findViewById(R.id.list_view);
            listView.setAdapter(listAdapter);
            listAdapter.notifyDataSetChanged();
        }
    }

    public ArrayList onSelecetedSpinner (String select ){
        ArrayList <Task> filter = new ArrayList<>();
        for (int i =0 ; i<taskArrayList.size(); i++){
            if(taskArrayList.get(i).getCategory().equalsIgnoreCase(select)){
                filter.add(taskArrayList.get(i));
            }
        }
        listAdapter = new customListAdapter(this,R.layout.list_adapter_view,filter);
        listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(listAdapter);
        listAdapter.notifyDataSetChanged();

    return filter;
    }


}