package com.example.calculator

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private fun Group.setAllOnClickListener(listener: View.OnClickListener?) {
        referencedIds.forEach { id ->
            rootView.findViewById<View>(id).setOnClickListener(listener)
        }
    }

    private val calculator = CalculatorProcessing()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        groupOfNumbers.setAllOnClickListener(View.OnClickListener {
            val num = (it as Button).text.toString()
            padText.text = calculator.clickOnDigit(num)
        })

        groupOfOperations.setAllOnClickListener(View.OnClickListener {
            val oper = (it as Button).text.toString()
            val forPad = calculator.clickOnOperation(oper)
            if (forPad != null) {
                padText.text = forPad
            }
        })

        acButton.setOnClickListener {
            padText.text = calculator.processingAC(padText.text.toString())
        }

        commaButton.setOnClickListener {
            val forPad = calculator.processingComma()
            if (forPad != null) {
                padText.text = forPad
            }
        }

        plusminusButton.setOnClickListener {
            padText.text = calculator.processingPlusMinus()
        }

        percentButton.setOnClickListener {
            val forPad = calculator.processingPercent()
            if (forPad != null) {
                padText.text = forPad
            }
        }

        equalButton.setOnClickListener {
            val forPad = calculator.processingEquality()
            if (forPad != null) {
                padText.text = forPad
            }
        }
    }
}