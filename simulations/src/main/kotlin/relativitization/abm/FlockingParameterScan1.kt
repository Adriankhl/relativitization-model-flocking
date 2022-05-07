package relativitization.abm

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.describe
import org.jetbrains.kotlinx.dataframe.io.writeCSV
import relativitization.universe.maths.collection.DoubleRange
import relativitization.universe.maths.random.Rand
import java.io.File

fun main() {
    Rand.setSeed(100L)

    val dfList: MutableList<DataFrame<*>> = mutableListOf()

    // range of speed of light
    val speedOfLightList: List<Double> = DoubleRange.computeList(
        from = 0.6,
        to = 1.4,
        step = 0.2,
        decimalPlace = 1
    )

    // in radian
    val maxAnglePerturbationList: List<Double> = DoubleRange.computeList(
        from = 0.1,
        to = 3.0,
        step = 0.2,
        decimalPlace = 1
    )

    for (speedOfLight in speedOfLightList) {
        for (maxAnglePerturbation in maxAnglePerturbationList) {
            println("Speed of light: $speedOfLight. Perturbation angle: $maxAnglePerturbation. ")
            dfList.add(
                flockingSingleRun(
                    numPlayer = 50,
                    nearbyRadius = 3.0,
                    flockSpeed = 0.3,
                    maxAnglePerturbation = maxAnglePerturbation,
                    accelerationFuelFraction = 1.0,
                    speedOfLight = speedOfLight,
                    numStep = 1000,
                    printStep = false,
                )
            )
        }
    }

    val df = dfList.concat()

    println(df.describe())

    File("data").mkdirs()
    df.writeCSV("./data/flockingParameterScan1.csv")
}
