package com.example.petfindermap.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.annotation.NonNull
import androidx.fragment.app.DialogFragment
import com.example.petfindermap.activities.DialogsActivity
import com.example.petfindermap.services.DialogsService

class DialogRemoveDialog: DialogFragment() {
    @NonNull
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Внимание!")
        builder.setMessage("Вы действительно хотите обновить страницу?")
        builder.setPositiveButton("Да", DialogInterface.OnClickListener() { dialogInterface: DialogInterface, i: Int ->
            val dialogs = Intent(context, DialogsActivity::class.java)
            startActivity(dialogs)
        })
        builder.setNegativeButton("Нет", null)
        return builder.create()
    }
}