package com.rsschool.quiz.data

import com.rsschool.quiz.QuizClient
import java.util.*

var category = ""

class QuizData(var category: String = "") {

    private val questions: MutableList<QuizQuestion> = mutableListOf()

    init {
        questions.addAll(
            QuizClient.getClient()!!
                .getQuestions(category = if (this.category == CATEGORIES[1]) "" else this.category.lowercase(Locale.getDefault()))
                .execute().body()!!
        )
    }

    fun questionsList(): Map<Int, String> = questions.indices.associateWith {
        questions[it].question
    }

    fun answersList(): Map<Int, List<String>> = questions.indices.associateWith {
        questions[it].answers.values.toList()
    }

    fun rightAnswersList(): Map<Int, String> = questions.indices.associateWith {
        val correctAnswers = questions[it].correctAnswers.toList()
        correctAnswers.indices.find { i -> correctAnswers[i].equals("true") }.toString()
    }

    companion object {
        private var data: QuizData? = null

        fun getData(): QuizData {
            if (data == null) {
                data = QuizData(category)
            }
            return data!!
        }
    }
}