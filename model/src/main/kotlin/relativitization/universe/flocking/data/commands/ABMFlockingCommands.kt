package relativitization.universe.flocking.data.commands

import kotlinx.serialization.Serializable
import relativitization.universe.core.data.MutablePlayerData
import relativitization.universe.core.data.UniverseSettings
import relativitization.universe.core.data.commands.Command
import relativitization.universe.core.maths.physics.Int4D
import relativitization.universe.core.maths.physics.Relativistic
import relativitization.universe.core.maths.physics.TargetVelocityData
import relativitization.universe.core.maths.physics.Velocity
import relativitization.universe.flocking.data.components.abmFlockingData

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