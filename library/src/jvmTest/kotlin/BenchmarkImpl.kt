package org.angproj.big

import org.angproj.aux.io.binOf
import org.angproj.aux.io.securelyRandomize
import org.angproj.aux.sec.SecureRandom
import java.math.BigInteger
import kotlin.math.absoluteValue
import kotlin.time.Duration
import kotlin.time.measureTime
import kotlinx.coroutines.*
import org.angproj.aux.io.toByteArray
import org.angproj.big.*
import org.angproj.big.BigInt.Companion.bigIntOf


object DataGenerator {

    private val byteArrayCache = mutableMapOf<Int, ByteArray>()

    private fun getRandomizedArray(size: Int): ByteArray {
        require(size in 0..256)
        if(size !in byteArrayCache) byteArrayCache[size] = binOf(size).let {
            it.securelyRandomize()
            it.toByteArray()
        }
        return byteArrayCache[size] ?: error("Shouldn't happen!")
    }

    fun emptyCache(): Unit = byteArrayCache.clear()

    fun generateDataRange(from: Int, to: Int, action: (Int, ByteArray) -> Unit) {
        (from until to).forEach { if(it != 0) action(it, getRandomizedArray(it.absoluteValue)) }
    }

    fun generateIntRange(from: Int, to: Int, action: (Int) -> Unit) { (from until to).forEach { action(it) } }

    fun generateDataRangeBigInt(from: Int, to: Int, action: (Int, BigInt) -> Unit): Unit = withLogic {
        generateDataRange(from, to) { size, bytes ->
            val big = bigIntOf(bytes).abs()
            action(size, if (size < 0) big.negate() else big)
        }
    }

    fun innerGenerateDataRangeBigInt(
        from: Int, to: Int, action: (Int, BigInt) -> Unit
    ) = generateDataRangeBigInt(from, to, action)

    fun generateDataRangeBigInteger(from: Int, to: Int, action: (Int, BigInteger) -> Unit) {
        generateDataRange(from, to) { size, bytes ->
            val big = BigInteger(bytes).abs()
            action(size, if(size < 0) big.negate() else big)
        }
    }

    fun innerGenerateDataRangeBigInteger(
        from: Int, to: Int, action: (Int, BigInteger) -> Unit
    ) = generateDataRangeBigInteger(from, to, action)
}


object TestRig {

    fun openMatrixBigInt(
        r1: IntRange, r2: IntRange, riggedExperiment: (BigInt, BigInt) -> Unit
    ): Pair<Int, Duration> {
        var cnt = 0
        var time = measureTime {  }
        runBlocking {
            launch {
                DataGenerator.generateDataRangeBigInt(r1.first, r1.last) { _, xb ->
                    DataGenerator.innerGenerateDataRangeBigInt(r2.first, r2.last) { _, yb ->
                        cnt++
                        launch { time += measureTime { riggedExperiment(xb, yb) } }
                    }
                }
            }.join()
        }
        return Pair(cnt, time)
    }

    fun openMatrixBigInteger(
        r1: IntRange, r2: IntRange, riggedExperiment: (BigInteger, BigInteger) -> Unit
    ): Pair<Int, Duration> {
        var cnt = 0
        var time = measureTime {  }
        runBlocking {
            launch {
                DataGenerator.generateDataRangeBigInteger(r1.first, r1.last) { _, xb ->
                    DataGenerator.innerGenerateDataRangeBigInteger(r2.first, r2.last) { _, yb ->
                        cnt++
                        launch { time += measureTime { riggedExperiment(xb, yb) } }
                    }
                }
            }.join()
        }
        return Pair(cnt, time)
    }

    fun openVectorBigInt(
        r1: IntRange, riggedExperiment: (BigInt) -> Unit
    ): Pair<Int, Duration> {
        var cnt = 0
        var time = measureTime {  }
        runBlocking {
            launch {
                DataGenerator.generateDataRangeBigInt(r1.first, r1.last) { _, xb ->
                    cnt++
                    launch { time += measureTime { riggedExperiment(xb) } }
                }
            }.join()
        }
        return Pair(cnt, time)
    }

    fun openVectorBigInteger(
        r1: IntRange, riggedExperiment: (BigInteger) -> Unit
    ): Pair<Int, Duration> {
        var cnt = 0
        var time = measureTime {  }
        runBlocking {
            launch {
                DataGenerator.generateDataRangeBigInteger(r1.first, r1.last) { _, xb ->
                    cnt++
                    launch { time += measureTime { riggedExperiment(xb) } }
                }
            }.join()
        }
        return Pair(cnt, time)
    }

    fun openHybridBigInt(
        r1: IntRange, r2: IntRange, riggedExperiment: (BigInt, Int) -> Unit
    ): Pair<Int, Duration> {
        var cnt = 0
        var time = measureTime {  }
        runBlocking {
            launch {
                DataGenerator.generateDataRangeBigInt(r1.first, r1.last) { _, xb ->
                    DataGenerator.generateIntRange(r2.first, r2.last) { yi ->
                        cnt++
                        launch { time += measureTime { riggedExperiment(xb, yi) } }
                    }
                }
            }.join()
        }
        return Pair(cnt, time)
    }

    fun openHybridBigInteger(
        r1: IntRange, r2: IntRange, riggedExperiment: (BigInteger, Int) -> Unit
    ): Pair<Int, Duration> {
        var cnt = 0
        var time = measureTime {  }
        runBlocking {
            launch {
                DataGenerator.generateDataRangeBigInteger(r1.first, r1.last) { _, xb ->
                    DataGenerator.generateIntRange(r2.first, r2.last) { yi ->
                        cnt++
                        launch { time += measureTime { riggedExperiment(xb, yi) } }
                    }
                }
            }.join()
        }
        return Pair(cnt, time)
    }

    fun openVector(
        r1: IntRange, riggedExperiment: (ByteArray) -> Unit
    ): Pair<Int, Duration> {
        var cnt = 0
        var time = measureTime {  }
        runBlocking {
            launch {
                DataGenerator.generateDataRange(r1.first, r1.last) { _, xb ->
                    cnt++
                    launch { time += measureTime { riggedExperiment(xb) } }
                }
            }.join()
        }
        return Pair(cnt, time)
    }

    fun openFuzz(
        count: Int, riggedExperiment: (Long) -> Unit
    ): Pair<Int, Duration> {
        var cnt = 0
        var time = measureTime {  }
        runBlocking {
            launch {
                (0 until count).forEach { _ ->
                    val fuzz = SecureRandom.readLong()
                    cnt++
                    launch { time += measureTime { riggedExperiment(fuzz) } }
                }
            }.join()
        }
        return Pair(cnt, time)
    }
}


object TextFormat {
    fun printResult(name: String,
        n1: String, m1: Pair<Int, Duration>,
        n2: String, m2: Pair<Int, Duration>,
    ) {
        println("=============== $name ===============")
        println(n1)
        println("    Count: ${m1.first}")
        println("    Time: ${m1.second}")
        println("    Avg: ${m1.second.div(m1.first)}")

        println(n2)
        println("    Count: ${m2.first}")
        println("    Time: ${m2.second}")
        println("    Avg: ${m2.second.div(m2.first)}")

        println("Factor: ${m2.second.div(m2.first) / m1.second.div(m1.first)}")
        println()
    }
}


object Harness {

    val c1 = "Java BigInteger"
    val c2 = "BigInt"

    val byteRange = -128..128

    fun addition() {
        val m1 = TestRig.openMatrixBigInteger(byteRange, byteRange) { x, y -> x.add(y) }
        val m2 = TestRig.openMatrixBigInt(byteRange, byteRange) { x, y -> x.add(y) }
        TextFormat.printResult("Addition: x.add(y)", c1, m1, c2, m2)
    }

    fun subtraction() {
        val m1 = TestRig.openMatrixBigInteger(byteRange, byteRange) { x, y -> x.subtract(y) }
        val m2 = TestRig.openMatrixBigInt(byteRange, byteRange) { x, y -> x.subtract(y) }
        TextFormat.printResult("Subtraction: x.subtract(y)", c1, m1, c2, m2)
    }

    fun multiplication() {
        val m1 = TestRig.openMatrixBigInteger(byteRange, byteRange) { x, y -> x.multiply(y) }
        val m2 = TestRig.openMatrixBigInt(byteRange, byteRange) { x, y -> x.multiply(y) }
        TextFormat.printResult("Multiplication: x.multiply(y)", c1, m1, c2, m2)
    }

    fun division() {
        val m1 = TestRig.openMatrixBigInteger(byteRange, byteRange) { x, y -> x.divideAndRemainder(y) }
        val m2 = TestRig.openMatrixBigInt(byteRange, byteRange) { x, y -> x.divideAndRemainder(y) }
        TextFormat.printResult("Division: x.divideAndRemainder(y)", c1, m1, c2, m2)
    }

    fun or() {
        val m1 = TestRig.openMatrixBigInteger(byteRange, byteRange) { x, y -> x.or(y) }
        val m2 = TestRig.openMatrixBigInt(byteRange, byteRange) { x, y -> x.or(y) }
        TextFormat.printResult("Bitwise OR: x.or(y)", c1, m1, c2, m2)
    }

    fun xor() {
        val m1 = TestRig.openMatrixBigInteger(byteRange, byteRange) { x, y -> x.xor(y) }
        val m2 = TestRig.openMatrixBigInt(byteRange, byteRange) { x, y -> x.xor(y) }
        TextFormat.printResult("Bitwise XOR: x.xor(y)", c1, m1, c2, m2)
    }

    fun and() {
        val m1 = TestRig.openMatrixBigInteger(byteRange, byteRange) { x, y -> x.and(y) }
        val m2 = TestRig.openMatrixBigInt(byteRange, byteRange) { x, y -> x.and(y) }
        TextFormat.printResult("Bitwise AND: x.and(y)", c1, m1, c2, m2)
    }

    fun andNot() {
        val m1 = TestRig.openMatrixBigInteger(byteRange, byteRange) { x, y -> x.andNot(y) }
        val m2 = TestRig.openMatrixBigInt(byteRange, byteRange) { x, y -> x.andNot(y) }
        TextFormat.printResult("Bitwise AND NOT: x.andNot(y)", c1, m1, c2, m2)
    }

    fun min() {
        val m1 = TestRig.openMatrixBigInteger(byteRange, byteRange) { x, y -> x.min(y) }
        val m2 = TestRig.openMatrixBigInt(byteRange, byteRange) { x, y -> x.min(y) }
        TextFormat.printResult("Min compare: x.min(y)", c1, m1, c2, m2)
    }

    fun max() {
        val m1 = TestRig.openMatrixBigInteger(byteRange, byteRange) { x, y -> x.max(y) }
        val m2 = TestRig.openMatrixBigInt(byteRange, byteRange) { x, y -> x.max(y) }
        TextFormat.printResult("Max compare: x.max(y)", c1, m1, c2, m2)
    }

    fun compare() {
        val m1 = TestRig.openMatrixBigInteger(byteRange, byteRange) { x, y -> x.compareTo(y) }
        val m2 = TestRig.openMatrixBigInt(byteRange, byteRange) { x, y -> x.compareTo(y) }
        TextFormat.printResult("Advanced compare: x.compareTo(y)", c1, m1, c2, m2)
    }

    val positiveByteRange = 0..128

    fun mod() {
        val m1 = TestRig.openMatrixBigInteger(positiveByteRange, positiveByteRange) { x, y -> x.mod(y) }
        val m2 = TestRig.openMatrixBigInt(positiveByteRange, positiveByteRange) { x, y -> x.mod(y) }
        TextFormat.printResult("Modulus: x.mod(y)", c1, m1, c2, m2)
    }

    fun abs() {
        val m1 = TestRig.openVectorBigInteger(byteRange) { x -> x.abs() }
        val m2 = TestRig.openVectorBigInt(byteRange) { x -> x.abs() }
        TextFormat.printResult("Absolute value: x.abs()", c1, m1, c2, m2)
    }

    fun negate() {
        val m1 = TestRig.openVectorBigInteger(byteRange) { x -> x.negate() }
        val m2 = TestRig.openVectorBigInt(byteRange) { x -> x.negate() }
        TextFormat.printResult("Negation: x.negate()", c1, m1, c2, m2)
    }

    val bitRange = -128..128

    fun shiftLeft() {
        val m1 = TestRig.openHybridBigInteger(byteRange, bitRange) { x, y -> x.shiftLeft(y) }
        val m2 = TestRig.openHybridBigInt(byteRange, bitRange) { x, y -> x.shiftLeft(y) }
        TextFormat.printResult("Bitwise shl: x.shiftLeft(y)", c1, m1, c2, m2)
    }

    fun shiftRight() {
        val m1 = TestRig.openHybridBigInteger(byteRange, bitRange) { x, y -> x.shiftRight(y) }
        val m2 = TestRig.openHybridBigInt(byteRange, bitRange) { x, y -> x.shiftRight(y) }
        TextFormat.printResult("Bitwise shr: x.shiftRight(y)", c1, m1, c2, m2)
    }

    val positiveBitRange = 0..128

    fun clearBit() {
        val m1 = TestRig.openHybridBigInteger(byteRange, positiveBitRange) { x, y -> x.clearBit(y) }
        val m2 = TestRig.openHybridBigInt(byteRange, positiveBitRange) { x, y -> x.clearBit(y) }
        TextFormat.printResult("Binary clearBit: x.clearBit(y)", c1, m1, c2, m2)
    }

    fun flipBit() {
        val m1 = TestRig.openHybridBigInteger(byteRange, positiveBitRange) { x, y -> x.flipBit(y) }
        val m2 = TestRig.openHybridBigInt(byteRange, positiveBitRange) { x, y -> x.flipBit(y) }
        TextFormat.printResult("Binary flipBit: x.flipBit(y)", c1, m1, c2, m2)
    }

    fun setBit() {
        val m1 = TestRig.openHybridBigInteger(byteRange, positiveBitRange) { x, y -> x.setBit(y) }
        val m2 = TestRig.openHybridBigInt(byteRange, positiveBitRange) { x, y -> x.setBit(y) }
        TextFormat.printResult("Binary setBit: x.setBit(y)", c1, m1, c2, m2)
    }

    fun testBit() {
        val m1 = TestRig.openHybridBigInteger(byteRange, positiveBitRange) { x, y -> x.testBit(y) }
        val m2 = TestRig.openHybridBigInt(byteRange, positiveBitRange) { x, y -> x.testBit(y) }
        TextFormat.printResult("Binary testBit: x.testBit(y)", c1, m1, c2, m2)
    }

    val shortPositiveBitRange = 0..16

    fun pow() {
        val m1 = TestRig.openHybridBigInteger(byteRange, shortPositiveBitRange) { x, y -> x.pow(y) }
        val m2 = TestRig.openHybridBigInt(byteRange, shortPositiveBitRange) { x, y -> x.pow(y) }
        TextFormat.printResult("Pow: x.pow(y)", c1, m1, c2, m2)
    }

    fun loadBytes() {
        val m1 = TestRig.openVector(byteRange) { x -> bigIntOf(x).toByteArray() }
        val m2 = TestRig.openVector(byteRange) { x -> BigInteger(x).toByteArray() }
        TextFormat.printResult("Load/Export ByteArray: bigIntOf(x).toByteArray()", c1, m1, c2, m2)
    }

    val fuzzCount = Short.MAX_VALUE.toInt()

    fun loadLongs() {
        val m1 = TestRig.openFuzz(fuzzCount) { x -> bigIntOf(x).toLong() }
        val m2 = TestRig.openFuzz(fuzzCount) { x -> BigInteger.valueOf(x).toLong() }
        TextFormat.printResult("Load/Export Long: bigIntOf(x).toLong()", c1, m1, c2, m2)
    }
}


fun main(args: Array<String>) = runBlocking {
    val time = measureTime {
        launch {
            launch { Harness.addition() }
            launch { Harness.subtraction() }
            launch { Harness.multiplication() }
            launch { Harness.division() }
            launch { Harness.or() }
            launch { Harness.xor() }
            launch { Harness.and() }
            launch { Harness.andNot() }
            launch { Harness.min() }
            launch { Harness.max() }
            launch { Harness.compare() }
            launch { Harness.mod() }
            launch { Harness.abs() }
            launch { Harness.negate() }
            launch { Harness.shiftLeft() }
            launch { Harness.shiftRight() }
            launch { Harness.clearBit() }
            launch { Harness.flipBit() }
            launch { Harness.setBit() }
            launch { Harness.testBit() }
            launch { Harness.pow() }
            launch { Harness.loadBytes() }
            launch { Harness.loadLongs() }
        }.join()
    }
    println("Total time: $time")
}
