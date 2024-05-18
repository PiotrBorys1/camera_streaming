package can.blutwifitest.camerastreaming

import android.annotation.SuppressLint
import android.bluetooth.BluetoothSocket
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.Message
import androidx.annotation.RequiresApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.Socket
import java.util.logging.Handler
import kotlin.random.Random

class ThreadReadWrite(var socketWIFI: Socket?, var handler:android.os.Handler, val activity: ReceiverActivity) {

    val MESSAGE_RESPONSE_STREAM_VIDEO: Int = 2
    @SuppressLint("SuspiciousIndentation")
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
   suspend fun run() {
       // activity.muatblesharedflowofByearrayImage.emit(Random.nextInt(0,10))

        socketWIFI!!.outputStream.write(("STREAMVIDEO" + "\n").encodeToByteArray())
        socketWIFI!!.outputStream.flush()


        val inputStream=socketWIFI!!.getInputStream()
        try {
            var bytearrforSize = ByteArray(4)
            val mysingle = MySingleton.getInstance()

                inputStream.read(bytearrforSize)
                val sizeOFArray = bytearrforSize[0].toUByte().toInt().shl(24) +
                        bytearrforSize[1].toUByte().toInt().shl(16) +
                        bytearrforSize[2].toUByte().toInt().shl(8) +
                        bytearrforSize[3].toUByte().toInt()

                //mysingle.VideoImageBytearray=inputStream.readNBytes(sizeOFArray)
            val bytteArrayImage=inputStream.readNBytes(sizeOFArray)

            activity.muatblesharedflowofByearrayImage.emit(bytteArrayImage)


                bytearrforSize = ByteArray(4)



            inputStream.read(bytearrforSize)
            val sizeOFAudio = bytearrforSize[0].toUByte().toInt().shl(24) +
                    bytearrforSize[1].toUByte().toInt().shl(16) +
                    bytearrforSize[2].toUByte().toInt().shl(8) +
                    bytearrforSize[3].toUByte().toInt()
           // mysingle.audioByteArraytoPlay = inputStream.readNBytes(sizeOFAudio)
            val bytteArrayAudio=inputStream.readNBytes(sizeOFAudio)
                activity.muatblesharedflowofByearrayAudio.emit(bytteArrayAudio)
            //inputStream.close()
           /* val Msg = Message.obtain(
                handler,
                MESSAGE_RESPONSE_STREAM_VIDEO, 0, 0
            )
            handler.sendMessage(Msg)*/
        }
        catch(e:Exception)
        {

        }
        /*val Msg = Message.obtain(
            handler,
            MESSAGE_RESPONSE_STREAM_VIDEO, 0, 0
        )
        handler.sendMessage(Msg)*/
    }
}