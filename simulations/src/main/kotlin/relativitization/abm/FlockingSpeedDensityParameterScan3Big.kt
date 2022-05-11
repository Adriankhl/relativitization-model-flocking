package relativitization.abm

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.describe
import org.jetbrains.kotlinx.dataframe.io.writeCSV
import relativitization.universe.maths.collection.DoubleRange
import relativitization.universe.maths.number.Notation
import java.io.File

fun main() {
    val dfList: MutableList<DataFrame<*>> = mutableListOf()

    // range of speed of light
    val speedRangeList: List<Double> = DoubleRange.computeList(
        from = 0.0,
        to = 0.8,
        step = 0.1,
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

    for (speedRange in speedRangeList) {
        for (maxAnglePerturbation in maxAnglePerturbationList) {
            for (randomSeed in randomSeedList) {
                println(
                    "Speed diff: $speedRange. " +
                            "Perturbation angle: $maxAnglePerturbation. " +
                            "Seed: $randomSeed"
                )
                dfList.add(
                    flockingSpeedDensitySingleRun(
                        numPlayer = 50,
                        speedOfLight = 1.0,
                        initialFlockSpeed = 0.9,
                        minFlockSpeed = Notation.roundDecimal(0.9 - speedRange, 1),
                        maxFlockSpeed = 0.9,
                        speedDecayFactor = 0.2,
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
    df.writeCSV("./data/flockingSpeedDensityParameterScan3Big.csv")
}
