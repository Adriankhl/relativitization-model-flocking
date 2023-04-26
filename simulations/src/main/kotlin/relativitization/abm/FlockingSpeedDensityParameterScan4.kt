package relativitization.abm

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.describe
import org.jetbrains.kotlinx.dataframe.io.writeCSV
import relativitization.universe.core.maths.collection.DoubleRange
import java.io.File

fun main() {
    val dfList: MutableList<DataFrame<*>> = mutableListOf()

    // range of speed of light
    val speedDecayFactorList: List<Double> = DoubleRange.computeList(
        from = 0.0,
        to = 3.0,
        step = 0.5,
        decimalPlace = 1
    )

    // in radian
    val maxAnglePerturbationList: List<Double> = DoubleRange.computeList(
        from = 0.0,
        to = 2.0,
        step = 0.4,
        decimalPlace = 1
    )

    for (speedDecayFactor in speedDecayFactorList) {
        for (maxAnglePerturbation in maxAnglePerturbationList) {
            println("Speed decay: $speedDecayFactor. Perturbation angle: $maxAnglePerturbation. ")
            dfList.add(
                flockingSpeedDensitySingleRun(
                    numPlayer = 50,
                    speedOfLight = 1.0,
                    initialFlockSpeed = 0.9,
                    minFlockSpeed = 0.1,
                    maxFlockSpeed = 0.9,
                    speedDecayFactor = speedDecayFactor,
                    nearbyRadius = 3.0,
                    densityNearbyRadius = 1.0,
                    maxAnglePerturbation = maxAnglePerturbation,
                    accelerationFuelFraction = 1.0,
                    numStep = 1000,
                    randomSeed = 100L,
                    printStep = false
                )
            )
        }
    }

    val df = dfList.concat()

    println(df.describe())

    File("data").mkdirs()
    df.writeCSV("./data/flockingSpeedDensityParameterScan4Big.csv")
}
