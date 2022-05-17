package com.billion_dollor_company.securutysystem.activities.main.camera

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.billion_dollor_company.securutysystem.R
import com.billion_dollor_company.securutysystem.databinding.ActivityPicCaptureActivityBinding
import com.billion_dollor_company.securutysystem.model.MessageInfo
import com.billion_dollor_company.securutysystem.other.LoadingDialogBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
const val IMAGE_URI = "IMAGE_URI"
const val IMAGE_NAME = "IMAGE_NAME"

class PicCaptureActivity : AppCompatActivity() {
    private val CAMERA_PERM_CODE = 101
    private val CAMERA_REQ_CODE = 102


    private var backCamera: Boolean = true


    private var imageCapture: ImageCapture? = null

    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null

    private lateinit var cameraExecutor: ExecutorService

    private lateinit var vibrate: Vibrator
    private lateinit var player: MediaPlayer

    lateinit var user:FirebaseUser
    lateinit var database: FirebaseDatabase

    private lateinit var binding: ActivityPicCaptureActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPicCaptureActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initFirebase()
        player = MediaPlayer.create(this, R.raw.doorbell)
        vibrate = this.getSystemService(VIBRATOR_SERVICE) as Vibrator

        if (askCameraPermission()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        // Set up the listeners for take photo and video capture buttons
        binding.camera.setOnClickListener {
            player.start()
            player.setOnCompletionListener {
                takePhoto()
            }
        }


        cameraExecutor = Executors.newSingleThreadExecutor()
    }


    private fun initFirebase(){
        user = FirebaseAuth.getInstance().currentUser!!
        database = FirebaseDatabase.getInstance()
    }

    private fun askCameraPermission(): Boolean {
        Log.d("PIC_ACT0", "In the asking per function")
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("PIC_ACT0", "In the asking per function")
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERM_CODE
            )
        } else {
            return true
        }
        return false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERM_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera()
            }
        } else {
            Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }

    private fun playSound() {
        player.start()
        player.setOnCompletionListener {
//            sendPhoto()
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .build()

            // Select back camera as a default
            val cameraSelector = if (backCamera) {
                CameraSelector.DEFAULT_BACK_CAMERA
            } else {
                CameraSelector.DEFAULT_FRONT_CAMERA
            }

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }



    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time stamped name and MediaStore entry.

        val name = FirebaseAuth.getInstance().currentUser?.uid.toString()+"_"+SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/SecuritySysSend")
            }
        }

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(
                contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            .build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val uri = output.savedUri
                    val msg = "Photo capture succeeded: ${output.savedUri}"
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)
//                    val intent = Intent(applicationContext,PicDisplayActivity::class.java)
//                    intent.putExtra(IMAGE_URI,uri)
//                    intent.putExtra(IMAGE_NAME,name)
//                    startActivity(intent)

                    sendImage(uri!!,name)
                }
            }
        )
    }

    private fun sendImage(imageUri: Uri,imageName:String){
        val loadingDialogBar = LoadingDialogBar(this)
        loadingDialogBar.showDialog("Sending", R.raw.loading_plane)
        val databaseReference = database.reference.child("DeviceDatabase").child("Devices").child(user.uid).child("Messages")

        val storageReference = FirebaseStorage.getInstance().reference.child("Messages").child(user.uid).child(imageName)
        storageReference.putFile(imageUri).addOnSuccessListener {

            storageReference.downloadUrl.addOnSuccessListener {
                Log.d("DOWN_URL","The url is $it")
                val message = MessageInfo(name=user.displayName!!,text="",uid=user.uid,sentFromDevice=true, dp_bitmap = it.toString(), picPresent = true, photo = imageName, time = getTime())
                databaseReference.push().setValue(message)
                Toast.makeText(applicationContext,"Upload Successful",Toast.LENGTH_SHORT).show()
                loadingDialogBar.setCheck()
            }
        }.addOnFailureListener{
            Toast.makeText(applicationContext,"Upload Failed",Toast.LENGTH_LONG).show()
            loadingDialogBar.setError()
        }
    }

    private fun getTime():String{
        val c = Calendar.getInstance()
        val day = c.get(Calendar.DAY_OF_MONTH)
        val timeHr = c.get(Calendar.HOUR)
        val timeMin = c.get(Calendar.MINUTE)
        val timeMinString = if (timeMin < 10) {
            "0$timeMin"
        } else {
            timeMin.toString()
        }
        val monthName = getMonthName(c.get(Calendar.MONTH))
        return "$day $monthName, $timeHr:$timeMinString"
    }
    private fun getMonthName(no: Int): String {
        return when (no) {
            1 -> "Jan"
            2 -> "Feb"
            3 -> "Mar"
            4 -> "Apr"
            5 -> "May"
            6 -> "Jun"
            7 -> "Jul"
            8 -> "Aug"
            9 -> "Sep"
            10 -> "Oct"
            11 -> "Nov"
            else -> "Dec"
        }
    }


}