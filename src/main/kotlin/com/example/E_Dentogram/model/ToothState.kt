package com.example.E_Dentogram.model

interface ToothState {
    fun name(): String
    fun combineWith(otherState: ToothState): ToothState

    fun combineWithPartial(partial: PartialToothState): ToothState
    fun combineWithTotal(total: TotalToothState): ToothState
}

// T - T
// MISSING.combineWith(EXTRACTION)
//     EXTRACTION.combineWithTotal(MISSING)
//          EXTRACTION

// T - P
// MISSING.combineWith(RESTORATION)
//     RESTORATION.combineWithTotal(MISSING)
//          EXTRACTION

// P - T
// RESTORATION.combineWith(MISSING)
//     MISSING.combineWithPartial(RESTORATION)
//          MISSING

// P - HEALTHFUL
// RESTORATION.combineWith(HEALTHFUL)
//     HEALTHFUL.combineWithPartial(RESTORATION)
//          RESTORATION

// P - P
// CARIES.combineWith(RESTORATION)
//     RESTORATION.combineWithPartial(CARIES)
//          RESTORATION


enum class TotalToothState : ToothState {
    HEALTHFUL,
    MISSING,
    EXTRACTION;

    override fun name(): String = this.name

    override fun combineWith(otherState: ToothState): ToothState {
        return otherState.combineWithTotal(this)
    }

    override fun combineWithTotal(total: TotalToothState): ToothState {
        return this
    }

    override fun combineWithPartial(partial: PartialToothState): ToothState {
        return if (this == HEALTHFUL) { partial } else { this }
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


    override fun name(): String = this.name

    override fun combineWith(otherState: ToothState): ToothState {
        return otherState.combineWithPartial(this)
    }

    override fun combineWithTotal(total: TotalToothState): ToothState {
        return total
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
