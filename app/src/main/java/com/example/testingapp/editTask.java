package com.example.testingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class editTask extends AppCompatActivity {


    String title, cat, date, ora, priority, selectedItemTitle, selectedItemCat;
    TextView tvTitle, tvCategory, tvData, tvOra, tvPriority, tvState, tcData, tcOra;
    EditText tcTitle;
    DataBaseHelper myDb;
    ArrayList<String> arrayList, listaCategorie;
    Button editBtn, saveBtn, deleteBtn, data_button, hour_button, doneBtn, onDoingBtn;
    Calendar c, currentTime;
    DatePickerDialog dpd;
    int hour, minute, anno = 0, mese = 0, giorno = 0;
    Spinner spinnerCat, spinnerPrior;
    String selecetedSpin, selectedSpin2;
    Cursor data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvCategory = (TextView) findViewById(R.id.tvCategory);
        tvData = findViewById(R.id.tvData);
        tvOra = findViewById(R.id.tvOra);
        tvPriority = findViewById(R.id.tvPriority);
        tvState = findViewById(R.id.textViewState);

        editBtn = (Button) findViewById(R.id.editBtn);
        saveBtn = (Button) findViewById(R.id.saveBtn);


        doneBtn = (Button) findViewById(R.id.doneBtn);

        onDoingBtn = (Button)findViewById(R.id.onDoingBtn);

        data_button = (Button)findViewById(R.id.button_data);
        currentTime = Calendar.getInstance();
        hour_button = (Button)findViewById(R.id.hour_button);


        hour = currentTime.get(Calendar.HOUR_OF_DAY);
        minute = currentTime.get(Calendar.MINUTE);

        tcTitle = (EditText) findViewById(R.id.tcTitle);
        spinnerCat = (Spinner)findViewById(R.id.spinner_cat2);
        tcData = (TextView)findViewById(R.id.tcData);
        tcOra = (TextView)findViewById(R.id.tcOra);
        spinnerPrior = (Spinner)findViewById(R.id.spinner_prior);


        myDb = new DataBaseHelper(this);
        arrayList = new ArrayList<>();

        final Data editNotification = new Data(anno, mese, giorno, hour, minute);

        selectedItemTitle = getIntent().getStringExtra("title");
        selectedItemCat = getIntent().getStringExtra("cat");
        data = myDb.getEditContent(selectedItemTitle, selectedItemCat);

        if (data.getCount() == 0) {

            Toast.makeText(com.example.testingapp.editTask.this, "Errore", Toast.LENGTH_LONG).show();
        }

        else {

            showTextView();
            if(tvState.getText().toString().equalsIgnoreCase("ONDOING")){
                onDoingBtn.setVisibility(GONE);
            }
        }

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editLayout();
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

                dpd = new DatePickerDialog(editTask.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int mYear, int mMonth, int mDay) {
                        tcData.setText(mDay+"/"+(mMonth+1)+"/"+mYear);
                        editNotification.setYear(mYear);
                        editNotification.setMonth(mMonth);
                        editNotification.setDay(mDay);


                    }
                },year,month,day);
                dpd.show();

            }



        });

        hour_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(editTask.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        //selectedTimeFormat(hourOfDay);
                        tcOra.setText(hourOfDay+":"+minute);
                        editNotification.setHour(hourOfDay);
                        editNotification.setMinute(minute);

                    }
                },hour, minute, true);

                timePickerDialog.show();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                title = tcTitle.getText().toString();
                cat = selecetedSpin;
                date = tcData.getText().toString();
                ora = tcOra.getText().toString();
                priority = selectedSpin2;


                if (title.length() != 0 && cat.length() != 0 && date.length() != 0 && ora.length() != 0) {

                    editData( title, cat, date, ora, priority, selectedItemTitle, selectedItemCat);

                    selectedItemTitle = title;
                    selectedItemCat = cat;

                    data.moveToFirst();
                    String code = data.getString(0);
                    int requestCode = Integer.parseInt(code);



                    Intent alarmIntent = new Intent (editTask.this, AlarmReceiver.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(editTask.this, requestCode, alarmIntent, PendingIntent.FLAG_ONE_SHOT);
                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    alarmManager.cancel(pendingIntent);

                    scheduleAlarm(editNotification.getYear(), editNotification.getMonth(), editNotification.getDay(), editNotification.getHour(), editNotification.getMinute(), requestCode, title, cat);

                    Intent intent = new Intent(com.example.testingapp.editTask.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }

                else {

                    Toast.makeText(com.example.testingapp.editTask.this, "Devi inserire tutti i parametri", Toast.LENGTH_LONG).show();
                }


            }
        });



        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = tvTitle.getText().toString();
                cat = tvCategory.getText().toString();

                boolean done = myDb.setDone(title, cat);
                if (done == true){
                    Toast.makeText(com.example.testingapp.editTask.this, "TASK  CLOSED", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(com.example.testingapp.editTask.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    Toast.makeText(com.example.testingapp.editTask.this, "TASK NOT CLOSED", Toast.LENGTH_LONG).show();
                }



            }
        });

        onDoingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = tvTitle.getText().toString();
                cat = tvCategory.getText().toString();

                boolean done = myDb.setOnDoing(title, cat);
                if (done == true){
                    Toast.makeText(com.example.testingapp.editTask.this, "TASK ON DOING", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(com.example.testingapp.editTask.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    Toast.makeText(com.example.testingapp.editTask.this, "error", Toast.LENGTH_LONG).show();
                }

            }
        });

    }




    public void editData(String title, String cat, String date, String ora, String priority, String selectedItemTitle, String selectedItemCat) {

        boolean result = myDb.editData(title, cat, date, ora, priority, selectedItemTitle, selectedItemCat);

        if (result == true) {
            Toast.makeText(com.example.testingapp.editTask.this, "Inserimento avvenuto con successo", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(com.example.testingapp.editTask.this, "Inserimento fallito", Toast.LENGTH_LONG).show();

        }
    }

    public void editLayout() {

        tvTitle.setVisibility(GONE);
        tvCategory.setVisibility(GONE);
        tvData.setVisibility(GONE);
        tvOra.setVisibility(GONE);
        tvPriority.setVisibility(GONE);
        tvState.setVisibility(GONE);
        tcTitle.setVisibility(VISIBLE);
        tcTitle.setText("" + arrayList.get(0));
        tcData.setVisibility(VISIBLE);
        tcOra.setVisibility(VISIBLE);

        spinnerCat.setVisibility(VISIBLE);
        spinnerPrior.setVisibility(VISIBLE);
        hour_button.setVisibility(VISIBLE);
        data_button.setVisibility(VISIBLE);
        saveBtn.setVisibility(VISIBLE);
        editBtn.setVisibility(GONE);
        deleteBtn.setVisibility(GONE);
        onDoingBtn.setVisibility(GONE);
        doneBtn.setVisibility(GONE);

        //gestione dello spinner per le categorie
        listaCategorie = myDb.getAllCat();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, listaCategorie);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerCat.setAdapter(adapter);

        ArrayList<String> list = new ArrayList<String>();
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, list);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerPrior.setAdapter(adapter2);


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

        spinnerPrior.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedSpin2 = spinnerPrior.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    public void showTextView() {

        data.moveToFirst();

        arrayList.add(data.getString(1));
        arrayList.add(data.getString(2));
        arrayList.add(data.getString(3));
        arrayList.add(data.getString(4));
        arrayList.add(data.getString(5));
        arrayList.add(data.getString(6));

        tvTitle.setText("" + arrayList.get(0));
        tvCategory.setText("" + arrayList.get(1));
        tvData.setText(""+ arrayList.get(2));
        tvOra.setText(""+ arrayList.get(3));
        tvPriority.setText(""+ arrayList.get(4));
        tvState.setText(""+ arrayList.get(5));


    }

    private void scheduleAlarm(int year, int month, int day, int hour, int minute, int requestCode, String task, String cat) {
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        alarmIntent.putExtra("data", task);
        alarmIntent.putExtra("category", cat);
        alarmIntent.putExtra("code",requestCode);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, requestCode, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Calendar cal = Calendar.getInstance();

        cal.setTimeInMillis(System.currentTimeMillis());
        cal.clear();
        cal.set(year,month,day,hour,minute);


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