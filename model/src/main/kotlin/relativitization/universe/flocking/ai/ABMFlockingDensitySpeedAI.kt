package relativitization.universe.flocking.ai

import relativitization.universe.core.ai.AI
import relativitization.universe.core.data.UniverseData3DAtPlayer
import relativitization.universe.core.data.commands.Command
import relativitization.universe.core.maths.physics.Velocity
import relativitization.universe.core.utils.RelativitizationLogManager
import relativitization.universe.flocking.data.commands.ABMFlockingChangeVelocityCommand
import relativitization.universe.flocking.data.components.abmFlockingData
import kotlin.math.exp
import kotlin.random.Random

object ABMFlockingDensitySpeedAI : AI() {
    private val logger = RelativitizationLogManager.getLogger()

    override fun compute(
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        random: Random,
    ): List<Command> {

        val minFlockSpeed: Double = universeData3DAtPlayer.universeSettings.getOtherDoubleOrDefault(
            "minFlockSpeed",
            0.1
        )

        val maxFlockSpeed: Double = universeData3DAtPlayer.universeSettings.getOtherDoubleOrDefault(
            "maxFlockSpeed",
            0.9
        )

        val speedDecayFactor: Double = universeData3DAtPlayer.universeSettings
            .getOtherDoubleOrDefault(
                "speedDecayFactor",
                0.1
            )

        val nearbyRadius: Double = universeData3DAtPlayer.universeSettings.getOtherDoubleOrDefault(
            "nearbyRadius",
            3.0
        )

        val densityNearbyRadius: Double = universeData3DAtPlayer.universeSettings
            .getOtherDoubleOrDefault(
                "densityNearbyRadius",
                3.0
            )

        val maxAnglePerturbation: Double = universeData3DAtPlayer.universeSettings.otherDoubleMap
            .getOrElse("maxAnglePerturbation") {
                logger.error("No maxAnglePerturbation defined")
                0.1
            }

        val accelerationFuelFraction: Double = universeData3DAtPlayer.universeSettings
            .getOtherDoubleOrDefault(
                "accelerationFuelFraction",
                1.0
            )

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