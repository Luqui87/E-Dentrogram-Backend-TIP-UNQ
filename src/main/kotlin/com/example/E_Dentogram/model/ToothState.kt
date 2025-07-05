package com.example.E_Dentogram.model

interface ToothState {
    fun combineWith(otherState: ToothState): ToothState

    fun combineWithPartial(partial: PartialToothState): ToothState
    fun combineWithTotal(total: TotalToothState): ToothState
    fun isTotalState(): Boolean {return  false}

}

object ToothStateParser {
    fun stringToState(state: String): ToothState {
        return try {
            TotalToothState.stringToState(state)
        } catch (e: IllegalArgumentException) {
            try {
                PartialToothState.stringToState(state)
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("Invalid tooth state: $state")
            }
        }
    }
}

enum class TotalToothState : ToothState {
    HEALTHFUL,
    MISSING,
    MISSING_NO_ERUPTION,
    TO_ERUPT,
    IMPLANT,
    EXTRACTION;


    override fun combineWith(otherState: ToothState): ToothState {
        return otherState.combineWithTotal(this)
    }

    override fun combineWithTotal(total: TotalToothState): ToothState {
        return this
    }

    override fun combineWithPartial(partial: PartialToothState): ToothState {
        return if (this == HEALTHFUL) { partial } else { this }
    }

    override fun isTotalState() : Boolean{
        return true
    }

    companion object {
        fun stringToState(state: String): TotalToothState {
            return TotalToothState.valueOf(state.uppercase())
        }
    }



}

enum class PartialToothState : ToothState {
    HEALTHY,
    RESTORATION,
    CARIES;


    override fun combineWith(otherState: ToothState): ToothState {
        return otherState.combineWithPartial(this)
    }

    override fun combineWithTotal(total: TotalToothState): ToothState {
        return if (total == TotalToothState.HEALTHFUL) {this} else {total}
    }

    override fun combineWithPartial(partial: PartialToothState): ToothState {
        return this
    }

    companion object {
        fun stringToState(state: String): PartialToothState {
            return PartialToothState.valueOf(state.uppercase())
        }
    }
}
