package relativitization.universe.ai

import relativitization.universe.data.PlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.commands.ABMFlockingChangeVelocityCommand
import relativitization.universe.data.commands.Command
import relativitization.universe.data.components.abmFlockingData
import relativitization.universe.maths.physics.Intervals
import relativitization.universe.maths.physics.Velocity
import relativitization.universe.utils.RelativitizationLogManager

object ABMFlockingSVMAI : AI() {
    private val logger = RelativitizationLogManager.getLogger()

    override fun compute(universeData3DAtPlayer: UniverseData3DAtPlayer): List<Command> {

        val flockSpeed: Double = universeData3DAtPlayer.universeSettings.otherDoubleMap
            .getOrElse("flockSpeed") {
                logger.error("No flockSpeed defined")
                0.5
            }

        val nearbyRadius: Double = universeData3DAtPlayer.universeSettings.otherDoubleMap
            .getOrElse("nearbyRadius") {
                logger.error("No nearbyRadius defined")
                3.0
            }

        val maxAnglePerturbation: Double = universeData3DAtPlayer.universeSettings.otherDoubleMap
            .getOrElse("maxAnglePerturbation") {
                logger.error("No maxAnglePerturbation defined")
                0.1
            }

        val accelerationFuelFraction: Double = universeData3DAtPlayer.universeSettings
            .otherDoubleMap.getOrElse("accelerationFuelFraction") {
                logger.error("No accelerationFuelFraction defined")
                1.0
            }

        val averageVelocity: Velocity = universeData3DAtPlayer.getPlayerInSphere(nearbyRadius)
            .fold(Velocity(0.0, 0.0, 0.0)) { acc, playerData ->
                acc + playerData.velocity
            }.scaleVelocity(flockSpeed)

        // Perturb the velocity by a random angle
        val targetVelocity: Velocity = averageVelocity.randomRotate(maxAnglePerturbation)

        val maxDeltaRestMass: Double = universeData3DAtPlayer.getCurrentPlayerData()
            .playerInternalData.abmFlockingData().restMass * accelerationFuelFraction

        val abmFlockingChangeVelocityCommand = ABMFlockingChangeVelocityCommand(
            toId = universeData3DAtPlayer.id,
            fromId = universeData3DAtPlayer.id,
            fromInt4D = universeData3DAtPlayer.getCurrentPlayerData().int4D,
            targetVelocity = targetVelocity,
            maxDeltaRestMass = maxDeltaRestMass,
        )

        return listOf(abmFlockingChangeVelocityCommand)
    }
}