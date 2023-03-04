package com.example.stopsmoking4me.fragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stopsmoking4me.MainActivity
import com.example.stopsmoking4me.R
import com.example.stopsmoking4me.adapter.QuotesRecyclerViewAdapter
import com.example.stopsmoking4me.databinding.FragmentQuotesBinding
import com.example.stopsmoking4me.databinding.LayoutMakeQuoteBinding
import com.example.stopsmoking4me.model.Quotes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class QuotesFragment : Fragment(), View.OnClickListener {
    private lateinit var binding: FragmentQuotesBinding
    private var quotesList: List<Quotes> = mutableListOf<Quotes>()
    private lateinit var adapter: QuotesRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = QuotesRecyclerViewAdapter(requireContext())
        CoroutineScope(Dispatchers.IO).launch {
//            quotesList = (requireContext() as MainActivity).viewModel.getQuotes()

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentQuotesBinding.inflate(layoutInflater, container, false)
        binding.quotesRv.layoutManager = LinearLayoutManager(requireContext())
        binding.quotesRv.adapter = adapter

        binding.addFab.setOnClickListener(this)
        return binding.root
    }

    override fun onClick(v: View?) {
        val dialog = Dialog(requireContext())
        dialog.setCancelable(false)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val bindingQuoteLayout: LayoutMakeQuoteBinding = DataBindingUtil.inflate(LayoutInflater.from(dialog.context), R.layout.layout_make_quote, null, false)
        dialog.setContentView(bindingQuoteLayout.root)
        dialog.show()
        dialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        bindingQuoteLayout.ivClose.setOnClickListener {
            dialog.dismiss()
        }
        bindingQuoteLayout.btnSaveQuote.setOnClickListener {
            var list = mutableListOf<Quotes>()
            list.add(Quotes(0, bindingQuoteLayout.tilQuote.editText!!.text.toString(), "User Created"))
            (requireContext() as MainActivity).viewModel.insertQuotes(list)
            dialog.dismiss()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireContext() as MainActivity).viewModel.getQuotes().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            quotesList = it
            adapter.setData(quotesList)
//            adapter = QuotesRecyclerViewAdapter(requireContext(), quotesList)
//            adapter.notifyDataSetChanged()
        })
    }
}