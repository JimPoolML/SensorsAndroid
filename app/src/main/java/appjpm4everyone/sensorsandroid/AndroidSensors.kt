package appjpm4everyone.sensorsandroid

import com.google.gson.annotations.SerializedName

data class AndroidSensors(
    @SerializedName(value = "name", alternate = ["Name"])
    var name: String?,

    @SerializedName(value = "vendor", alternate = ["Vendor"])
    var vendor: String?,

    @SerializedName(value = "type", alternate = ["Type"])
    var type: Int?,

    @SerializedName(value = "maxRange", alternate = ["MaxRange"])
    var maxRange: Double?,

    @SerializedName(value = "resolution", alternate = ["Resolution"])
    var resolution: Double,

    @SerializedName(value = "power", alternate = ["Power"])
    var power: Double,

    @SerializedName(value = "minDelay", alternate = ["minDelay"])
    var minDelay: Int?
) {
    constructor() : this("",  "",0, 0.0, 0.0, 0.0, 0)

    override fun toString(): String {
        return "Sensor values: $name, $vendor, $type, $maxRange, $resolution, $power, $minDelay"
    }
}