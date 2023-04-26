package relativitization.universe.ai

import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.commands.ABMFlockingChangeVelocityCommand
import relativitization.universe.data.commands.Command
import relativitization.universe.data.components.abmFlockingData
import relativitization.universe.maths.physics.Velocity
import relativitization.universe.utils.RelativitizationLogManager
import kotlin.random.Random

object ABMFlockingSVMAI : AI() {
    private val logger = RelativitizationLogManager.getLogger()

    override fun compute(
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        random: Random,
    ): List<Command> {

        val flockSpeed: Double = universeData3DAtPlayer.universeSettings.getOtherDoubleOrDefault(
            "flockSpeed",
            0.5
        )

        val nearbyRadius: Double = universeData3DAtPlayer.universeSettings.getOtherDoubleOrDefault(
            "nearbyRadius",
            3.0
        )

        val maxAnglePerturbation: Double = universeData3DAtPlayer.universeSettings
            .getOtherDoubleOrDefault(
                "maxAnglePerturbation",
                0.1
            )

        val accelerationFuelFraction: Double = universeData3DAtPlayer.universeSettings
            .getOtherDoubleOrDefault(
                "accelerationFuelFraction",
                1.0
            )

        val averageVelocity: Velocity = universeData3DAtPlayer.getPlayerInSphere(nearbyRadius)
            .fold(Velocity(0.0, 0.0, 0.0)) { acc, playerData ->
                acc + playerData.velocity
            }.scaleVelocity(flockSpeed)

        // Perturb the velocity by a random angle
        val targetVelocity: Velocity = averageVelocity.randomRotate(maxAnglePerturbation, random)

        val maxDeltaRestMass: Double = universeData3DAtPlayer.getCurrentPlayerData()
            .playerInternalData.abmFlockingData().restMass * accelerationFuelFraction

        val abmFlockingChangeVelocityCommand = ABMFlockingChangeVelocityCommand(
            toId = universeData3DAtPlayer.id,
            targetVelocity = targetVelocity,
            maxDeltaRestMass = maxDeltaRestMass,
        )

        return listOf(abmFlockingChangeVelocityCommand)
    }
}