package com.example.testingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class add_task extends AppCompatActivity  {
    DataBaseHelper myDbC;
    Button data_button;
    Button hour_button;
    TextView dateT;
    TextView hourT;
    private String dateS = "";
    private String hourS = "";
    private String taskS = "";
    private String categoryS = "";

    int hour = 0, minute = 0, anno = 0, mese = 0, giorno = 0;
    Calendar currentTime;

    DataBaseHelper myDb;

    private String radioS = "";
    Calendar c;
    DatePickerDialog dpd;
    Spinner spinnerCat;
    String selecetedSpin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        Toolbar toolbar = findViewById(R.id.toolbar);


        myDb = new DataBaseHelper(this);

        final Data data = new Data(anno, mese, giorno, hour, minute);


        dateT = (TextView)findViewById(R.id.insert_date);
        data_button = (Button)findViewById(R.id.button_data);

        currentTime = Calendar.getInstance();
        hourT = (TextView)findViewById(R.id.textView_hour);
        hour_button = (Button)findViewById(R.id.hour_button);
        hour = currentTime.get(Calendar.HOUR_OF_DAY);
        minute = currentTime.get(Calendar.MINUTE);
        hourT.setText(hour+":"+minute);

        ArrayList <String> listaCategorie = new ArrayList<String>();

        //funzione per prendere l'orario dal picker
        hour_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(add_task.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        //selectedTimeFormat(hourOfDay);
                        hourT.setText(hourOfDay+":"+minute);
                        data.setHour(hourOfDay);
                        data.setMinute(minute);

                    }
                },hour, minute, true);
                timePickerDialog.show();
            }
        });

        //funzione per prendere la data dal picker
        data_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                c = Calendar.getInstance(TimeZone.getTimeZone("Europe/Rome"), Locale.ITALY);

                final int day = c.get(Calendar.DAY_OF_MONTH);
                final int month = c.get(Calendar.MONTH);
                final int year = c.get(Calendar.YEAR);

                dpd = new DatePickerDialog(add_task.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int mYear, int mMonth, int mDay) {
                        dateT.setText(mDay+"/"+(mMonth+1)+"/"+mYear);
                        data.setYear(mYear);
                        data.setMonth(mMonth);
                        data.setDay(mDay);
                    }
                },year,month,day);
                dpd.show();

            }



        });

        //gestione dello spinner per le categorie
        listaCategorie = myDb.getAllCat();
        spinnerCat = (Spinner)findViewById(R.id.spinner_cat);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, listaCategorie);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerCat.setAdapter(adapter);


        spinnerCat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selecetedSpin = spinnerCat.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //codice
            }
        });

        final Switch sw = (Switch) findViewById(R.id.switch1);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sw.setChecked(true);
                    //Toast.makeText(add_task.this, swicherino, Toast.LENGTH_SHORT).show();
                } else {
                    sw.setChecked(false);
                    //Toast.makeText(add_task.this, swicherino, Toast.LENGTH_SHORT).show();
                }
            }
        });


        FloatingActionButton addFab = (FloatingActionButton)findViewById(R.id.floatingActionButton3);
        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Prendo il Task come stringa
                EditText taskE = (EditText) findViewById(R.id.insert_task);
                taskS = taskE.getText().toString();
                //Prendo la categoria come stringa da spinner
                categoryS = selecetedSpin;
                //Prendo la data come stringa
                dateS = (String)dateT.getText().toString();
                //Prendo l'orario come stringa
                hourS = (String)hourT.getText().toString();
                //funzione del per prendere il valore del radio button
                RadioGroup radioG = (RadioGroup)findViewById(R.id.radioG);
                radioG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {

                        RadioButton radioB = (RadioButton)findViewById(checkedId);
                        switch (radioB.getId()){
                            case R.id.radio_one:{
                                radioS = "1";
                            }
                            break;
                            case R.id.radio_two:{
                                radioS = "2";
                            }
                            break;
                            case R.id.radio_three:{
                                radioS = "3";
                            }
                            break;
                            case R.id.radio_four:{
                                radioS = "4";
                            }
                            break;
                        }
                    }
                });


                if(taskS.length() == 0 || categoryS.length() == 0 || dateS.length() == 0 || hourS.length() == 0 ||radioS.length() == 0) {
                    Toast.makeText(add_task.this, "no empty value", Toast.LENGTH_SHORT).show();
                }
                else {

                    AddData(taskS, categoryS, dateS, hourS, radioS);
                    Cursor id = myDb.getIdTask(taskS,categoryS,dateS,hourS);
                    id.moveToFirst();
                    int requestCode = id.getInt(0);

                    if(sw.isChecked()) {

                        scheduleAlarm(data.getYear(), data.getMonth(), data.getDay(), data.getHour(), data.getMinute(), requestCode, taskS, categoryS);
                    }

                    Intent intent = new Intent(add_task.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }

            }


        });

    }
    //funzione che aggiunge i valori al database
    public void AddData (String task, String  category, String data, String ora, String priority){
        boolean insertData = myDb.addData(task, category, data, ora, priority);

        if (insertData==true){
            Toast.makeText(add_task.this, "Task added",Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(add_task.this, "empty Task",Toast.LENGTH_SHORT).show();
        }

    }

    private void scheduleAlarm(int year, int month, int day, int hour, int minute, int requestCode, String task, String category) {
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        alarmIntent.putExtra("data", task);
        alarmIntent.putExtra("category", category);
        alarmIntent.putExtra("code",requestCode);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, requestCode, alarmIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Calendar cal = Calendar.getInstance();

        cal.setTimeInMillis(System.currentTimeMillis());
        cal.clear();
        cal.set(year,month,day,hour,minute);
        Toast.makeText(add_task.this, "Notifica settata per il giorno: " + cal.getTime().toString(), Toast.LENGTH_SHORT).show();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            alarmManager.setExactAndAllowWhileIdle
                    (AlarmManager.RTC_WAKEUP,
                            cal.getTimeInMillis(), pendingIntent);
        else
            alarmManager.setExact
                    (AlarmManager.RTC_WAKEUP,
                            cal.getTimeInMillis(), pendingIntent);
    }


}
