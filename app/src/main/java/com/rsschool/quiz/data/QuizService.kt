package com.rsschool.quiz

import com.rsschool.quiz.data.ApiKeyInterceptor
import com.rsschool.quiz.data.QuizQuestion
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


interface QuizService {
    @GET("/api/v1/questions")
    fun getQuestions(@Query("limit") limit: Int = 5, @Query("category") category: String = ""): Call<List<QuizQuestion>>
}

class QuizClient {

    companion object {
        private var quizService: QuizService? = null
        private const val baseUrl = "https://quizapi.io"

        fun getClient(): QuizService? {
            if (quizService == null) {
                val client = OkHttpClient.Builder()
                    .addInterceptor(ApiKeyInterceptor())
                    .build()
                quizService = Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build()
                    .create(QuizService::class.java)
            }
            return quizService
        }
    }

}