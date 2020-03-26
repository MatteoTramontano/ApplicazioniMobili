package com.example.testingapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class editCategory extends AppCompatActivity {
    DataBaseHelper myDbC;
    String catS;
    ListView listViewCat;
    ArrayList <String> itemList;
    private ArrayAdapter  adapter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_category);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher_foreground);

        //Dovrebbe estrarre dal database le categorie e mostrarle nella list View
        listViewCat = (ListView)findViewById(R.id.list_cat);
        myDbC = new DataBaseHelper(this);
        final ListAdapter listAdapter = null;

        final ArrayList <String> arrayListCat = new ArrayList<>();
        final Cursor dataC = myDbC.getListCategory();

        showList(dataC,arrayListCat,listAdapter);

        adapter = new ArrayAdapter(editCategory.this, android.R.layout.simple_list_item_1,arrayListCat);

        listViewCat.setAdapter(adapter);
        listViewCat.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final int pos = position;
                new AlertDialog.Builder(editCategory.this)
                    .setIcon(android.R.drawable.ic_delete)
                        .setTitle("DELETE")
                        .setMessage("Are you sure to delete this category?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                String name = arrayListCat.get(pos);
                                arrayListCat.remove(pos);
                                adapter.notifyDataSetChanged();
                                myDbC.deleteCat(name);
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();


                return true;
            }
        });



        final EditText catText = (EditText)findViewById(R.id.editText_Cat);
        Button addCat = (Button)findViewById(R.id.button_addCat);
        addCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                catS = (String)catText.getText().toString();
                if (catS.length() != 0) {
                    AddCat(catS);
                    arrayListCat.add(catS);
                    adapter.notifyDataSetChanged();


                }
                else{
                    Toast.makeText(editCategory.this, "need category",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    //funzione che aggiunge la categorie nel databse delle categorie
    public void AddCat (String category){
        boolean insertData = myDbC.addCategory(category);


        if (insertData==true){
            Toast.makeText(editCategory.this, "category added",Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(editCategory.this, "empty category",Toast.LENGTH_SHORT).show();

        }

    }
    //funzione che mostra le categorie dinamicamente
    public void showList (Cursor dataC, ArrayList <String> arrayListCat, ListAdapter listAdapter) {
        if (dataC.getCount() == 0) {
            Toast.makeText(editCategory.this, "empty category db", Toast.LENGTH_LONG).show();
        } else {
            while (dataC.moveToNext()) {
                arrayListCat.add(dataC.getString(1));
                listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayListCat);
                listViewCat.setAdapter(listAdapter);

            }
        }
    }
}


