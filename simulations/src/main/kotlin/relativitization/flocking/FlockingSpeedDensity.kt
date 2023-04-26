package relativitization.flocking

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.describe
import org.jetbrains.kotlinx.dataframe.io.writeCSV
import relativitization.universe.flocking.ai.ABMFlockingDensitySpeedAI
import relativitization.universe.core.Universe
import relativitization.universe.core.data.MutableUniverseSettings
import relativitization.universe.core.data.PlayerData
import relativitization.universe.core.data.commands.AllCommandAvailability
import relativitization.universe.core.generate.GenerateSettings
import relativitization.universe.core.generate.GenerateUniverseMethodCollection
import relativitization.universe.core.global.EmptyGlobalMechanismList
import relativitization.universe.core.maths.physics.Relativistic
import relativitization.universe.flocking.data.components.abmFlockingData
import relativitization.universe.flocking.generate.ABMFlockingGenerate
import relativitization.universe.flocking.mechanisms.ABMFlockingMechanismLists
import java.io.File
import kotlin.math.pow

fun main() {
    val df = flockingSpeedDensitySingleRun(
        numPlayer = 50,
        speedOfLight = 1.0,
        initialFlockSpeed = 0.5,
        minFlockSpeed = 0.1,
        maxFlockSpeed = 0.9,
        speedDecayFactor = 0.5,
        nearbyRadius = 3.0,
        densityNearbyRadius = 1.0,
        maxAnglePerturbation = 0.5,
        accelerationFuelFraction = 1.0,
        numStep = 1000,
        randomSeed = 100L,
        printStep = true
    )

    println(df.describe())

    File("data").mkdirs()
    df.writeCSV("./data/flockingSpeedDensity.csv")
}

internal fun flockingSpeedDensitySingleRun(
    numPlayer: Int,
    speedOfLight: Double,
    initialFlockSpeed: Double,
    minFlockSpeed: Double,
    maxFlockSpeed: Double,
    speedDecayFactor: Double,
    nearbyRadius: Double,
    densityNearbyRadius: Double,
    maxAnglePerturbation: Double,
    accelerationFuelFraction: Double,
    numStep: Int,
    randomSeed: Long,
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
            randomSeed = randomSeed,
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

        val restMassFractionMean: Double = currentPlayerDataList.sumOf {
            it.playerInternalData.abmFlockingData().restMassFraction
        } / currentPlayerDataList.size

        val speedList: List<Double> = currentPlayerDataList.map {
            it.velocity.mag()
        }

        val speedMean: Double = speedList.sum() / speedList.size

        val speedVariance: Double = speedList.sumOf { (it - speedMean).pow(2) } / speedList.size

        val dilatedTimeList: List<Double> = currentPlayerDataList.map {
            Relativistic.dilatedTime(1.0, it.velocity, speedOfLight)
        }

        val dilatedTimeMean: Double = dilatedTimeList.sum() / dilatedTimeList.size

        dfList.add(
            dataFrameOf(
                "randomSeed" to listOf(randomSeed),
                "turn" to listOf(turn),
                "speedOfLight" to listOf(speedOfLight),
                "minFlockSpeed" to listOf(minFlockSpeed),
                "maxFlockSpeed" to listOf(maxFlockSpeed),
                "speedDecayFactor" to listOf(speedDecayFactor),
                "nearbyRadius" to listOf(nearbyRadius),
                "densityNearbyRadius" to listOf(densityNearbyRadius),
                "maxAnglePerturbation" to listOf(maxAnglePerturbation),
                "accelerationFuelFraction" to listOf(accelerationFuelFraction),
                "orderParameter" to listOf(orderParameter),
                "restMassFractionMean" to listOf(restMassFractionMean),
                "dilatedTimeMean" to listOf(dilatedTimeMean),
                "speedMean" to listOf(speedMean),
                "speedVariance" to listOf(speedVariance),
            )
        )

        if (printStep) {
            println("Turn: $turn. " +
                    "Order parameter: $orderParameter." +
                    "Rest mass fraction mean: $restMassFractionMean. " +
                    "Speed mean: $speedMean. "
            )
        }

        universe.pureAIStep()
    }

    return dfList.concat()
}