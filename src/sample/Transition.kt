package sample

/**
 * Created by Michał on 2017-12-26.
 */
data class Transition(val fromState: State,
                      val sign: Sign,
                      val write: Sign,
                      val toState: State,
                      val direction: Direction,
                      val shouldWrite: Boolean = false)