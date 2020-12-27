package com.example.calculator

open class CalculatorProcessing {

    private companion object {
        const val DISPLAY_NUMBERS_COUNT = 14
        const val DISPLAY_ADD_NUMBER = DISPLAY_NUMBERS_COUNT - 2
        const val DISPLAY_CONCLUSION = DISPLAY_NUMBERS_COUNT - 3
        const val POINT = '.'
        const val COMMA = ','
        const val E = 'E'
    }

    private var first = ""
    private var last = ""
    private var operation: Operations? = null
    private var afterOp = ""
    private var newCircle = true
    // - Эта переменная принимает значение false только после операций
    // - "%" и "=". Это значит, что мы можем взять его как первое
    // - Слагаемое/множитель (вместо first), то есть после него сразу
    // - Можно применить операции (+,-,*,/), а потом задать второе число.
    // - Если этой возможностью не воспользоваться(Нажать AC или начать
    // - Вводить первое число, то переменная снова принимает значение true)

    private fun pointToComma(number: String): String {
        return if (number.contains(POINT)) {
            number.replace(POINT, COMMA)
        } else number
    }

    private fun commaToPoint(number: String): String {
        return if (number.contains(COMMA)) {
            number.replace(COMMA, POINT)
        } else number
    }

    private fun firstCheck(): String {
        return if (first.length > DISPLAY_NUMBERS_COUNT) {
                "..${first.substring(first.length - DISPLAY_ADD_NUMBER, first.length)}"
        } else {
            first
        }
    }

    private fun lastCheck(): String {
        return if (afterOp.length + last.length > DISPLAY_NUMBERS_COUNT) {
            "..${(afterOp + last).substring((afterOp + last).length - DISPLAY_ADD_NUMBER,
                (afterOp + last).length)}"
        } else {
            "$afterOp$last"
        }
    }

    private fun addNumber(num: String):String {
        if (operation == null) {
            if (!newCircle) {
                newCircle = true
                first = ""
            }

            if (first == "0") {
                first = num
            }
            else if (!(first == "-" && num == "0")) {
                first += num
            }
            return firstCheck()
        } else {
            if (last == "0") {
                last = num
            }
            else if (!(last == "-" && num == "0")) {
                last += num
            }
            return lastCheck()
        }
    }

    fun clickOnDigit(num: String):String {
        return addNumber(num)
    }

    private fun operationProcessing(oper: String): String? {
        if (first.isNotEmpty() && first != "-" && last.isEmpty()) {
            operation = if (oper == "×") {
                Operations.MULTIPLICATION
            } else if (oper == "÷") {
                Operations.DIVIDE
            } else {
                if (oper == Operations.MINUS.def) {
                    Operations.MINUS
                } else {
                    Operations.PLUS
                }
            }

            afterOp = first
            first += operation?.def

            val padText = firstCheck()
            first = afterOp
            afterOp = first + operation?.def
            return padText
        }
        return null
    }

    fun clickOnOperation(oper: String): String? {
        return operationProcessing(oper)
    }

    fun processingAC(padText: String): String {
        if (padText.isNotEmpty()) {
            first = "0"
            last = ""
            afterOp = ""
            operation = null
            newCircle = true
            return "0"
        }
        return padText
    }

    private fun commaInput(num: String): String {
        var number = num
        if (number.isNotEmpty() && !(number.length == 1 && number == "-")) {
            if (!number.contains(COMMA)) {
                number += COMMA
            }
        } else {
            number += "0,"
        }
        return number
    }

    fun processingComma(): String? {
        if (operation == null) {
            if (newCircle) {
                first = commaInput(first)
                return firstCheck()
            }
        } else {
            last = commaInput(last)
            return lastCheck()
        }
        return null
    }

    private fun processPlusMinus(num: String, check: Boolean): String {
        var number = num
        if (number.isNotEmpty()) {
            if (number.contains('-')) {
                number = number.drop(1)
                if (check && number.isEmpty()) {
                    number = "0"
                }
            } else {
                if (number != "0") {
                    number = "-$number"
                } else if (check) {
                    number = "-"
                }
            }
        } else {
            number = "-"
        }
        return number
    }

    fun processingPlusMinus(): String {
        return if (operation == null) {
            first = processPlusMinus(first, true)
            firstCheck()

        } else {
            last = processPlusMinus(last, false)
            lastCheck()
        }
    }

    private fun processingConclusion(concl: String, l: Double): String {
        val padText: String
        var conclusion = concl
        if (conclusion[conclusion.length - 1] == '0'
            && conclusion[conclusion.length - 2] == POINT) {
            conclusion = conclusion.substring(0, conclusion.length - 2)
        }
        if (conclusion != "-0") {
            if (conclusion.length > DISPLAY_NUMBERS_COUNT) {
                padText = if (conclusion.contains(E)) {

                    val indOfE = conclusion.indexOf(E)
                    val partE = conclusion.substring(indOfE)
                    val partELength = partE.length

                    val firstPartOfPad = conclusion.substring(0,
                        DISPLAY_CONCLUSION - partELength)
                    "=${pointToComma(firstPartOfPad)}..$partE"
                } else {
                    "=${pointToComma(conclusion.substring(0, DISPLAY_CONCLUSION))}.."
                }
            }
            else {
                padText = "=${pointToComma(conclusion)}"
            }
        } else {
            padText = "=0"
        }

        if (l != 0.0) {
            first = pointToComma(conclusion)
            newCircle = false
        }
        else  {
            first = ""
            newCircle = true
        }
        last = ""
        afterOp = ""
        operation = null
        return padText
    }

    private fun preCounting(): Int {
        var toMultiplicate = 1
        if (operation == Operations.MULTIPLICATION || operation == Operations.DIVIDE) {
            if (first.contains('-')) {
                toMultiplicate *= -1
                first = first.drop(1)
            }
            if (last.contains('-')) {
                toMultiplicate *= -1
                last = last.drop(1)
            }
        } else {
            if (last.contains('-')) {
                last = last.drop(1)
                when(operation) {
                    Operations.PLUS -> operation = Operations.MINUS
                    Operations.MINUS -> operation = Operations.PLUS
                    else -> {
                    }
                }
            }
        }
        return toMultiplicate
    }

    fun processingPercent(): String? {
        if (last.isNotEmpty() && last != "-") {

            val toMultiplicate = preCounting()

            val f = commaToPoint(first).toDouble()
            var l = commaToPoint(last).toDouble()

            if (operation == Operations.MULTIPLICATION || operation == Operations.DIVIDE) {
                l /= 100
            } else {
                l = l/100 * f
            }

            var conclusion = ""
            when(operation) {
                Operations.PLUS->{
                    conclusion = ((f + l)*toMultiplicate).toString()
                }
                Operations.MINUS->{
                    conclusion = ((f - l)*toMultiplicate).toString()
                }
                Operations.MULTIPLICATION->{
                    conclusion = ((f * l)*toMultiplicate).toString()
                }
                Operations.DIVIDE->{
                    conclusion = if (l == 0.0) {
                        "Can't divide by 0"
                    } else {
                        ((f / l) * toMultiplicate).toString()
                    }
                }
            }

            return processingConclusion(conclusion, l)
        }
        return null
    }

    fun processingEquality(): String? {
        if (last.isNotEmpty() && last != "-") {

            first = commaToPoint(first)
            last = commaToPoint(last)

            val f: Double = first.toDouble()
            val l: Double = last.toDouble()
            var conclusion = ""

            when (operation) {
                Operations.PLUS -> conclusion = (f + l).toString()
                Operations.MINUS -> conclusion = (f - l).toString()
                Operations.MULTIPLICATION -> conclusion = (f * l).toString()
                Operations.DIVIDE -> {
                    conclusion = if (l == 0.0) {
                        "Can't divide by 0"
                    } else {
                        (f / l).toString()
                    }
                }
            }

            return processingConclusion(conclusion, l)
        }
        return null
    }
}