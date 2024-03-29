package relativitization.universe.flocking.ai

import relativitization.universe.core.ai.AI
import relativitization.universe.core.data.PlayerData
import relativitization.universe.core.data.UniverseData3DAtPlayer
import relativitization.universe.core.data.commands.Command
import relativitization.universe.core.maths.physics.Double3D
import relativitization.universe.core.maths.physics.Intervals.distance
import relativitization.universe.core.maths.physics.Velocity
import relativitization.universe.core.utils.RelativitizationLogManager
import relativitization.universe.flocking.data.commands.ABMFlockingChangeVelocityCommand
import relativitization.universe.flocking.data.components.abmFlockingData
import kotlin.random.Random

object ABMFlockingAI : AI() {

    private val logger = RelativitizationLogManager.getLogger()

    override fun compute(
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        random: Random,
    ): List<Command> {
        logger.debug("Computing with FlockingAI")

        val nearbyRadius: Double = universeData3DAtPlayer.universeSettings.getOtherDoubleOrDefault(
            "nearbyRadius",
            2.0
        )

        val desiredSeparation: Double = universeData3DAtPlayer.universeSettings
            .getOtherDoubleOrDefault(
                "desiredSeparation",
                0.5
            )

        val flockSpeed: Double = universeData3DAtPlayer.universeSettings.getOtherDoubleOrDefault(
            "flockSpeed",
            0.5
        )

        val cohesionDouble3D = cohesion(universeData3DAtPlayer, nearbyRadius)

        val alignmentDouble3D: Double3D = alignment(universeData3DAtPlayer, nearbyRadius)

        val separationDouble3D: Double3D = separation(universeData3DAtPlayer, desiredSeparation)

        val avoidBoundaryDouble3D: Double3D = avoidBoundary(universeData3DAtPlayer)

        val weightedDouble3D = cohesionDouble3D * 1.0 +
                alignmentDouble3D * 1.0 +
                separationDouble3D * 2.0 +
                avoidBoundaryDouble3D * 10.0

        // Constant velocity 0.5
        val targetVelocity: Velocity = Velocity(
            weightedDouble3D.x,
            weightedDouble3D.y,
            weightedDouble3D.z
        ).scaleVelocity(flockSpeed)

        val abmFlockingChangeVelocityCommand = ABMFlockingChangeVelocityCommand(
            toId = universeData3DAtPlayer.id,
            targetVelocity = targetVelocity,
            maxDeltaRestMass = universeData3DAtPlayer.getCurrentPlayerData().playerInternalData
                .abmFlockingData().restMass,
        )

        return listOf(abmFlockingChangeVelocityCommand)
    }

    private fun cohesion(universeData3DAtPlayer: UniverseData3DAtPlayer, radius: Double): Double3D {
        val selfDouble4D = universeData3DAtPlayer.getCurrentPlayerData().double4D
        val nearbyPlayerData: List<PlayerData> = universeData3DAtPlayer.getNeighbourInSphere(radius)

        return if (nearbyPlayerData.isEmpty()) {
            Double3D(0.0, 0.0, 0.0)
        } else {
            val avgX: Double = nearbyPlayerData.fold(0.0) { acc, playerData ->
                acc + playerData.double4D.x
            } / nearbyPlayerData.size.toDouble()

            val avgY: Double = nearbyPlayerData.fold(0.0) { acc, playerData ->
                acc + playerData.double4D.y
            } / nearbyPlayerData.size.toDouble()

            val avgZ: Double = nearbyPlayerData.fold(0.0) { acc, playerData ->
                acc + playerData.double4D.z
            } / nearbyPlayerData.size.toDouble()

            Double3D(avgX - selfDouble4D.x, avgY - selfDouble4D.y, avgZ - selfDouble4D.z)
        }
    }

    private fun alignment(
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        radius: Double
    ): Double3D {
        val nearbyPlayerData: List<PlayerData> = universeData3DAtPlayer.getNeighbourInSphere(radius)

        return if (nearbyPlayerData.isEmpty()) {
            Double3D(0.0, 0.0, 0.0)
        } else {
            val avgX: Double = nearbyPlayerData.fold(0.0) { acc, playerData ->
                acc + playerData.velocity.vx
            } / nearbyPlayerData.size.toDouble()

            val avgY: Double = nearbyPlayerData.fold(0.0) { acc, playerData ->
                acc + playerData.velocity.vy
            } / nearbyPlayerData.size.toDouble()

            val avgZ: Double = nearbyPlayerData.fold(0.0) { acc, playerData ->
                acc + playerData.velocity.vz
            } / nearbyPlayerData.size.toDouble()

            Double3D(avgX, avgY, avgZ)
        }
    }

    private fun separation(
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        desiredSeparation: Double
    ): Double3D {
        val selfDouble4D = universeData3DAtPlayer.getCurrentPlayerData().double4D
        val nearbyPlayerData: List<PlayerData> =
            universeData3DAtPlayer.playerDataMap.values.filter {
                val otherDouble4D = it.double4D
                val distance = distance(selfDouble4D, otherDouble4D)
                (distance < desiredSeparation) && (distance > 0.0) && (it.playerId != universeData3DAtPlayer.getCurrentPlayerData().playerId)
            }

        return if (nearbyPlayerData.isEmpty()) {
            Double3D(0.0, 0.0, 0.0)
        } else {
            val avgX: Double = nearbyPlayerData.fold(0.0) { acc, playerData ->
                val otherDouble4D = playerData.double4D
                val distance = distance(selfDouble4D, otherDouble4D)
                val double3D = Double3D(
                    selfDouble4D.x - otherDouble4D.x,
                    selfDouble4D.y - otherDouble4D.y,
                    selfDouble4D.z - otherDouble4D.z
                )

                if (distance > 0.0) {
                    acc + double3D.normalize().x / distance
                } else {
                    acc + 100.0
                }
            } / nearbyPlayerData.size.toDouble()

            val avgY: Double = nearbyPlayerData.fold(0.0) { acc, playerData ->
                val otherDouble4D = playerData.double4D
                val distance = distance(selfDouble4D, otherDouble4D)
                val double3D = Double3D(
                    selfDouble4D.x - otherDouble4D.x,
                    selfDouble4D.y - otherDouble4D.y,
                    selfDouble4D.z - otherDouble4D.z
                )

                if (distance > 0.0) {
                    acc + double3D.normalize().y / distance
                } else {
                    acc + 100.0
                }
            } / nearbyPlayerData.size.toDouble()

            val avgZ: Double = nearbyPlayerData.fold(0.0) { acc, playerData ->
                val otherDouble4D = playerData.double4D
                val distance = distance(selfDouble4D, otherDouble4D)
                val double3D = Double3D(
                    selfDouble4D.x - otherDouble4D.x,
                    selfDouble4D.y - otherDouble4D.y,
                    selfDouble4D.z - otherDouble4D.z
                )

                if (distance > 0.0) {
                    acc + double3D.normalize().z / distance
                } else {
                    acc + 100.0
                }
            } / nearbyPlayerData.size.toDouble()

            Double3D(avgX, avgY, avgZ)
        }
    }

    private fun avoidBoundary(universeData3DAtPlayer: UniverseData3DAtPlayer): Double3D {
        val selfDouble4D = universeData3DAtPlayer.getCurrentPlayerData().double4D

        val xComp = when {
            selfDouble4D.x < 0.1 -> {
                1.0
            }

            universeData3DAtPlayer.universeSettings.xDim.toDouble() - selfDouble4D.x < 0.1 -> {
                -1.0
            }

            else -> {
                0.0
            }
        }

        val yComp = when {
            selfDouble4D.y < 0.1 -> {
                1.0
            }

            universeData3DAtPlayer.universeSettings.yDim.toDouble() - selfDouble4D.y < 0.1 -> {
                -1.0
            }

            else -> {
                0.0
            }
        }


        val zComp = when {
            selfDouble4D.z < 0.1 -> {
                1.0
            }

            universeData3DAtPlayer.universeSettings.zDim.toDouble() - selfDouble4D.z < 0.1 -> {
                -1.0
            }

            else -> {
                0.0
            }
        }

        return Double3D(xComp, yComp, zComp)
    }
}