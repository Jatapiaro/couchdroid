package couchdroid.com.pruebacouchdroid;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.couchdroid.crud.CRUDOperations;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jacobotapia on 04/05/17.
 */

public class OnClickListenerCreateDoggo implements View.OnClickListener {

    CRUDOperations cf;

    public OnClickListenerCreateDoggo(CRUDOperations cf){
        this.cf = cf;
    }

    @Override
    public void onClick(final View view) {
        final Context context = view.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View formElementsView = inflater.inflate(R.layout.doggo_input_form, null, false);
        final EditText editDoggoNombre = (EditText) formElementsView.findViewById(R.id.editDoggoNombre);
        final EditText editDoggoRaza = (EditText) formElementsView.findViewById(R.id.editDoggoRaza);
        final EditText editDoggoEdad = (EditText) formElementsView.findViewById(R.id.editDoggoEdad);
        final EditText editDoggoJuguetes = (EditText) formElementsView.findViewById(R.id.editDoggoJuguetes);
        new AlertDialog.Builder(context)
                .setView(formElementsView)
                .setTitle("Create Doggo")
                .setPositiveButton("Crear",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String doggoNombre = editDoggoNombre.getText().toString();
                                String doggoRaza = editDoggoRaza.getText().toString();
                                int doggoEdad = Integer.parseInt(editDoggoEdad.getText().toString());
                                String[] doggoJuguete = editDoggoJuguetes.getText().toString().split(",");
                                List<String> dj = new ArrayList<String>();
                                for(String s:doggoJuguete){
                                    dj.add(s);
                                }
                                Doggo g = new Doggo(doggoNombre,doggoRaza,doggoEdad,dj);
                                Log.d("DoggoEvent",g.toString());
                                cf.create(g);
                                ((DoggoCrud) context).cleanRecords();
                                ((DoggoCrud) context).readRecords();
                            }
                        }).show();
    }


}
