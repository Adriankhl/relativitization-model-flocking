package relativitization.abm

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.io.writeCSV
import relativitization.universe.Universe
import relativitization.universe.ai.ABMFlockingSVMAI
import relativitization.universe.data.MutableUniverseSettings
import relativitization.universe.data.PlayerData
import relativitization.universe.data.commands.AllCommandAvailability
import relativitization.universe.data.components.abmFlockingData
import relativitization.universe.generate.GenerateSettings
import relativitization.universe.generate.GenerateUniverseMethodCollection
import relativitization.universe.generate.abm.ABMFlockingGenerate
import relativitization.universe.global.EmptyGlobalMechanismList
import relativitization.universe.maths.physics.Relativistic
import relativitization.universe.maths.physics.Velocity
import relativitization.universe.mechanisms.ABMFlockingMechanismLists
import java.io.File

fun main() {
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
    val dfList: MutableList<DataFrame<*>> = mutableListOf()

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

        dfList.add(
            dataFrameOf(
                "randomSeed" to listOf(randomSeed),
                "turn" to listOf(turn),
                "speedOfLight" to listOf(speedOfLight),
                "flockSpeed" to listOf(flockSpeed),
                "maxAnglePerturbation" to listOf(maxAnglePerturbation),
                "accelerationFuelFraction" to listOf(accelerationFuelFraction),
                "orderParameter" to listOf(orderParameter),
                "restMassFractionMean" to listOf(restMassFractionMean),
                "dilatedTimeMean" to listOf(dilatedTimeMean),
            )
        )

        if (printStep) {
            println("Turn: $turn. " +
                    "Order parameter: $orderParameter." +
                    "Rest mass fraction mean: $restMassFractionMean. ")
        }

        universe.pureAIStep()
    }

    return dfList.concat()
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