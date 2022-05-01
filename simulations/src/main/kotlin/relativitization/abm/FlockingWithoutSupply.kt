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
import relativitization.universe.generate.method.GenerateSettings
import relativitization.universe.generate.method.GenerateUniverseMethodCollection
import relativitization.universe.generate.method.abm.ABMFlockingGenerate
import relativitization.universe.generate.method.name
import relativitization.universe.global.EmptyGlobalMechanismList
import relativitization.universe.global.name
import relativitization.universe.maths.random.Rand
import relativitization.universe.mechanisms.EmptyMechanismLists
import relativitization.universe.mechanisms.name
import java.io.File

/*
fun main() {
    Rand.setSeed(100L)

    val initDf = dataFrameOf(
        "Step",
        "speedOfLight",
        "flockSpeed",
        "maxAnglePerturbation",
        "accelerationFuelFraction",
        "orderParameter",
        "totalRestMass"
    )(
        -1,
        1.0,
        0.5,
        0.5,
        0.0,
        0.0,
        0.0
    ).drop(1)

    var df = initDf

    df = df.concat(
        singleFlockingWithoutSupplyRun(
            nearByRadius = 3.0,
            flockSpeed = 0.5,
            maxAnglePerturbation = 0.5,
            accelerationFuelFraction = 1.0,
            speedOfLight = 1.0,
            numStep = 1000,
            initDataFrame = initDf,
            printStep = true,
        )
    )

    println(df.describe())

    File("data").mkdirs()
    df.writeCSV("./data/flockingWithoutSupply.csv")
}

internal fun singleFlockingWithoutSupplyRun(
    nearByRadius: Double,
    flockSpeed: Double,
    maxAnglePerturbation: Double,
    accelerationFuelFraction: Double,
    speedOfLight: Double,
    numStep: Int,
    initDataFrame: DataFrame<*>,
    printStep: Boolean = false,
): DataFrame<*> {
    var df = initDataFrame

    val generateSetting = GenerateSettings(
        generateMethod = ABMFlockingGenerate.name(),
        numPlayer = 50,
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
            mechanismCollectionName = EmptyMechanismLists.name(),
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
            flockSpeed,
        )

        val totalRestMass: Double = currentPlayerDataList.sumOf {
            it.playerInternalData.abmFlockingData().restMass
        }

        df = df.append(
            turn,
            speedOfLight,
            flockSpeed,
            maxAnglePerturbation,
            accelerationFuelFraction,
            orderParameter,
            totalRestMass
        )

        if (printStep) {
            println("Turn: $turn. Order parameter: $orderParameter. Total rest mass: $totalRestMass. ")
        }

        universe.pureAIStep()
    }

    return df
}*/