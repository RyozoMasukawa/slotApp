package com.example.wasacon.slotapp.history

import android.os.Bundle
import android.view.InputQueue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wasacon.slotapp.MainActivity
import com.example.wasacon.slotapp.R
import kotlinx.android.synthetic.main.content_data.*
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.fragment_main.view.*

class FourthFragment() : Fragment(){
    private lateinit var fhrvAdapter: FourthHistoryRecyclerViewAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler_view.apply {
            layoutManager = LinearLayoutManager(activity)
            //recyclerView.layoutManager = layoutManager

            fhrvAdapter = FourthHistoryRecyclerViewAdapter(MainActivity.queueOfFourthResults)
            //recyclerView.adapter = fhrvAdapter
            //recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL))
        }
    }

    fun update() {
        fhrvAdapter!!.notifyDataSetChanged()
    }
}