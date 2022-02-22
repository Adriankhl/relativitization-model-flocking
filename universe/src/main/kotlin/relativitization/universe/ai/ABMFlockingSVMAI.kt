package relativitization.universe.ai

import relativitization.universe.data.PlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.commands.ABMFlockingChangeVelocityCommand
import relativitization.universe.data.commands.Command
import relativitization.universe.maths.physics.Intervals
import relativitization.universe.maths.physics.Velocity
import relativitization.universe.utils.RelativitizationLogManager

object ABMFlockingSVMAI : AI() {
    private val logger = RelativitizationLogManager.getLogger()

    override fun compute(universeData3DAtPlayer: UniverseData3DAtPlayer): List<Command> {

        val flockSpeed: Double = universeData3DAtPlayer.universeSettings.otherDoubleMap.getOrElse(
            "flockSpeed"
        ) {
            logger.error("No flockSpeed defined")
            0.5
        }

        val nearByRadius: Double = universeData3DAtPlayer.universeSettings.otherDoubleMap.getOrElse(
            "nearByRadius"
        ) {
            logger.error("No nearByRadius defined")
            3.0
        }

        val maxAnglePerturbation: Double = universeData3DAtPlayer.universeSettings.otherDoubleMap.getOrElse(
            "maxAnglePerturbation"
        ) {
            logger.error("No maxAnglePerturbation defined")
            0.1
        }

        val selfDouble4D = universeData3DAtPlayer.getCurrentPlayerData().double4D

        val nearByPlayerDataList: List<PlayerData> = universeData3DAtPlayer.playerDataMap.values.filter {
            val otherDouble4D = it.double4D
            Intervals.distance(
                selfDouble4D,
                otherDouble4D
            ) < nearByRadius
        }

        val newVelocity: Velocity = nearByPlayerDataList.fold(
            Velocity(0.0, 0.0, 0.0)
        ) { acc, playerData ->
           acc + playerData.velocity
        }.scaleVelocity(flockSpeed)

        val newPerturbedVelocity: Velocity = newVelocity.randomRotate(maxAnglePerturbation)

        val abmFlockingChangeVelocityCommand = ABMFlockingChangeVelocityCommand(
            toId = universeData3DAtPlayer.id,
            fromId = universeData3DAtPlayer.id,
            fromInt4D = universeData3DAtPlayer.getCurrentPlayerData().int4D,
            targetVelocity = newPerturbedVelocity,
        )

        return listOf(abmFlockingChangeVelocityCommand)
    }
}