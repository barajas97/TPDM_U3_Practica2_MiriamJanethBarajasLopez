package mx.edu.ittepic.miriambarajas.tpdm_u3_practica1_miriamjanethbarajaslpez;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

public class Main3Activity extends AppCompatActivity{
    EditText nombreL, razaL, edadL, sexoL;
    RadioButton desSi, desNo;
    Button ins, eli, con;
    FirebaseFirestore servicioBD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        nombreL = findViewById(R.id.nombre);
        razaL = findViewById(R.id.raza);
        edadL = findViewById(R.id.edad);
        sexoL = findViewById(R.id.sexo);
        desSi = findViewById(R.id.sides);
        desNo = findViewById(R.id.nodes);
        ins = findViewById(R.id.insertar);
        eli = findViewById(R.id.eliminar);
        con = findViewById(R.id.consultar);
        servicioBD = FirebaseFirestore.getInstance();

        ins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertarPerro();
            }
        });

        eli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eliminarPerro();
            }
        });

        con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                consultarPerro();
            }
        });
    }

    public void insertarPerro(){
        if(desSi.isChecked()){
            Perros nuevo = new Perros(nombreL.getText().toString(), razaL.getText().toString(), sexoL.getText().toString(),Integer.parseInt(edadL.getText().toString()), true);
            servicioBD.collection("perros")
                    .document(nombreL.getText().toString())
                    .set(nuevo)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(Main3Activity.this, "Dato ingresado con éxito", Toast.LENGTH_LONG).show();
                            nombreL.setText("");
                            razaL.setText("");
                            edadL.setText("");
                            sexoL.setText("");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Main3Activity.this, "Error al registrar", Toast.LENGTH_LONG).show();
                        }
                    });
            return;
        }if(desNo.isChecked()){
            Perros nuevo = new Perros(nombreL.getText().toString(), razaL.getText().toString(), sexoL.getText().toString(),Integer.parseInt(edadL.getText().toString()), false);
            servicioBD.collection("perros")
                    .document(nombreL.getText().toString())
                    .set(nuevo)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(Main3Activity.this, "Dato ingresado con éxito", Toast.LENGTH_LONG).show();
                            nombreL.setText("");
                            razaL.setText("");
                            edadL.setText("");
                            sexoL.setText("");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Main3Activity.this, "Error al registrar", Toast.LENGTH_LONG).show();
                        }
                    });
            return;
        }else{
            AlertDialog.Builder alerta = new AlertDialog.Builder(this);
            alerta.setTitle("Atención")
                    .setMessage("Indique si el perro esta desparacitado")
                    .setPositiveButton("Aceptar", null)
                    .show();
        }
    }

    public void eliminarPerro(){
        AlertDialog.Builder alerta = new AlertDialog.Builder(Main3Activity.this);
        final EditText nombre = new EditText(this);
        nombre.setHint("Nombre");
        alerta.setTitle("Eliminar perro")
                .setMessage("Ingresa el nombre del perro a eliminar")
                .setView(nombre)
                .setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(nombre.getText().toString().isEmpty()){
                            Toast.makeText(Main3Activity.this, "Favor de ingresar un numero de lote", Toast.LENGTH_LONG).show();
                            return;
                        }
                        eliminar(nombre.getText().toString());
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void eliminar(String i) {
        servicioBD.collection("perros")
                .document(i)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Main3Activity.this, "Perro eliminado", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Main3Activity.this, "Error, no hay coincidencias", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void consultarPerro(){
        AlertDialog.Builder alerta = new AlertDialog.Builder(Main3Activity.this);
        final EditText nombre = new EditText(this);
        nombre.setHint("Nombre");
        alerta.setTitle("Busqueda")
                .setMessage("Ingresa el nombre del perro a buscar")
                .setView(nombre)
                .setPositiveButton("Buscar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(nombre.getText().toString().isEmpty()){
                            Toast.makeText(Main3Activity.this, "Favor de ingresar un nombre", Toast.LENGTH_LONG).show();
                            return;
                        }
                        consultar(nombre.getText().toString());
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    public void consultar(String i){
        servicioBD.collection("perros")
                .whereEqualTo("nom", i)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        Query q = queryDocumentSnapshots.getQuery();
                        q.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    for (QueryDocumentSnapshot registro : task.getResult()){
                                        Map<String, Object> dato = registro.getData();
                                        nombreL.setText(dato.get("nom").toString());
                                        razaL.setText(dato.get("raz").toString());
                                        edadL.setText(dato.get("eda").toString());
                                        sexoL.setText(dato.get("sex").toString());

                                        Toast.makeText(Main3Activity.this,"Exito",Toast.LENGTH_LONG).show();
                                    }//for
                                }//If
                                else {
                                    Toast.makeText(Main3Activity.this,"Dato no encontrado", Toast.LENGTH_LONG).show();
                                }
                            }//onComplete
                        });

                    }
                });
    }
}
