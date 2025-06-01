package org.angproj.big

import com.code_intelligence.jazzer.api.FuzzedDataProvider
import java.math.BigInteger
import kotlin.test.assertEquals
import org.academic.adams.BigInt2 as AdamsBigInt



public object FuzzerCalibrationValueOfJavaVsAdams: FuzzerHelper {
    @JvmStatic
    public fun fuzzerTestOneInput(data: FuzzedDataProvider) {
        val f1 = BigInteger(byteArrayOf(0) + data.consumeBytes(64)).toString()
        //val f2 = BigInteger(data.consumeBytes(64)).toString()

        val r1 = try {
            AdamsBigInt.valueOf(f1).toString()
        } catch (e: NumberFormatException) {
            ""
        } catch (e: ArithmeticException) {
            ""
        }


        val r2 = try {
            BigInteger(f1).toString()
        } catch (e: NumberFormatException) {
            ""
        } catch (e: ArithmeticException) {
            ""
        }

        assertEquals(r1, r2)
    }
}