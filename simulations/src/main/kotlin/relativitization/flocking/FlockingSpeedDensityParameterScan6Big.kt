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
    val densityNearbyRadiusList: List<Double> = DoubleRange.computeList(
        from = 0.0,
        to = 5.0,
        step = 0.2,
        decimalPlace = 1
    )

    // in radian
    val maxAnglePerturbationList: List<Double> = DoubleRange.computeList(
        from = 0.0,
        to = 3.0,
        step = 0.5,
        decimalPlace = 1
    )

    // range of speed of light
    val speedOfLightList: List<Double> = DoubleRange.computeList(
        from = 1.0,
        to = 3.0,
        step = 1.0,
        decimalPlace = 0
    )

    val randomSeedList: List<Long> = (100L..110L).toList()

    for (densityNearbyRadius in densityNearbyRadiusList) {
        for (maxAnglePerturbation in maxAnglePerturbationList) {
            for (speedOfLight in speedOfLightList) {
                for (randomSeed in randomSeedList) {
                    println(
                        "Density nearby radius: $densityNearbyRadius. " +
                                "Perturbation angle: $maxAnglePerturbation. " +
                                "Speed of light: $speedOfLight. " +
                                "Seed: $randomSeed. "
                    )
                    dfList.add(
                        flockingSpeedDensitySingleRun(
                            numPlayer = 50,
                            speedOfLight = speedOfLight,
                            initialFlockSpeed = 0.9,
                            minFlockSpeed = 0.1,
                            maxFlockSpeed = 0.9,
                            speedDecayFactor = 0.5,
                            nearbyRadius = 3.0,
                            densityNearbyRadius = densityNearbyRadius,
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
    }

    val df = dfList.concat()

    println(df.describe())

    File("data").mkdirs()
    df.writeCSV("./data/flockingSpeedDensityParameterScan6Big.csv")
}
