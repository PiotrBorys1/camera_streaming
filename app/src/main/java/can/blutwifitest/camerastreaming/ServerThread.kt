package can.blutwifitest.camerastreaming

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.SystemClock
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import kotlin.math.absoluteValue
import kotlin.math.min

class ServerThread(val activity:ServerActivity):Thread() {
    lateinit var socketw: Socket
    lateinit var reader:BufferedReader
    var  res = ""
    var oldAudioTimeStemple=0L
    var audiobytearraysize=15000
    var audioSampleRate=44100
    var audioBufferMaxSize=50000
    var flagaudiopermissiongranted=false
    lateinit var audiorecord: AudioRecord
    lateinit var ByteArryForAudioRecord:ByteArray
    var messageResponsetoClientByte:ByteArray= ByteArray(0)

    @SuppressLint("MissingPermission", "SuspiciousIndentation")
    override fun run() {
        super.run()

        val server = MySingleton.getInstance().ServerSocket
        socketw = server.accept()
        activity.connectedwithReceiver=true
        reader = BufferedReader(InputStreamReader(socketw.getInputStream()))
        oldAudioTimeStemple=activity.oldAudioTimeStemple
        audiobytearraysize=activity.audiobytearraysize
        audioSampleRate=activity.audioSampleRate
        audioBufferMaxSize=activity.audioBufferMaxSize
        flagaudiopermissiongranted=activity.flagaudiopermissiongranted
        if (flagaudiopermissiongranted) {
            audiorecord = AudioRecord.Builder().setAudioSource(MediaRecorder.AudioSource.DEFAULT)
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(audioSampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_IN_MONO)
                        .build()
                )
                .setBufferSizeInBytes(audioBufferMaxSize)
                .build()
            ByteArryForAudioRecord = ByteArray(audiobytearraysize)
            audiorecord.startRecording()
        }

        while (true)
        {

            res= reader.readLine()
            if (res.contains("STREAMVIDEO"))
            {
                try{
                val mysingelo=MySingleton.getInstance()
                var imgaetSend =mysingelo.PhotoImageSize
                    imgaetSend = imgaetSend.plus(mysingelo.StreamCameraImage)
                 if (flagaudiopermissiongranted) {
                        audiobytearraysize =
                            ((((SystemClock.elapsedRealtime() - oldAudioTimeStemple).toFloat() / 1000f) * audioSampleRate.toFloat()) * 2f).toInt()
                        if (audiobytearraysize > 25000) {
                            audiobytearraysize = 25000
                        } else if (audiobytearraysize < 1) {
                            audiobytearraysize = 1
                        }
                        oldAudioTimeStemple = SystemClock.elapsedRealtime()
                        ByteArryForAudioRecord = ByteArray(audiobytearraysize)
                        audiorecord.read(
                            ByteArryForAudioRecord,
                            0,
                            audiobytearraysize,
                            AudioRecord.READ_NON_BLOCKING
                        )
                        val AudioSize = ByteArray(4) { i ->
                            audiobytearraysize.shr((i - 3).absoluteValue * 8).toByte()
                        }
                        imgaetSend = imgaetSend.plus(AudioSize)
                        imgaetSend = imgaetSend.plus(ByteArryForAudioRecord)
                    }
                messageResponsetoClientByte =imgaetSend

            }
            catch (e:Exception)
            {
                messageResponsetoClientByte = " ".encodeToByteArray()
            }
                socketw.outputStream.write(messageResponsetoClientByte)

            }


        }



    }

}