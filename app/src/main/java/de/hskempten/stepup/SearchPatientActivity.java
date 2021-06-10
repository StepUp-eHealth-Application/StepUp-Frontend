package de.hskempten.stepup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class SearchPatientActivity extends AppCompatActivity {

    SearchView searchView;
    ListView listView;
    ArrayList<String> listName;
    ArrayList<String> listId;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_patient);
        searchView = findViewById(R.id.searchView);
        listView = findViewById(R.id.listView);
        listName = new ArrayList<>();
        listId = new ArrayList<>();

        // Placeholder fill list
        listName.add("Max Mustermann");
        listName.add("Ursula Mueller");
        listName.add("Ralph Schneider");
        listName.add("Vanessa Berg");
        listId.add("0000000001");
        listId.add("0000000002");
        listId.add("0000000003");

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listName);
        listView.setAdapter(adapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(listName.contains(query)){
                    adapter.getFilter().filter(query);
                }else{
                    Toast.makeText(SearchPatientActivity.this, "Keine Übereinstimmung gefunden.",Toast.LENGTH_LONG).show();
                }
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Intent intent = new Intent(SearchPatientActivity.this, MenuLeActivity.class);
                Bundle b = new Bundle();
                b.putString("patientID", listId.get(position));
                b.putString("patientName", listName.get(position));
                intent.putExtras(b);
                startActivity(intent);
                finish();
            }
        });
    }
}