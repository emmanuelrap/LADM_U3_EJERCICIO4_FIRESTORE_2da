package com.example.ladm_u3_ejercicio4_firestore_2da

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.ladm_u3_ejercicio4_firestore_2da.databinding.ActivityMainBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //EVENTO (se dispara solo) (Es para poner datos en el ListView)
      FirebaseFirestore.getInstance()
          .collection("persona")
          .addSnapshotListener { query, error ->

            if(error!=null){
                //si hubo error
                AlertDialog.Builder(this)
                    .setMessage(error.message)
                    .show()
                return@addSnapshotListener //pasa salirme
            }

              val arreglo = ArrayList<String>()
              for(documento in query!!){
                  var cadena = " ${documento.getString("nombre")}" +
                         " - ${documento.getLong("edad") }"+ " Años"
                  arreglo.add(cadena)
              }

              binding.lista.adapter=ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,arreglo)
          }
        //-----------------------------------------------------------------------

        binding.btnInsertar.setOnClickListener {
            val baseRemota = FirebaseFirestore.getInstance()
            val datos= hashMapOf(
                "nombre" to binding.etNombre.text.toString(),
                "domicilio" to binding.etDomicilio.text.toString(),
                "edad" to binding.etEdad.text.toString().toInt()
            )

            baseRemota.collection("usuarios")
                .add(datos)
                .addOnSuccessListener { Toast.makeText(this,"Exito!, Si se Inserto correctamente",Toast.LENGTH_LONG).show() } //si se pudo
                .addOnFailureListener {
                    AlertDialog.Builder(this)
                        .setMessage(it.message)
                        .show()
                } //no se pudo
        }//Boton Insertar
    }
}