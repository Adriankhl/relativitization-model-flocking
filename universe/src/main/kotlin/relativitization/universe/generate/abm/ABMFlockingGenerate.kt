package relativitization.universe.generate.abm

import relativitization.universe.ai.ABMFlockingAI
import relativitization.universe.data.*
import relativitization.universe.data.components.MutableABMFlockingData
import relativitization.universe.maths.physics.MutableVelocity
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.data.serializer.DataSerializer
import relativitization.universe.generate.GenerateSettings
import relativitization.universe.maths.grid.Grids.create4DGrid
import relativitization.universe.utils.RelativitizationLogManager
import kotlin.random.Random

object ABMFlockingGenerate : ABMGenerateUniverseMethod() {
    private val logger = RelativitizationLogManager.getLogger()

    override fun generate(settings: GenerateSettings, random: Random): UniverseData {
        val universeSettings: UniverseSettings = DataSerializer.copy(settings.universeSettings)

        val initialRestMass: Double = settings.otherDoubleMap.getOrElse("initialRestMass") {
            logger.error("No initialRestMass defined")
            1.0
        }

        val initialFlockSpeed: Double = settings.otherDoubleMap.getOrElse(
            "initialFlockSpeed"
        ) {
            logger.error("No initialFlockSpeed defined")
            0.5
        }

        val aiName: String = settings.otherStringMap.getOrElse("aiName") {
            logger.error("No aiName defined")
            ABMFlockingAI.name()
        }


        val data = MutableUniverseData4D(
            create4DGrid(
                universeSettings.tDim,
                universeSettings.xDim,
                universeSettings.yDim,
                universeSettings.zDim
            ) { _, _, _, _ -> mutableMapOf() }
        )

        val universeState = UniverseState(
            currentTime = universeSettings.tDim - 1,
            maxPlayerId = 0,
        )

        for (i in 1..settings.numPlayer) {
            val playerId: Int = universeState.getNewPlayerId()

            val mutablePlayerData = MutablePlayerData(playerId)

            mutablePlayerData.playerInternalData.playerDataComponentMap.put(
                MutableABMFlockingData(
                    restMass = initialRestMass,
                )
            )

            mutablePlayerData.playerType = PlayerType.AI

            mutablePlayerData.int4D.x = random.nextInt(0, universeSettings.xDim)
            mutablePlayerData.int4D.y = random.nextInt(0, universeSettings.yDim)
            mutablePlayerData.int4D.z = random.nextInt(0, universeSettings.zDim)


            val vx = random.nextDouble(-1.0, 1.0)
            val vy = random.nextDouble(-1.0, 1.0)
            val vz = random.nextDouble(-1.0, 1.0)

            // Constant velocity 0.5
            mutablePlayerData.velocity = MutableVelocity(vx, vy, vz).scaleVelocity(initialFlockSpeed)

            // Choose AI
            mutablePlayerData.playerInternalData.aiName = aiName

            data.addPlayerDataToLatestWithAfterImage(
                mutablePlayerData,
                universeState.getCurrentTime(),
                universeSettings.groupEdgeLength,
                universeSettings.playerAfterImageDuration
            )
        }

        return UniverseData(
            universeData4D = DataSerializer.copy(data),
            universeSettings = universeSettings,
            universeState = universeState,
            commandMap = mutableMapOf(),
            universeGlobalData = UniverseGlobalData()
        )
    }
}