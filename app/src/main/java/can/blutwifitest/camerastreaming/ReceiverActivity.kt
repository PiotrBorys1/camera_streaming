package can.blutwifitest.camerastreaming

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.PersistableBundle
import android.os.SystemClock
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Surface
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Thread.sleep
import androidx.compose.ui.Modifier
import java.net.Socket

class ReceiverActivity : ComponentActivity() {
    val MESSAGE_RESPONSE_STREAM_VIDEO: Int = 2
    lateinit var matrixImageStreaming:Matrix
    var SocketWIFI: Socket?=null
    lateinit var handler: Handler
    var shownextframe by mutableStateOf(false)
    val CONNECTION_ESTABLISHED=1
    var connectionEstablished=false
    var muatblesharedflowofByearrayImage=MutableSharedFlow<ByteArray>(replay = 1)
    var muatblesharedflowofByearrayAudio=MutableSharedFlow<ByteArray>(replay = 1)
    var cansendnextquery=false
    var time_between_frames=SystemClock.elapsedRealtime()
    val frame_rate=30L
    var texttest by mutableStateOf<String>("")
    lateinit var audiotrack:AudioTrack
    lateinit var bitmapimagearray:Bitmap
    var bitmapStream by mutableStateOf<Bitmap?>(null)
    var MutableMAPOfVideBitmaps= mutableListOf<Bitmap>()
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val corountScop=CoroutineScope(Dispatchers.Default)
        MutableMAPOfVideBitmaps.add(BitmapFactory.decodeResource(resources, R.drawable.loading))
        bitmapStream= MutableMAPOfVideBitmaps[0]
        setContent{
            Column (modifier = Modifier.background(Color.LightGray)) {
                StreamScreen(btmp = bitmapStream!!)

            }

        }
        audiotrack=AudioTrack.Builder().setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_UNKNOWN)
                .build())
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(44100)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build())
            .setBufferSizeInBytes(50000)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()

        audiotrack.play()
        matrixImageStreaming= Matrix()
        matrixImageStreaming.postRotate(90f)

        /*corountScop.launch {

                while (true) {
                    sleep(50)
                    try
                    {
                        bitmapStream=MutableMAPOfVideBitmaps[0]
                        MutableMAPOfVideBitmaps.removeAt(0)
                    }
                    catch(e:Exception)
                    {

                    }
                }

            }*/


            corountScop.launch {

                    muatblesharedflowofByearrayAudio.collect { value ->
                        try {
                        audiotrack.write(
                            value,
                            0,
                            value.size,
                            AudioTrack.WRITE_NON_BLOCKING
                        )
                        }
                        catch(e:Exception)
                        {

                        }
                    }


            }
            corountScop.launch{
               /* muatblesharedflowofByearrayImage.collect{
                    value->texttest=value.toString()
                }*/
                    muatblesharedflowofByearrayImage.collect { value ->

                      try {
                          bitmapimagearray = BitmapFactory.decodeByteArray(
                              value,
                              0,
                              value.size
                          )
                          bitmapimagearray = Bitmap.createBitmap(
                              bitmapimagearray,
                              0,
                              0,
                              bitmapimagearray.width,
                              bitmapimagearray.height,
                              matrixImageStreaming,
                              true
                          )

                          bitmapStream = bitmapimagearray
                      }
                      catch(e:Exception)
                      {}


                }


            }







        handler=object:Handler(Looper.getMainLooper())
        {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                if (msg.what==CONNECTION_ESTABLISHED)
                {
                    corountScop.launch {
                        var timetOFFunction=0L
                        var timeOfFunctionStart=0L
                        var timetoSleep=0L
                        while (true) {
                            timeOfFunctionStart=SystemClock.elapsedRealtime()
                            ThreadReadWrite(SocketWIFI, handler, this@ReceiverActivity).run()
                            timetOFFunction=SystemClock.elapsedRealtime()-timeOfFunctionStart
                            if (timetOFFunction<=0 || timetOFFunction>=35)
                            {
                                timetoSleep=0
                            }
                            else
                            {
                                timetoSleep=35-timetOFFunction
                            }


                            sleep(timetoSleep)
                        }
                    }

                    connectionEstablished=true
                }
               /* else if(msg.what==MESSAGE_RESPONSE_STREAM_VIDEO) {
                    corountScop.launch{ ThreadReadWrite(SocketWIFI, handler,this@ReceiverActivity).run()}
                        /*corountScop.launch {
                            try {

                                val mysingl = MySingleton.getInstance()
                                val audiobytarray = mysingl.audioByteArraytoPlay
                                audiotrack.write(
                                    audiobytarray,
                                    0,
                                    audiobytarray.size,
                                    AudioTrack.WRITE_NON_BLOCKING
                                )
                                val ImageByteArray =
                                    mysingl.VideoImageBytearray //msg.obj as ByteArray

                                    bitmapimagearray = BitmapFactory.decodeByteArray(
                                        ImageByteArray,
                                        0,
                                        ImageByteArray.size
                                    )
                                    bitmapimagearray = Bitmap.createBitmap(
                                        bitmapimagearray,
                                        0,
                                        0,
                                        bitmapimagearray.width,
                                        bitmapimagearray.height,
                                        matrixImageStreaming,
                                        true
                                    )

                                bitmapStream=bitmapimagearray

                            } catch (e: Exception) {

                            }
                        }*/


                }*/
            }
        }

Thread {
    try {
        SocketWIFI =
            Socket(MySingleton.getInstance().wifiIP, MySingleton.getInstance().SocketPort, null, 0)
        if (SocketWIFI!!.isConnected) {
            SocketWIFI!!.soTimeout=2000
            runOnUiThread {handler.sendMessage(Message.obtain(handler, CONNECTION_ESTABLISHED))}
        } else {
            runOnUiThread {
                val text = getString(R.string.try_again)
                val duration = Toast.LENGTH_SHORT
                val toast = Toast.makeText(this, text, duration)
                toast.show()
            }
        }
    }
    catch (e:Exception)
    {

    }
}.start()
    }
    
    
    @Composable
    fun StreamScreen(btmp:Bitmap)
    {


            Image(bitmap = btmp.asImageBitmap(), contentDescription = null,modifier = Modifier.fillMaxSize())

        
    }

    @Composable
    fun WaitScreen()
    {
        Text(text = "Wait...")

    }
    
    
}