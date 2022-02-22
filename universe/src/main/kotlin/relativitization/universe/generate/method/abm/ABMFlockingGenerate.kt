package relativitization.universe.generate.method.abm

import relativitization.universe.ai.ABMFlockingAI
import relativitization.universe.ai.name
import relativitization.universe.data.*
import relativitization.universe.data.components.MutableABMFlockingData
import relativitization.universe.data.components.MutablePlayerDataComponentMap
import relativitization.universe.maths.physics.MutableVelocity
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.data.serializer.DataSerializer
import relativitization.universe.generate.method.GenerateSettings
import relativitization.universe.maths.grid.Grids.create4DGrid
import relativitization.universe.maths.random.Rand
import relativitization.universe.utils.RelativitizationLogManager

object ABMFlockingGenerate : ABMGenerateUniverseMethod() {
    private val logger = RelativitizationLogManager.getLogger()

    override fun generate(settings: GenerateSettings): UniverseData {
        val universeSettings: UniverseSettings = DataSerializer.copy(settings.universeSettings)

        val coreRestMass: Double = settings.otherDoubleMap.getOrElse("coreRestMass") {
            logger.error("No coreRestMass defined")
            1.0
        }

        val initialFuelRestMass: Double = settings.otherDoubleMap.getOrElse("initialFuelRestMass") {
            logger.error("No initialFuelRestMass defined")
            1.0
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
            ) { _, _, _, _ -> mutableListOf() }
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
                    coreRestMass = coreRestMass,
                    fuelRestMass = initialFuelRestMass,
                )
            )

            mutablePlayerData.playerType = PlayerType.AI

            mutablePlayerData.int4D.x = Rand.rand().nextInt(0, universeSettings.xDim)
            mutablePlayerData.int4D.y = Rand.rand().nextInt(0, universeSettings.yDim)
            mutablePlayerData.int4D.z = Rand.rand().nextInt(0, universeSettings.zDim)


            val vx = Rand.rand().nextDouble(-1.0, 1.0)
            val vy = Rand.rand().nextDouble(-1.0, 1.0)
            val vz = Rand.rand().nextDouble(-1.0, 1.0)

            // Constant velocity 0.5
            mutablePlayerData.velocity = MutableVelocity(vx, vy, vz).scaleVelocity(0.5)

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