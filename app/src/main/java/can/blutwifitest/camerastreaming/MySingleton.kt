package can.blutwifitest.camerastreaming

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import java.io.InputStream
import java.io.OutputStream
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import javax.net.ssl.SSLServerSocketFactory

class MySingleton private constructor() {

    companion object {


        @Volatile
        private var instance: MySingleton? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: MySingleton().also { instance = it }
            }
    }
    var wifiIP:String=""
    var SocketPort:Int=9999
    var ServerSocket:ServerSocket=ServerSocket(0)
    var GateHostName:String=""
    var StreamCameraImage = ByteArray(0)
    var flagDoNextPhoto=true
    var ServerActivity:ServerActivity?=null
    var PhotoImageSize=ByteArray(0)
    var VideoImageBytearray =ByteArray(0)
    var numberofBytesInAusioByteArray=10000
    var audioByteArraytoPlay=ByteArray(numberofBytesInAusioByteArray)
}