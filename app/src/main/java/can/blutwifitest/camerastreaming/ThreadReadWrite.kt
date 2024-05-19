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

class ThreadReadWrite(var socketWIFI: Socket?, val activity: ReceiverActivity) {

    @SuppressLint("SuspiciousIndentation")
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
   suspend fun run() {


        socketWIFI!!.outputStream.write(("STREAMVIDEO" + "\n").encodeToByteArray())
        socketWIFI!!.outputStream.flush()


        val inputStream=socketWIFI!!.getInputStream()
        try {
            var bytearrforSize = ByteArray(4)
                inputStream.read(bytearrforSize)
                val sizeOFArray = bytearrforSize[0].toUByte().toInt().shl(24) +
                        bytearrforSize[1].toUByte().toInt().shl(16) +
                        bytearrforSize[2].toUByte().toInt().shl(8) +
                        bytearrforSize[3].toUByte().toInt()

            val bytteArrayImage=inputStream.readNBytes(sizeOFArray)

            activity.muatblesharedflowofByearrayImage.emit(bytteArrayImage)


                bytearrforSize = ByteArray(4)

            inputStream.read(bytearrforSize)
            val sizeOFAudio = bytearrforSize[0].toUByte().toInt().shl(24) +
                    bytearrforSize[1].toUByte().toInt().shl(16) +
                    bytearrforSize[2].toUByte().toInt().shl(8) +
                    bytearrforSize[3].toUByte().toInt()

            val bytteArrayAudio=inputStream.readNBytes(sizeOFAudio)
                activity.muatblesharedflowofByearrayAudio.emit(bytteArrayAudio)

        }
        catch(e:Exception)
        {

        }

    }
}