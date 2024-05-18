package can.blutwifitest.camerastreaming

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ImageReader
import android.os.Handler
import android.os.Looper
import android.os.Message
import java.io.ByteArrayOutputStream
import kotlin.math.absoluteValue


class ThreadCameraForHandler:Thread() {
    var mHandler: Handler? = null

    override fun run() {
        Looper.prepare()
        mHandler = object : Handler(Looper.myLooper()!!) {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)

            }

        }
        Looper.loop()
    }
}