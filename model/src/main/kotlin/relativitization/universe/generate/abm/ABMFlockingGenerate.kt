package relativitization.universe.generate.abm

import relativitization.universe.ai.ABMFlockingAI
import relativitization.universe.core.data.MutablePlayerData
import relativitization.universe.core.data.MutableUniverseData4D
import relativitization.universe.core.data.PlayerType
import relativitization.universe.core.data.UniverseData
import relativitization.universe.core.data.UniverseSettings
import relativitization.universe.core.data.UniverseState
import relativitization.universe.core.data.global.MutableUniverseGlobalData
import relativitization.universe.core.data.serializer.DataSerializer
import relativitization.universe.core.generate.GenerateSettings
import relativitization.universe.core.generate.GenerateUniverseMethod
import relativitization.universe.core.maths.grid.Grids.create4DGrid
import relativitization.universe.core.maths.physics.MutableVelocity
import relativitization.universe.core.utils.RelativitizationLogManager
import relativitization.universe.data.components.MutableABMFlockingData
import kotlin.random.Random

object ABMFlockingGenerate : GenerateUniverseMethod() {
    private val logger = RelativitizationLogManager.getLogger()

    override fun generate(
        generateSettings: GenerateSettings,
        random: Random
    ): UniverseData {
        val universeSettings: UniverseSettings = DataSerializer.copy(
            generateSettings.universeSettings
        )

        val initialRestMass: Double = generateSettings.getOtherDoubleOrDefault(
            "initialRestMass",
            1.0
        )

        val initialFlockSpeed: Double = generateSettings.getOtherDoubleOrDefault(
            "initialFlockSpeed",
            0.5
        )

        val aiName: String = generateSettings.getOtherStringOrDefault(
            "aiName",
            ABMFlockingAI.name()
        )

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

        for (i in 1..generateSettings.numPlayer) {
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
            mutablePlayerData.velocity =
                MutableVelocity(vx, vy, vz).scaleVelocity(initialFlockSpeed)

            // Choose AI
            mutablePlayerData.playerInternalData.aiName = aiName

            data.addPlayerDataToLatestDuration(
                mutablePlayerData = mutablePlayerData,
                currentTime = universeState.getCurrentTime(),
                duration = universeSettings.playerAfterImageDuration,
                edgeLength = universeSettings.groupEdgeLength,
            )
        }

        return UniverseData(
            universeData4D = DataSerializer.copy(data),
            universeSettings = universeSettings,
            universeState = universeState,
            commandMap = mutableMapOf(),
            universeGlobalData = DataSerializer.copy(MutableUniverseGlobalData())
        )
    }
}