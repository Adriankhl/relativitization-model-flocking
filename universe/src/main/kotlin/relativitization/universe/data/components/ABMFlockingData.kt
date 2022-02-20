package relativitization.universe.data.components

import kotlinx.serialization.Serializable

@Serializable
data class ABMFlockingData(
    val mass: Double = 1.0,
    val fuelRestMass: Double = 0.0,
) : PlayerDataComponent()

@Serializable
data class MutableABMFlockingData(
    val mass: Double = 1.0,
    val fuelRestMass: Double = 0.0,
) : MutablePlayerDataComponent()