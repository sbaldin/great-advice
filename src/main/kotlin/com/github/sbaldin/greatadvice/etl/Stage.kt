package com.github.sbaldin.greatadvice.etl

interface Stage<IN, OUT> {
    fun next(value: IN): OUT

}

interface FirstStage<OUT> : Stage<Unit, OUT>, Iterator<OUT> {
    override fun next(value: Unit): OUT = next()

    fun <NEXT_OUT> bindNextStage(nextStage: Stage<OUT, NEXT_OUT>): FirstStage<NEXT_OUT> {
        return object : FirstStage<NEXT_OUT> {
            override fun hasNext() = this@FirstStage.hasNext()

            override fun next(value: Unit): NEXT_OUT = next()
            override fun next(): NEXT_OUT {
                val currentStageValue = this@FirstStage.next(Unit)
                return nextStage.next(currentStageValue)
            }
        }
    }

    fun run(){
        while (hasNext()){
            next()
        }
    }
}

interface LastStage<IN> : Stage<IN, Unit> {
    override fun next(value: IN)
}
