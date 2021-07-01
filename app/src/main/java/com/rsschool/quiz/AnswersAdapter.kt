package com.rsschool.quiz

import android.content.Context
import android.widget.ArrayAdapter

class AnswersAdapter(context: Context, answers: List<String>) :
    ArrayAdapter<String>(context, android.R.layout.simple_list_item_multiple_choice, answers)
