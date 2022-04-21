package relativitization.abm

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.describe
import org.jetbrains.kotlinx.dataframe.io.writeCSV
import relativitization.universe.maths.number.Notation
import relativitization.universe.maths.random.Rand
import java.io.File

fun main() {
    Rand.setSeed(100L)

    val dfList: MutableList<DataFrame<*>> = mutableListOf()

    // range of speed of light
    val speedOfLightList: List<Double> = (0..8).map {
        Notation.roundDecimal(0.1 + 0.1 * it, 1)
    }

    // in radian
    val maxAnglePerturbationList: List<Double> = (0..4).map {
        Notation.roundDecimal(0.8 + 0.2 * it, 1)
    }

    for (speedOfLight in speedOfLightList) {
        for (maxAnglePerturbation in maxAnglePerturbationList) {
            println("Speed of light: $speedOfLight. Perturbation angle: $maxAnglePerturbation. ")
            dfList.add(
                singleFlockingRun(
                    numPlayer = 200,
                    nearByRadius = 5.0,
                    flockSpeed = 0.05,
                    maxAnglePerturbation = maxAnglePerturbation,
                    accelerationFuelFraction = 1.0,
                    speedOfLight = speedOfLight,
                    numStep = 500,
                    printStep = true,
                )
            )
        }
    }

    val df = dfList.concat()

    println(df.describe())

    File("data").mkdirs()
    df.writeCSV("./data/flockingParameterScan3.csv")
}