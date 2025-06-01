package org.angproj.big

import com.code_intelligence.jazzer.api.FuzzedDataProvider
import internalOf
import toByteArray
import java.math.BigInteger
import kotlin.test.assertContentEquals



public object FuzzerCalibrationValueOfJavaVsNewBig: FuzzerHelper {
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
}