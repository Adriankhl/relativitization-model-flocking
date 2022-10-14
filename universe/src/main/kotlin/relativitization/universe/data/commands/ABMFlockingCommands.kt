package relativitization.universe.data.commands

import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.components.abmFlockingData
import relativitization.universe.maths.physics.Int4D
import relativitization.universe.maths.physics.Relativistic
import relativitization.universe.maths.physics.TargetVelocityData
import relativitization.universe.maths.physics.Velocity

@Serializable
data class ABMFlockingChangeVelocityCommand(
    override val toId: Int,
    val targetVelocity: Velocity,
    val maxDeltaRestMass: Double,
) : Command() {
    override fun execute(
        playerData: MutablePlayerData,
        fromId: Int,
        fromInt4D: Int4D,
        universeSettings: UniverseSettings
    ) {
        val targetVelocityData: TargetVelocityData = Relativistic.targetVelocityByPhotonRocket(
            initialRestMass = playerData.playerInternalData.abmFlockingData().restMass,
            maxDeltaRestMass = maxDeltaRestMass,
            initialVelocity = playerData.velocity.toVelocity(),
            targetVelocity = targetVelocity,
            speedOfLight = universeSettings.speedOfLight
        )

        playerData.velocity.vx = targetVelocityData.newVelocity.vx
        playerData.velocity.vy = targetVelocityData.newVelocity.vy
        playerData.velocity.vz = targetVelocityData.newVelocity.vz

        playerData.playerInternalData.abmFlockingData().restMass -= targetVelocityData.deltaRestMass
    }
}