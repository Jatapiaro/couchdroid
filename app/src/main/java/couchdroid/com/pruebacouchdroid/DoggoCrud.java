package couchdroid.com.pruebacouchdroid;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.couchdroid.crud.CRUDOperations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DoggoCrud extends AppCompatActivity {

    private final String TAG = "CouchbaseEvents";
    private CRUDOperations cf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            cf = CRUDOperations.getInstance(getApplicationContext());
        } catch (IOException e) {
            e.printStackTrace();
        }

        //cf.deleteDatabase();


        setContentView(R.layout.activity_crud);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.createDoggo);
        fab.setOnClickListener(new OnClickListenerCreateDoggo(cf));
        readRecords();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_crud, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void cleanRecords(){
        Log.d("Cleaning","Clieaning");
        LinearLayout linearLayoutRecords = (LinearLayout) findViewById(R.id.linearLayoutRecords);
        linearLayoutRecords.removeAllViews();
    }

    public void readRecords() {

        LinearLayout linearLayoutRecords = (LinearLayout) findViewById(R.id.linearLayoutRecords);
        //linearLayoutRecords.removeAllViews();
        List<Doggo> doggos = this.cf.list(Doggo.class);
        ///Log.d("Llego",doggos.size()+"");
        Doggo mock = new Doggo("","",0,new ArrayList<String>());
        mock.setId("---");
        doggos.add(0,mock);

        if (doggos.size() > 0) {
            int count = 0;
            for (Doggo obj:doggos) {
                //Log.d("Doggo",obj.toString());
                //int id = count++;
                String name = obj.getName();
                String raze = obj.getRaze();
                String edad = obj.getAge()+"";
                String jugetes = obj.getJuguetes().toString();
                String id_id = obj.getId();

                String textViewContents = name+","+raze+","+edad+","+jugetes;

                TextView textViewStudentItem = new TextView(this);
                textViewStudentItem.setPadding(0, 10, 0, 10);
                textViewStudentItem.setText(textViewContents);
                textViewStudentItem.setTag(obj.getId());
                linearLayoutRecords.addView(textViewStudentItem);
                textViewStudentItem.setOnLongClickListener(new OnClickListenerDoggoRecord(cf));
            }

        }
        else {
            TextView locationItem = new TextView(this);
            locationItem.setPadding(8, 8, 8, 8);
            locationItem.setText("No records yet.");
            linearLayoutRecords.addView(locationItem);
        }

    }
}