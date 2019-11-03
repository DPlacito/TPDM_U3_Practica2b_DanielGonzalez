package mx.edu.ittepic.tpdm_u3_practica2b_danielgonzalez

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_main.*
import java.net.URL
import java.util.*

class MainActivity : AppCompatActivity() {

    var descripcion: EditText? = null
    var monto: EditText? = null
    var RSI_Pago: RadioButton? = null
    var RNO_Pago: RadioButton? = null
    var insertar: Button? = null
    var cargar: Button? = null
    var mostrar: Button? = null
    var etiqueta: TextView? = null
    var layout_fecha: LinearLayout? = null
    var date: TextView? = null
    var jsonRegreso = ArrayList<org.json.JSONObject>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(cabezera_Principal)
        title = "Recibo Pagos"

        descripcion = findViewById(R.id.EditText1)
        monto = findViewById(R.id.EditText2)
        layout_fecha = findViewById(R.id.layout_fecha_date)
        RSI_Pago = findViewById(R.id.SiPago)
        RNO_Pago = findViewById(R.id.NoPago)
        insertar = findViewById(R.id.Boton_Insertar)
        cargar = findViewById(R.id.Boton_Cargar)
        mostrar = findViewById(R.id.Boton_Mostrar)
        etiqueta = findViewById(R.id.etiqueta_Activity)
        date = findViewById(R.id.date_text)

        mostrar?.isEnabled = false

        layout_fecha?.setOnClickListener {
            getDatePickerDialog()
        }

        insertar?.setOnClickListener {
            var conexionWeb = ConexionWeb(this)
            conexionWeb.agregarVariables("descripcion", descripcion?.text.toString()+"\n")
            conexionWeb.agregarVariables("monto", monto?.text.toString()+"\n")
            conexionWeb.agregarVariables("fechavencimiento", date?.text.toString()+"\n")
            conexionWeb.agregarVariables("pagado", RSI_Pago?.isChecked.toString()+"\n")
            conexionWeb.execute((URL("https://tranquil-springs-38452.herokuapp.com/insertar.php")))
            //Metodo de ejecucion en segundo plano del AnsyTask
        }
        cargar?.setOnClickListener {
            var conexionWeb = ConexionWeb(this)
            conexionWeb.execute((URL("https://tranquil-springs-38452.herokuapp.com/consultageneral.php")))
            bloqueoDeCargar()

        }
        mostrar?.setOnClickListener {
            val posicion = descripcion?.text.toString().toInt()
            val jsonObject = jsonRegreso.get(posicion)
            etiqueta?.setText("IDPago: " +jsonObject.getString("idpago")+
                "\nDescripcion: " + jsonObject.getString("descripcion") +
                        "\nMonto: " + jsonObject.getString("monto") +
                        "\nFecha De Vencimiento: " + jsonObject.getString("fechavencimiento") +
                        "\nPagado: " + jsonObject.getString("pagado"))
            DesbloqueoDeMostrar()
        }
    }

private fun bloqueoDeCargar(){
    mostrar?.isEnabled = true
    cargar?.isEnabled = false
    insertar?.isEnabled = false
    monto?.isEnabled = false
    layout_fecha?.isEnabled = false
    RSI_Pago?.isEnabled = false
    RNO_Pago?.isEnabled = false
    descripcion?.setHint("ID a Mostrar")
    monto?.setHint("Bloqueado")
    date?.setHint("Bloqueado")
}

    private fun DesbloqueoDeMostrar(){
        mostrar?.isEnabled = false
        cargar?.isEnabled = true
        insertar?.isEnabled = true
        monto?.isEnabled = true
        layout_fecha?.isEnabled = true
        RSI_Pago?.isEnabled = true
        RNO_Pago?.isEnabled = true
        descripcion?.setHint("Descripcion")
        descripcion?.setText("")
        monto?.setHint("Monto")
        date?.setHint("Toque Aqui")
    }

    private fun getDatePickerDialog() {
        val calender = Calendar.getInstance()
        val year = calender.get(Calendar.YEAR)
        val month = calender.get(Calendar.MONTH)
        val day = calender.get(Calendar.DAY_OF_MONTH)
        var uno = 1

        val dialog = DatePickerDialog(this, { view, year, month, dayOfMonth ->
            var mes = month + uno
            val endDate = "$dayOfMonth.$mes.$year"
            date?.setText(endDate)
        }, year, month, day)

        dialog.datePicker.minDate =
            System.currentTimeMillis()
        dialog.show()
    }

    fun mostrarResultados(result: String) {

        val jsonarray = org.json.JSONArray(result)
        var total = jsonarray.length() - 1
        (0..total).forEach {
            jsonRegreso.add(jsonarray.getJSONObject(it))
        }
        etiqueta?.setText(result)
    }
}

