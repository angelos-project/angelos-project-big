package org.angproj.big

import com.code_intelligence.jazzer.Jazzer
import com.code_intelligence.jazzer.api.FuzzedDataProvider
import java.math.BigInteger
import kotlin.test.assertContentEquals


public object FuzzerXorKt : FuzzPrefs() {

    @JvmStatic
    public fun fuzzerTestOneInput(data: FuzzedDataProvider) {
        val f1 = data.consumeBytes(64)
        val f2 = data.consumeBytes(64)

        val r1 = try {
            bigIntOf(f1).xor(bigIntOf(f2)).toByteArray()
        } catch (_: BigMathException) {
            byteArrayOf()
        }

        val r2 = try {
            BigInteger(f1).xor(BigInteger(f2)).toByteArray()
        } catch (_: NumberFormatException) {
            byteArrayOf()
        }

        assertContentEquals(r1, r2)
    }

    @JvmStatic
    public fun main(args: Array<String>) {
        Jazzer.main(arrayOf(
            "--target_class=${javaClass.name}",
            "-max_total_time=${maxTotalTime}"
        ))
    }
}