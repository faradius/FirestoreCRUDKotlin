package com.example.firestorecrud

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.firestorecrud.databinding.ActivityMainBinding
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.util.Util
import java.lang.Exception
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    var fechaEscogida = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Obtener una conexión a la base de datos
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()

        binding.btnGuardarSinID.setOnClickListener {
            if (binding.etNombre.text.isNotBlank() &&
                    binding.etEmail.text.isNotBlank()){

                val id = db.collection("amigos").document().id
                val dato = hashMapOf(

                    "nombre" to binding.etNombre.text.toString(),
                    "correo" to binding.etEmail.text.toString(),
                    "id" to id
                )

                db.collection("amigos").document(id)
                    .set(dato)
                    .addOnSuccessListener {
                        binding.tvConsulta.text = "Procesado correctamente"
                    }
                    .addOnFailureListener {
                        binding.tvConsulta.text = "No se ha podido procesar"
                    }
            }
        }

        binding.btnGuardarConId.setOnClickListener {
            if (binding.etNombre.text.isNotBlank() &&
                binding.etEmail.text.isNotBlank()&&
                    binding.etId.text.isNotBlank()){

                val dato = hashMapOf(

                    "nombre" to binding.etNombre.text.toString(),
                    "correo" to binding.etEmail.text.toString(),
                    "id" to binding.etId.text.toString(),
                    "fecha" to Timestamp(fechaEscogida.timeInMillis/1000,0)
                )

                db.collection("amigos").document(binding.etId.text.toString())
                    .set(dato)
                    .addOnSuccessListener {
                        binding.tvConsulta.text = "Procesado correctamente"
                    }
                    .addOnFailureListener {
                        binding.tvConsulta.text = "No se ha podido procesar"
                    }
            }
        }

        binding.btnRegistrar.setOnClickListener {
            if (binding.etNombre.text.isNotBlank() &&
                binding.etEmail.text.isNotBlank()){
                val dato = hashMapOf(
                    "nombre" to binding.etNombre.text.toString(),
                    "correo" to binding.etEmail.text.toString()
                )
                db.collection("amigos").add(dato)
                    .addOnSuccessListener {
                        binding.tvConsulta.text = "Procesado correctamente"
                    }
                    .addOnFailureListener {
                        binding.tvConsulta.text = "No se ha podido procesar"
                    }
            }
        }


        binding.btnBorrar.setOnClickListener {

                if (binding.etId.text.isNotBlank()){

                    db.collection("amigos").document(binding.etId.text.toString())
                        .delete()
                        .addOnSuccessListener {
                            binding.tvConsulta.text = "Borrado correctamente"
                        }
                        .addOnFailureListener {
                            binding.tvConsulta.text = "No se ha podido borrar"
                        }
                }
        }


        binding.btnConsulta.setOnClickListener {
            var datos = ""
            db.collection("amigos")
                .get()
                .addOnSuccessListener { resultado ->
                    for(documento in resultado){
                        //datos += "${documento.id}:  ${documento.data}\n"
                        val nombre = documento["nombre"].toString()
                        val correo = documento["correo"].toString()
                        var fecha = "(fecha no indicada)"
                        try {
                            if (documento["fecha"]!= null){
                                fecha = (documento["fecha"] as Timestamp).toDate().toString()
                            }
                        }catch (e: Exception){

                        }
                        datos += "${documento.id}: $nombre, $correo : $fecha\n"

                    }
                    binding.tvConsulta.text = datos
                }
                .addOnFailureListener { exception ->
                    binding.tvConsulta.text = "No se ha podido conectar"
                }
        }


        val listenerFecha = DatePickerDialog.OnDateSetListener { datePicker, anyo, mes, dia ->
            fechaEscogida.clear() //para borrar hora, minutos y segundos
            fechaEscogida.set(Calendar.YEAR, anyo)
            fechaEscogida.set(Calendar.MONTH, mes)
            fechaEscogida.set(Calendar.DAY_OF_MONTH, dia)

            binding.etFecha.setText(Date(fechaEscogida.timeInMillis).toString())
        }

        var cal = Calendar.getInstance()
        binding.btnFecha.setOnClickListener {
            DatePickerDialog(this,
            listenerFecha,
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)).show()
        }
    }
}