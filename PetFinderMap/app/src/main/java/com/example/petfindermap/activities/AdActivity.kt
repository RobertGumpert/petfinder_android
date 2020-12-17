package com.example.petfindermap.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.petfindermap.R
import com.example.petfindermap.services.AdService
import com.example.petfindermap.services.DialogsService
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter

class AdActivity : AppCompatActivity() {
    private val adService: AdService = AdService.getInstance()
    private val dialogsService: DialogsService = DialogsService.getInstance()

    private var adId: Long = -1
    private var isMine: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ad)
        super.getSupportActionBar()?.hide()

        val adId = intent.getStringExtra("adId").toLong()
        this.adId = adId
        val isMine = intent.getStringExtra("isMine").toBoolean()
        this.isMine = isMine
        val ad = adService.getAd(adId)
        if (ad == null) {
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }
        else {
            val textViewType: TextView  = findViewById(R.id.textViewType)
            var typeText = "Потерян!"
            var typeTextDate = "Потерялся: "
            if (!ad.typeAd)  {
                typeText = "Найден!"
                typeTextDate = "Нашелся: "
            }
            textViewType.text = typeText

            val textViewPet: TextView  = findViewById(R.id.textViewPet)
            textViewPet.text = ad.pet + ", " + ad.name + ", " + ad.breed

            val textViewName: TextView  = findViewById(R.id.textViewName)
            textViewName.text = "Владелец: " + ad.userName

            val textViewDate: TextView  = findViewById(R.id.textViewDate)
            val format = SimpleDateFormat("dd/MM/yyy")
            textViewDate.text = typeTextDate + format.format(ad.date)

            val textViewAddress: TextView  = findViewById(R.id.textViewAddress)
            textViewAddress.text = ad.address

            val textViewComment: TextView  = findViewById(R.id.textViewComment)
            textViewComment.text = ad.comment
        }

        if (isMine) {
            val buttonWrite: Button = findViewById(R.id.buttonWrite)
            buttonWrite.visibility = View.VISIBLE
            val buttonComplaint: Button = findViewById(R.id.buttonComplaint)
            buttonComplaint.visibility = View.INVISIBLE

            val buttonUpdate: Button = findViewById(R.id.buttonUpdate)
            buttonUpdate.visibility = View.VISIBLE
            val buttonClose: Button = findViewById(R.id.buttonClose)
            buttonClose.visibility = View.VISIBLE
        }
        else {
            val buttonWrite: Button = findViewById(R.id.buttonWrite)
            buttonWrite.visibility = View.VISIBLE
            val buttonComplaint: Button = findViewById(R.id.buttonComplaint)
            buttonComplaint.visibility = View.VISIBLE

            val buttonUpdate: Button = findViewById(R.id.buttonUpdate)
            buttonUpdate.visibility = View.INVISIBLE
            val buttonClose: Button = findViewById(R.id.buttonClose)
            buttonClose.visibility = View.INVISIBLE
        }
    }

    fun goBack (view: View?) {
        if (isMine) {
            val intent = Intent(this, MyAdsActivity::class.java)
            startActivity(intent)
        }
        else {
            toMain(view)
        }
    }

    fun toMain (view: View?) {
        val intent = Intent(this, MapsActivity::class.java)
        startActivity(intent)
    }

    fun createComplaint (view: View?) {
        val intent = Intent(this, ComplaintActivity::class.java)
        intent.putExtra("adId", adId.toString())
        startActivity(intent)
    }

    fun goToDialog (view: View) {
        //if (isMine) return
        val ad = adService.getAd(adId)
        if (ad == null) return
        dialogsService.createDialog(10002, ad.userName) {
            if (it != null) {
                val intent = Intent(this, MessagesActivity::class.java)
                intent.putExtra("dialog_id", it)
                startActivity(intent)
            }
        }
    }
}