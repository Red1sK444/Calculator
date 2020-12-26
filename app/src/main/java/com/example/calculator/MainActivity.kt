package com.example.calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var first: String = ""
    var last: String = ""
    var operation: String = ""
    var afterOp: String = ""
    var newCircle = true

    private fun firstCheck() {
        if (first.length > 14) {
            pad.text = ".." + first.substring(first.length - 12, first.length)
        } else {
            pad.text = first
        }
    }

    private fun lastCheck() {
        if (afterOp.length + last.length > 14) {
            pad.text = ".." + (afterOp + last).substring(
                (afterOp + last).length - 12,
                (afterOp + last).length
            )
        } else {
            pad.text = afterOp + last
        }
    }

    private fun operationProcessing(oper: String) {
        if (first.isNotEmpty() && first != "-" && last.isEmpty()) {
            if (oper == "×") {
                operation = "*"
            }
            else if (oper == "÷") {
                operation = "/"
            }
            else {
                operation = oper
            }

            afterOp = first
            first += operation

            firstCheck()
            first = afterOp
            afterOp = first + operation
        }
    }

    private fun addNumber(num: String) {
        if (operation.isEmpty()) {
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
            firstCheck()
        } else {
            if (last == "0") {
                last = num
            }
            else if (!(last == "-" && num == "0")) {
                last += num
            }
        lastCheck()
        }
    }

    fun clickOnDigit(v: View) {
        v as Button
        addNumber(v.text.toString())
    }

    fun clickOnOperation(v: View) {
        v as Button
        operationProcessing(v.text.toString())
    }

    private fun pointToComma(number: String): String {
        if (number.contains('.')) {
            return number.replace('.', ',')
        }
        else return number
    }

    private fun commaToPoint(number: String): String {
        if (number.contains(',')) {
            return number.replace(',', '.')
        }
        else return number
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ac.setOnClickListener {
            if (pad.text.isNotEmpty()) {
                pad.text = "0"
                first = "0"
                last = ""
                afterOp = ""
                operation = ""
                newCircle = true
            }
        }

        comma.setOnClickListener {
            if (operation.isEmpty()) {
                if (newCircle) {
                    if (first.isNotEmpty() && !(first.length == 1 && first == "-")) {
                        if (!first.contains(',')) {
                            first += ","
                        }
                    } else {
                        first += "0,"
                    }
                    firstCheck()
                }
            } else {
                if (last.isNotEmpty() && !(last.length == 1 && last == "-")) {
                    if (!last.contains(',')) {
                        last += ","
                    }
                } else {
                    last += "0,"
                }
                lastCheck()
            }
        }

        plusminus.setOnClickListener {
            if (operation.isEmpty()) {

                if (first.length >= 1) {
                    if (first[0] == '-') {
                        first = first.drop(1)
                        if (first.length == 0) {
                            first = "0"
                        }
                    } else {
                        if (first != "0") {
                            first = "-" + first
                        }
                        else {
                            first = "-"
                        }
                    }
                } else {
                    first = "-"
                }
                firstCheck()

            } else {

                if (last.length >= 1) {
                    if (last[0] == '-') {
                        last = last.drop(1)
                    } else {
                        if (last != "0") {
                            last = '-' + last
                        }
                    }
                } else {
                    last = "-"
                }
                lastCheck()
            }
        }

        equal.setOnClickListener {
            if (last.isNotEmpty() && last != "-") {

                first = commaToPoint(first)
                last = commaToPoint(last)

                val f: Double = first.toDouble()
                val l: Double = last.toDouble()
                var conclusion: String = ""

                when (operation) {
                    "+" -> conclusion = (f + l).toString()
                    "-" -> conclusion = (f - l).toString()
                    "*" -> conclusion = (f * l).toString()
                    "/" -> {
                        if (l == 0.0) {
                            conclusion = "Can't divide by 0"
                        } else {
                            conclusion = (f / l).toString()
                        }
                    }
                    "%" -> {
                        if (l == 0.0) {
                            conclusion = "0.0"
                        } else {
                            conclusion = (f % l).toString()
                        }
                    }
                }

                if (conclusion[conclusion.length - 1] == '0'
                    && conclusion[conclusion.length - 2] == '.') {
                    conclusion = conclusion.substring(0, conclusion.length - 2)
                }
                if (conclusion != "-0") {
                    if (conclusion.length > 14) {
                        if (conclusion.contains("E")) {
                            val indOfE = conclusion.indexOf('E')
                            val partE = conclusion.substring(indOfE)
                            val partELength = partE.length

                            var firstPartOfPad = conclusion.substring(0, 11 - partELength)
                            pad.text = "=" + pointToComma(firstPartOfPad) + ".." + partE
                        }
                        else {

                            pad.text = "=" + pointToComma(conclusion.substring(0, 11)) + ".."
                        }
                    }
                    else {
                        pad.text = '=' + pointToComma(conclusion)
                    }
                } else {
                    pad.text = "=0"
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
                operation = ""
            }
        }
    }
}