package can.blutwifitest.camerastreaming

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.SystemClock
import android.system.Os.socket
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.ServerSocket
import java.net.Socket
import java.util.*
import java.util.concurrent.Executor
import kotlin.math.absoluteValue


class ServerActivity : AppCompatActivity() {

    var connectedwithReceiver by mutableStateOf(false)
    var oldAudioTimeStemple=0L
    var audiobytearraysize=15000
    val audioSampleRate=44100
    val audioBufferMaxSize=50000
    var flagaudiopermissiongranted=false
    lateinit var serverthread:ServerThread

    @SuppressLint("MissingPermission")
    val laucher_permission_CAMERA=registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {

        if (!it.containsValue(false))
        {
            this.startService(Intent(this,CameraService::class.java))
            flagaudiopermissiongranted=true
        }
    }


    @RequiresApi(Build.VERSION_CODES.P)
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Column (modifier = Modifier.background(Color.LightGray)){


                if (connectedwithReceiver )
                {
                    Image(imageVector = ImageVector.vectorResource(R.drawable.baseline_thumb_up_24), contentDescription = "ff",Modifier.fillMaxSize())
                }
                else
                {
                    AsyncImage(
                        model = R.drawable.loading,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )

                }
            }
        }
        MySingleton.getInstance().ServerActivity=this
        if (ActivityCompat.checkSelfPermission(this,"android.permission.CAMERA") == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this,"android.permission.RECORD_AUDIO") == PackageManager.PERMISSION_GRANTED) {
            this.startService(Intent(this,CameraService::class.java))
            flagaudiopermissiongranted=true
        }
        else
        {
            laucher_permission_CAMERA.launch(arrayOf("android.permission.CAMERA","android.permission.RECORD_AUDIO"))
        }


            serverthread = ServerThread(this)
            serverthread.start()


        val backCallback=object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {


                try {
                    serverthread.audiorecord.stop()
                    serverthread.audiorecord.release()
                }
                catch(e:Exception)
                {

                }
                finish()
            }

        }
        onBackPressedDispatcher.addCallback(backCallback)



    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

   /* override fun onResume() {
        super.onResume()
        mainExecutor.execute(runnablewaitGiff)
    }*/


}