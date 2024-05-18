package can.blutwifitest.camerastreaming

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.drawable.shapes.OvalShape
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.provider.Telephony
import android.telephony.SmsManager
import android.telephony.SubscriptionManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.Collections

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class MainActivity : ComponentActivity() {

    var TransmitterIPAdress=""
    var phonenumber by mutableStateOf("")
    var receivedIPAddr by mutableStateOf("")
    var TransmitterhostName=""
    lateinit var SMSManager:SmsManager
    lateinit var SMSReceiver:SMSReceiverAA
    var showTrsmRescv by mutableStateOf(false)
    var showPhonedial by mutableStateOf(false)
    var showPutIPAddr by mutableStateOf(false)
    val laucher_permission_SMS=registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (it)
        {
            sendSMS()
        }
    }



    val laucher_permission_READ_SMS=registerForActivityResult(ActivityResultContracts.RequestPermission()) {

        if(ContextCompat.checkSelfPermission(
                this,
                "android.permission.RECEIVE_SMS"
            ) == PackageManager.PERMISSION_GRANTED) {
            SMSReceiver= SMSReceiverAA(this)
            this.registerReceiver(
                SMSReceiver,
                IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION),
                Context.RECEIVER_EXPORTED
            )
            showPutIPAddr=true
        }
        else
        {
            laucher_permission_RECEIVE_SMS.launch("android.permission.RECEIVE_SMS")
        }

    }
    val laucher_permission_RECEIVE_SMS=registerForActivityResult(ActivityResultContracts.RequestPermission()) {

        if (it)
        { SMSReceiver=SMSReceiverAA(this)
            this.registerReceiver(
            SMSReceiver,
            IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION),
            Context.RECEIVER_EXPORTED
        )
            showPutIPAddr=true

        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        phonenumber=this@MainActivity.getSharedPreferences(
            "Phone Number",
            Context.MODE_PRIVATE
        ).getString("PHONENUMBER", "")!!
        setContent {
            Box( modifier = Modifier
                .background(color = Color.White)
                .fillMaxSize()) {

                TextButton(
                    onClick = { showTrsmRescv=true },
                    modifier = Modifier
                        .padding(1.dp)
                        .align(Alignment.Center)
                        .border(3.dp, Color.Red),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = Color.Gray
                    )

                ) {
                    Text(resources.getString(R.string.StartStrm), fontSize = 35.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                }
                if (showTrsmRescv)
                {
                    DialogTrsmRCV {
                        showTrsmRescv=false
                    }
                }

                //showPutPhoneDial=showPhonedial

                if (showPhonedial)
                {
                    DialogPutPhoneNumber {
                        showPhonedial=false
                    }
                }


               if(showPutIPAddr)
               {
                   DialogPutIPAdress {
                       showPutIPAddr=false
                   }
               }
            }
        }


    }


    @Composable
    fun MainActivUI()
    {

    }


    @Composable
    fun DialogTrsmRCV(
        onDismissRequest: () -> Unit
    ) {
        var isTransimitter by remember { mutableStateOf(true) }
        var isReceiver by remember { mutableStateOf(false) }
        Dialog(onDismissRequest = { onDismissRequest() },) {

            Column( modifier = Modifier
                .background(
                    Color.LightGray
                )
                .border(BorderStroke(2.dp, Color.Red)))
            {Text(text = resources.getString(R.string.ChsifTrRCv), fontSize = 20.sp, modifier = Modifier.padding(10.dp))
                Row (horizontalArrangement = Arrangement.Start){

                    Checkbox(
                        modifier = Modifier
                            .layoutId("Transmitter")
                            .padding(5.dp),
                        checked = isTransimitter,
                        onCheckedChange = {
                            isTransimitter=it
                            if (isTransimitter)
                            {
                                isReceiver=false
                            }
                            else
                            {
                                isTransimitter=true
                            }
                        }

                    )
                    Text(text = resources.getString(R.string.transmitter), fontSize = 20.sp, fontStyle = FontStyle.Italic,modifier = Modifier.padding(5.dp))

                }
                Row (horizontalArrangement = Arrangement.Start) {
                    Checkbox(
                        modifier = Modifier
                            .layoutId("Transmitter")
                            .padding(5.dp),
                        checked = isReceiver,
                        onCheckedChange = {
                            isReceiver=it
                            if (isReceiver)
                            {
                                isTransimitter=false
                            }
                            else
                            {
                                isReceiver=true
                            }
                        }
                    )

                        Text(
                            text = resources.getString(R.string.receiver),
                            fontSize = 20.sp,
                            modifier = Modifier.padding(5.dp),
                            fontStyle = FontStyle.Italic
                        )


                }

                TextButton(
                    onClick = {
                        if (isTransimitter)
                    {
                        showPhonedial= true

                    }
                        else
                        {
                            when {
                                ContextCompat.checkSelfPermission(
                                    this@MainActivity,
                                    "android.permission.READ_SMS"
                                ) == PackageManager.PERMISSION_GRANTED -> {

                                    if(ContextCompat.checkSelfPermission(
                                            this@MainActivity,
                                            "android.permission.RECEIVE_SMS"
                                        ) == PackageManager.PERMISSION_GRANTED) {
                                        SMSReceiver=SMSReceiverAA(this@MainActivity)
                                        this@MainActivity.registerReceiver(
                                            SMSReceiver,
                                            IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION),
                                            Context.RECEIVER_EXPORTED
                                        )
                                        showPutIPAddr=true
                                    }
                                    else
                                    {
                                        laucher_permission_RECEIVE_SMS.launch("android.permission.RECEIVE_SMS")
                                    }

                                }

                                else -> {
                                    laucher_permission_READ_SMS.launch("android.permission.READ_SMS")
                                }
                            }

                        }
                        showTrsmRescv= false
                              },
                    modifier = Modifier.padding(8.dp),
                ) {
                    Box (modifier = Modifier.fillMaxWidth()) {
                        Text(resources.getString(R.string.Confirm), fontSize = 20.sp,modifier = Modifier
                            .padding(5.dp)
                            .align(Alignment.Center))
                    }

                }
            }


        }
    }


    @Composable
    fun DialogPutPhoneNumber(
        onDismissRequest: () -> Unit
    ) {

        Dialog(onDismissRequest = { onDismissRequest() }) {

            Column( modifier = Modifier
                .background(
                    Color.LightGray
                )
                .border(BorderStroke(2.dp, Color.Red)))
            {
                Text(text = resources.getString(R.string.put_phone_num), fontSize = 20.sp, modifier = Modifier.padding(10.dp))
                Box(modifier= Modifier
                    .fillMaxWidth()
                    .onSizeChanged { size ->

                    }) {

                    TextField(
                        maxLines = 1,
                        modifier = Modifier.align(Alignment.Center),
                        textStyle = TextStyle(
                            color = Color.Black,
                            fontSize = 20.sp,
                            fontStyle = FontStyle.Italic,
                            letterSpacing = 0.5.em,
                        ), value = phonenumber, onValueChange = {
                            phonenumber = it
                        },
                        placeholder = {
                            Text(
                                text = "Enter phone number",
                                color = Color.Gray,
                                fontSize = 16.sp
                            )
                        })

                }
                TextButton(
                    onClick = {
                        this@MainActivity.getSharedPreferences(
                            "Phone Number",
                            Context.MODE_PRIVATE
                        ).edit().putString("PHONENUMBER",phonenumber).apply()
                        OnCLickTransmReceicChoose() },
                    modifier = Modifier.padding(8.dp),
                ) {
                    Box(modifier = Modifier.fillMaxWidth() )
                    {
                        Text(resources.getString(R.string.Confirm), fontSize = 20.sp, modifier = Modifier.align(
                            Alignment.Center))
                    }

                }

            }


        }
    }



    @Composable
    fun DialogPutIPAdress(
        onDismissRequest: () -> Unit
    ) {

        Dialog(onDismissRequest = { onDismissRequest() }) {

            Column( modifier = Modifier
                .background(
                    Color.LightGray
                )
                .border(BorderStroke(2.dp, Color.Red)))
            {
                Text(text = resources.getString(R.string.IP_address), fontSize = 20.sp, modifier = Modifier.padding(10.dp))
                Box(modifier= Modifier.fillMaxWidth()) {
                    TextField(
                        maxLines = 1,
                        modifier= Modifier
                            .align(Alignment.Center),
                        textStyle = TextStyle(
                        color = Color.Black,
                        fontSize = 20.sp,
                        fontStyle = FontStyle.Italic,
                        letterSpacing = 0.5.em,
                    ),
                        value = receivedIPAddr,
                        onValueChange = { receivedIPAddr = it },
                        placeholder = {
                            Text(
                                text = "Enter IP address",
                                color = Color.Gray,
                                fontSize = 16.sp
                            )
                        })
                }
                TextButton(onClick = {
                    this@MainActivity.startActivity(Intent(this@MainActivity, ReceiverActivity::class.java))
                }) {
                    Box(modifier = Modifier.fillMaxWidth() )
                    {
                        Text(resources.getString(R.string.Confirm), fontSize = 20.sp, modifier = Modifier.align(
                            Alignment.Center))
                    }

                }
            }


        }
    }






        fun OnCLickTransmReceicChoose()
        {
            Thread {
            val interfaces= mutableListOf<NetworkInterface>()
            Collections.list(NetworkInterface.getNetworkInterfaces()).forEach {
                if (it.isUp)
                {
                    interfaces.add(it)
                }
            }
            for (intf in interfaces) {
                val addrs: List<InetAddress> = Collections.list(intf.inetAddresses)

                for (addr in addrs) {
                    if (!addr.isLoopbackAddress && !addr.isLinkLocalAddress && !addr.isSiteLocalAddress) {

                        if (!addr.hostAddress!!.contains(':')) {
                            TransmitterIPAdress = addr.hostName
                            TransmitterhostName = addr.hostAddress!!
                        }

                    }
                }
            }

                runOnUiThread {
                    when {
                        ContextCompat.checkSelfPermission(
                            this,
                            "android.permission.SEND_SMS"
                        ) == PackageManager.PERMISSION_GRANTED -> {
                            sendSMS()

                        }

                        else -> {
                            laucher_permission_SMS.launch("android.permission.SEND_SMS")
                        }
                    }
                }
            }.start()



        }


    fun sendSMS()
    {try {
        SMSManager=this@MainActivity.getSystemService(SmsManager::class.java).createForSubscriptionId(
            SubscriptionManager.getDefaultSmsSubscriptionId())
        SMSManager.sendTextMessage(
            this@MainActivity.getSharedPreferences(
                "Phone Number",
                Context.MODE_PRIVATE
            )
                .getString("PHONENUMBER", ""),
            null,
            "IP GATE:" + TransmitterIPAdress+ "+PORT:" + MySingleton.getInstance().ServerSocket.localPort +
                    "@HOST NAME:" + TransmitterhostName,
            null,
            null
        )

        val inte = Intent(
            this@MainActivity,
            ServerActivity::class.java
        ).putExtra("IsWifiDevice", false)
        this@MainActivity.startActivity(inte)


    }
    catch (e:java.lang.Exception)
    {
        val text = getString(R.string.wrong_phone)
        val duration = Toast.LENGTH_SHORT
        val toast = Toast.makeText(this, text, duration)
        toast.show()
    }}


}