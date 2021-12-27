package com.maltepeuniversiyesi.stok.ui

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.maltepeuniversiyesi.stok.Constants
import com.maltepeuniversiyesi.stok.R
import com.maltepeuniversiyesi.stok.TransceiverViewModel
import com.maltepeuniversiyesi.stok.factories.BarcodeViewModelFactory
import com.maltepeuniversiyesi.stok.helpers.AlertHelper
import com.maltepeuniversiyesi.stok.models.ChangeStockRequest
import com.maltepeuniversiyesi.stok.models.Resource
import com.maltepeuniversiyesi.stok.models.TaskModel
import com.maltepeuniversiyesi.stok.repositories.TransceiverRepository

/**
 * A fragment representing a list of Items.
 */
class TaskFragment : Fragment(), TaskRecyclerViewAdapter.OnItemClickListener {
    private var LAUNCH_WRITE_NFC_ACTIVITY = 2
    private var columnCount = 1
    private lateinit var transceiverViewModel: TransceiverViewModel
    private lateinit var taskListRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        taskListRecyclerView =
            inflater.inflate(R.layout.fragment_task_list, container, false) as RecyclerView
        transceiverViewModel = ViewModelProvider(
            requireActivity(),
            BarcodeViewModelFactory(repository = TransceiverRepository())
        ).get(
            TransceiverViewModel::class.java
        )
        transceiverViewModel.showRefreshButton()
        loadTasks()
        return taskListRecyclerView
    }

    private fun TaskFragment.loadTasks() {
        with(taskListRecyclerView) {
            layoutManager = when {
                columnCount <= 1 -> LinearLayoutManager(context)
                else -> GridLayoutManager(context, columnCount)
            }
            transceiverViewModel.tasks().observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Resource.Status.SUCCESS -> {
                            //loadingProgressBar.visibility = View.GONE
                            @Suppress("UNCHECKED_CAST")

                            adapter = resource.data?.let { it1 ->
                                TaskRecyclerViewAdapter(
                                    it1 as List<TaskModel>,
                                    this@TaskFragment
                                )
                            }
                        }
                        Resource.Status.ERROR -> {
                            it.message?.let { it1 ->
                                AlertHelper.showInfoDialog(
                                    requireContext(), resources.getString(R.string.notify),
                                    it1
                                )
                            }
                        }
                        Resource.Status.LOADING -> {
                            // loadingProgressBar.visibility = View.VISIBLE
                        }
                    }
                }
            })

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        transceiverViewModel.refreshState.observe(viewLifecycleOwner,
            Observer { loginFormState ->
                if (loginFormState == null) {
                    return@Observer
                }
                if (loginFormState == "REFRESHED") {
                    refreshTasks()
                }
            })


    }

    private fun refreshTasks() {
        loadTasks()
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            TaskFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }

    override fun onStockChangeRequested(item: TaskModel?) {
        if (item == null) {
            Toast.makeText(requireContext(), R.string.task_model_null_error, Toast.LENGTH_LONG)
                .show()
            return
        }
        val builder: AlertDialog.Builder = android.app.AlertDialog.Builder(requireContext())
        builder.setTitle("Stok Sayısı Giriniz")

        val layout = LinearLayout(requireContext())
        layout.orientation = LinearLayout.VERTICAL


        val nameBox = EditText(requireContext())
        nameBox.hint = "Ürün adını giriniz"
        nameBox.setText(item.urun_isim)
        layout.addView(nameBox) // Notice this is an add method
        val stockBox = EditText(requireContext())
        stockBox.hint = "Stok sayısını giriniz"
        stockBox.inputType = InputType.TYPE_CLASS_NUMBER
        layout.addView(stockBox) // Another add method


        builder.setView(layout)

        builder.setPositiveButton("OK") { _, _ ->
            // Here you get get input text from the Edittext
            var name = nameBox.text.toString()
            var stockCount = stockBox.text.toString()
            val request = ChangeStockRequest(
                "urun_stok_isim_degis",
                stockCount,
                name,
                Constants.ACCESS_TOKEN
            )
            transceiverViewModel.changeStockCount(item.urun_id, request)
                .observe(requireActivity(), {
                    it?.let { resource ->
                        when (resource.status) {
                            Resource.Status.SUCCESS -> {
                                if (resource.message == null) {
                                    Toast.makeText(
                                        requireContext(),
                                        resource.data.toString(),
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                } else {
                                    Toast.makeText(
                                        requireContext(),
                                        resource.message,
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                }


                            }
                            Resource.Status.API_ERROR -> {
                                Toast.makeText(
                                    requireContext(),
                                    resource.message,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()

                            }
                            Resource.Status.ERROR -> {
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.unhandled_error),
                                    Toast.LENGTH_SHORT
                                ).show()

                            }
                            Resource.Status.LOADING -> {
                                // loadingProgressBar.visibility = View.VISIBLE
                            }
                        }
                    }
                })
        }
        builder.setNegativeButton("İptal Et") { dialog, _ -> dialog.cancel() }
        builder.show()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == LAUNCH_WRITE_NFC_ACTIVITY) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                val result = data?.getStringExtra("result")
                if (result != null) {
                    Log.i("DENGETELEKOM_NFC_WRITE", result)
                }
                if (result == null) {
                    Toast.makeText(activity, R.string.barcode_write_cancelled, Toast.LENGTH_LONG)
                        .show()
                    return
                }
                Toast.makeText(activity, "Başarılı bir şekilde yazıldı", Toast.LENGTH_LONG).show()
            }
            if (resultCode == AppCompatActivity.RESULT_CANCELED) {
                Toast.makeText(activity, R.string.barcode_write_cancelled, Toast.LENGTH_LONG).show()
            }
        }
    }
}