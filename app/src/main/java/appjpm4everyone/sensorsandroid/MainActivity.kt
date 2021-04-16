package appjpm4everyone.sensorsandroid

import android.R.attr
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import appjpm4everyone.sensorsandroid.databinding.ActivityMainBinding
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt


class MainActivity : BaseActivity(), SensorEventListener {

    //Sensors
    lateinit var sensorManager: SensorManager
    lateinit var sensor: List<Sensor>
    lateinit var listSensor: MutableList<AndroidSensors>
    private var opSensor: Int = 0

    //Individuals sensors
    private lateinit var defaultSensor: Sensor
    lateinit var sensorAccel: Sensor
    lateinit var sensorGyro: Sensor
    lateinit var sensorProx: Sensor


    private var lastUpdate: Long = 0
    private var last_x = 0f
    private var last_y = 0f
    private var last_z = 0f

    //Chart
    private var plotData: Boolean = true
    private var plotValues: Boolean = true
    private var auxSensorType = 0
    private var flagSensor: Boolean = false
    private var yValues: MutableList<Entry> = mutableListOf()
    private var zValues: MutableList<Entry> = mutableListOf()
    private var count: Int = 0

    //DataBinding
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        getSensor()
        initSensors()
        initClicks()
    }

    private fun initClicks() {
        binding.buttonSensor.setOnClickListener {
            showLongSnackError(this, "Desarrollado por Jim Moreno")
        }
    }

    private fun initSensors() {
        defaultSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        auxSensorType = Sensor.TYPE_ACCELEROMETER
        //defaultSensor = null
        sensorAccel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorGyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        sensorProx = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
    }

    private fun getSensor() {
        sensorManager = applicationContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = sensorManager.getSensorList(Sensor.TYPE_ALL)

        for (sensors in sensor) {
            println(sensors)
        }

        if (!sensor.isNullOrEmpty()) {
            mappingSensor()
        }

    }

    private fun mappingSensor() {
        listSensor = ArrayList()
        for (i in 0 until sensor.lastIndex) {
            //Mapping
            val auxSensors = AndroidSensors(
                sensor[i].name,
                sensor[i].vendor,
                sensor[i].type,
                sensor[i].maximumRange.toDouble(),
                sensor[i].resolution.toDouble(),
                sensor[i].power.toDouble(),
                sensor[i].minDelay
            )
            listSensor.add(auxSensors)
        }
        val listStringSensors: MutableList<String?> = ArrayList()
        for (i in 0 until listSensor.lastIndex) {
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
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(R.layout.custom_spinner)
        binding.spinnerSensors.adapter = dataAdapter
        binding.spinnerSensors.setSelection(0)
        binding.spinnerSensors.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    p0: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    opSensor = p0?.selectedItemPosition!!
                    if (!sensor.isNullOrEmpty()) {
                        showSensorValue()
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    //No implementation needed
                }
            }
    }

    private fun showSensorValue() {
        //updateText()
        if(auxSensorType != listSensor[opSensor].type!!){
            auxSensorType = listSensor[opSensor].type!!
            yValues = mutableListOf()
            zValues = mutableListOf()
        }
        initChar()
        startPlot()
    }

    private fun initChar() {
        binding.chart.isDragXEnabled = true
        binding.chart.setScaleEnabled(false)
        binding.chart.description.text = "Real time Data"
        binding.chart.setBackgroundColor(Color.WHITE)

        val leftAxis = binding.chart.axisLeft
        leftAxis.setDrawLimitLinesBehindData(true)

        val rightAxis = binding.chart.axisRight
        rightAxis.setDrawLimitLinesBehindData(true)

        val xAxis = binding.chart.xAxis
        xAxis.axisMinimum = 0f
        xAxis.axisMaximum = 20f
        xAxis.setDrawLimitLinesBehindData(true)
    }


    private fun startPlot() {
        Thread(Runnable {
            while (true) {
                plotData = true
                try {
                    Thread.sleep(1000)
                    flagSensor = !flagSensor
                    refreshSensors()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }).start()
    }

    private fun refreshSensors() {
        try {
            sensorManager.getDefaultSensor(listSensor[opSensor].type!!)
        } catch (e: InterruptedException) {
            e.printStackTrace()
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        }
        sensorManager.registerListener(
            this,
            defaultSensor ,
            SensorManager.SENSOR_DELAY_GAME
        )
    }

    @SuppressLint("SetTextI18n")
    private fun updateText(sensorMagnitude: String, magnitude: String, x: Float, y: Float, z: Float) {
        binding.textViewSensor.text = ""
        binding.textViewSensor.text =
            "$sensorMagnitude\n$magnitude:  X=$x Y=$y Z=$z"
    }

    @SuppressLint("SetTextI18n")
    private fun updateText(sensorMagnitude: String, magnitude: String, x: Float) {
        binding.textViewSensor.text = ""
        binding.textViewSensor.text =
            "$sensorMagnitude\n$magnitude:  X=$x"
    }

    @SuppressLint("SetTextI18n")
    private fun updateText(sensor: String, noSensor: String) {
        binding.textViewSensor.text = ""
        binding.textViewSensor.text =
            "$sensor\n$noSensor"
    }

    @SuppressLint("SetTextI18n")
    override fun onSensorChanged(sensorEvent: SensorEvent?) {
        if(flagSensor) {
            when (listSensor[opSensor].type) {
                Sensor.TYPE_ACCELEROMETER -> {
                    if (sensorEvent != null) {
                        val x = sensorEvent.values[0]
                        val y = sensorEvent.values[1]
                        val z = sensorEvent.values[2]

                        val curTime = System.currentTimeMillis()

                        if (curTime - lastUpdate > 100) {
                            val diffTime = curTime - lastUpdate
                            lastUpdate = curTime
                            val speed =
                                abs(attr.x + attr.y + z - last_x - last_y - last_z) / diffTime * 10000
                            last_x = x
                            last_y = y
                            last_z = z
                        }
                        updateText(sensor[opSensor].toString(), "Acelerometer: ", x, y, z)
                        addEntrySensorValues(sensorEvent.values[0], true)
                    } else{
                        updateText(sensor[opSensor].toString() ,"No sensor detected")
                    }
                }
                Sensor.TYPE_GYROSCOPE -> {
                    if (sensorEvent != null) {
                        val x = sensorEvent.values[0]
                        val y = sensorEvent.values[1]
                        val z = sensorEvent.values[2]

                        val curTime = System.currentTimeMillis()

                        if (curTime - lastUpdate > 100) {
                            val diffTime = curTime - lastUpdate
                            lastUpdate = curTime
                            val speed =
                                abs(attr.x + attr.y + z - last_x - last_y - last_z) / diffTime * 10000
                            last_x = x
                            last_y = y
                            last_z = z
                        }
                        updateText(sensor[opSensor].toString(), "Gyroscope: ", x, y, z)
                        addEntrySensorValues(sensorEvent.values[0], true)
                    } else{
                        updateText(sensor[opSensor].toString() ,"No sensor detected")
                    }
                }
                Sensor.TYPE_PROXIMITY -> {
                    if (sensorEvent != null) {
                        updateText(
                            sensor[opSensor].toString(),
                            "Proximity: ",
                            sensorEvent.values[0]
                        )
                        addEntrySensorValues(sensorEvent.values[0], false)
                    } else{
                        updateText(sensor[opSensor].toString() ,"No sensor detected")
                    }
                }
                Sensor.TYPE_MAGNETIC_FIELD -> {
                    if (sensorEvent != null) {
                        val x = sensorEvent.values[0]
                        val y = sensorEvent.values[1]
                        val z = sensorEvent.values[2]

                        val curTime = System.currentTimeMillis()

                        if (curTime - lastUpdate > 100) {
                            val diffTime = curTime - lastUpdate
                            lastUpdate = curTime
                            val speed =
                                abs(attr.x + attr.y + z - last_x - last_y - last_z) / diffTime * 10000
                            last_x = x
                            last_y = y
                            last_z = z
                        }
                        updateText(sensor[opSensor].toString(), "Magnetic Field: ", x, y, z)
                        addEntrySensorValues(sensorEvent.values[0], true)
                    } else{
                        updateText(sensor[opSensor].toString() ,"No sensor detected")
                    }
                }
                Sensor.TYPE_AMBIENT_TEMPERATURE -> {
                    if (sensorEvent != null) {
                        updateText(
                            sensor[opSensor].toString(),
                            "Temperature: ",
                            sensorEvent.values[0]
                        )
                        addEntrySensorValues(sensorEvent.values[0], false)
                    } else{
                        updateText(sensor[opSensor].toString() ,"No sensor detected")
                    }
                }
                Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR -> {
                    if (sensorEvent != null) {
                        updateText(sensor[opSensor].toString(), "GRV: ", sensorEvent.values[0])
                        addEntrySensorValues(sensorEvent.values[0], false)
                    }
                    else{
                        updateText(sensor[opSensor].toString() ,"No sensor detected")
                    }
                }
                Sensor.TYPE_GRAVITY -> {
                    if (sensorEvent != null) {
                        val x = sensorEvent.values[0]
                        val y = sensorEvent.values[1]
                        val z = sensorEvent.values[2]
                        val gravity = sqrt(x.pow(2) + y.pow(2) + z.pow(2) )
                        updateText(sensor[opSensor].toString(), "Gravity: ", gravity)
                        addEntrySensorValues(gravity, false)
                    }
                    else{
                        updateText(sensor[opSensor].toString() ,"No sensor detected")
                    }
                }
                Sensor.TYPE_LIGHT -> {
                    if (sensorEvent != null) {
                        updateText(
                            sensor[opSensor].toString(),
                            "nLight: ",
                            sensorEvent.values[0]
                        )
                        addEntrySensorValues(sensorEvent.values[0], false)
                    }
                    else{
                        updateText(sensor[opSensor].toString() ,"No sensor detected")
                    }
                }
                Sensor.TYPE_LINEAR_ACCELERATION -> {
                    if (sensorEvent != null) {
                        val x = sensorEvent.values[0]
                        val y = sensorEvent.values[1]
                        val z = sensorEvent.values[2]

                        val curTime = System.currentTimeMillis()

                        if (curTime - lastUpdate > 100) {
                            val diffTime = curTime - lastUpdate
                            lastUpdate = curTime
                            val speed =
                                abs(attr.x + attr.y + z - last_x - last_y - last_z) / diffTime * 10000
                            last_x = x
                            last_y = y
                            last_z = z
                        }
                        val linearAccel = sqrt(x.pow(2) + y.pow(2) + z.pow(2) )
                        updateText(sensor[opSensor].toString(), "Linear acceleration: ", linearAccel)
                        addEntrySensorValues(linearAccel, false)
                    }
                    else{
                        updateText(sensor[opSensor].toString() ,"No sensor detected")
                    }
                }
                else -> {
                    if (sensorEvent != null) {
                        updateText(sensor[opSensor].toString() , "Another Sensor: ", sensorEvent.values[0])
                        addEntrySensorValues(sensorEvent.values[0], false)
                    }else{
                        updateText(sensor[opSensor].toString() ,"No sensor detected")
                    }
                }
            }
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
       // Not yet implemented
    }

    private fun addEntrySensorValues(sensorValues: Float, multiValues: Boolean) {


        yValues.add(Entry(count.toFloat(), sensorValues))

        val set1: LineDataSet = LineDataSet(yValues, "Data set 1")
        set1.fillAlpha = 110
        set1.color = Color.RED
        set1.lineWidth = 3f
        set1.valueTextSize = 10f
        set1.valueTextColor = R.color.purple_toolbar

        val dataSets: ArrayList<ILineDataSet> = ArrayList()
        dataSets.add(set1)

        val data: LineData = LineData(dataSets)
        binding.chart.data = data

        if(multiValues){
            zValues.add(Entry(count.toFloat(), sensorValues, 0))
            zValues.add(Entry(count.toFloat(), sensorValues*2, 1))

            val set2: LineDataSet = LineDataSet(yValues, "Data set 2")
            set2.fillAlpha = 110
            set2.color = Color.BLUE
            set2.lineWidth = 3f
            set2.valueTextSize = 10f
            set2.valueTextColor = R.color.purple_toolbar

            val dataSets2: ArrayList<ILineDataSet> = ArrayList()
            dataSets2.add(set2)

            val data2: LineData = LineData(dataSets2)
            binding.chart.data = data2
        }


        binding.chart.invalidate()

        if (count > 19) {
            yValues = mutableListOf()
            if(multiValues){
                zValues = mutableListOf()
            }
            count = 0
        } else {
            count += 1
        }

    }

    override fun onResume() {
        super.onResume()
        if(defaultSensor!=null) {
            sensorManager.registerListener(
                this,
                defaultSensor,
                SensorManager.SENSOR_DELAY_GAME
            )
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }
}