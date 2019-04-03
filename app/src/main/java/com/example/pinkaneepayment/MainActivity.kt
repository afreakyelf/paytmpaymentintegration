package com.example.pinkaneepayment

import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat
import android.content.Intent
import android.widget.EditText
import android.view.WindowManager
import android.widget.Button


class MainActivity : AppCompatActivity() {
    private lateinit var orderid: EditText
    private lateinit var custid: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //initOrderId();
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        val btn = findViewById<Button>(R.id.start_transaction)
        orderid = findViewById(R.id.orderid)
        custid = findViewById(R.id.custid)

        btn.setOnClickListener {
            val intent = Intent(this@MainActivity, Checksum::class.java)
            intent.putExtra("orderid", orderid.text.toString())
            intent.putExtra("custid", custid.text.toString())
            startActivity(intent)
        }
        if (ContextCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.READ_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS),
                101
            )
        }
    }
}