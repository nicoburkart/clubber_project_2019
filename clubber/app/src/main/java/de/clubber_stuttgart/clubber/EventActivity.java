package de.clubber_stuttgart.clubber;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EventActivity extends Activity {


    //ToDo: die Navigation funktioniert nicht:  Error inflating class android.support.design.widget.BottomNavigationView
    final private String LOG = "EventActivity";

    private RecyclerView eRecyclerView;
    private RecyclerView.Adapter eAdapter;
    private RecyclerView.LayoutManager eLayoutManager;

    private DataBaseHelper dataBaseHelper = new DataBaseHelper(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        //creates an Array List of event items
        ArrayList<Event> eventList = new ArrayList<>();
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();

        Log.d(this.getClass().toString(), db.getPath());

        Bundle bundle = getIntent().getExtras();
        Cursor cursor;

        //Intent does not have to contain any selected date (for example if the events tab is reached via the tab bar)
        if (bundle.containsKey("selectedDate")){
            //custom query
            Log.i(this.getClass().toString(), "Events will be filtered for date " + bundle.getString("selectedDate"));
            cursor = db.query(DataBaseHelper.TABLE_NAME_EVENTS, null, "dte = ?", new String[]{bundle.getString("selectedDate")}, null, null, "dte, srttime asc", null);
        }
        else {
            //default query
            cursor = db.query(DataBaseHelper.TABLE_NAME_EVENTS, null, null, null, null, null, "dte, srttime asc", null);
        }

        while (cursor.moveToNext()) {
            eventList.add(new Event(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6)));
        }


        boolean noNetwork = bundle.getBoolean("noNetwork", false);
        Log.i(LOG, "Check if there is network access... result: " + !noNetwork);

        if (noNetwork) {
            if (eventList.isEmpty()) {
                Log.w(LOG, "There are no entries in the database");
                //ToDo: Hier evtl. eher eine TextView einfügen.
                Toast.makeText(this, "Keine Events vorhanden, bitte stelle eine Internetverbindung her.", Toast.LENGTH_LONG).show();
            } else {
                Log.i(LOG, "There are entries in the database but they might not be up to date");
                //prints information about the lack of network access
                Toast.makeText(this, "Achtung, keine Internetverbindung. Events eventuell unvollständig", Toast.LENGTH_LONG).show();
            }
        }

        eRecyclerView = findViewById(R.id.recycler_view);
        //ToDo: enlische Kommentare
        //wenn man weiß, dass sich die Größe des RecyclerView nicht verändern wird
        eRecyclerView.setHasFixedSize(true);
        eLayoutManager = new LinearLayoutManager(this);
        eAdapter = new CardEventAdapter(eventList, getApplicationContext());
        eRecyclerView.setLayoutManager(eLayoutManager);
        eRecyclerView.setAdapter(eAdapter);

    }





}
