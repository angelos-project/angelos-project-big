/**
 * Copyright (c) 2024 by Kristoffer Paulsson <kristoffer.paulsson@talenten.se>.
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
package org.angproj.big.oldbig

import kotlin.jvm.JvmStatic

/**
 * The goal is to avoid all type of NULL pointer exceptions, therefore it is necessary to provide a
 * null object instance wherever it is needed with empty initialized objects. Here are the base empty arrays
 * and objects of any type. The idea is to reduce the initialization of empty objects to reduce processing
 * power and memory consumption.
 * */
public object NullObject {

    public const val string: String = ""

    @JvmStatic
    public val byteArray: ByteArray = byteArrayOf()

    @JvmStatic
    public val shortArray: ShortArray = shortArrayOf()

    @JvmStatic
    public val intArray: IntArray = intArrayOf()

    @JvmStatic
    public val longArray: LongArray = longArrayOf()

    @JvmStatic
    public val floatArray: FloatArray = floatArrayOf()

    @JvmStatic
    public val doubleArray: DoubleArray = doubleArrayOf()
}

public fun String.isNull(): Boolean = this.isEmpty()

public fun ByteArray.isNull(): Boolean = NullObject.byteArray === this

public fun ShortArray.isNull(): Boolean = NullObject.shortArray === this

public fun IntArray.isNull(): Boolean = NullObject.intArray === this

public fun LongArray.isNull(): Boolean = NullObject.longArray === this

public fun FloatArray.isNull(): Boolean = NullObject.floatArray === this

public fun DoubleArray.isNull(): Boolean = NullObject.doubleArray === this