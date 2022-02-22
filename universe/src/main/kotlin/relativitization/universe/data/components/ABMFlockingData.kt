package relativitization.universe.data.components

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerInternalData
import relativitization.universe.data.PlayerInternalData

@Serializable
@SerialName("ABMFlockingData")
data class ABMFlockingData(
    val coreRestMass: Double = 1.0,
    val fuelRestMass: Double = 0.0,
) : PlayerDataComponent() {
    fun totalMass(): Double = coreRestMass + fuelRestMass
}

@Serializable
@SerialName("ABMFlockingData")
data class MutableABMFlockingData(
    var coreRestMass: Double = 1.0,
    var fuelRestMass: Double = 0.0,
) : MutablePlayerDataComponent() {
    fun totalMass(): Double = coreRestMass + fuelRestMass
}


fun PlayerInternalData.abmFlockingData(): ABMFlockingData =
    playerDataComponentMap.getOrDefault(ABMFlockingData::class, ABMFlockingData())

fun MutablePlayerInternalData.abmFlockingData(): MutableABMFlockingData =
    playerDataComponentMap.getOrDefault(MutableABMFlockingData::class, MutableABMFlockingData())

fun MutablePlayerInternalData.abmFlockingData(newABMFlockingData: MutableABMFlockingData) =
    playerDataComponentMap.put(newABMFlockingData)