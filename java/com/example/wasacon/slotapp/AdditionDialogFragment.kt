package com.example.wasacon.slotapp

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.annotation.IntegerRes
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.dialog_addition.*

class AdditionDialogFragment : DialogFragment() {
    // Use this instance of the interface to deliver action events
    internal lateinit var listener: AdditionDialogListener

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    interface AdditionDialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = context as AdditionDialogListener
        } catch (e: ClassCastException) {
            // The activity doesn't implement the interface, throw exception
            throw ClassCastException((context.toString() +
                    " must implement NoticeDialogListener"))
        }
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            builder.setView(inflater.inflate(R.layout.dialog_addition, null))
                .setPositiveButton(R.string.confirm_text,
                    DialogInterface.OnClickListener { dialog, which ->
                        Log.d("numBallsEdit = ", numBallsEdit?.text.toString())
                        listener.onDialogPositiveClick(this)
                    })
                .setNegativeButton(R.string.cancel_text,
                    DialogInterface.OnClickListener { dialog, which ->
                        getDialog()?.cancel()
                    })
            Log.d("numBallsEdit52 = ", numBallsEdit?.text.toString())
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}