package relativitization.abm

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import relativitization.universe.Universe
import relativitization.universe.ai.ABMFlockingDensitySpeedAI
import relativitization.universe.ai.name
import relativitization.universe.data.MutableUniverseSettings
import relativitization.universe.data.PlayerData
import relativitization.universe.data.commands.AllCommandAvailability
import relativitization.universe.data.commands.name
import relativitization.universe.data.components.abmFlockingData
import relativitization.universe.generate.GenerateSettings
import relativitization.universe.generate.GenerateUniverseMethodCollection
import relativitization.universe.generate.abm.ABMFlockingGenerate
import relativitization.universe.generate.name
import relativitization.universe.global.EmptyGlobalMechanismList
import relativitization.universe.global.name
import relativitization.universe.mechanisms.ABMFlockingMechanismLists
import relativitization.universe.mechanisms.name

internal fun flockingSpeedDensitySingleRun(
    numPlayer: Int,
    initialFlockSpeed: Double,
    minFlockSpeed: Double,
    maxFlockSpeed: Double,
    speedDecayFactor: Double,
    nearbyRadius: Double,
    densityNearbyRadius: Double,
    maxAnglePerturbation: Double,
    accelerationFuelFraction: Double,
    speedOfLight: Double,
    numStep: Int,
    printStep: Boolean = false,
): DataFrame<*> {
    val dfList: MutableList<DataFrame<*>> = mutableListOf()

    val generateSetting = GenerateSettings(
        generateMethod = ABMFlockingGenerate.name(),
        numPlayer = numPlayer,
        numHumanPlayer = 0,
        otherIntMap = mutableMapOf(),
        otherDoubleMap = mutableMapOf(
            "initialRestMass" to 1.0,
            "initialFlockSpeed" to initialFlockSpeed,
        ),
        otherStringMap = mutableMapOf(
            "aiName" to ABMFlockingDensitySpeedAI.name(),
        ),
        universeSettings = MutableUniverseSettings(
            universeName = "Flocking",
            commandCollectionName = AllCommandAvailability.name(),
            mechanismCollectionName = ABMFlockingMechanismLists.name(),
            globalMechanismCollectionName = EmptyGlobalMechanismList.name(),
            speedOfLight = speedOfLight,
            xDim = 10,
            yDim = 10,
            zDim = 10,
            otherDoubleMap = mutableMapOf(
                "minFlockSpeed" to minFlockSpeed,
                "maxFlockSpeed" to maxFlockSpeed,
                "speedDecayFactor" to speedDecayFactor,
                "nearbyRadius" to nearbyRadius,
                "densityNearbyRadius" to densityNearbyRadius,
                "maxAnglePerturbation" to maxAnglePerturbation,
                "accelerationFuelFraction" to accelerationFuelFraction,
            ),
        )
    )

    val universe = Universe(GenerateUniverseMethodCollection.generate(generateSetting))

    for (turn in 1..numStep) {
        val currentPlayerDataList: List<PlayerData> = universe.getCurrentPlayerDataList()

        val orderParameter: Double = computeOrderParameter(
            currentPlayerDataList.map { it.velocity },
        )

        val totalRestMass: Double = currentPlayerDataList.sumOf {
            it.playerInternalData.abmFlockingData().restMass
        }

        val averageDilatedTime: Double = computeAverageDilatedTime(
            currentPlayerDataList.map { it.velocity },
            speedOfLight,
        )

        dfList.add(
            dataFrameOf(
                "turn" to listOf(turn),
                "speedOfLight" to listOf(speedOfLight),
                "minFlockSpeed" to listOf(minFlockSpeed),
                "maxFlockSpeed" to listOf(maxFlockSpeed),
                "speedDecayFactor" to listOf(speedDecayFactor),
                "maxAnglePerturbation" to listOf(maxAnglePerturbation),
                "accelerationFuelFraction" to listOf(accelerationFuelFraction),
                "orderParameter" to listOf(orderParameter),
                "totalRestMass" to listOf(totalRestMass),
                "averageDilatedTime" to listOf(averageDilatedTime),
            )
        )

        if (printStep) {
            println("Turn: $turn. Order parameter: $orderParameter. Total rest mass: $totalRestMass. ")
        }

        universe.pureAIStep()
    }

    return dfList.concat()
}