package appjpm4everyone.sensorsandroid

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import appjpm4everyone.sensorsandroid.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    //Sensors
    lateinit var sensorManager : SensorManager
    lateinit var sensor : List<Sensor>
    lateinit var listSensor : MutableList<AndroidSensors>
    private var opSensor : Int = 0

    //DataBinding
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        //setContentView(R.layout.activity_main)

        getSensor()
    }

    private fun getSensor() {
        sensorManager = applicationContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = sensorManager.getSensorList(Sensor.TYPE_ALL)

        for(sensors in sensor){
            println(sensors)
        }

        if(!sensor.isNullOrEmpty()){
            mappingSensor()
        }

    }

    private fun mappingSensor() {
        listSensor = ArrayList()
        for(i in 0 until sensor.lastIndex){
            //Mapping
            val auxSensors : AndroidSensors = AndroidSensors(sensor[i].name, sensor[i].vendor, sensor[i].type, sensor[i].maximumRange.toDouble(), sensor[i].resolution.toDouble(), sensor[i].power.toDouble(), sensor[i].minDelay )
            listSensor.add(auxSensors)
        }
        val listStringSensors : MutableList<String?> = ArrayList()
        for(i in 0 until listSensor.lastIndex){
            //Mapping
            val auxSensors : AndroidSensors = AndroidSensors(sensor[i].name, sensor[i].vendor, sensor[i].type, sensor[i].maximumRange.toDouble(), sensor[i].resolution.toDouble(), sensor[i].power.toDouble(), sensor[i].minDelay )
            listStringSensors.add(listSensor[i].name)
        }
        setSpinner(listStringSensors)

    }

    private fun setSpinner(listStringSensors: MutableList<String?>) {
        val dataAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this,
            R.layout.spinner_style,
            listStringSensors
        )
        //sensor.get(0).
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(R.layout.custom_spinner)
        binding.spinnerSensors.adapter = dataAdapter
        binding.spinnerSensors.setSelection(0)
        binding.spinnerSensors.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, view: View?, position: Int, id: Long) {
                //opSensor = p0?.getItemAtPosition(position) as Int
                opSensor = p0?.selectedItemPosition!!
                if(!sensor.isNullOrEmpty()){
                    binding.textViewSensor.text = sensor[opSensor].toString()
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                //No implementation needed
            }
        }
    }
}