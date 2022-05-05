package relativitization.universe.ai

import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.commands.Command
import relativitization.universe.utils.RelativitizationLogManager

object ABMFlockingTwoSpeedAI : AI() {
    private val logger = RelativitizationLogManager.getLogger()
    override fun compute(universeData3DAtPlayer: UniverseData3DAtPlayer): List<Command> {

        val maxFlockSpeed: Double = universeData3DAtPlayer.universeSettings.otherDoubleMap
            .getOrElse("maxFlockSpeed") {
                logger.error("No maxFlockSpeed defined")
                0.5
            }

        val minFlockSpeed: Double = universeData3DAtPlayer.universeSettings.otherDoubleMap
            .getOrElse("minFlockSpeed") {
                logger.error("No minFlockSpeed defined")
                0.5
            }

        val nearByRadius: Double = universeData3DAtPlayer.universeSettings.otherDoubleMap
            .getOrElse("nearByRadius") {
                logger.error("No nearByRadius defined")
                3.0
            }

        val accelerationFuelFraction: Double = universeData3DAtPlayer.universeSettings
            .otherDoubleMap.getOrElse("accelerationFuelFraction") {
                logger.error("No accelerationFuelFraction defined")
                1.0
            }

        return listOf()
    }
}