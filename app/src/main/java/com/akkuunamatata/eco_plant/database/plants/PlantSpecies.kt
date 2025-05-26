package com.akkuunamatata.eco_plant.database.plants

enum class PlantService(val id: Int) {
    NITROGEN_PROVISION(0),
    STORAGE_AND_RETURN_WATER(1),
    SOIL_STRUCTURATION(2),
    NONE(-1); // err
}

fun getPlantServiceByString(service: String): PlantService {
    return when (service) {
        "nitrogen_provision" -> PlantService.NITROGEN_PROVISION
        "storage_and_return_water" -> PlantService.STORAGE_AND_RETURN_WATER
        "soil_structuration" -> PlantService.SOIL_STRUCTURATION
        else -> PlantService.NONE // err
    }
}

class PlantSpecies {
    var name: String = ""
    var services: FloatArray = FloatArray(3) { -1f }
    var reliabilities: FloatArray = FloatArray(3) { -1f }
    var culturalConditions: Array<String> = arrayOf("", "", "")

    constructor(
        name: String,
        services: FloatArray,
        reliabilities: FloatArray,
        culturalConditions: Array<String>
    ) {
        this.name = name
        this.services = services
        this.reliabilities = reliabilities
        this.culturalConditions = culturalConditions
    }

    constructor(
        name: String,
    ) {
        this.name = name
        this.services = FloatArray(3) { -1f }
        this.reliabilities = FloatArray(3) { -1f }
        this.culturalConditions = arrayOf("", "", "")
    }

    fun setServiceValue(plantService: PlantService, value: Float) {
        val index = plantService.id
        if (index in 0..2) {
            services[index] = value
        } else {
            throw IllegalArgumentException("Invalid PlantService index: $index")
        }
    }

    fun setServiceReliability(plantService: PlantService, value: Float) {
        val index = plantService.id
        if (index in 0..2) {
            reliabilities[index] = value
        } else {
            throw IllegalArgumentException("Invalid PlantService index: $index")
        }
    }

    fun setCulturalCondition(plantService: PlantService, value: String) {
        val index = plantService.id
        if (index in 0..2) {
            culturalConditions[index] = value
        } else {
            throw IllegalArgumentException("Invalid CulturalCondition index: $index")
        }
    }

    fun getServiceValue(plantService: PlantService): Float {
        val index = plantService.id
        if (index in 0..2) {
            return services[index]
        } else {
            throw IllegalArgumentException("Invalid PlantService index: $index")
        }
    }

    fun getServiceReliability(plantService: PlantService): Float {
        val index = plantService.id
        if (index in 0..2) {
            return reliabilities[index]
        } else {
            throw IllegalArgumentException("Invalid PlantService index: $index")
        }
    }

    fun getCulturalCondition(plantService: PlantService): String {
        val index = plantService.id
        if (index in 0..2) {
            return culturalConditions[index]
        } else {
            throw IllegalArgumentException("Invalid CulturalCondition index: $index")
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PlantSpecies

        if (name != other.name) return false
        if (!services.contentEquals(other.services)) return false
        if (!reliabilities.contentEquals(other.reliabilities)) return false
        if (!culturalConditions.contentEquals(other.culturalConditions)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + services.contentHashCode()
        result = 31 * result + reliabilities.contentHashCode()
        result = 31 * result + culturalConditions.contentHashCode()
        return result
    }
}

class ParcelleData(
    val lat: Double,
    val long: Double,
    val idAuthor: String,
    val plants: List<PlantSpecies>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ParcelleData

        if (lat != other.lat) return false
        if (long != other.long) return false
        if (idAuthor != other.idAuthor) return false
        if (plants != other.plants) return false

        return true
    }

    override fun hashCode(): Int {
        var result = lat.hashCode()
        result = 31 * result + long.hashCode()
        result = 31 * result + idAuthor.hashCode()
        result = 31 * result + plants.hashCode()
        return result
    }
}