package com.rsschool.quiz

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView.CHOICE_MODE_MULTIPLE
import android.widget.CheckedTextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.rsschool.quiz.data.QuizData
import com.rsschool.quiz.databinding.FragmentQuizBinding


class QuizFragment : Fragment() {

    private var quizData: QuizData = QuizData.getData()
    private var _binding: FragmentQuizBinding? = null
    private val binding get() = requireNotNull(_binding)
    private var questionCount: Int? = 0
    private var clickListener: QuizFragmentListener? = null
    private var currentAnswers: MutableMap<Int, MutableList<Int>> =
        (0 until quizData.questionsList().size).associateWith { mutableListOf<Int>() }.toMutableMap()
    private var reset = false
    private var answersAdapter: AnswersAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val map: MutableMap<Int, MutableList<Int>>?

        var arg: Bundle? = null
        try {
            arg = requireArguments()
            reset = arg.getBoolean(IS_RESET)

        } catch (e: IllegalStateException) {

        }

        if (arg != null && !reset) {
            map = mutableMapOf()
            (0 until quizData.questionsList().size).forEach { i ->
                val value = arg.getIntegerArrayList(i.toString())
                map[i] = value ?: mutableListOf()
            }

            currentAnswers = map
            questionCount = arg.getInt(QUESTION_COUNT)

        } else {
            reset = false

        }

        val themId = getThemeId()
        val typedValue = TypedValue()
        inflater.context.setTheme(themId)
        val currentTheme = context?.theme
        currentTheme?.resolveAttribute(android.R.attr.statusBarColor, typedValue, true)
        val window = activity?.window


        when (context?.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {
                window?.statusBarColor = Color.BLACK

            }
            Configuration.UI_MODE_NIGHT_NO -> {
                window?.statusBarColor = typedValue.data

            }
            Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                window?.statusBarColor = typedValue.data
            }
        }


        _binding = FragmentQuizBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun changeTheme(): Unit {

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        clickListener = context as MainActivity
    }

    override fun onDetach() {
        super.onDetach()
        clickListener = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        answersAdapter = AnswersAdapter(requireContext(), quizData.answersList()[questionCount]!!)
        binding.answers.adapter = answersAdapter

        binding.answers.setOnItemClickListener { _, itemView, position, _ ->
            run {
                val v = itemView as CheckedTextView
                if (v.isChecked) {
                    currentAnswers[questionCount]!!.add(position)
                } else {
                    currentAnswers[questionCount]!!.remove(position)
                }
                binding.nextButton.isEnabled = currentAnswers[questionCount]!!.isNotEmpty()
            }
        }

        uiUpdate()

        binding.toolbar.getChildAt(1)?.setOnClickListener {
            getPreviousQuestion()
            clickListener?.replaceFragment(reset, questionCount, currentAnswers)
            uiUpdate()
        }

        binding.previousButton.setOnClickListener {
            getPreviousQuestion()
            clickListener?.replaceFragment(reset, questionCount, currentAnswers)
            uiUpdate()
        }

        binding.nextButton.setOnClickListener {
            getNextQuestion()
            if (questionCount == quizData.questionsList().size) {
                val score = countRightAnswers()
                clickListener?.openResultFragment(score, currentAnswers)

            } else {
                binding.toolbar.title = "Question $questionCount"
                clickListener?.replaceFragment(reset, questionCount, currentAnswers)
                uiUpdate()

            }
        }

        binding.answers.choiceMode = CHOICE_MODE_MULTIPLE
    }

    private fun countRightAnswers(): Int {
        var score = 0
        (0 until quizData.questionsList().size).forEach {
            if (currentAnswers[it]!!.sorted() == quizData.rightAnswersList()[it]) {
                score++
            }
        }
        return score * 20
    }

    private fun getThemeId(): Int = when (questionCount!! % 6) {
        1 -> R.style.Theme_Quiz_First
        2 -> R.style.Theme_Quiz_Second
        3 -> R.style.Theme_Quiz_Third
        4 -> R.style.Theme_Quiz_Fourth
        5 -> R.style.Theme_Quiz_Fifth
        else -> R.style.Theme_Quiz
    }

    private fun uiUpdate() {
        binding.apply {
            toolbar.title = "Question $questionCount"
            question.text = quizData.questionsList()[questionCount]
            answersAdapter!!.notifyDataSetChanged()

            when (questionCount) {
                in 1 until quizData.questionsList().size -> {
                    previousButton.isEnabled = true
                    if (questionCount == quizData.questionsList().size) {
                        nextButton.text = getString(R.string.submit)

                    } else {
                        nextButton.text = getString(R.string.next)

                    }
                }
                0 -> {
                    previousButton.isEnabled = false
                    binding.toolbar.navigationIcon = null
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        binding.apply {
            if (currentAnswers[questionCount]!!.isEmpty()) {
                answers.clearChoices()
                nextButton.isEnabled = false
            } else {
                currentAnswers[questionCount]!!.forEach {
                    answers.clearFocus()
                    answers.requestFocusFromTouch()
                    answers.post {
                        answers.setItemChecked(it, true)
                        answers.setSelection(it)
                    }
                    Toast.makeText(requireContext(), "Hello", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getNextQuestion() {
        questionCount = questionCount?.plus(1)

    }

    private fun getPreviousQuestion() {
        questionCount = questionCount?.minus(1)
    }

    companion object {

        private const val QUESTION_COUNT = "COUNT"
        private const val CURRENT_ANSWERS = "CURRENT_ANSWERS"
        private const val IS_RESET = "reset"

        @JvmStatic
        fun newInstance(
            reset: Boolean,
            questionCount: Int? = null,
            currentAnswers: MutableMap<Int, MutableList<Int>>? = null
        ): QuizFragment {
            val fragment = QuizFragment()
            val args = Bundle()
            args.putBoolean(IS_RESET, reset)
            questionCount?.let { args.putInt(QUESTION_COUNT, it) }
            if (currentAnswers != null) {
                for (item in currentAnswers) {
                    args.putIntegerArrayList(item.key.toString(), item.value as ArrayList<Int>)
                }
            }
            fragment.arguments = args
            return fragment
        }
    }
}

interface QuizFragmentListener {

    fun replaceFragment(
        reset: Boolean,
        questionCount: Int? = null,
        currentAnswers: MutableMap<Int, MutableList<Int>>? = null
    )

    fun openResultFragment(result: Int, answers: Map<Int, MutableList<Int>>)

}

