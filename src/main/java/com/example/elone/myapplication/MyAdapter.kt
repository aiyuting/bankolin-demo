package com.example.elone.myapplication

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.elone.myapplication.model.Question
import kotlinx.android.synthetic.main.item_view_list_layout.view.*


class MainAdapter(private val items : List<Question>) : RecyclerView.Adapter<MainAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_view_list_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(questions: Question) {
            view.subjectText.text = "[题目]" + questions.subject
            view.optionsText.text = "[选项]\n" + this.handleOptions(questions.options)
            view.answerText.text = questions.answer
        }

        private fun handleOptions(options: String): String{
            return options.replace("||","\n")
        }
    }

}

