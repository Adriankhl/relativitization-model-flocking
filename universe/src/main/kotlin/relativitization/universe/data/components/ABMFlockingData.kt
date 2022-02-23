package relativitization.universe.data.components

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerInternalData
import relativitization.universe.data.PlayerInternalData

@Serializable
@SerialName("ABMFlockingData")
data class ABMFlockingData(
    val restMass: Double = 1.0,
) : PlayerDataComponent()

@Serializable
@SerialName("ABMFlockingData")
data class MutableABMFlockingData(
    var restMass: Double = 1.0,
) : MutablePlayerDataComponent()


fun PlayerInternalData.abmFlockingData(): ABMFlockingData =
    playerDataComponentMap.getOrDefault(ABMFlockingData::class, ABMFlockingData())

fun MutablePlayerInternalData.abmFlockingData(): MutableABMFlockingData =
    playerDataComponentMap.getOrDefault(MutableABMFlockingData::class, MutableABMFlockingData())

fun MutablePlayerInternalData.abmFlockingData(newABMFlockingData: MutableABMFlockingData) =
    playerDataComponentMap.put(newABMFlockingData)