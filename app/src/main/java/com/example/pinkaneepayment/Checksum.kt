package com.example.pinkaneepayment

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import com.paytm.pgsdk.PaytmOrder
import com.paytm.pgsdk.PaytmPGService
import com.paytm.pgsdk.PaytmPaymentTransactionCallback
import org.json.JSONException
import java.util.ArrayList
import java.util.HashMap


class Checksum : AppCompatActivity(), PaytmPaymentTransactionCallback {
    internal var custid: String? = ""
    internal var orderId: String? = ""
    internal var mid = ""
    private var checksumHASH = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        val intent = intent
        orderId = intent.extras!!.getString("orderid")
        custid = intent.extras!!.getString("custid")
        mid = "elfSta50398398448785"
        val dl = SendUserDetailToServer()
        dl.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
    }

    @SuppressLint("StaticFieldLeak")
    inner class SendUserDetailToServer : AsyncTask<ArrayList<String>, Void, String>() {

        private val dialog = ProgressDialog(this@Checksum)
        private var url = "https://dockside-hairpins.000webhostapp.com/generateChecksum.php"
        //var url = "https://www.blueappsoftware.com/payment/payment_paytm/generateChecksum.php"

      //  var varifyurl = "https://pguat.paytm.com/paytmchecksum/paytmCallback.jsp"
      private var varifyurl = "https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID=$orderId"

        override fun onPreExecute() {
            this.dialog.setMessage("Please wait")
            this.dialog.show()
        }

        override fun doInBackground(vararg alldata: ArrayList<String>): String {
            val jsonParser = JSONParser(this@Checksum)
            val param = "MID=" + mid +
                    "&ORDER_ID=" + orderId +
                    "&CUST_ID=" + custid +
                    "&CHANNEL_ID=WAP&TXN_AMOUNT=100&WEBSITE=WEBSTAGING" +
                    "&CALLBACK_URL=" + varifyurl + "&INDUSTRY_TYPE_ID=Retail"
            val jsonObject = jsonParser.makeHttpRequest(url, "POST", param)
            Log.e("Checksum result >>", jsonObject.toString())
            try {
                checksumHASH = if (jsonObject.has("CHECKSUMHASH")) jsonObject.get("CHECKSUMHASH").toString() else ""
                Log.e("Checksum result >>", checksumHASH)
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            return checksumHASH
        }

        override fun onPostExecute(result: String) {
            Log.e(" setup acc ", "  signup result  $result")
            if (dialog.isShowing) {
                dialog.dismiss()
            }
            val service = PaytmPGService.getStagingService()
            val paramMap = HashMap<String, String>()
            paramMap["MID"] = mid
            paramMap["CALLBACK_URL"] = varifyurl
            paramMap["ORDER_ID"] = orderId!!
            paramMap["CUST_ID"] = custid!!
            paramMap["CHANNEL_ID"] = "WAP"
            paramMap["TXN_AMOUNT"] = "100"
            paramMap["WEBSITE"] = "WEBSTAGING"
         paramMap["CHECKSUMHASH"] = checksumHASH
            paramMap["PAYMENT_TYPE_ID"] = "CC"
            paramMap["INDUSTRY_TYPE_ID"] = "Retail"
            val order = PaytmOrder(paramMap)
            Log.e("Checksum ", "param $paramMap")
            service.initialize(order, null)
            // start payment service call here
            service.startPaymentTransaction(
                this@Checksum, true, true,
                this@Checksum
            )
        }
    }

    override fun onTransactionResponse(bundle: Bundle) {
        Log.e("Checksum ", " respon true $bundle")
        Toast.makeText(applicationContext,bundle.toString(),Toast.LENGTH_SHORT).show()
    }

    override fun networkNotAvailable() {}
    override fun clientAuthenticationFailed(s: String) {}
    override fun someUIErrorOccurred(s: String) {
        Log.e("Checksum ", " ui fail respon  $s")
    }

    override fun onErrorLoadingWebPage(i: Int, s: String, s1: String) {
        Log.e("Checksum ", " error loading pagerespon true $s  s1 $s1")
    }

    override fun onBackPressedCancelTransaction() {
        Log.e("Checksum ", " cancel call back respon  ")
    }

    override fun onTransactionCancel(s: String, bundle: Bundle) {
        Log.e("Checksum ", "  transaction cancel ")
    }
}