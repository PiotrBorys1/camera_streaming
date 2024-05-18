package can.blutwifitest.camerastreaming

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Message
import android.provider.Telephony
import androidx.annotation.RequiresApi

class SMSReceiverAA(val activity: MainActivity) : BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("SuspiciousIndentation", "UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {

        val FullMessage= Telephony.Sms.Intents.getMessagesFromIntent(intent)
        var DataFromSMS=""
        FullMessage.forEach { mess-> DataFromSMS+=mess.displayMessageBody }
        if (DataFromSMS.contains("IP GATE:") && DataFromSMS.contains("+PORT:") ) {
            MySingleton.getInstance().wifiIP=DataFromSMS.substring(8,DataFromSMS.indexOf('+'))
            MySingleton.getInstance().SocketPort=Integer.parseInt(DataFromSMS.substring(DataFromSMS.indexOf('+')+6,DataFromSMS.indexOf('@')))
            MySingleton.getInstance().GateHostName=DataFromSMS.substring(DataFromSMS.indexOf('@')+11)
            activity.receivedIPAddr=MySingleton.getInstance().wifiIP
        }



    }
}