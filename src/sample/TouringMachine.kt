package sample

/**
 * Created by Michał on 2017-12-26.
 */
class TouringMachine(val acceptState: State,
                     val rejectState: State,
                     val startState: State) {
    var transitions = arrayListOf<Transition>()
    var tape = arrayListOf<Char>()
    var currentState = startState
    var currentPos = 2
    var sign = Sign.E
    var direction = Direction.RIGHT
    var previousPos = currentPos

    fun addTransition(transition: Transition) {
        transitions.add(transition)
    }

    fun loadTransitions() {
        addTransition(Transition(State.Q0, Sign.E, Sign.E, State.Q1, Direction.LEFT))
        addTransition(Transition(State.Q0, Sign.A, Sign.A, State.Q0, Direction.RIGHT))
        addTransition(Transition(State.Q0, Sign.B, Sign.B, State.Q0, Direction.RIGHT))

        addTransition(Transition(State.Q1, Sign.A, Sign.E, State.Q2, Direction.LEFT, true))
        addTransition(Transition(State.Q1, Sign.B, Sign.E, State.Q8, Direction.LEFT, true))

        addTransition(Transition(State.Q2, Sign.E, Sign.E, State.Q3, Direction.LEFT, true))
        addTransition(Transition(State.Q2, Sign.A, Sign.A, State.Q4, Direction.LEFT))
        addTransition(Transition(State.Q2, Sign.B, Sign.B, State.Q4, Direction.LEFT))

        addTransition(Transition(State.Q4, Sign.E, Sign.E, State.Q5, Direction.RIGHT))
        addTransition(Transition(State.Q4, Sign.A, Sign.A, State.Q4, Direction.LEFT))
        addTransition(Transition(State.Q4, Sign.A, Sign.B, State.Q4, Direction.LEFT))

        addTransition(Transition(State.Q5, Sign.B, Sign.E, State.Q6, Direction.RIGHT))
        addTransition(Transition(State.Q5, Sign.A, Sign.E, State.Q7, Direction.RIGHT, true))

        addTransition(Transition(State.Q7, Sign.E, Sign.E, State.Q3, Direction.RIGHT))
        addTransition(Transition(State.Q7, Sign.A, Sign.E, State.Q0, Direction.RIGHT))
        addTransition(Transition(State.Q7, Sign.B, Sign.E, State.Q0, Direction.RIGHT))

        addTransition(Transition(State.Q8, Sign.E, Sign.E, State.Q3, Direction.RIGHT))
        addTransition(Transition(State.Q8, Sign.A, Sign.E, State.Q9, Direction.LEFT))
        addTransition(Transition(State.Q8, Sign.E, Sign.E, State.Q9, Direction.LEFT))

        addTransition(Transition(State.Q9, Sign.E, Sign.E, State.Q10, Direction.RIGHT))
        addTransition(Transition(State.Q9, Sign.A, Sign.E, State.Q9, Direction.LEFT))
        addTransition(Transition(State.Q9, Sign.B, Sign.E, State.Q9, Direction.LEFT))

        addTransition(Transition(State.Q10, Sign.A, Sign.E, State.Q6, Direction.RIGHT))
        addTransition(Transition(State.Q10, Sign.B, Sign.E, State.Q11, Direction.RIGHT, true))

        addTransition(Transition(State.Q11, Sign.E, Sign.E, State.Q3, Direction.RIGHT))
        addTransition(Transition(State.Q11, Sign.A, Sign.E, State.Q0, Direction.RIGHT))
        addTransition(Transition(State.Q11, Sign.B, Sign.E, State.Q0, Direction.RIGHT))
    }

    fun prepareMachine() {
        sign = getSignFromTape()
    }

    fun addSignToTape(sign: Char) {
        tape.add(sign)
    }

    fun nextStep(): Boolean {
        if (currentState == acceptState || currentState == rejectState)
            return true

        previousPos = currentPos
        System.out.print(currentState.name + "  ->  ")

        processNextTransition()
        System.out.print(currentState.name + "\n")

        if (direction == Direction.RIGHT) currentPos++ else currentPos--

        sign = getSignFromTape()

        return false
    }

    fun getSignFromTape(): Sign {
        return Sign.static.getForChar(tape[currentPos])
    }

    fun processNextTransition() {
        val transition = findByStateAndSign()

        transition?.let {
            if (it.shouldWrite) {
                System.out.print("Current: $currentState next: ${it.toState} should: ${it.shouldWrite} ${it.write} pos: $currentPos\n")
                tape[currentPos] = it.write.sign
            }
            direction = it.direction
            currentState = it.toState
        }

    }

    private fun findByStateAndSign(): Transition? {
        transitions.forEach { transition ->
            if (transition.fromState == currentState && transition.sign == sign) {
                return transition
            }
        }
        return null
    }

    fun hasReachedEndState(): Boolean {
        return currentState == acceptState || currentState == rejectState
    }

    fun hasSucceeded(): Boolean {
        return currentState == acceptState
    }
}