/**
 * Copyright (c) 2023-2024 by Kristoffer Paulsson <kristoffer.paulsson@talenten.se>.
 *
 * This software is available under the terms of the MIT license. Parts are licensed
 * under different terms if stated. The legal terms are attached to the LICENSE file
 * and are made available on:
 *
 *      https://opensource.org/licenses/MIT
 *
 * SPDX-License-Identifier: MIT
 *
 * Contributors:
 *      Kristoffer Paulsson - initial implementation
 */
package org.angproj.big

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Line 244:     if (qhat == 0) {
 * Otherwise:    if (qhat != 0) {
 * */
class DivisionBug1Test {

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testDivideAndRemainderEmptyEmpty() {
        faultList.forEach {
            val x = it.first
            val y = it.second

            val dividend = bigIntOf(x)
            val divisor = bigIntOf(y)

            proof(dividend, divisor)
        }
    }

    fun proof(dividend: BigInt, divisor: BigInt) {
        if(divisor != BigInt.zero) {
            val result = dividend.divideAndRemainder(divisor)

            val quotient = result.first
            val remainder = result.second

            assertEquals(dividend, divisor.times(quotient).plus(remainder))
        } else {
            assertFailsWith<BigMathException> { dividend.divideAndRemainder(divisor) }
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    private val faultList = listOf(
        Pair<ByteArray, ByteArray>("ffffffffffff30ff0b0100a5ffffffff".hexToByteArray(), "ff30ff0b0100a5ffff".hexToByteArray()),
        Pair<ByteArray, ByteArray>("7eb00000f7f7f7f7f7f7f7f7f7f7f7f7".hexToByteArray(), "f7f7f7f7f7f7f7f7f7f7f7".hexToByteArray()),
        Pair<ByteArray, ByteArray>("ffef00000000000d0000000000020000".hexToByteArray(), "0000ffffffffffffff000000000033".hexToByteArray()),
        Pair<ByteArray, ByteArray>("8000000009000000000000000004fb21".hexToByteArray(), "80000000090000000000".hexToByteArray()),
        Pair<ByteArray, ByteArray>("00060606060606060606060600060000".hexToByteArray(), "00060606060606060606060606".hexToByteArray()),
        Pair<ByteArray, ByteArray>("fffffffcfb28ffff000300fff640ffff".hexToByteArray(), "fffcfb28ffff000300fff640".hexToByteArray()),
        Pair<ByteArray, ByteArray>("00fffffffffffffeffe0e01de0e0e0e0".hexToByteArray(), "e0000000000000000000bf9900".hexToByteArray()),
        Pair<ByteArray, ByteArray>("000000000000008d8d8d8d8d8d000000".hexToByteArray(), "008d8d8d8d8d8df7ff".hexToByteArray()),
        Pair<ByteArray, ByteArray>("ff0000000000000000000000998e0100".hexToByteArray(), "9f9f9f9f9f9f9f9f9f80000000".hexToByteArray()),
        Pair<ByteArray, ByteArray>("ff000000000000000000000000ff0000".hexToByteArray(), "00ffffffffffffff".hexToByteArray()),
        Pair<ByteArray, ByteArray>("c4c4c4c4c4c4c4c4c4c4c4c4c4c400c4".hexToByteArray(), "c4c4c4c4c4c4c4c4c4c4c400".hexToByteArray()),
        Pair<ByteArray, ByteArray>("ff00000000000000000001ffffdfff5c".hexToByteArray(), "ff00000000000000000000fffd2e".hexToByteArray()),
        Pair<ByteArray, ByteArray>("ff0000000000ffffffffffffffffffff".hexToByteArray(), "ff0000000000003d00".hexToByteArray()),
        Pair<ByteArray, ByteArray>("00565656565620565600002fffff00ff".hexToByteArray(), "5656565656205656000056".hexToByteArray()),
        Pair<ByteArray, ByteArray>("f6000aff00001717171717170004000a".hexToByteArray(), "fff6000aff0000000800ffffffffff0a".hexToByteArray()),
        Pair<ByteArray, ByteArray>("0000202020202001000000c8f51b1a20".hexToByteArray(), "2020202020202020a02000ff00".hexToByteArray()),
        Pair<ByteArray, ByteArray>("ff0000003f00ffffffffffff0aff0aff".hexToByteArray(), "ffffff0000003f00ffffffffffff0aff".hexToByteArray()),
        Pair<ByteArray, ByteArray>("c8c8c8c8c8c8c8c8c8c8c8c8c8c8c8c8".hexToByteArray(), "c8c8c8c8c8c8c8c81ac8c801000004".hexToByteArray()),
        Pair<ByteArray, ByteArray>("fffffffaffffffffffffffffffffffff".hexToByteArray(), "fffffffffffffafafafafafafafafa".hexToByteArray()),
        Pair<ByteArray, ByteArray>("00000000000000c7c2b88d0a0a000000".hexToByteArray(), "0000c7c2b88d0a0a0a".hexToByteArray()),
        Pair<ByteArray, ByteArray>("b7b7b7b7b7b7b7b7b7b7b7b7b7b7b7b7".hexToByteArray(), "b7b7b7b7b7b7b7b7b70000003d".hexToByteArray()),
        Pair<ByteArray, ByteArray>("e6e6e6e6e6e6e6e6e6e6e6e6e6e6e6e6".hexToByteArray(), "e6e6e6e6e6e6e6e6e6e62a3b7e0b".hexToByteArray()),
        Pair<ByteArray, ByteArray>("46464646464646464646464646464646".hexToByteArray(), "464646464646464646f6f641".hexToByteArray()),
        Pair<ByteArray, ByteArray>("990a1e1e1e1e1e1e1e1e1e1e1e1e1e1e".hexToByteArray(), "1e1e1e1e1e1e1e1e1e1e1e1e99".hexToByteArray()),
        Pair<ByteArray, ByteArray>("f6000000000000000000000000000000".hexToByteArray(), "0f0f0f0f0f1f00000000000000f13d".hexToByteArray()),
        Pair<ByteArray, ByteArray>("ffffffff1e1e8c0000001e1e1effffff".hexToByteArray(), "ff1e1e8c0000001e1e1effff".hexToByteArray()),
        Pair<ByteArray, ByteArray>("c3c3c3c3c3c3c3ffffffffffffffffc3".hexToByteArray(), "c3c3c3c3c3c3c3ffffffff".hexToByteArray()),
        Pair<ByteArray, ByteArray>("93939393939393939393939393939393".hexToByteArray(), "93939393939393939393230000ff".hexToByteArray()),
        Pair<ByteArray, ByteArray>("0affffffffffffff25ffffff00000000".hexToByteArray(), "00000affffffffffffffffffffffffff".hexToByteArray()),
        Pair<ByteArray, ByteArray>("ff9f9f9f9f9f9f9f9f9f9f9f9f9f9f9f".hexToByteArray(), "9f9f9f9f9f9f9f9f9f9f07".hexToByteArray()),
        Pair<ByteArray, ByteArray>("0a3c3c3c3c3c3c3c3c3c3c3c3c3c3c3c".hexToByteArray(), "3c3c3c3c3c3c3c3c3ce64d6a".hexToByteArray()),
        Pair<ByteArray, ByteArray>("01ffffffffffff397878787878787839".hexToByteArray(), "78787878787878".hexToByteArray()),
        Pair<ByteArray, ByteArray>("ff0000009bffffffffffff9b9b9b9b9b".hexToByteArray(), "9b9b9b9b9b9b9b9b9b9b00".hexToByteArray()),
        Pair<ByteArray, ByteArray>("fffffffffffff5ffff19ff2159ffffff".hexToByteArray(), "fffffffff5ffff19ff2159ff".hexToByteArray()),
        Pair<ByteArray, ByteArray>("ff000000000000000000ff0000000000".hexToByteArray(), "00ffffffffffffffffffff26ff2c".hexToByteArray()),
        Pair<ByteArray, ByteArray>("ff2fbf24ffa2a2a2a2a2a2a2a2a2a2a2".hexToByteArray(), "a2a2a2a2a2a2a2a2a28495".hexToByteArray()),
        Pair<ByteArray, ByteArray>("20202020202020202020202020202020".hexToByteArray(), "2020202020202020202020ffff".hexToByteArray()),
        Pair<ByteArray, ByteArray>("00000000bf0a006666666666ffffff43".hexToByteArray(), "6666666666666666ff".hexToByteArray()),
        Pair<ByteArray, ByteArray>("00002ad7d7d7d7d7d7d7d7d7d7d7d7d7".hexToByteArray(), "d7d7d7d7d7d7d7d7d7d7".hexToByteArray()),
        Pair<ByteArray, ByteArray>("feaac8000000000000000000c8c8c8aa".hexToByteArray(), "aaaaaaaaaaaaaaaaaaaa".hexToByteArray()),
        Pair<ByteArray, ByteArray>("d777770000000088888800001f403200".hexToByteArray(), "d7777700000000886e7700001f4032".hexToByteArray()),
        Pair<ByteArray, ByteArray>("15151515151515151515151515151515".hexToByteArray(), "151515151515151515f08900d7".hexToByteArray()),
        Pair<ByteArray, ByteArray>("fffff7ff0000000000ffffffffffffff".hexToByteArray(), "ff0000000000000000000002".hexToByteArray()),
        Pair<ByteArray, ByteArray>("ffffffff000000000000000000ffffff".hexToByteArray(), "ffffffffff0000000000000000000006".hexToByteArray()),
        Pair<ByteArray, ByteArray>("ffffffff00000000000040000055ff00".hexToByteArray(), "00ffffffffffffff001061".hexToByteArray()),
        Pair<ByteArray, ByteArray>("00000000dddddddddddddddddddddddd".hexToByteArray(), "dddddddddddddddddddd".hexToByteArray()),
        Pair<ByteArray, ByteArray>("a3efefefefefefefefefefefefefefef".hexToByteArray(), "efefefefefefefefefefef00".hexToByteArray()),
        Pair<ByteArray, ByteArray>("ffffffe9ffffff99ff71ffffdcdcdcdc".hexToByteArray(), "ffffe9ffffff99ff71ffffdcdcdc".hexToByteArray()),
        Pair<ByteArray, ByteArray>("d328dddddddddddddddddddddddddddd".hexToByteArray(), "dddddddddddddddddddddddd23".hexToByteArray()),
        Pair<ByteArray, ByteArray>("ff00e200000000000005000000464646".hexToByteArray(), "46464646464646464646464646".hexToByteArray()),
        Pair<ByteArray, ByteArray>("fffffffffffff2f221f2f2ffffffffff".hexToByteArray(), "fff2f221f2f2ffffffff".hexToByteArray()),
        Pair<ByteArray, ByteArray>("d7d7d7d7d7d7d7d7d7d7d7d7d7d7d7d7".hexToByteArray(), "d7d7d7d7d7d7d7d7d7d7d7d70a".hexToByteArray()),
        Pair<ByteArray, ByteArray>("000008a5a5a5a5a5a5a5a5a5a5a5a5a5".hexToByteArray(), "a5a5a5a5a5a5a5a50000".hexToByteArray()),
        Pair<ByteArray, ByteArray>("ed0000000000000000ffffff5500ffff".hexToByteArray(), "ff000000000000000000400000000000".hexToByteArray()),
        Pair<ByteArray, ByteArray>("6800000000000000000000007a000a00".hexToByteArray(), "0101010101010101010108ff".hexToByteArray()),
        Pair<ByteArray, ByteArray>("2f2f2f2f2f2f2f2f2f2f2f2f2f2f2f2f".hexToByteArray(), "2f2f2f2f2f2f2f2f2f2faf".hexToByteArray()),
        Pair<ByteArray, ByteArray>("ffffff00000000000040000000000000".hexToByteArray(), "00000000ffffffffffffffffffffff00".hexToByteArray()),
        Pair<ByteArray, ByteArray>("fbfbfbfbfbfbfbfbfbfbfbfbfbfbfbfb".hexToByteArray(), "fbfbfbfbfbfbfbfbfbfbd9".hexToByteArray()),
        Pair<ByteArray, ByteArray>("b0b0b0b0b0b0b0b0b0b0ffffffffffb0".hexToByteArray(), "b0b0b0b0b0b0b0b0b0b0b000".hexToByteArray()),
        Pair<ByteArray, ByteArray>("75ffffffffffffffffffffff55555555".hexToByteArray(), "5555555555555555ffffff401515".hexToByteArray()),
        Pair<ByteArray, ByteArray>("a8666666666666666666666666666666".hexToByteArray(), "6666666666a8000000000000000000".hexToByteArray()),
        Pair<ByteArray, ByteArray>("ffbf0000000000000000bf00014b4b4b".hexToByteArray(), "4b4b4b4b4b4b4b4b4b4b00".hexToByteArray()),
        Pair<ByteArray, ByteArray>("feffffffffffffffffbfffffffffdf00".hexToByteArray(), "aaaaaaaaaaaaaa7effffffffff".hexToByteArray()),
        Pair<ByteArray, ByteArray>("00000000000008ffffffffff29000000".hexToByteArray(), "0000000000ffffffffffffff1d".hexToByteArray()),
        Pair<ByteArray, ByteArray>("000000f9ffffff77ffffff0000000000".hexToByteArray(), "00f9ffffff77ffffffffff0b".hexToByteArray()),
        Pair<ByteArray, ByteArray>("ffacacacacacacacacacacacacacacac".hexToByteArray(), "acacacacacacacac0000000000".hexToByteArray()),
        Pair<ByteArray, ByteArray>("00000000d72a3c2a2a2a2a2a2a2a2a2a".hexToByteArray(), "2a2a2a2a2a2a2a2a42".hexToByteArray()),
        Pair<ByteArray, ByteArray>("fffffffffff7000000000000010a0a0a".hexToByteArray(), "0a0a0a0a0a0a0a0a".hexToByteArray()),
        Pair<ByteArray, ByteArray>("fefefefefefefefefefefefeffffffff".hexToByteArray(), "fefefefefefefe00".hexToByteArray()),
        Pair<ByteArray, ByteArray>("ffebc4a7a7a7a7a7a700000000010000".hexToByteArray(), "ebc4a7a7a7a7a7a700".hexToByteArray()),
        Pair<ByteArray, ByteArray>("fc2b0000002700040000f8fef3530000".hexToByteArray(), "fc2b00000027000400000000".hexToByteArray()),
        Pair<ByteArray, ByteArray>("c2c2c2c2c2c2c2c2c2c2c2c2c2c2c2c2".hexToByteArray(), "c2c2c2c2c2c2c2c2c200fffb".hexToByteArray()),
        Pair<ByteArray, ByteArray>("cb00000000ffffffffffffffffffffff".hexToByteArray(), "ff000000000000ffffffffffffffffff".hexToByteArray()),
        Pair<ByteArray, ByteArray>("00000000000000006363636363636363".hexToByteArray(), "636363636363fa".hexToByteArray()),
        Pair<ByteArray, ByteArray>("e000000000000024000024ffff000000".hexToByteArray(), "ffff0000000000000000000024ffffff".hexToByteArray()),
        Pair<ByteArray, ByteArray>("ff8e8e8e8e8e8e8e8e8e8e8e8e8e8e8e".hexToByteArray(), "8e8e8e8e8e8e8e8e8e8e8e8e84".hexToByteArray()),
        Pair<ByteArray, ByteArray>("ffff000000000000000000fbffffffff".hexToByteArray(), "ffff000000000000000000fbffff00".hexToByteArray()),
        Pair<ByteArray, ByteArray>("01dddddddddddddddddddddddddddddd".hexToByteArray(), "dddddddddddddddddddddddddddd".hexToByteArray()),
        Pair<ByteArray, ByteArray>("0000004a4a4a4a4a4a4a4a4a4a4a4a4a".hexToByteArray(), "4a4a4a4a4a4a7272720000".hexToByteArray()),
        Pair<ByteArray, ByteArray>("ff010000000000006300000000000000".hexToByteArray(), "ff0000000000000000ffffffc0".hexToByteArray()),
        Pair<ByteArray, ByteArray>("000000d3d30a0a0e0e0e0e0e0e000000".hexToByteArray(), "00d3d30a0a0e0e0e0e0e0e0e0e".hexToByteArray()),
        Pair<ByteArray, ByteArray>("8f8f8f8f8f8f8f8f8f8f8f8f8f8f8f8f".hexToByteArray(), "8f8f8f8f8f8f8f8f8f8f00".hexToByteArray()),
        Pair<ByteArray, ByteArray>("74747474747474747474747474747474".hexToByteArray(), "74747474747474ffffffffffff00".hexToByteArray()),
        Pair<ByteArray, ByteArray>("d6967a1e1e1e1e1e1e1edd1e1e1e1e1e".hexToByteArray(), "1e1e1e1e1e1e1e1e1e1e1f1e".hexToByteArray()),
        Pair<ByteArray, ByteArray>("93939393939393939393939393939393".hexToByteArray(), "ffffff939393939393939393930a00ff".hexToByteArray()),
        Pair<ByteArray, ByteArray>("00bbbbbbbbbbbbbbbbbbbbbbbbbbbbbb".hexToByteArray(), "bbbbbbbbbbbbbbbbbbbbbbbb8a".hexToByteArray()),
        Pair<ByteArray, ByteArray>("07070707070707070707070707070707".hexToByteArray(), "07070707070707ff000000f000e4".hexToByteArray()),
        Pair<ByteArray, ByteArray>("fffefefefefefefefefefefefefefefe".hexToByteArray(), "fefefefefefefefe00ffffffff".hexToByteArray()),
        Pair<ByteArray, ByteArray>("e5e5e5e5e5e5e5e5e5e5e5e5e5e5e5e5".hexToByteArray(), "e5e5e5e5e5e5e5dc000006f52a".hexToByteArray()),
        Pair<ByteArray, ByteArray>("cfffebebebebebebebebebebebebebeb".hexToByteArray(), "ebebebebebebebebebebebebeb".hexToByteArray()),
        Pair<ByteArray, ByteArray>("8400000000f9ffff00ffffff00eb0000".hexToByteArray(), "00ffffffffffffffffffff0dffffffff".hexToByteArray()),
        Pair<ByteArray, ByteArray>("72727272727272727272727272727272".hexToByteArray(), "7272727272727272727272728a1e".hexToByteArray()),
        Pair<ByteArray, ByteArray>("ff30ffffff2ad6d6d6d6d6d6d6d6d6d6".hexToByteArray(), "d6d6d6d6d6d6d6d6d6d6".hexToByteArray()),
        Pair<ByteArray, ByteArray>("ffffffffffff03030303030303030303".hexToByteArray(), "03030303030303ffffff".hexToByteArray()),
        Pair<ByteArray, ByteArray>("1c1c1c1c1c1c1c1c1c1c1c1c1c1c1c1c".hexToByteArray(), "1c1c1c1c1c1c1c1c1c1c1c1c1c1c8a".hexToByteArray()),
        Pair<ByteArray, ByteArray>("5dffffffffffff00ff00000000000000".hexToByteArray(), "00ffffffffffffff".hexToByteArray()),
        Pair<ByteArray, ByteArray>("cdcdcdcdcdcdcdcdcdcdcdcdcdcdcdcd".hexToByteArray(), "cdcdcdcdcdcdcdcdcdcd1a7b3f".hexToByteArray()),
        Pair<ByteArray, ByteArray>("a7a7a7a7a7a7a7a7a7a7a7a7a7a7a7a7".hexToByteArray(), "a7a7a7a7a7a7a7a7000000000404".hexToByteArray()),
        Pair<ByteArray, ByteArray>("ffffffff00000000000000000000ff31".hexToByteArray(), "ffff000000000082".hexToByteArray()),
        Pair<ByteArray, ByteArray>("00000000ffffffffff000000ffffffff".hexToByteArray(), "ff0000000000c9c9c9000000".hexToByteArray()),
        Pair<ByteArray, ByteArray>("d9000000cccccccccccccccccccccccc".hexToByteArray(), "cccccccccccccccccccccc00".hexToByteArray()),
        Pair<ByteArray, ByteArray>("82eb000000000000000000ebebebebeb".hexToByteArray(), "ebebebebebebebebebebeb".hexToByteArray()),
        Pair<ByteArray, ByteArray>("ffc3c3c3c3c3c3c3c3c3c3c3c3c3c3c3".hexToByteArray(), "c3c3c3c3c3c3c3c3c3c3c3".hexToByteArray()),
        Pair<ByteArray, ByteArray>("f9f9f9f9f9f9f9f9f9f9f9f9f9f9f9f9".hexToByteArray(), "f9f9f9f9f9f9f9f9f9f9f9f9f90000".hexToByteArray()),
        Pair<ByteArray, ByteArray>("1a0ad14b4b4b4b4b180000004b244b26".hexToByteArray(), "4b4b4b4b4b4b4b4b4b4b4b4b4b".hexToByteArray()),
        Pair<ByteArray, ByteArray>("f8f8f8f8f8f8f8f8f8f8f8f8f8f8f8f8".hexToByteArray(), "f8f8f8f8f8f8f8f8f8f80a".hexToByteArray()),
        Pair<ByteArray, ByteArray>("0a555555555555555501000000000000".hexToByteArray(), "0055555555555555555555550a".hexToByteArray()),
        Pair<ByteArray, ByteArray>("d6000000fafafafafafafafafafafafa".hexToByteArray(), "fafafafafafafafafafa0a00".hexToByteArray()),
        Pair<ByteArray, ByteArray>("47474747474747474747474747474747".hexToByteArray(), "4747474747474747ff002ce40a18".hexToByteArray()),
        Pair<ByteArray, ByteArray>("3a3a3a3a3a3a3a3a3a3a3a3a3a3a3a3a".hexToByteArray(), "3a3a3a3a3a3a3a3a3a3aff3f".hexToByteArray()),
        Pair<ByteArray, ByteArray>("a1000000000010000000000000000000".hexToByteArray(), "0000ffffffffffffffffff080000ff34".hexToByteArray()),
        Pair<ByteArray, ByteArray>("5bff00ffffffffe4ff00000000003a00".hexToByteArray(), "5bff00ffffffffe4ff00000a".hexToByteArray()),
        Pair<ByteArray, ByteArray>("ff2e2e2e2e2e2effffffffffffff2bef".hexToByteArray(), "efefefefefefff".hexToByteArray()),
        Pair<ByteArray, ByteArray>("cb01000000000015667700000f0f0f0f".hexToByteArray(), "0f0f0f0f0f0f0f0f0f0000f6002c".hexToByteArray()),
        Pair<ByteArray, ByteArray>("00c5c5c5c5c5c5000000da0000000000".hexToByteArray(), "0000c5c5c5c5c5c5c5c5c50000000000".hexToByteArray()),
        Pair<ByteArray, ByteArray>("b6b6b6b6b6b6b6b6b6b6b6b6b6b6b6b6".hexToByteArray(), "b6b6b6b6b6b6b6b6b6b6b6b6283bff".hexToByteArray()),
        Pair<ByteArray, ByteArray>("000000007ffd00000000000000000000".hexToByteArray(), "999999999999999999".hexToByteArray()),
        Pair<ByteArray, ByteArray>("00fdffffffffffffff00000000000000".hexToByteArray(), "00ffffffffffffffffffff00000008".hexToByteArray()),
        Pair<ByteArray, ByteArray>("ffff5fff009898d15e8000080840a5ff".hexToByteArray(), "ff5fff009898d15e8000080840a5".hexToByteArray()),
        Pair<ByteArray, ByteArray>("4abfbfbfbfbfbfbfbfbfbfbfbfbfbfbf".hexToByteArray(), "bfbfbfbfbfbfbfbfbfbf4a0a0a".hexToByteArray()),
        Pair<ByteArray, ByteArray>("004e4e4e4e4e4e4e4e4e00064e4e4e4e".hexToByteArray(), "4e4e4e4e4e4e4e4e4e4e00".hexToByteArray()),
        Pair<ByteArray, ByteArray>("1717171717171717171717177b171717".hexToByteArray(), "17171717171717171717fa".hexToByteArray()),
        Pair<ByteArray, ByteArray>("0004555555555555550000ff55555555".hexToByteArray(), "55555555555555555555000068".hexToByteArray()),
        Pair<ByteArray, ByteArray>("ffffffff000000000000000041b70000".hexToByteArray(), "ffffffff00000000000000000041".hexToByteArray()),
        Pair<ByteArray, ByteArray>("ff02c0000000000000b000c00200ffff".hexToByteArray(), "ff0000000000000000b000c00200".hexToByteArray()),
        Pair<ByteArray, ByteArray>("0000003d000000ffffffffffff000000".hexToByteArray(), "3d000000ffffffffffff3a".hexToByteArray()),
        Pair<ByteArray, ByteArray>("ffffffffffffff2ddcdcdcdcdcdcdcdc".hexToByteArray(), "dcdcdcdcdcdcdcdc".hexToByteArray()),
        Pair<ByteArray, ByteArray>("9b9b9b9b9b9b9bffffffffffffffff9b".hexToByteArray(), "9b9b9b9b9b9b9b9b9b9bff2175".hexToByteArray()),
        Pair<ByteArray, ByteArray>("2e555555555555555555550000000000".hexToByteArray(), "555555555555555555555555".hexToByteArray()),
        Pair<ByteArray, ByteArray>("45454545454545454545454545454545".hexToByteArray(), "45454545454545ffffffffffffffff".hexToByteArray()),
        Pair<ByteArray, ByteArray>("000000004cfbebebeb76ff7e00000000".hexToByteArray(), "4cfbebebeb76ff7e7e".hexToByteArray()),
        Pair<ByteArray, ByteArray>("ff41ebebebebebebebebebebebebebeb".hexToByteArray(), "ebebebebebebebebebebebebeb".hexToByteArray()),
        Pair<ByteArray, ByteArray>("9c9c9c9c9c9c9c9c9c9c249c9c9c9c9c".hexToByteArray(), "9c9c9c9c9c9c9c9c9c89ffd7".hexToByteArray()),
        Pair<ByteArray, ByteArray>("b029000000000000b02900003f000000".hexToByteArray(), "00ffffffffffffffff0100008a5d01".hexToByteArray()),
        Pair<ByteArray, ByteArray>("ff404040404040404040404040404040".hexToByteArray(), "404040404040404040404040ff".hexToByteArray()),
        Pair<ByteArray, ByteArray>("000000000000ffffffffffff00000000".hexToByteArray(), "0000ffffffffffffffffff".hexToByteArray()),
        Pair<ByteArray, ByteArray>("ff000000f0f0f0f0f0f0f0f0f0f0f0f0".hexToByteArray(), "f0f0f0f0f0f0f0f0f0f000".hexToByteArray()),
        Pair<ByteArray, ByteArray>("0000006d010000000000ffffff23ffff".hexToByteArray(), "006d010000000000ffffffff03".hexToByteArray()),
        Pair<ByteArray, ByteArray>("00007f7f7f7f7f7f7f7ff67f7f7f7f7f".hexToByteArray(), "7f7f7f7f7f7f7f8b8b8b7ff6".hexToByteArray()),
        Pair<ByteArray, ByteArray>("0afbfbfbfbfbfbfbfbfbfbfbfbfbfbfb".hexToByteArray(), "fbfbfbfbfbfbfbfbfbfbfb".hexToByteArray()),
    )
}