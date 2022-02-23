package relativitization.abm

import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.describe
import org.jetbrains.kotlinx.dataframe.api.drop
import org.jetbrains.kotlinx.dataframe.io.writeCSV
import relativitization.universe.maths.random.Rand
import java.io.File

fun main() {
    Rand.setSeed(100L)

    val initDf = dataFrameOf(
        "Step",
        "flockSpeed",
        "maxAnglePerturbation",
        "orderParameter",
        "totalRestMass",
    )(
        -1,
        0.5,
        0.5,
        0.0,
        0.0,
    ).drop(1)

    var df = initDf

    // range of speed of light
    val speedOfLightList: List<Double> = (0..8).map { 0.6 + 0.2 * it }

    // in radian
    val maxAnglePerturbationList: List<Double> = (0..15).map { 0.1 + 0.2 * it }

    for (speedOfLight in speedOfLightList) {
        for (maxAnglePerturbation in maxAnglePerturbationList) {
            println("Speed of light: $speedOfLight. Perturbation angle: $maxAnglePerturbation. ")
            df = df.concat(
                singleFlockingWithSupplyRun(
                    nearByRadius = 3.0,
                    flockSpeed = 0.3,
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
    df.writeCSV("./data/flockingWithSupplyParameterScan.csv")
}