package relativitization.universe.flocking.data.components

import kotlinx.serialization.SerialName
import ksergen.annotations.GenerateImmutable
import relativitization.universe.core.data.MutablePlayerInternalData
import relativitization.universe.core.data.PlayerInternalData
import relativitization.universe.core.data.components.MutablePlayerDataComponent

@GenerateImmutable
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