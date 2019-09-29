package com.tr1.javatokotlin.extensions

import android.widget.EditText
import com.google.android.material.textfield.TextInputLayout

fun EditText.isNotEmpty( textInputLayout : TextInputLayout) : Boolean{
   return if(text.toString().isEmpty())
    {
        textInputLayout.error = "Connot be black !"
         false
    }
    else
    {
        textInputLayout.isErrorEnabled = false
        true
    }
}