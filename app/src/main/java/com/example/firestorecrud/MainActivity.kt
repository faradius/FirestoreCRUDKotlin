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

    //Habilitamos el viewBinding en el proyecto
    lateinit var binding: ActivityMainBinding

    //En esta variable se le asigna la instancia del calendario para poder gestionar las fechas
    var fechaEscogida = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Obtenemos la conexión a la base de datos con FirebaseFirestore.getInstance()
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()

        /*Dentro del onCreate del activity implementaremos 5 botones por medio del binding, cada boton tendrá
        * su propio evento OncClickListener los botoenes haran lo siguiente:
        *
        * btnGuardarSinID: crea un registro a la base de datos con su respectiva información, el id que tendrá el documento
        *                  no será definido por nosotros si no que se creará el docuemnto con un ID autogenerado por
        *                  por firebase y ese ID va hacer registrado dentro del documento
        *
        * btnGuardarConId: crea un registro a la base de datos con su respectiva información, el id que tendrá el documento
        *                  se lo definiremos nosotros por medio del editText, ademas registrará la fecha escogida por el usuario
        *
        * btnRegistrar: este de igual forma crea un registro a la base de datos como los botones anteriores, pero la diferencia
        *               esta en que este solo permite añadir registros a la base con el metodo add() y los botones anteriores
        *               utiliza un metodo diferente que es con set(), la diferencia mas grande entre estas dos funciones es que
        *               el metodo set() permite registrar un documento a la base pero si volvemos a intentar registrar nuevamente
        *               un documento con el mismo id pero con valores en los campos con diferente información lo que hará es una
        *               actulización de información, por lo tanto, la función set() realiza de acción de registrar un documento y actualizar
        *               la información de ese documento caso contrario de lo que no hace la función add() que solo registra un documento
        *
        * btnBorrar: Este boton lo que hace es borrar un documento en la base de datos por medio del id del documento
        *
        * btnConsultar: Este ultimo boton lo que hace es consultar la información de todos los registros que estan dentro de la colección amigos
        *               trayendo como resultado la información de cada documento que se encuentra en la base de datos de firestore*/

        binding.btnGuardarSinID.setOnClickListener {
            //validamos que que los editText no estan vacios ni que dejen un espacio en blanco
            if (binding.etNombre.text.isNotBlank() &&
                    binding.etEmail.text.isNotBlank()){

                /*En una variable obtenemos el id que generará firebase al realizar el registro del documento,
                //ese id lo queremos guardar dentro del mismo documento creado para luego poder realizar las consultas necesarias hacia ese documento*/
                val id = db.collection("amigos").document().id

                //Creamos una variable de tipo hashMapOf para poder guardar los datos obtenidos en el editText y adjuntarlo con su respectivo campo
                val dato = hashMapOf(
                    /*nombre, correo y id son los nombres que se le pusieron a los campos dentro de la base de firestore y despues del to son los valores que se le pondrán
                    //a cada campo pertenecientes del documento, esto quedaria como llave: valor en la base de datos, los campos se deben de ponder preferentemente como constantes.
                    //pero para este ejemplo se dejaron asi para mayor comprension del codigo*/
                    "nombre" to binding.etNombre.text.toString(),
                    "correo" to binding.etEmail.text.toString(),
                    "id" to id
                )

                /*Una vez creado nuestro modelo de datos por medio del hashMap procederemos a realizar un registro de un documento
                //agregamos la instancia de la base de datos, despues la colección y hacemos referencia a la coleccion amigos, nota: si la colección amigos
                //no se encuentra lo creará automaticamente firebase, continuamos, despues de agregar el nombre de la colección procederemos a crear el documento
                //con su respectivo nombre en este caso le pasaremos el id que nos autogenero firebase y con el metodo set() le decimos que nos registre o actualice
                //el documento con la siguiente información que tiene guardada la variable dato*/
                db.collection("amigos").document(id)
                    .set(dato)
                        /*agregamos dos metodos que nos ayudará como referencia en caso de que la operación solicitada sea satisfactoria y el otro representa
                        //el caso de que sea un error, para ambos casos solo mostraremos un mensaje de que fue correcta la operación o que no se ha podido procesar*/
                    .addOnSuccessListener {
                        binding.tvConsulta.text = "Procesado correctamente"
                    }
                    .addOnFailureListener {
                        binding.tvConsulta.text = "No se ha podido procesar"
                    }
            }
        }

        /*En este es casi lo mismo que el boton anterior nadamas que aqui le asignamos el nombre que tendrá el id mediante
        //la obtención del valor del editTextId*/
        binding.btnGuardarConId.setOnClickListener {
            //hacemos lo mismo, validamos los editText que no esten vacios incluyendo el editText del id
            if (binding.etNombre.text.isNotBlank() &&
                binding.etEmail.text.isNotBlank()&&
                    binding.etId.text.isNotBlank()){

                //Creamos el modelo de datos con el hashMapOf, pero este tendrá dos campos nuevos el id y la fecha
                val dato = hashMapOf(

                    "nombre" to binding.etNombre.text.toString(),
                    "correo" to binding.etEmail.text.toString(),
                    "id" to binding.etId.text.toString(),
                    //pendiente
                    "fecha" to Timestamp(fechaEscogida.timeInMillis/1000,0)
                )

                /*Despues de crear el modelo de datos, procedemos hacer lo mismo que el boton anterior, solamente que en el nombre
                //de la colección tendrá el mismo valor que fue capturado por le editTextId */
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

        //Este boton hace casi lo mismo que los anteriores pero lo unico que hace es registrar un documento nuevo y no permite actualizar la información
        binding.btnRegistrar.setOnClickListener {
            if (binding.etNombre.text.isNotBlank() &&
                binding.etEmail.text.isNotBlank()){
                val dato = hashMapOf(
                    "nombre" to binding.etNombre.text.toString(),
                    "correo" to binding.etEmail.text.toString()
                )
                /*la unica diferencia en esta linea de codigo es que no especificamos el documento ni el nombre que tendrá, esto
                //se hace por que no queremos definirlo si no que queremos que firebase nos autogenere el documento con un id aleatorio
                //por lo que solo quedaria implementar el metodo para crear el registro a la base por medio de la función add() y le pasamos
                //dentro del metodo el modelo de datos que tendrá el documento*/
                db.collection("amigos").add(dato)
                    .addOnSuccessListener {
                        binding.tvConsulta.text = "Procesado correctamente"
                    }
                    .addOnFailureListener {
                        binding.tvConsulta.text = "No se ha podido procesar"
                    }
            }
        }

        /*Este boton lo unico que hace es borrar un documento en base al id, este dato se lo proporcionaremos por medio del editText Id, capturaremos el valor
        //y procederemos a ejecutar la operación de borrado con el metodo delete()*/
        binding.btnBorrar.setOnClickListener {

                //aqui solo validamos el editText del id
                if (binding.etId.text.isNotBlank()){
                    //despues de validar colocamos la ruta hacia el documento que deseamos eliminar y le pasamos el metodo delete() para realizar la acción
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


        //En este ultimo boton hara la consulta de todos los registros que esten en la colección amigos
        binding.btnConsulta.setOnClickListener {
            //primero declaramos una variable de tipo string para despues utilizar esta variable para mostrar
            //los datos obtenidos de la base de datos y se pueda mostrar en pantalla la información
            var datos = ""
            //para obtner la información de todos los documentos que tenga la coleccion amigos solo ponemos db.collection("amigos") sin especificar un documento
            //le agregamos el metodo get()
            db.collection("amigos")
                .get()
                .addOnSuccessListener { resultado ->
                    //dentro del metodo Success haremos una iteración para poder obtener la información de cada documento, por lo que en el for se puede interpretar
                    //de la siguiente manera, de lo que obtuvo de resultado en la consulta quiero que recorras cada documento y me obtengas los siguientes datos
                    for(documento in resultado){
                        /*en las variables vamos a guardar los valores obtenidos de cada campo que nos interese obtener, en este caso es el nombre, correo y fecha,
                        //pero como en la base de datos en algunos documentos no se le agregaron el campo fecha se le pondrá una leyenda como "(fecha no indicada)"
                        //pero si algun documento si tiene ese campo y no esta vacio pues entonces sobreescribiremos la variable obteniendo de la base de datos la fecha*/
                        val nombre = documento["nombre"].toString()
                        val correo = documento["correo"].toString()
                        var fecha = "(fecha no indicada)"
                        //la validación que se menciona anteriormente se pondrá en try catch para evitar que la aplicación deje de funcionar
                        try {
                            if (documento["fecha"]!= null){
                                //como mencionamos que se va a sobre escribir la variable fecha hacemos la obtención del dato con el codigo documento["fecha"] y este
                                //valor es de tipo Timestamp y a eso lo tenemos que convertir a una fecha legible para nosotros y convertirlo a string
                                //para entender esto es que la fecha de tipo timestamp puede estar en segundos o milisegundos teniendo como representación de la fecha
                                //de esta forma 1659541600 = Timestamp, por lo que no se puede entender que es eso por lo que es necesario hacer la conversion de eso a una fecha
                                //mas entendible por nosotros como es Wed, 03 Aug 2022 15:46:40 GMT algo asi quedaria y eso convertirlo a un string
                                fecha = (documento["fecha"] as Timestamp).toDate().toString()
                            }
                            //en el catch podriamos poner un mensaje de error en caso de q algo haya ocurrido
                        }catch (e: Exception){

                        }
                        //fuera del trycatch concatenamos los valores nombre, correo y fecha con un salto de linea
                        datos += "${documento.id}: $nombre, $correo : $fecha\n"

                    }
                    //y eso se lo pasaremos a un textView asignandole el valor con la variable datos que es el que trae toda la información que extragimos de la base de datos
                    binding.tvConsulta.text = datos
                }
                    //Finalmente en el Failure nadamas le ponemos un mensaje de que no se pudo conectar
                .addOnFailureListener { exception ->
                    binding.tvConsulta.text = "No se ha podido conectar"
                }
        }


        //En una variable vamos a poner un listener de tipo DatePickerDialog para administrar la fecha que se va a colocar en el
        //editText, esta lambda tiene como parametros la referencia al datePicker, el año, mes y dia
        val listenerFecha = DatePickerDialog.OnDateSetListener { datePicker, anyo, mes, dia ->
            //dentro de la lambda vamos primero a borrar o limpiar el formato de tipo hora, minutos y segundos que tiene la variable fechaEscogida
            //ya que ademas de poner el dia, mes y año pone lo que se menciona anteriormente por lo tanto eso no lo queremos y por eso hacemos esta limpia
            fechaEscogida.clear()
            //aqui lo que hace es que al momento de seleccionar una fecha en el DatePickerDialog vamos a asignarle a cada campo su valor correspondiente
            //es decir quedaria interpretado de la siguiente forma:
            //Calendar.YEAR es la llave indicando el año y anyo vendria siendo el valor o el año escogido por ejemplo:
            //fechaEscogida.set(año, 2022)
            //fechaEscogida.set(mes, 08)
            //fechaEscogida.set(dia, 02)
            fechaEscogida.set(Calendar.YEAR, anyo)
            fechaEscogida.set(Calendar.MONTH, mes)
            fechaEscogida.set(Calendar.DAY_OF_MONTH, dia)

            //Despues de obtener la fecha seleccionada por el usuario mediante el DatePickerDialog procederemos a colocar esa información en el editTextFecha
            //La expresion Date(fechaEscogida.timeInMillis) significa que la fecha escogida por el usuario necesita estar en milisegundos para poderla transformar a una fecha
            //normal y eso se convertira a un string
            binding.etFecha.setText(Date(fechaEscogida.timeInMillis).toString())
        }


        var cal = Calendar.getInstance()
        //Ahora por medio del boton lo que hacemos es que nos muestra un DatePickerDialog al presionarlo por lo que es necesario configurar como se va mostrar
        //ese calendario y eso lo hacemos con la siguientes lineas de codigo
        binding.btnFecha.setOnClickListener {
            //le pasamos al DatepickerDialog el contexto de la activity, despues un listener que viene siendo el oyente para cuando el usuario establece una fecha,
            //despues se obtiene el año, mes y dia actual y finalmente se muestra ese DatePickerDialog, pero no hay que olvidar que debemos hacer uso de la variable
            //cal que tiene almacenado la instancia del calendario poder establecer la configuración del calendario al abrirlo
            DatePickerDialog(this,
            listenerFecha,
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)).show()
        }
    }
}