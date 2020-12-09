package com.example.petfindermap.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.annotation.NonNull
import androidx.fragment.app.DialogFragment
import com.example.petfindermap.activities.AdActivity

class DialogConfirmationComplaint: DialogFragment() {
    @NonNull
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)

        builder.setTitle("Внимание!")
        builder.setMessage("Вы действительно хотите оставить жалобу?")
        builder.setPositiveButton("Да") { _: DialogInterface, i: Int ->
            val intent = Intent(context, AdActivity::class.java)
            startActivity(intent)
        }
        builder.setNegativeButton("Отмена", null)
        return builder.create()

    }
}