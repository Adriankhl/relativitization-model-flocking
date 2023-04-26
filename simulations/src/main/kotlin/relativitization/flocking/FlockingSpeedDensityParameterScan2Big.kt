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
        to = 0.5,
        step = 0.05,
        decimalPlace = 2
    )

    // in radian
    val maxAnglePerturbationList: List<Double> = DoubleRange.computeList(
        from = 0.0,
        to = 2.0,
        step = 0.4,
        decimalPlace = 1
    )

    val randomSeedList: List<Long> = (100L..110L).toList()

    for (speedOfLight in speedOfLightList) {
        for (maxAnglePerturbation in maxAnglePerturbationList) {
            for (randomSeed in randomSeedList) {
                println(
                    "Speed of light: $speedOfLight. " +
                            "Perturbation angle: $maxAnglePerturbation. " +
                            "Seed: $randomSeed"
                )
                dfList.add(
                    flockingSpeedDensitySingleRun(
                        numPlayer = 50,
                        speedOfLight = speedOfLight,
                        initialFlockSpeed = 0.05,
                        minFlockSpeed = 0.05,
                        maxFlockSpeed = 0.05,
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
    df.writeCSV("./data/flockingSpeedDensityParameterScan2Big.csv")
}
