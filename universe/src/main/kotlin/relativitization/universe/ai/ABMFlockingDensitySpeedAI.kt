package relativitization.universe.ai

import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.commands.ABMFlockingChangeVelocityCommand
import relativitization.universe.data.commands.Command
import relativitization.universe.data.components.abmFlockingData
import relativitization.universe.maths.physics.Velocity
import relativitization.universe.utils.RelativitizationLogManager
import kotlin.math.exp
import kotlin.random.Random

object ABMFlockingDensitySpeedAI : AI() {
    private val logger = RelativitizationLogManager.getLogger()

    override fun compute(
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        random: Random,
    ): List<Command> {

        val minFlockSpeed: Double = universeData3DAtPlayer.universeSettings.otherDoubleMap
            .getOrElse("minFlockSpeed") {
                logger.error("No minFlockSpeed defined")
                0.1
            }

        val maxFlockSpeed: Double = universeData3DAtPlayer.universeSettings.otherDoubleMap
            .getOrElse("maxFlockSpeed") {
                logger.error("No maxFlockSpeed defined")
                0.9
            }

        val speedDecayFactor: Double = universeData3DAtPlayer.universeSettings.otherDoubleMap
            .getOrElse("speedDecayFactor") {
                logger.error("No speedDecayFactor defined")
                0.1
            }

        val nearbyRadius: Double = universeData3DAtPlayer.universeSettings.otherDoubleMap
            .getOrElse("nearbyRadius") {
                logger.error("No nearbyRadius defined")
                3.0
            }

        val densityNearbyRadius: Double = universeData3DAtPlayer.universeSettings.otherDoubleMap
            .getOrElse("densityNearbyRadius") {
                logger.error("No densityNearbyRadius defined")
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

        val flockSpeed: Double = computeFlockSpeed(
            minFlockSpeed = minFlockSpeed,
            maxFlockSpeed = maxFlockSpeed,
            speedDecayFactor = speedDecayFactor,
            densityNearbyRadius = densityNearbyRadius,
            universeData3DAtPlayer = universeData3DAtPlayer,
        )

        // sum of direction
        val directionSum: Velocity = universeData3DAtPlayer.getPlayerInSphere(nearbyRadius)
            .fold(Velocity(0.0, 0.0, 0.0)) { acc, playerData ->
                acc + playerData.velocity.scaleVelocity(1.0)
            }

        val scaledVelocity: Velocity = directionSum.scaleVelocity(flockSpeed)

        // Perturb the velocity by a random angle
        val targetVelocity: Velocity = scaledVelocity.randomRotate(maxAnglePerturbation, random)

        val maxDeltaRestMass: Double = universeData3DAtPlayer.getCurrentPlayerData()
            .playerInternalData.abmFlockingData().restMass * accelerationFuelFraction

        val abmFlockingChangeVelocityCommand = ABMFlockingChangeVelocityCommand(
            toId = universeData3DAtPlayer.id,
            targetVelocity = targetVelocity,
            maxDeltaRestMass = maxDeltaRestMass,
        )

        return listOf(abmFlockingChangeVelocityCommand)
    }

    /**
     * Compute density-dependent flock speed
     */
    private fun computeFlockSpeed(
        minFlockSpeed: Double,
        maxFlockSpeed: Double,
        speedDecayFactor: Double,
        densityNearbyRadius: Double,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
    ): Double {
        val numNeighbour: Int =
            universeData3DAtPlayer.getNeighbourInSphere(densityNearbyRadius).size

        return (maxFlockSpeed - minFlockSpeed) * exp(-numNeighbour * speedDecayFactor) +
                minFlockSpeed
    }
}