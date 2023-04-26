package relativitization.universe.flocking.data.components

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import relativitization.universe.core.data.MutablePlayerInternalData
import relativitization.universe.core.data.PlayerInternalData
import relativitization.universe.core.data.components.MutablePlayerDataComponent
import relativitization.universe.core.data.components.PlayerDataComponent

@Serializable
@SerialName("ABMFlockingData")
data class ABMFlockingData(
    val restMass: Double = 1.0,
    val restMassFraction: Double = 1.0,
) : PlayerDataComponent()

@Serializable
@SerialName("ABMFlockingData")
data class MutableABMFlockingData(
    var restMass: Double = 1.0,
    var restMassFraction: Double = 1.0,
) : MutablePlayerDataComponent()


fun PlayerInternalData.abmFlockingData(): ABMFlockingData =
    playerDataComponentMap.get()

fun MutablePlayerInternalData.abmFlockingData(): MutableABMFlockingData =
    playerDataComponentMap.get()

fun MutablePlayerInternalData.abmFlockingData(newABMFlockingData: MutableABMFlockingData) =
    playerDataComponentMap.put(newABMFlockingData)