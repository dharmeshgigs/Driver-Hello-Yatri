package com.helloyatri.utils.fileselector


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.ClipData
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.media.ExifInterface
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.ext.SdkExtensions.getExtensionVersion
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.helloyatri.BuildConfig
import com.helloyatri.R
import com.helloyatri.ui.base.BaseActivity
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.math.roundToInt

@ActivityScoped
class MediaSelectHelper @Inject constructor(@ActivityContext private var mActivity: BaseActivity) :
        FileSelectorMethods, DefaultLifecycleObserver {

    private var extraMimeTypeVideo: Array<String> = arrayOf()
    private var canSelectMultipleFlag: Boolean = false
    private var canSelectMultipleVideo: Boolean = false
    private var isSelectAnyFile: Boolean = false
    private var isSelectingVideo: Boolean = false
    private var mMediaSelector: MediaSelector? = null
    private var fileForCameraIntent: String = ""
    private var photoFile: File? = null
    private var galleryIntent: Intent? = null
    private var videoIntent: Intent? = null

    private val permissionList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.CAMERA)
    } else {
        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)

    }

    private val permissionVideo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.CAMERA)
    } else {
        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
    }

    object Constant {
        const val CropSquare = "CropSquare"
        const val CropRectangle = "CropRectangle"
        const val CropCircle = "CropCircle"
    }

    private var cropType = Constant.CropSquare
    private var isCrop = true

    private var cameraResult: ActivityResultLauncher<Intent>
    private var activityResultLauncherCamera: ActivityResultLauncher<Array<String>>
    private var cropResult: ActivityResultLauncher<Intent>
    private var galleryResult: ActivityResultLauncher<Intent>
    private var galleryVideoResult: ActivityResultLauncher<Intent>
    private var activityResultLauncherGallery: ActivityResultLauncher<Array<String>>

    private var singlePhotoPickerLauncher: ActivityResultLauncher<PickVisualMediaRequest>? = null
    private var multiplePhotoPickerLauncher: ActivityResultLauncher<PickVisualMediaRequest>

    private var anyFilePicker: ActivityResultLauncher<Intent>
    private var cacheDir: File? = mActivity.cacheDir

    init {
        cameraResult = mActivity.registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                if (isSelectingVideo) {
                    mMediaSelector?.onCameraVideoUri(Uri.fromFile(File(fileForCameraIntent)))
                } else {/*compressImage(fileForCameraIntent).apply {
                            openCropViewOrNot(Uri.fromFile(File(this)))
                        }*/
                    openCropViewOrNot(Uri.fromFile(File(fileForCameraIntent)))
                }
            }
        }

        activityResultLauncherCamera = mActivity.registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()) { grantResults ->
            if (checkAllPermission(grantResults)) {
                if (isSelectingVideo) dispatchTakeVideoIntent()
                else openCamera()
            } else if (!checkAllPermission(grantResults)) {
                if (!deniedForever(grantResults)) {
                    mActivity.showToast("Please allow permission from setting ")
                }
            }
        }

        activityResultLauncherGallery = mActivity.registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()) { grantResults ->
            if (checkAllPermission(grantResults)) {
                if (isSelectAnyFile) {
                    openAnyIntent()
                } else {
                    if (isSelectingVideo) selectVideoFromGallery(extraMimeTypeVideo)
                    else openGallery()
                }
            } else if (!checkAllPermission(grantResults)) {
                if (!deniedForever(grantResults)) {
                    mActivity.showToast("Please allow permission from setting ")
                }
            }
        }

        singlePhotoPickerLauncher = mActivity.registerForActivityResult(
                ActivityResultContracts.PickVisualMedia()) { uri ->
            // Callback is invoked after the user selects a media item or closes the
            // photo picker.
            if (uri != null) {

                if (isSelectingVideo) mMediaSelector?.onVideoUri(uri)
                else openCropViewOrNot(uri)
                Log.d("PhotoPicker", "Selected URI: $uri")
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }


        anyFilePicker = mActivity.registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                result.data?.apply {
                    val selectedMedia: Uri? = data
                    val cR = mActivity.contentResolver
                    val mime = MimeTypeMap.getSingleton()
                    mime.getExtensionFromMimeType(cR.getType(selectedMedia!!))

                    val contentResolver = mActivity.contentResolver
                    when (contentResolver?.getType(selectedMedia)) {
                        "image/jpeg", "image/png" -> {
                            mMediaSelector?.onAnyFileSelected(OutPutFileAny(
                                    Uri.fromFile(getActualPath(selectedMedia, createImageFile())),
                                    FileType.Image))
                        }

                        "application/pdf" -> {
                            val copyFile = getActualPath(selectedMedia, createAnyFile(".pdf"))
                            mMediaSelector?.onAnyFileSelected(
                                    OutPutFileAny(Uri.fromFile(copyFile), FileType.Pdf))

                        }

                        "application/msword" -> {
                            val copyFile = getActualPath(selectedMedia, createAnyFile(".doc"))
                            mMediaSelector?.onAnyFileSelected(
                                    OutPutFileAny(Uri.fromFile(copyFile), FileType.Doc))

                        }

                        else -> {

                        }
                    }


                }
            }
        }

        multiplePhotoPickerLauncher = mActivity.registerForActivityResult(
                ActivityResultContracts.PickMultipleVisualMedia(10)) { imageUris: List<Uri> ->
            if (imageUris.isNotEmpty()) {
                if (isSelectingVideo) mMediaSelector?.onVideoURIList(imageUris as ArrayList<Uri>)
                else mMediaSelector?.onImageUriList(imageUris as ArrayList<Uri>)
            }
        }

        galleryVideoResult = mActivity.registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                result.data?.apply {
                    if (!canSelectMultipleVideo) {
                        val retriever = MediaMetadataRetriever()
                        val selectedMedia: Uri? = data
                        val cR = mActivity.contentResolver
                        val mime = MimeTypeMap.getSingleton()
                        mime.getExtensionFromMimeType(cR.getType(selectedMedia!!))
                        mMediaSelector?.onVideoUri(selectedMedia)
                        retriever.release()
                    } else {
                        if (this.clipData != null) {
                            val mClipData: ClipData? = this.clipData
                            val mArrayUri = ArrayList<Uri>()
                            for (i in 0 until mClipData?.itemCount!!) {
                                val item = mClipData.getItemAt(i)
                                val uri: Uri = item.uri
                                mArrayUri.add(uri)
                                // Get the cursor
                            }
                            mMediaSelector?.onVideoURIList(mArrayUri)
                        } else {
                            val selectedMedia: Uri? = data
                            selectedMedia?.let {
                                ArrayList<Uri>().apply {
                                    add(it)
                                    mMediaSelector?.onVideoURIList(this)
                                }
                            }
                        }
                    }
                }
            }

        }
        galleryResult = mActivity.registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                result.data?.apply {
                    if (!canSelectMultipleFlag) {
                        val retriever = MediaMetadataRetriever()
                        val selectedMedia: Uri? = data
                        val cR = mActivity.contentResolver
                        val mime = MimeTypeMap.getSingleton()
                        val type = mime.getExtensionFromMimeType(cR.getType(selectedMedia!!))
                        if (Objects.requireNonNull(type)
                                    .equals("png", ignoreCase = true) || type!!.equals("jpeg",
                                        ignoreCase = true) || type.equals("jpg",
                                        ignoreCase = true)) {
                            if (selectedMedia.toString().contains("image")) {
                                openCropViewOrNot(selectedMedia)
                            } else {
                                openCropViewOrNot(selectedMedia)
                            }
                        }
                        retriever.release()
                    } else {
                        if (this.clipData != null) {
                            val mClipData: ClipData? = this.clipData
                            val mArrayUri = ArrayList<Uri>()
                            for (i in 0 until mClipData?.itemCount!!) {
                                val item = mClipData.getItemAt(i)
                                val uri: Uri = item.uri
                                mArrayUri.add(uri)
                                // Get the cursor
                            }
                            mMediaSelector?.onImageUriList(mArrayUri)
                        } else {
                            val selectedMedia: Uri? = data
                            selectedMedia?.let {
                                ArrayList<Uri>().apply {
                                    add(it)
                                    mMediaSelector?.onImageUriList(this)
                                }
                            }
                        }
                    }
                }
            }
        }

        cropResult = mActivity.registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()) { data: ActivityResult ->
            if (data.resultCode == RESULT_OK) {
                val result = CropImage.getActivityResult(data.data)
                val resultUri = result.uri
                try {
                    mMediaSelector?.onImageUri(resultUri)
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
            }
        }

        canSelectMultipleImages(false)
    }

    override fun setLifecycle(lifecycleOwner: LifecycleOwner) {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    /*Register This for getting callbacks*/
    override fun registerCallback(mMediaSelector: MediaSelector) {
        this.mMediaSelector = null
        this.mMediaSelector = mMediaSelector
    }

    override fun selectVideoAndImagePicker(childFragmentManager: FragmentManager, isCrop1: Boolean,
                                           cropType: String, extraMimeType: Array<String>) {
        this.cropType = cropType
        this.isCrop = isCrop1
        isSelectingVideo = false
        isSelectAnyFile = false

        DialogSelectMediaForImageVideo {
            when (it.id) {
                R.id.layoutTakePhoto -> {
                    openCamera()
                }

                R.id.layoutSelectPhoto -> {
                    openGallery()
                }

                R.id.layoutTakeVideo -> {
                    isSelectingVideo = true
                    dispatchTakeVideoIntent()
                }

                R.id.layoutSelectVideo -> {
                    isSelectingVideo = true
                    selectVideoFromGallery(extraMimeType)
                }

            }
        }.apply {}.show(childFragmentManager, "")
    }

    override fun selectOptionsForImagePicker(isCrop1: Boolean, cropType: String) {
        this.cropType = cropType
        this.isCrop = isCrop1
        isSelectingVideo = false
        isSelectAnyFile = false


        val builder = AlertDialog.Builder(mActivity)
        builder.setTitle("Choose Image Source")

        val items = arrayOf(mActivity.resources?.getString(R.string.label_camera),
                mActivity.resources?.getString(R.string.label_gallery))

        builder.setItems(items) { _, which ->

            when (which) {
                0 -> openCamera()
                1 -> openGallery()
            }
        }

        builder.show()
    }


    override fun selectOptionsForVideoPicker(extraMimeType: Array<String>) {
        isSelectAnyFile = false
        isSelectingVideo = true

        val builder = AlertDialog.Builder(mActivity)
        builder.setTitle("Choose Video Source")

        val items = arrayOf(mActivity.resources?.getString(R.string.label_camera),
                mActivity.resources?.getString(R.string.label_gallery))

        builder.setItems(items) { _, which ->

            when (which) {
                0 -> dispatchTakeVideoIntent()
                1 -> selectVideoFromGallery(extraMimeType)
            }
        }

        builder.show()

    }

    override fun selectVideoFromGallery(extraMimeType: Array<String>) {
        extraMimeTypeVideo = extraMimeType
        canSelectMultipleVideo(canSelectMultipleVideo, extraMimeType)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(
                        mActivity,
                        Manifest.permission.READ_MEDIA_VIDEO) != PackageManager.PERMISSION_GRANTED) {
            activityResultLauncherGallery.launch(permissionVideo)
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(
                        mActivity,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            activityResultLauncherGallery.launch(permissionVideo)
        } else {
            if (isPhotoPickerAvailable() && extraMimeType.isEmpty()) {
                if (canSelectMultipleFlag) multiplePhotoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))
                else singlePhotoPickerLauncher?.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))
            } else {
                galleryVideoResult.launch(videoIntent)
            }
        }
    }

    override fun showImageMenu(view: View) {
        val popup = PopupMenu(mActivity, view)
        popup.menu.add("Camera")
        popup.menu.add("Gallery")
        popup.setOnMenuItemClickListener { item ->

            when (item.title.toString()) {
                "Camera" -> {
                    openCamera()
                }

                "Gallery" -> {
                    openGallery()
                }
            }
            true
        }
        popup.show()


    }

    // Core Code for selecting image
    // ****************************
    // ****************************
    // ****************************

    /**
     * Open camera to click image
     */
    private fun openCamera() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(
                        mActivity,
                        Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
            activityResultLauncherCamera.launch(permissionList)
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU && (ContextCompat.checkSelfPermission(
                        mActivity,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {
            activityResultLauncherCamera.launch(permissionList)
        } else {
            dispatchTakePictureIntent()
        }

    }

    /*private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Ensure that there's a camera activity to handle the intent
        // Create the File where the photo should go
        try {
            photoFile = createImageFile()
        } catch (ex: IOException) {
            // Error occurred while creating the File
            ex.printStackTrace()
        }
        // Continue only if the File was successfully created
        if (photoFile != null) {
            val photoURI = FileProvider.getUriForFile(mActivity,
                "${mActivity.packageName}.provider",
                photoFile!!)
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            cameraResult.launch(takePictureIntent)
        } else {
            takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            cameraResult.launch(takePictureIntent)
        }
    }*/


    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (mActivity.packageManager?.let { takePictureIntent.resolveActivity(it) } != null) {
            val photoFile: File? = try {
                createImageFile()
            } catch (ex: IOException) {
                // Error occurred while creating the File
                null
            }
            // Continue only if the File was successfully created
            photoFile?.also {
                val photoURI: Uri = FileProvider.getUriForFile(mActivity,
                        "${BuildConfig.APPLICATION_ID}.provider", it)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                cameraResult.launch(takePictureIntent)

            }
        }
    }


    private fun dispatchTakeVideoIntent() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(
                        mActivity,
                        Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
            activityResultLauncherCamera.launch(permissionVideo)
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU && (ContextCompat.checkSelfPermission(
                        mActivity,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                        mActivity,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            activityResultLauncherCamera.launch(permissionVideo)
        } else {
            val takePictureIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            // Ensure that there's a camera activity to handle the intent
            // Create the File where the photo should go
            try {
                photoFile = createAnyFile(".mp4")
                fileForCameraIntent = photoFile!!.absolutePath
            } catch (ex: IOException) {
                // Error occurred while creating the File
                ex.printStackTrace()
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                val photoURI =
                        FileProvider.getUriForFile(mActivity, "${mActivity.packageName}.provider",
                                photoFile!!)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                takePictureIntent.addFlags(
                        Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                cameraResult.launch(takePictureIntent)
            } else {
                takePictureIntent.addFlags(
                        Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                cameraResult.launch(takePictureIntent)
            }
        }
    }


    override fun openAnyIntent() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(
                        mActivity,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                activityResultLauncherGallery.launch(permissionList)
            } else {
                activityResultLauncherGallery.launch(permissionList)
            }
        } else {
            anyFilePicker.launch(getFileChooserIntent(arrayOf("image/*", "application/pdf")))
        }
    }


    override fun openPdfIntent() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(
                        mActivity,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                activityResultLauncherGallery.launch(permissionList)
            } else {
                activityResultLauncherGallery.launch(permissionList)
            }
        } else {
            anyFilePicker.launch(
                    getFileChooserIntent(arrayOf("application/msword", "application/pdf")))
        }
    }

    /**
     * Open gallery for select single image
     */
    private fun openGallery() {

        isSelectAnyFile = false


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(
                        mActivity,
                        Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
            activityResultLauncherGallery.launch(permissionList)
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(
                        mActivity,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            activityResultLauncherGallery.launch(permissionList)

        } else {
            if (isPhotoPickerAvailable()) {
                if (canSelectMultipleFlag) multiplePhotoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                else singlePhotoPickerLauncher?.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            } else {
                galleryResult.launch(galleryIntent)
            }
        }
    }


    private fun getFileChooserIntent(type: Array<String>): Intent {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = if (type.size == 1) type[0] else "*/*"
        if (type.isNotEmpty()) {
            intent.putExtra(Intent.EXTRA_MIME_TYPES, type)
        }
        return intent
    }

    private fun isPhotoPickerAvailable(): Boolean {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> true
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    getExtensionVersion(
                            Build.VERSION_CODES.R) >= ANDROID_R_REQUIRED_EXTENSION_VERSION
                } else {
                    true
                }
            }

            else -> false
        }
    }

    override fun canSelectMultipleImages(canSelect: Boolean) {
        canSelectMultipleFlag = canSelect
        if (canSelect) {
            galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
            galleryIntent!!.type = "*/*"
            galleryIntent!!.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
            galleryIntent!!.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        } else {
            galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            galleryIntent!!.type = "image/*"
        }
    }

    override fun canSelectMultipleVideo(canSelect: Boolean, extraMimeType: Array<String>) {
        canSelectMultipleVideo = canSelect

        videoIntent = Intent(Intent.ACTION_GET_CONTENT)
        videoIntent?.type = "*/*"

        if (extraMimeType.isEmpty()) videoIntent?.putExtra(Intent.EXTRA_MIME_TYPES,
                arrayOf("video/*"))
        else videoIntent?.putExtra(Intent.EXTRA_MIME_TYPES, extraMimeType)

        videoIntent?.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, canSelect)

    }

    override fun getThumbnailFromVideo(uri: Uri): File {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(mActivity, uri)
        val thumbnail = retriever.frameAtTime
        retriever.release()

        val file = createImageFile()

        try {
            FileOutputStream(file).use { out ->
                thumbnail?.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return file
    }


    private fun compressImage(filePath: String): String {
        /**
         * method requires EXTERNAL_STORAGE permission
         */

        var scaledBitmap: Bitmap? = null

        val options = BitmapFactory.Options()

        //      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
        //      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true
        var bmp = BitmapFactory.decodeFile(filePath, options)

        var actualHeight = options.outHeight
        var actualWidth = options.outWidth

        //      max Height and width values of the compressed image is taken as 816x612

        val maxHeight = 1920.0f//1280.0f;//816.0f;
        val maxWidth = 1080.0f//852.0f;//612.0f;

        var imgRatio = (actualWidth / actualHeight).toFloat()
        val maxRatio = maxWidth / maxHeight

        //      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight
                actualWidth = (imgRatio * actualWidth).toInt()
                actualHeight = maxHeight.toInt()
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth
                actualHeight = (imgRatio * actualHeight).toInt()
                actualWidth = maxWidth.toInt()
            } else {
                actualHeight = maxHeight.toInt()
                actualWidth = maxWidth.toInt()

            }
        }

        //setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight)

        //inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false

        //this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true
        options.inInputShareable = true
        options.inTempStorage = ByteArray(16 * 1024)

        try {
            //          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options)
        } catch (exception: OutOfMemoryError) {
            exception.printStackTrace()

        }

        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888)
        } catch (exception: OutOfMemoryError) {
            exception.printStackTrace()
        }

        val ratioX = actualWidth / options.outWidth.toFloat()
        val ratioY = actualHeight / options.outHeight.toFloat()
        val middleX = actualWidth / 2.0f
        val middleY = actualHeight / 2.0f

        val scaleMatrix = Matrix()
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)

        val canvas = Canvas(scaledBitmap!!)
        canvas.setMatrix(scaleMatrix)
        canvas.drawBitmap(bmp, middleX - bmp.width / 2, middleY - bmp.height / 2,
                Paint(Paint.FILTER_BITMAP_FLAG))

        //      check the rotation of the image and display it properly
        val exif: ExifInterface
        try {
            exif = ExifInterface(filePath)

            val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0)
            Log.d("EXIF", "Exif: $orientation")
            val matrix = Matrix()
            when (orientation) {
                6 -> {
                    matrix.postRotate(90f)
                    Log.d("EXIF", "Exif: $orientation")
                }

                3 -> {
                    matrix.postRotate(180f)
                    Log.d("EXIF", "Exif: $orientation")
                }

                8 -> {
                    matrix.postRotate(270f)
                    Log.d("EXIF", "Exif: $orientation")
                }
            }
            scaledBitmap =
                    Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.width, scaledBitmap.height,
                            matrix, true)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val out: FileOutputStream?
        val filename = createImageFile().path
        try {
            out = FileOutputStream(filename)
            //          write the compressed bitmap at the destination specified by filename.
            scaledBitmap!!.compress(Bitmap.CompressFormat.JPEG, 90, out)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

        return filename

    }


    private fun calculateInSampleSize(
            options: BitmapFactory.Options,
            reqWidth: Int,
            reqHeight: Int,
    ): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val heightRatio = (height.toFloat() / reqHeight.toFloat()).roundToInt()
            val widthRatio = (width.toFloat() / reqWidth.toFloat()).roundToInt()
            inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
        }
        val totalPixels = (width * height).toFloat()
        val totalReqPixelsCap = (reqWidth * reqHeight * 2).toFloat()
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++
        }

        return inSampleSize
    }


    private fun openCropViewOrNot(file: Uri) {
        if (isCrop) {
            val intent: Intent = when (this.cropType) {
                Constant.CropSquare -> CropImage.activity(file).setAspectRatio(4, 4)
                        .getIntent(mActivity)

                Constant.CropRectangle -> CropImage.activity(file).setAspectRatio(6, 4)
                        .getIntent(mActivity)

                Constant.CropCircle -> CropImage.activity(file)
                        .setCropShape(CropImageView.CropShape.OVAL).setAspectRatio(1, 1)
                        .getIntent(mActivity)

                else -> CropImage.activity(file).getIntent(mActivity)
            }
            cropResult.launch(intent)
        } else {
            mMediaSelector?.onImageUri(Uri.fromFile(getActualPath(file, createImageFile())))
        }
    }

    @SuppressLint("Recycle")
    private fun getActualPath(uri: Uri, outPutFile: File): File {
        val fos = FileOutputStream(outPutFile)
        val inputStream = mActivity.contentResolver.openInputStream(uri)

        val buffer = ByteArray(1024)
        var len: Int
        try {
            len = inputStream?.read(buffer)!!
            while (len != -1) {
                fos.write(buffer, 0, len)
                len = inputStream.read(buffer)
            }
            fos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return outPutFile
    }

    @Throws(IOException::class)
    fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
//        val storageDir = mActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(timeStamp, /* prefix */
                ".jpg", /* suffix */
                cacheDir      /* directory */)
        fileForCameraIntent = image.absolutePath
        return image
    }

    @Throws(IOException::class)
    private fun createAnyFile(extension: String): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
//        val storageDir = mActivity.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        return File.createTempFile(timeStamp, /* prefix */
                extension, /* suffix */
                cacheDir      /* directory */)
    }

    private fun clearCacheFile() {
        Log.e("ClearFileCalled", "****")
        if (cacheDir?.isDirectory == true) {
            Log.e("ClearFileCalled", "****IsDir")
            val files = cacheDir?.listFiles()
            if (files != null) {
                Log.e("ClearFileCalled", "****files != null")
                for (file in files) {
                    Log.e("ClearFileCalled", "****delete")
                    file.delete()
                }
            }
        }
    }


    private fun checkAllPermission(grantResults: Map<String, Boolean>): Boolean {
        for (data in grantResults) {
            if (!data.value) return false
        }
        return true
    }

    fun getFilePathFromUri(context: Context, uri: Uri, uniqueName: Boolean): String =
            if (uri.path?.contains("file://") == true) uri.path!!
            else getFileFromContentUri(context, uri, uniqueName).path

    private fun getFileFromContentUri(
            context: Context,
            contentUri: Uri,
            uniqueName: Boolean,
    ): File {
        // Preparing Temp file name
        val fileExtension = getFileExtension(context, contentUri) ?: ""
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = ("temp_file_" + if (uniqueName) timeStamp else "") + ".$fileExtension"
        // Creating Temp file
        val tempFile = File(context.cacheDir, fileName)
        tempFile.createNewFile()
        // Initialize streams
        var oStream: FileOutputStream? = null
        var inputStream: InputStream? = null

        try {
            oStream = FileOutputStream(tempFile)
            inputStream = context.contentResolver.openInputStream(contentUri)

            inputStream?.let { copy(inputStream, oStream) }
            oStream.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            // Close streams
            inputStream?.close()
            oStream?.close()
        }

        return tempFile
    }

    private fun getFileExtension(context: Context, uri: Uri): String? =
            if (uri.scheme == ContentResolver.SCHEME_CONTENT) MimeTypeMap.getSingleton()
                    .getExtensionFromMimeType(context.contentResolver.getType(uri))
            else uri.path?.let {
                MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(File(it)).toString())
            }

    @Throws(IOException::class)
    private fun copy(source: InputStream, target: OutputStream) {
        val buf = ByteArray(8192)
        var length: Int
        while (source.read(buf).also { length = it } > 0) {
            target.write(buf, 0, length)
        }
    }

    private fun deniedForever(grantResults: Map<String, Boolean>): Boolean {
        for (data in grantResults) {
            if (!mActivity.shouldShowRequestPermissionRationale(data.key)) return false
        }
        return true
    }


    companion object {
        private const val ANDROID_R_REQUIRED_EXTENSION_VERSION = 2
    }


    override fun onDestroy(owner: LifecycleOwner) {
        clearCacheFile()
        super.onDestroy(owner)
    }
}


data class OutPutFileAny(val uri: Uri, val type: FileType, val thumbImage: String? = null)

enum class FileType {
    Image, Pdf, Doc
}

interface FileSelectorMethods {
    fun setLifecycle(lifecycleOwner: LifecycleOwner)
    fun showImageMenu(view: View)
    fun registerCallback(mMediaSelector: MediaSelector)
    fun selectOptionsForVideoPicker(extraMimeType: Array<String> = arrayOf())

    /*Sample
     mediaSelectHelper.selectOptionsForVideoPicker(arrayOf("video/mp4", "video/avi"))
    */
    fun selectVideoFromGallery(extraMimeType: Array<String> = arrayOf())
    fun selectOptionsForImagePicker(
            isCrop1: Boolean,
            cropType: String = MediaSelectHelper.Constant.CropSquare,
    )

    fun selectVideoAndImagePicker(childFragmentManager: FragmentManager, isCrop1: Boolean,
                                  cropType: String = MediaSelectHelper.Constant.CropSquare,
                                  extraMimeType: Array<String> = arrayOf())

    fun canSelectMultipleImages(canSelect: Boolean)
    fun canSelectMultipleVideo(canSelect: Boolean, extraMimeType: Array<String> = arrayOf())
    fun openAnyIntent()
    fun openPdfIntent()
    fun getThumbnailFromVideo(uri: Uri): File
}

interface MediaSelector {
    fun onImageUri(uri: Uri) {}
    fun onVideoUri(uri: Uri) {
    }

    fun onCameraVideoUri(uri: Uri) {
    }

    fun onImageUriList(uriArrayList: ArrayList<Uri>) {}
    fun onVideoURIList(uriArrayList: ArrayList<Uri>) {

    }

    fun onAnyFileSelected(outPutFileAny: OutPutFileAny) {

    }

}