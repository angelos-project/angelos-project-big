package org.angproj.big

import com.code_intelligence.jazzer.Jazzer
import com.code_intelligence.jazzer.api.FuzzedDataProvider
import java.math.BigInteger
import kotlin.test.assertContentEquals


public object FuzzerSqrtKt : FuzzPrefs() {
    @JvmStatic
    public fun fuzzerTestOneInput(data: FuzzedDataProvider) {
        val f1 = data.consumeBytes(64)

        val r1 = try {
            bigIntOf(f1).sqrtAndRemainder().let {
                Pair(it.first.toByteArray(), it.second.toByteArray()) }
        } catch (_: BigMathException) {
            Pair(byteArrayOf(), byteArrayOf())
        }

        val r2 = try {
            BigInteger(f1).sqrtAndRemainder().let {
                Pair(it[0].toByteArray(), it[1].toByteArray()) }
        } catch (_: NumberFormatException) {
            Pair(byteArrayOf(), byteArrayOf())
        } catch (_: ArithmeticException) {
            Pair(byteArrayOf(), byteArrayOf())
        }

        assertContentEquals(r1.first, r2.first)
        assertContentEquals(r1.second, r2.second)
    }

    @JvmStatic
    public fun main(args: Array<String>) {
        Jazzer.main(arrayOf(
            "--target_class=${javaClass.name}",
            "-max_total_time=${maxTotalTime}"
        ))
    }
}