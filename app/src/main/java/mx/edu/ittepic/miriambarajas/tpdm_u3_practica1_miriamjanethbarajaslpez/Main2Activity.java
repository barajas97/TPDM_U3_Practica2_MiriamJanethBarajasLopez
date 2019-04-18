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
import android.widget.ListView;
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

import java.util.List;
import java.util.Map;

public class Main2Activity extends AppCompatActivity {
    EditText fabri, prod, cat;
    RadioButton conS, sinS;
    Button ins, con, eli, act;
    FirebaseFirestore servicioBD;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        
        fabri = findViewById(R.id.fabricante);
        prod = findViewById(R.id.lote);
        cat = findViewById(R.id.categoria);
        conS = findViewById(R.id.sistock);
        sinS = findViewById(R.id.nostock);
        ins = findViewById(R.id.insertar);
        con = findViewById(R.id.consultar);
        eli = findViewById(R.id.eliminar);
        servicioBD = FirebaseFirestore.getInstance();
        
        ins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fabri.getText().toString().isEmpty()){
                    Toast.makeText(Main2Activity.this, "Favor de llenar todos los campos", Toast.LENGTH_LONG).show();
                    return;
                }if(prod.getText().toString().isEmpty()){
                    Toast.makeText(Main2Activity.this, "Favor de llenar todos los campos", Toast.LENGTH_LONG).show();
                    return;
                }if(cat.getText().toString().isEmpty()){
                    Toast.makeText(Main2Activity.this, "Favor de llenar todos los campos", Toast.LENGTH_LONG).show();
                    return;
                }
                insertarMaquillajeLoteID();
            }
        });

        eli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eliminarMaquillaje();
            }
        });
        con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                consultarDato();
            }
        });
    }

    private void consultarDato(){
        AlertDialog.Builder alerta = new AlertDialog.Builder(Main2Activity.this);
        final EditText lote = new EditText(this);
        lote.setHint("Número de lote");
        lote.setInputType(InputType.TYPE_CLASS_NUMBER);
        alerta.setTitle("Busqueda")
                .setMessage("Ingresa el número de lote a buscar")
                .setView(lote)
                .setPositiveButton("Buscar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(lote.getText().toString().isEmpty()){
                            Toast.makeText(Main2Activity.this, "Favor de ingresar un numero de lote", Toast.LENGTH_LONG).show();
                            return;
                        }
                        consultar(lote.getText().toString());
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    public void consultar(String i){
        servicioBD.collection("maquillaje")
                .whereEqualTo("noLote", i)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,@Nullable FirebaseFirestoreException e) {
                        Query q = queryDocumentSnapshots.getQuery();
                        q.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    for (QueryDocumentSnapshot registro : task.getResult()){
                                        Map<String, Object> dato = registro.getData();
                                        fabri.setText(dato.get("fabricante").toString());
                                        cat.setText(dato.get("categoria").toString());
                                        prod.setText(dato.get("noLote").toString());

                                        Toast.makeText(Main2Activity.this,"Exito",Toast.LENGTH_LONG).show();
                                    }//for
                                }//If
                                else {
                                    Toast.makeText(Main2Activity.this,"Dato no encontrado", Toast.LENGTH_LONG).show();
                                }
                            }//onComplete
                        });

                    }
                });
    }

    private void eliminarMaquillaje() {
        AlertDialog.Builder alerta = new AlertDialog.Builder(Main2Activity.this);
        final EditText id = new EditText(this);
        id.setHint("Ingrese número de Lote a elimimar");
        id.setInputType(InputType.TYPE_CLASS_NUMBER);
        alerta.setTitle("Eliminar Lote")
                .setMessage("Ingresa el número de lote a eliminar")
                .setView(id)
                .setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(id.getText().toString().isEmpty()){
                            Toast.makeText(Main2Activity.this, "Favor de ingresar un numero de lote", Toast.LENGTH_LONG).show();
                            return;
                        }
                        eliminar(id.getText().toString());
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void eliminar(String i) {
        servicioBD.collection("maquillaje")
                .document(i)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Main2Activity.this, "Lote eliminado", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Main2Activity.this, "Error, no hay coincidencias", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void insertarMaquillajeLoteID() {
        if(conS.isChecked()){
            Maquillajes nuevo = new Maquillajes(Integer.parseInt(prod.getText().toString()),
                    fabri.getText().toString(), true, cat.getText().toString());
            servicioBD.collection("maquillaje")
                    .document(prod.getText().toString())
                    .set(nuevo)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(Main2Activity.this, "Dato ingresado con éxito", Toast.LENGTH_LONG).show();
                            prod.setText("");
                            cat.setText("");
                            fabri.setText("");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Main2Activity.this, "Error al registrar", Toast.LENGTH_LONG).show();
                        }
                    });
            return;
        }if(sinS.isChecked()){
            Maquillajes nuevo = new Maquillajes(Integer.parseInt(prod.getText().toString()),
                    fabri.getText().toString(), false, cat.getText().toString());
            servicioBD.collection("maquillaje")
                    .document(prod.getText().toString())
                    .set(nuevo)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(Main2Activity.this, "Dato ingresado con éxito", Toast.LENGTH_LONG).show();
                            prod.setText("");
                            cat.setText("");
                            fabri.setText("");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Main2Activity.this, "Error al registrar", Toast.LENGTH_LONG).show();
                        }
                    });
            return;
        }else{
            AlertDialog.Builder alerta = new AlertDialog.Builder(this);
            alerta.setTitle("Atención")
                    .setMessage("Por favor seleccione la disponibilidad")
                    .setPositiveButton("Aceptar", null)
                    .show();
        }
    }
}
