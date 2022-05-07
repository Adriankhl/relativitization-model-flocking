package relativitization.abm

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.io.writeCSV
import relativitization.universe.Universe
import relativitization.universe.ai.ABMFlockingSVMAI
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
import relativitization.universe.maths.physics.Relativistic
import relativitization.universe.maths.physics.Velocity
import relativitization.universe.maths.random.Rand
import relativitization.universe.mechanisms.ABMFlockingMechanismLists
import relativitization.universe.mechanisms.name
import java.io.File

fun main() {
    Rand.setSeed(100L)

    val df = singleFlockingRun(
        numPlayer = 50,
        nearByRadius = 3.0,
        flockSpeed = 0.5,
        maxAnglePerturbation = 0.5,
        accelerationFuelFraction = 1.0,
        speedOfLight = 1.0,
        numStep = 1000,
        printStep = true,
    )

    println(df.describe())

    File("data").mkdirs()
    df.writeCSV("./data/flocking.csv")
}

internal fun singleFlockingRun(
    numPlayer: Int,
    nearByRadius: Double,
    flockSpeed: Double,
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
            otherDoubleMap = mutableMapOf(
                "flockSpeed" to flockSpeed,
                "nearByRadius" to nearByRadius,
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
                "flockSpeed" to listOf(flockSpeed),
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

internal fun computeAverageDilatedTime(
    velocityList: List<Velocity>,
    speedOfLight: Double
): Double {
    val totalDilatedTime: Double = velocityList.fold(0.0) { acc, velocity ->
        acc + Relativistic.dilatedTime(1.0, velocity, speedOfLight)
    }
    return totalDilatedTime / velocityList.size
}