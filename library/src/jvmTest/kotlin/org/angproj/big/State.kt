/**
 * Copyright (c) 2025 by Kristoffer Paulsson <kristoffer.paulsson@talenten.se>.
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

/**
 * ## Example implementation
 * ```
 * enum class SomeState : State<SomeState> {
 *     INITIAL,
 *     PROCESSING,
 *     COMPLETED;
 *
 *     companion object : State.FiniteStates<SomeState>(INITIAL, COMPLETED) {
 *         override val path: Map<SomeState, List<SomeState>>
 *             get() = mapOf(
 *             INITIAL to listOf(PROCESSING),
 *             PROCESSING to listOf(COMPLETED),
 *             COMPLETED to listOf()
 *         )
 *     }
 * }
 * ```
 *
 * ## Example usage
 * ```kotlin
 * val stateMachine = SomeState.execute {
 *     when(this) {
 *     SomeState.INITIAL -> println("Starting in INITIAL state.")
 *     SomeState.PROCESSING -> println("Now processing...")
 *     SomeState.COMPLETED -> println("Processing completed.")
 *     }
 *     }
 *
 *     stateMachine.goto(SomeState.PROCESSING)
 *     stateMachine.goto(SomeState.COMPLETED)
 * ```
 * */


/**
 * Represents a generic finite state machine (FSM) pattern for enums.
 *
 * The `State` interface and its nested classes provide a framework for modeling
 * and executing finite state machines using enum classes as states. This is
 * useful for workflows, protocols, or any process that can be described as a
 * series of discrete states with defined transitions.
 *
 * ## Overview
 * - The `State` interface is intended to be implemented by enum classes that represent
 *   the possible states of a finite state machine.
 * - The `FiniteStates` abstract class defines the valid transitions between states,
 *   the initial state, and the terminal state.
 * - The `StateMachine` abstract class provides the runtime logic for operating a state
 *   machine, including state transitions, validation, and execution of actions on state changes.
 *
 * ## Design
 * - States are represented by enum values.
 * - Valid transitions are defined in a map, where each state maps to a list of valid next states.
 * - The initial and terminal states are explicitly specified.
 * - The state machine enforces valid transitions and allows actions to be executed on each transition.
 *
 * ## Example implementation
 * ```
 * enum class SomeState : State<SomeState> {
 *     INITIAL,
 *     PROCESSING,
 *     COMPLETED;
 *
 *     companion object : State.FiniteStates<SomeState>(INITIAL, COMPLETED) {
 *         override val path: Map<SomeState, List<SomeState>>
 *             get() = mapOf(
 *             INITIAL to listOf(PROCESSING),
 *             PROCESSING to listOf(COMPLETED),
 *             COMPLETED to listOf()
 *         )
 *     }
 * }
 * ```
 *
 * @param E The enum type representing the states.
 */
public interface State<E: Enum<E>> {

    /**
     * Represents an instance of a state machine operating on a set of finite states.
     *
     * This class manages the current state, validates transitions, and executes
     * a user-provided action on each state change. It exposes properties to query
     * the current state, whether the machine is at the start or end, and the valid
     * next states.
     *
     * @property state The current state of the machine.
     * @property start `true` if the machine is at the initial state.
     * @property done `true` if the machine is at the terminal state.
     * @property choices The list of valid next states from the current state.
     * @constructor Internal, use [FiniteStates.execute] to create an instance.
     */
    public abstract class StateMachine<E>(
        protected var current: E,
        protected val states: FiniteStates<E>,
        protected val action: E.() -> Unit
    ){

        /**
         * The current state of the state machine.
         */
        public val state: E
            get() = current

        /**
         * Returns `true` if the state machine is at the initial state.
         */
        public val start: Boolean
            get() = state == states.begin

        /**
         * Returns `true` if the state machine is at the terminal state.
         */
        public val done: Boolean
            get() = state == states.end

        /**
         * Returns the list of valid next states from the current state.
         */
        public val choices: List<E>
            get() = states.getPaths(state)

        /**
         * Transitions the state machine to the specified [state], if it is a valid next state.
         * Executes the [action] associated with the new state.
         *
         * @param state The state to transition to.
         * @return The list of valid next states from the new state.
         * @throws IllegalArgumentException if the state is not a valid transition.
         */
        public fun goto(state: E): List<E> = choices.apply {
            current = elementAtOrNull(indexOf(state)) ?: error("State unavailable")
            current.action()
        }
    }

    /**
     * Describes the finite set of states and their valid transitions for a state machine.
     *
     * Extend this class in the companion object of your enum to define the state graph.
     * The initial and terminal states are specified in the constructor. The valid transitions
     * are defined in the [path] property, which maps each state to its possible next states.
     *
     * The class validates that the initial state has at least one outgoing transition and
     * that the terminal state has none.
     *
     * @property begin The initial state.
     * @property end The terminal (final) state.
     * @property path A map describing valid transitions: each state maps to a list of valid next states.
     * @constructor Validates that the initial state has at least one transition and the terminal state has none.
     */
    public abstract class FiniteStates<E>(
        public val begin: E,
        public val end: E
    ) {
        init {
            require(getPaths(begin).isNotEmpty()) { "Initial state must have at least one transition." }
            require(getPaths(end).isEmpty()) { "Terminal state must not have outgoing transitions." }
        }

        /**
         * The state transition map. Each key is a state, and its value is the list of valid next states.
         */
        public abstract val path: Map<E, List<E>>

        /**
         * Returns the list of valid next states from the given [state].
         *
         * @param state The current state.
         * @return The list of valid next states.
         * @throws IllegalArgumentException if the state is not configured.
         */
        public fun getPaths(state: E): List<E> = path[state] ?: error("Not configured")

        /**
         * Creates a new [StateMachine] instance starting at [begin], using the provided [action]
         * to be executed on each state transition.
         *
         * @param action The action to execute for each state.
         * @return A new [StateMachine] instance.
         */
        public fun execute(
            action: E.() -> Unit
        ): StateMachine<E> = object : StateMachine<E>(begin, this, action) {}
    }
}


enum class SomeState : State<SomeState> {
    INITIAL,
    PROCESSING,
    COMPLETED;

    companion object : State.FiniteStates<SomeState>(INITIAL, COMPLETED) {
        override val path: Map<SomeState, List<SomeState>>
            get() = mapOf(
                INITIAL to listOf(PROCESSING),
                PROCESSING to listOf(COMPLETED),
                COMPLETED to listOf()
            )
    }
}

class StateTest {

    @Test
    fun testStateMachine() {
        val stateMachine = SomeState.execute {
            when(this) {
                SomeState.INITIAL -> println("Starting in INITIAL state.")
                SomeState.PROCESSING -> println("Now processing...")
                SomeState.COMPLETED -> println("Processing completed.")
            }
        }

        assertEquals(stateMachine.state, SomeState.INITIAL)
        assertEquals(stateMachine.choices, listOf(SomeState.PROCESSING))

        stateMachine.goto(SomeState.PROCESSING)
        assertEquals(stateMachine.state, SomeState.PROCESSING)

        stateMachine.goto(SomeState.COMPLETED)
        assertEquals(stateMachine.state, SomeState.COMPLETED)
    }
}