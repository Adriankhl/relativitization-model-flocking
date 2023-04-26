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
            universeGlobalData = UniverseGlobalData()
        )
    }
}