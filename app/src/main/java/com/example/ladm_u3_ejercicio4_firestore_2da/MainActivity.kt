package com.example.ladm_u3_ejercicio4_firestore_2da

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.get
import com.example.ladm_u3_ejercicio4_firestore_2da.databinding.ActivityMainBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    val arregloDatos = ArrayList<String>()
    var arregloIDs = ArrayList<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

 //EVENTO (se dispara solo cuando ocurre un cambio en la BD, los pone en el ListView)-----------------


      FirebaseFirestore.getInstance()
          .collection("usuarios")
          .addSnapshotListener { query, error ->
              arregloDatos.clear() // si no se pone te estara duplicando datos

            if(error!=null){
                //si hubo error
                AlertDialog.Builder(this)
                    .setMessage(error.message)
                    .show()
                return@addSnapshotListener //pasa salirme
            }


              for(documento in query!!){//ciclo que recoje los datos de la colleccion
                  var cadena = "Nombre: ${documento.getString("nombre")}\n" +
                         " Edad: ${documento.getLong("edad") }"+ " Años\n"+
                          " Domicilio: ${documento.getString("domicilio") }"
                  arregloDatos.add(cadena)
                  arregloIDs.add(documento.id) //obtiene el ID de los documentos
              }

              binding.lista.adapter=ArrayAdapter(this,android.R.layout.simple_list_item_1,arregloDatos)

              binding.lista.setOnItemClickListener { adapterView, view, posicion, l ->
                  val idLista = arregloIDs.get(posicion)
                  AlertDialog.Builder(this)
                      .setMessage("¿Desea Elimnar o Modificar ?")
                      .setNegativeButton("Eliminar") {d,i ->
                          eliminar(idLista)
                      }
                      .setPositiveButton("Actualizar") {d,i ->

                      }
                      .setNeutralButton("Cerrar") {d,i -> }
                      .show()

              }

          } //---------------------------------------------------------------------------------

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
            binding.etNombre.setText("")
            binding.etEdad.setText("")
            binding.etDomicilio.setText("")

        }//boton para intertar en BDremota
    }

    private fun eliminar(idEliminar:String) {
        val baseRemota = FirebaseFirestore.getInstance()
        alerta("mensaje","El id que se elimino fue: ${idEliminar}")
        baseRemota.collection("usuarios")
            .document(idEliminar)
            .delete()
            .addOnSuccessListener {
                toast("Se Elimino Correctamente")
            }
            .addOnFailureListener{
                alerta("Error","Hubo ERROR: ${it.message!!}")
            }
    }



    private fun alerta(titulo:String,mensaje: String)
    {
        AlertDialog.Builder(this)
            .setTitle(titulo)
            .setMessage(mensaje)
            .show()


    }

    private fun toast(mensaje:String){
        Toast.makeText(this,mensaje,Toast.LENGTH_LONG).show()
    }


}