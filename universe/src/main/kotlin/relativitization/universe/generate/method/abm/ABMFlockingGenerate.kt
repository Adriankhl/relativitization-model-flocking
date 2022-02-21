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

object ABMFlockingGenerate : ABMGenerateUniverseMethod() {
    override fun generate(settings: GenerateSettings): UniverseData {
        val universeSettings: UniverseSettings = DataSerializer.copy(settings.universeSettings)

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

            val mutablePlayerDataComponentMap = MutablePlayerDataComponentMap()

            mutablePlayerDataComponentMap.put(
                MutableABMFlockingData(
                    coreMass = 1.0,
                    fuelRestMass = 100.0
                )
            )

            val mutablePlayerInternalData = MutablePlayerInternalData(
                directLeaderId = playerId,
                playerDataComponentMap = mutablePlayerDataComponentMap
            )

            val mutablePlayerData = MutablePlayerData(
                playerId = playerId,
                playerInternalData = mutablePlayerInternalData
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

            // Use flocking ai
            mutablePlayerData.playerInternalData.aiName = ABMFlockingAI.name()

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