package can.blutwifitest.camerastreaming

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.hardware.camera2.*
import android.hardware.camera2.params.OutputConfiguration
import android.hardware.camera2.params.SessionConfiguration
import android.media.AudioRecord
import android.media.ImageReader
import android.os.Build
import android.os.IBinder
import android.os.Message
import android.util.Range
import android.util.Size
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible

import java.io.ByteArrayOutputStream
import java.lang.Thread.sleep
import java.util.*
import java.util.concurrent.Executor
import kotlin.math.absoluteValue
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class CameraService : Service() {

lateinit var ImageREADERR: ImageReader
lateinit var CameraDev:CameraDevice
lateinit var CaptureRequesst:CaptureRequest
val threadforHandler=ThreadCameraForHandler()
val cameraStateCallback=object :CameraDevice.StateCallback(){


        override fun onOpened(p0: CameraDevice) {
            CameraDev = p0
            ImageREADERR =
                ImageReader.newInstance(400, 400, ImageFormat.JPEG, 3 )
            ImageREADERR.setOnImageAvailableListener(onImageAvailableListener, threadforHandler.mHandler)
            try {
                val outputConfig=OutputConfiguration(ImageREADERR.surface)
                val exetutror=Executor {r->Thread(r).start()}
                CameraDev.createCaptureSession(SessionConfiguration(SessionConfiguration.SESSION_REGULAR,
                    mutableListOf(outputConfig),exetutror,sessionStateCallback
                ))

            } catch (e: CameraAccessException) {

            }
        }

        override fun onDisconnected(p0: CameraDevice) {

        }

        override fun onError(p0: CameraDevice, p1: Int) {

        }

    }


    val onImageAvailableListener=object : ImageReader.OnImageAvailableListener
    {
        @SuppressLint("SuspiciousIndentation")
        override fun onImageAvailable(p0: ImageReader?) {
            try {
                val MySingleton=MySingleton.getInstance()
                val img = p0!!.acquireLatestImage()
                    val buf = img.planes[0].buffer
                    val ImageByteArray= ByteArray(buf.capacity())
                    buf.get(ImageByteArray)
                        MySingleton.StreamCameraImage=ImageByteArray
                        MySingleton.PhotoImageSize=ByteArray(4) { i ->
                            ImageByteArray.size.shr((i - 3).absoluteValue * 8).toByte()
                        }
                img.close()
            }
            catch (e:Exception)
            {

            }

        }

    }



    val sessionStateCallback=object :CameraCaptureSession.StateCallback(){
        override fun onConfigured(p0: CameraCaptureSession) {

            val builder = CameraDev.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            builder.addTarget(ImageREADERR.surface)
            builder.set(CaptureRequest.CONTROL_CAPTURE_INTENT,CaptureRequest.CONTROL_CAPTURE_INTENT_PREVIEW)
            builder.set(CaptureRequest.CONTROL_AF_MODE, CameraMetadata.CONTROL_AF_MODE_AUTO);
            CaptureRequesst=builder.build()
            p0.setRepeatingRequest(CaptureRequesst, captureCallbacck,threadforHandler.mHandler)


        }

        override fun onConfigureFailed(p0: CameraCaptureSession) {

        }

    }


    val captureCallbacck=object : CameraCaptureSession.CaptureCallback(){

        override fun onCaptureCompleted(
            session: CameraCaptureSession,
            request: CaptureRequest,
            result: TotalCaptureResult
        ) {
            super.onCaptureCompleted(session, request, result)

        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
    }

    @SuppressLint("MissingPermission")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val CameraManager:CameraManager=getSystemService(CAMERA_SERVICE) as CameraManager
        var choosenID=""
        try{
            val CameraidList=CameraManager.cameraIdList
            for (id in CameraidList)
            {
                val cameraCharacterictics=CameraManager.getCameraCharacteristics(id)
                if (cameraCharacterictics.get(CameraCharacteristics.LENS_FACING)==CameraMetadata.LENS_FACING_BACK)
                {
                  choosenID=id
                 break
                }
            }

            threadforHandler.start()
            CameraManager.openCamera(choosenID, cameraStateCallback, null)
        }
        catch (e:Exception)
        {

        }

        return super.onStartCommand(intent, flags, startId)
    }

     override fun onDestroy() {
        super.onDestroy()

        try
        {
            CameraDev.close()
            ImageREADERR.close()
        }
        catch(e:Exception)
        {

        }


    }

}