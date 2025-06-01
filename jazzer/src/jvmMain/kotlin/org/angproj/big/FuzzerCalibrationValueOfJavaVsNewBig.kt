package org.angproj.big

import com.code_intelligence.jazzer.Jazzer
import com.code_intelligence.jazzer.api.FuzzedDataProvider
import internalOf
import toByteArray
import java.math.BigInteger
import kotlin.test.assertContentEquals
import kotlin.time.Duration.Companion.minutes


public object FuzzerCalibrationValueOfJavaVsNewBigKt {
    @JvmStatic
    public fun fuzzerTestOneInput(data: FuzzedDataProvider) {
        val f1 = data.consumeBytes(64)
        //val f2 = BigInteger(data.consumeBytes(64)).toString()

        val r1 = try {
            internalOf(f1).let { toByteArray(it.mag, it.sigNum) }
        } catch (e: BigMathException) {
            byteArrayOf()
        }


        val r2 = try {
            BigInteger(f1).toByteArray()
        } catch (e: NumberFormatException) {
            byteArrayOf()
        } catch (e: ArithmeticException) {
            byteArrayOf()
        }

        assertContentEquals(r1, r2)
    }

    @JvmStatic
    public fun main(args: Array<String>) {
        Jazzer.main(arrayOf(
            "--target_class=${javaClass.name}",
            "-max_total_time=${2.minutes.inWholeSeconds}"
        ))
    }
}