package couchdroid.com.pruebacouchdroid;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.couchdroid.crud.CRUDOperations;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jacobotapia on 07/05/17.
 */

public class OnClickListenerDoggoRecord implements View.OnLongClickListener {

    Context context;
    String id;
    CRUDOperations cf;

    public OnClickListenerDoggoRecord(CRUDOperations cf){
        this.cf = cf;
    }


    @Override
    public boolean onLongClick(View view) {

        context = view.getContext();
        id = view.getTag().toString();
        final CharSequence[] items = { "Edit", "Delete" };

        new AlertDialog.Builder(context).setTitle("Student Record")
                .setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {

                        if (item == 0) {
                            editRecord(id);
                        }else if (item == 1) {


                            boolean deleteSuccessful = cf.delete(id);


                            if (deleteSuccessful){
                                    Toast.makeText(context, "Doggo record was deleted.", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(context, "Unable to delete doggo record.", Toast.LENGTH_SHORT).show();
                                }
                            ((DoggoCrud) context).cleanRecords();
                            ((DoggoCrud) context).readRecords();

                        }

                        dialog.dismiss();
                    }
                }).show();
        return false;
    }

    public void editRecord(final String doggoId) {
            Doggo d = cf.find(doggoId,Doggo.class);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View formElementsView = inflater.inflate(R.layout.doggo_input_form, null, false);
            final EditText editTextDoggoName = (EditText) formElementsView.findViewById(R.id.editDoggoNombre);
            editTextDoggoName.setText(d.getName());
            final EditText editTextDoggoRaze = (EditText) formElementsView.findViewById(R.id.editDoggoRaza);
            editTextDoggoRaze.setText(d.getRaze());
            final EditText editTextDoggoAge = (EditText) formElementsView.findViewById(R.id.editDoggoEdad);
            editTextDoggoAge.setText(String.valueOf(d.getAge()));
            final EditText editTextDoggoToys = (EditText) formElementsView.findViewById(R.id.editDoggoJuguetes);
            StringBuilder sb = new StringBuilder();
            for(String s : d.getJuguetes()){
                sb.append(s+",");
            }
            String rev = sb.reverse().toString().replaceFirst(",","");
            StringBuilder sb2 = new StringBuilder(rev);
            editTextDoggoToys.setText(sb2.reverse().toString());

            new AlertDialog.Builder(context)
                    .setView(formElementsView)
                    .setTitle("Edit Record")
                    .setPositiveButton("Save Changes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    String doggoNombre = editTextDoggoName.getText().toString();
                                    String doggoRaza = editTextDoggoRaze.getText().toString();
                                    int doggoEdad = Integer.parseInt(editTextDoggoAge.getText().toString());
                                    String[] doggoJuguete = editTextDoggoToys.getText().toString().split(",");
                                    List<String> dj = new ArrayList<String>();
                                    for (String s : doggoJuguete) {
                                        dj.add(s);
                                    }

                                    Doggo ud = new Doggo(doggoNombre, doggoRaza, doggoEdad, dj);
                                    ud.setId(doggoId);

                                    boolean updated = cf.update(ud);
                                    if (updated) {
                                        Toast.makeText(context, "Doggo actualizado.", Toast.LENGTH_SHORT).show();
                                        ((DoggoCrud) context).cleanRecords();
                                        ((DoggoCrud) context).readRecords();
                                    } else {
                                        Toast.makeText(context, "No se ha podido actualizar el doggo.", Toast.LENGTH_SHORT).show();
                                        ((DoggoCrud) context).cleanRecords();
                                        ((DoggoCrud) context).readRecords();
                                    }
                                    dialog.cancel();
                                }
                            }).show();
    }

}