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
    val speedList: List<Double> = DoubleRange.computeList(
        from = 0.1,
        to = 0.9,
        step = 0.2,
        decimalPlace = 1
    )

    // in radian
    val maxAnglePerturbationList: List<Double> = DoubleRange.computeList(
        from = 0.0,
        to = 3.0,
        step = 0.2,
        decimalPlace = 1
    )

    val randomSeedList: List<Long> = (100L..110L).toList()

    for (speed in speedList) {
        for (maxAnglePerturbation in maxAnglePerturbationList) {
            for (randomSeed in randomSeedList) {
                println(
                    "Speed: $speed. " +
                            "Perturbation angle: $maxAnglePerturbation. " +
                            "Seed: $randomSeed"
                )
                dfList.add(
                    flockingSpeedDensitySingleRun(
                        numPlayer = 50,
                        speedOfLight = 1.0,
                        initialFlockSpeed = speed,
                        minFlockSpeed = speed,
                        maxFlockSpeed = speed,
                        speedDecayFactor = 0.5,
                        nearbyRadius = 3.0,
                        densityNearbyRadius = 1.0,
                        maxAnglePerturbation = maxAnglePerturbation,
                        accelerationFuelFraction = 1.0,
                        numStep = 1000,
                        randomSeed = randomSeed,
                        printStep = false
                    )
                )
            }
        }
    }

    val df = dfList.concat()

    println(df.describe())

    File("data").mkdirs()
    df.writeCSV("./data/flockingSpeedDensityParameterScan7Big.csv")
}
