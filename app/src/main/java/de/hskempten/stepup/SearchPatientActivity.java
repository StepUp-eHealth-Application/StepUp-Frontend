package de.hskempten.stepup;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;
import java.util.ArrayList;

public class SearchPatientActivity extends AppCompatActivity {
    SearchView searchView;
    ListView listView;
    ArrayList list;
    ArrayAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_patient);
        searchView = findViewById(R.id.searchView);
        listView = findViewById(R.id.listView);
        list = new ArrayList<>();
        list.add("Max Mustermann");
        list.add("Ursula Mueller");
        list.add("Ralph Schneider");
        list.add("Vanessa Berg");
        list.add("Claudia Abendroth");
        list.add("Dennis Fruehauf");
        list.add("Stefan Braun");
        list.add("Anna Faust");
        list.add("Stefan Finkel");
        list.add("Bernd Lang");
        list.add("Sophie Mehler");
        list.add("Tanja Schweitzer");
        list.add("Anne Bader");
        list.add("Dennis Brauer");
        list.add("Laura Schuster");
        list.add("Kevin Gaertner");
        list.add("Frank Barth");
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,list);
        listView.setAdapter(adapter);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(list.contains(query)){
                    adapter.getFilter().filter(query);
                }else{
                    Toast.makeText(SearchPatientActivity.this, "No Match found",Toast.LENGTH_LONG).show();
                }
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
    }
}