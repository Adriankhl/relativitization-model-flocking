package relativitization.universe.data.components

import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerInternalData
import relativitization.universe.data.PlayerInternalData

@Serializable
data class ABMFlockingData(
    val coreMass: Double = 1.0,
    val fuelRestMass: Double = 0.0,
) : PlayerDataComponent() {
    fun totalMass(): Double = coreMass + fuelRestMass
}

@Serializable
data class MutableABMFlockingData(
    var coreMass: Double = 1.0,
    var fuelRestMass: Double = 0.0,
) : MutablePlayerDataComponent() {
    fun totalMass(): Double = coreMass + fuelRestMass
}


fun PlayerInternalData.abmFlockingData(): ABMFlockingData =
    playerDataComponentMap.getOrDefault(ABMFlockingData::class, ABMFlockingData())

fun MutablePlayerInternalData.abmFlockingData(): MutableABMFlockingData =
    playerDataComponentMap.getOrDefault(MutableABMFlockingData::class, MutableABMFlockingData())

fun MutablePlayerInternalData.abmFlockingData(newABMFlockingData: MutableABMFlockingData) =
    playerDataComponentMap.put(newABMFlockingData)