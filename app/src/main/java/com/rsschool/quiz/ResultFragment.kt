package com.rsschool.quiz

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.rsschool.quiz.data.QuizData
import com.rsschool.quiz.databinding.FragmentResultBinding

class ResultFragment : Fragment() {

    private var quizData: QuizData = QuizData.getData()
    private var _binding: FragmentResultBinding? = null
    private val binding get() = requireNotNull(_binding)
    private var score = 0
    private var clickListener: MainActivity? = null
    private var answers: MutableMap<Int, String?> = mutableMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        clickListener = context as MainActivity

    }

    override fun onDetach() {
        super.onDetach()
        clickListener = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var arg: Bundle? = null
        try {
            arg = requireArguments()

        } catch (e: IllegalStateException) {

        }
        if (arg != null) {
            score = arg.getInt(RESULT)

            for (i in 1..5) {
                val value = arg.getString(i.toString())
                answers[i] = value
            }
        }
        uiUpdate()

        binding.shareIv.setOnClickListener {
            clickListener?.share(generateQuizReport())
        }

        binding.resetIv.setOnClickListener {
            clickListener?.reset(true)
        }

        binding.closeIv.setOnClickListener {
            clickListener?.close()
        }
    }

    private fun uiUpdate() {
        binding.resultTv.text = getString(R.string.your_result_text, score)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun generateQuizReport(): String {
        var message = "Your result: $score %\n\n"
        for (i in 1..5) {
            message += "$i) ${quizData.questionsList()[i].toString()}\n"
            message += "Your answer: ${answers[i]?.let { quizData.answersList()[i]?.get(it.toInt() - 1) }}\n\n"

        }
        return message
    }

    companion object {
        private const val RESULT = "result"

        fun newInstance(result: Int, answers: Map<Int?, String>): ResultFragment {
            val fragment = ResultFragment()
            //использовать bundleOf не представляется возможным, т.к. нужно в bundle поместить map с моими типами параметров
            val args = Bundle()
            args.putInt(RESULT, result)
            for (answer in answers) {
                args.putString(answer.key.toString(), answer.value)
            }

            fragment.arguments = args
            return fragment
        }
    }
}

interface ResultFragmentListener {

    fun reset(reset: Boolean)
    fun close()
    fun share(message: String)

}