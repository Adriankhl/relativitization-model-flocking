package relativitization.flocking

import ksergen.serializers.module.GeneratedModule
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.describe
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.io.writeCSV
import relativitization.universe.core.RelativitizationInitializer
import relativitization.universe.flocking.ai.ABMFlockingSVMAI
import relativitization.universe.core.Universe
import relativitization.universe.core.data.MutableUniverseSettings
import relativitization.universe.core.data.PlayerData
import relativitization.universe.core.data.commands.AllCommandAvailability
import relativitization.universe.core.generate.GenerateSettings
import relativitization.universe.core.generate.GenerateUniverseMethodCollection
import relativitization.universe.core.global.EmptyGlobalMechanismList
import relativitization.universe.core.maths.physics.Relativistic
import relativitization.universe.core.maths.physics.Velocity
import relativitization.universe.flocking.FlockingInitializer
import relativitization.universe.flocking.data.components.abmFlockingData
import relativitization.universe.flocking.generate.ABMFlockingGenerate
import relativitization.universe.flocking.mechanisms.ABMFlockingMechanismLists
import java.io.File

fun main() {
    FlockingInitializer.initialize()

    val df = flockingSingleRun(
        numPlayer = 50,
        speedOfLight = 1.0,
        flockSpeed = 0.5,
        nearbyRadius = 3.0,
        maxAnglePerturbation = 0.5,
        accelerationFuelFraction = 1.0,
        numStep = 1000,
        randomSeed = 100L,
        printStep = true,
    )

    println(df.describe())

    File("data").mkdirs()
    df.writeCSV("./data/flocking.csv")
}

internal fun flockingSingleRun(
    numPlayer: Int,
    speedOfLight: Double,
    flockSpeed: Double,
    nearbyRadius: Double,
    maxAnglePerturbation: Double,
    accelerationFuelFraction: Double,
    numStep: Int,
    randomSeed: Long,
    printStep: Boolean = false,
): DataFrame<*> {
    // This map will be converted to dataframe
    val dfMap: MutableMap<String, MutableList<Any>> = mutableMapOf()

    val generateSetting = GenerateSettings(
        generateMethod = ABMFlockingGenerate.name(),
        numPlayer = numPlayer,
        numHumanPlayer = 0,
        otherIntMap = mutableMapOf(),
        otherDoubleMap = mutableMapOf(
            "initialRestMass" to 1.0,
            "initialFlockSpeed" to flockSpeed,
        ),
        otherStringMap = mutableMapOf(
            "aiName" to ABMFlockingSVMAI.name(),
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
                "flockSpeed" to flockSpeed,
                "nearbyRadius" to nearbyRadius,
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

        val dilatedTimeList: List<Double> = currentPlayerDataList.map {
            Relativistic.dilatedTime(1.0, it.velocity, speedOfLight)
        }

        val dilatedTimeMean: Double = dilatedTimeList.sum() / dilatedTimeList.size

        val outputDataMap = mapOf(
            "randomSeed" to randomSeed,
            "turn" to turn,
            "speedOfLight" to speedOfLight,
            "flockSpeed" to flockSpeed,
            "maxAnglePerturbation" to maxAnglePerturbation,
            "accelerationFuelFraction" to accelerationFuelFraction,
            "orderParameter" to orderParameter,
            "restMassFractionMean" to restMassFractionMean,
            "dilatedTimeMean" to dilatedTimeMean,
        )

        outputDataMap.forEach {
            dfMap.getOrPut(it.key) {
                mutableListOf()
            }.add(it.value)
        }

        if (printStep) {
            println(
                "Turn: $turn. " +
                        "Order parameter: $orderParameter." +
                        "Rest mass fraction mean: $restMassFractionMean. "
            )
        }

        universe.pureAIStep()
    }

    return dfMap.toDataFrame()
}

internal fun computeOrderParameter(
    velocityList: List<Velocity>,
): Double {
    val totalVelocity: Velocity = velocityList.fold(Velocity(0.0, 0.0, 0.0)) { acc, velocity ->
        acc + velocity
    }

    val totalSpeed: Double = velocityList.fold(0.0) { acc, velocity ->
        acc + velocity.mag()
    }

    return if (totalSpeed > 0.0) {
        totalVelocity.mag() / totalSpeed
    } else {
        1.0
    }
}