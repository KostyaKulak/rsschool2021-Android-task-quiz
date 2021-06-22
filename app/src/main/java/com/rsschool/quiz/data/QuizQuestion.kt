package com.rsschool.quiz.data

import com.google.gson.annotations.SerializedName


data class QuizQuestion(
    @SerializedName("question") val question: String,
    @SerializedName("answers") val answers: Map<String, String>,
    @SerializedName("multiple_correct_answers") val multipleCorrectAnswers: Boolean,
    @SerializedName("correct_answers") val correctAnswers: Map<String, String>,
    @SerializedName("category") val category: String
)