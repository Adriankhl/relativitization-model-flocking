package relativitization.abm

import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.describe
import org.jetbrains.kotlinx.dataframe.api.drop
import org.jetbrains.kotlinx.dataframe.io.writeCSV
import relativitization.universe.maths.number.Notation
import relativitization.universe.maths.random.Rand
import java.io.File

fun main() {
    Rand.setSeed(100L)

    val initDf = dataFrameOf(
        "Step",
        "speedOfLight",
        "flockSpeed",
        "maxAnglePerturbation",
        "orderParameter",
        "totalRestMass",
    )(
        -1,
        1.0,
        0.5,
        0.5,
        0.0,
        0.0,
    ).drop(1)

    var df = initDf

    // range of speed of light
    val speedOfLightList: List<Double> = (0..19).map { Notation.roundDecimal(0.1 + 0.1 * it, 1) }

    // in radian
    val maxAnglePerturbationList: List<Double> = (0..3).map { Notation.roundDecimal(0.1 + 0.2 * it, 1) }

    for (speedOfLight in speedOfLightList) {
        for (maxAnglePerturbation in maxAnglePerturbationList) {
            println("Speed of light: $speedOfLight. Perturbation angle: $maxAnglePerturbation. ")
            df = df.concat(
                singleFlockingWithSupplyRun(
                    numPlayer = 50,
                    nearByRadius = 3.0,
                    flockSpeed = 0.05,
                    maxAnglePerturbation = maxAnglePerturbation,
                    speedOfLight = speedOfLight,
                    numStep = 1000,
                    initDataFrame = initDf,
                    printStep = false,
                )
            )
        }
    }

    println(df.describe())

    File("data").mkdirs()
    df.writeCSV("./data/flockingWithSupplyParameterScan2.csv")
}