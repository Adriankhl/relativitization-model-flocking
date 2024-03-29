package relativitization.flocking

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.describe
import org.jetbrains.kotlinx.dataframe.io.writeCSV
import relativitization.universe.core.maths.collection.DoubleRange
import relativitization.universe.flocking.FlockingInitializer
import java.io.File

fun main() {
    FlockingInitializer.initialize()

    val dfList: MutableList<DataFrame<*>> = mutableListOf()

    // range of speed of light
    val speedOfLightList: List<Double> = DoubleRange.computeList(
        from = 0.1,
        to = 1.0,
        step = 0.1,
        decimalPlace = 1
    )

    // in radian
    val maxAnglePerturbationList: List<Double> = DoubleRange.computeList(
        from = 0.1,
        to = 0.7,
        step = 0.2,
        decimalPlace = 1
    )

    for (speedOfLight in speedOfLightList) {
        for (maxAnglePerturbation in maxAnglePerturbationList) {
            println("Speed of light: $speedOfLight. Perturbation angle: $maxAnglePerturbation. ")
            dfList.add(
                flockingSingleRun(
                    numPlayer = 50,
                    speedOfLight = speedOfLight,
                    flockSpeed = 0.05,
                    nearbyRadius = 3.0,
                    maxAnglePerturbation = maxAnglePerturbation,
                    accelerationFuelFraction = 1.0,
                    numStep = 1000,
                    randomSeed = 100L,
                    printStep = false,
                )
            )
        }
    }

    val df = dfList.concat()

    println(df.describe())

    File("data").mkdirs()
    df.writeCSV("./data/flockingParameterScan2.csv")
}