package com.example.testingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CalendarActivity extends AppCompatActivity {

    CalendarView calView;
    Cursor dateCur;
    ArrayList <Task> listDate;
    customListAdapter listCalADapter;
    Task task;
    ListView listCal;
    DataBaseHelper db;
    ArrayList <Task> taskList;

    ArrayList <Task> finale;

    TextView dateSel;

    String date ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        //TextView che mostra la data selezionata
        dateSel = (TextView)findViewById(R.id.date_selected);

        listCal = (ListView)findViewById(R.id.list_calendar_view);
        calView = (CalendarView)findViewById(R.id.calendarView);

        db= new DataBaseHelper(this);
        dateCur = db.getListContent();

        taskList =new ArrayList<>();

        takeTaskList(dateCur, taskList, listCalADapter);

        calView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

                    date = dayOfMonth + "/" + (month + 1) + "/" + year;
                    dateSel.setText(date);
                    if(dateSel.getText().toString().equalsIgnoreCase("date select")){
                        Toast.makeText(CalendarActivity.this, "Select a date", Toast.LENGTH_SHORT).show();

                    }
                    else {
                        ArrayList<Task> taskDateList = new ArrayList<>();
                        taskDateList = onDateSelect(date, taskList);
                        takeTaskList(dateCur, taskDateList, listCalADapter);
                    }
            }
        });

        listCal.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                Task task = (Task) listCal.getItemAtPosition(position);
                String title = task.getName();
                String cat = task.getCategory();
                Intent intent = new Intent(CalendarActivity.this, editTask.class);
                intent.putExtra("title", title);
                intent.putExtra("cat", cat);
                startActivity(intent);
                finish();
            }
        });
        listCal.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                Task task = (Task) listCal.getItemAtPosition(position);
                final String title = task.getName();
                final String cat = task.getCategory();
                final int pos = position;
                new AlertDialog.Builder(CalendarActivity.this)
                        .setIcon(android.R.drawable.ic_delete)
                        .setTitle("DELETE")
                        .setMessage("Are you sure to delete this item?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Cursor dataMio = db.getEditContent(title,cat);
                                dataMio.moveToFirst();
                                String code = dataMio.getString(0);
                                int requestCode = Integer.parseInt(code);


                                Intent alarmIntent = new Intent (CalendarActivity.this, AlarmReceiver.class);
                                PendingIntent pendingIntent = PendingIntent.getBroadcast(CalendarActivity.this, requestCode, alarmIntent, PendingIntent.FLAG_ONE_SHOT);
                                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                                alarmManager.cancel(pendingIntent);

                                Task deleted = taskList.get(pos);
                                taskList.remove(pos);
                                db.deleteTask(title,cat);
                                takeTaskList(dateCur, taskList, listCalADapter);
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();

                return  true;
            }

        });



    }


    //Metodo che rimepe la task list e la mostra
    public void takeTaskList (Cursor dataC, ArrayList<Task> taskList, customListAdapter listCalAdapter) {


        if (dataC.getCount() == 0) {
            Toast.makeText(CalendarActivity.this, "empty category db", Toast.LENGTH_LONG).show();
        } else {
            while (dataC.moveToNext()) {
                if (!dataC.getString(6).equals("CLOSE")) {
                    task = new Task(dataC.getString(1), dataC.getString(2), dataC.getString(3), dataC.getString(4),dataC.getString(6));
                    taskList.add(task);

                }

            }
            listCalAdapter = new customListAdapter (this, R.layout.list_adapter_view,taskList);
            listCal = (ListView)findViewById(R.id.list_calendar_view);
            listCal.setAdapter(listCalAdapter);
            listCalAdapter.notifyDataSetChanged();

        }


    }

    //filtro la tasklist per giorno in modo da mostrare solo quella seleizionata
    public  ArrayList onDateSelect (String dataSel, ArrayList <Task> taskDateList){
        ArrayList <Task> filterDate = new ArrayList<>();
        for(int i=0;i<taskDateList.size();i++){
            if(taskDateList.get(i).getDate().equalsIgnoreCase(dataSel)){
                filterDate.add(taskDateList.get(i));
            }
        }
        if(filterDate.size()==0){
            Toast.makeText(CalendarActivity.this, "No task in this date", Toast.LENGTH_SHORT).show();

        }
        listCalADapter = new customListAdapter(this,R.layout.list_adapter_view, filterDate);
        listCal = (ListView)findViewById(R.id.list_calendar_view);
        listCalADapter.notifyDataSetChanged();

        return filterDate;
    }

   public void circleDay (ArrayList <Task> taskList) {
        ArrayList <Date> listaD = new ArrayList<>();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/mm/yyyy");

       for (int i = 0; i < taskList.size(); i++) {
           try {
               Date dat = formatter.parse(taskList.get(i).getDate());
               listaD.add(dat);
           } catch (ParseException e) {
               e.printStackTrace();
           }
       }
    }




}
