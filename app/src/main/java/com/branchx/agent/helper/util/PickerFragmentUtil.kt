package com.branchx.agent.helper.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.branchx.agent.helper.extns.reduceFileSize
import com.branchx.agent.helper.extns.size
import com.branchx.agent.ui.dialog.AppDialog
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


object PickerFragmentUtil {


    var filePickPath: String? = null

    const val FILE_PICKER_CODE = 1
    const val IMAGE_PICKER_CODE = 2
    const val CAMERA_PICKER_CODE = 3


    fun pickFile(fragment: Fragment?) {
        val mimeTypes = arrayOf("image/*", "application/pdf")
        var chooseFile = Intent(Intent.ACTION_GET_CONTENT)
        chooseFile.type = "image/*|application/pdf"
        chooseFile.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        chooseFile = Intent.createChooser(chooseFile, "Choose a file")
        fragment?.startActivityForResult(chooseFile, FILE_PICKER_CODE)
    }


    fun pickImage(fragment: Fragment?) {
        val chooseImage = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        fragment?.startActivityForResult(chooseImage, IMAGE_PICKER_CODE);
    }

    fun cameraCapture(fragment: Fragment?) {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        fragment?.startActivityForResult(cameraIntent, CAMERA_PICKER_CODE)
    }


    suspend fun processForRightAngleImage(filePath: String) = withContext(Dispatchers.IO) {
        getRightAngleImage(filePath)
    }


    private fun getRightAngleImage(photoPath: String): String {
        try {
            val ei = ExifInterface(photoPath)
            val orientation: Int = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            var degree = 0f
            degree = when (orientation) {
                ExifInterface.ORIENTATION_NORMAL -> 0f
                ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                ExifInterface.ORIENTATION_UNDEFINED -> 0f
                else -> 90f
            }
            return rotateImage(degree, photoPath)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return photoPath
    }

    private fun rotateImage(degree: Float, imagePath: String): String {
        if (degree <= 0) {
            return imagePath
        }
        try {
            var b = BitmapFactory.decodeFile(imagePath)
            val matrix = Matrix()
            if (b.width > b.height) {
                matrix.setRotate(degree)
                b = Bitmap.createBitmap(b, 0, 0, b.width, b.height,
                        matrix, true)
            }
            val fOut = FileOutputStream(imagePath)
            val imageName = imagePath.substring(imagePath.lastIndexOf("/") + 1)
            val imageType = imageName.substring(imageName.lastIndexOf(".") + 1)
            val out = FileOutputStream(imagePath)
            if (imageType.equals("png", ignoreCase = true)) {
                b.compress(Bitmap.CompressFormat.PNG, 100, out)
            } else if (imageType.equals("jpeg", ignoreCase = true) || imageType.equals("jpg", ignoreCase = true)) {
                b.compress(Bitmap.CompressFormat.JPEG, 100, out)
            }
            fOut.flush()
            fOut.close()
            b.recycle()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return imagePath
    }

    private fun dispatchTakePictureIntent(fragment: Fragment) {

        val context = fragment.requireContext()

        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->

            takePictureIntent.resolveActivity(context.packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile(context)
                } catch (ex: IOException) {
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {

                    val photoURI: Uri = FileProvider.getUriForFile(
                            context,
                            context.applicationContext?.packageName.toString() + ".provider",
                            it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    fragment.startActivityForResult(takePictureIntent, CAMERA_PICKER_CODE)
                }
            }
        }
    }


    private fun createImageFile(context: Context): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
                "JPEG_${timeStamp}_", /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            filePickPath = absolutePath
        }
    }




    fun pickMultiple(fragment: Fragment?) {
        if (fragment == null) return@pickMultiple
        val context = fragment.requireActivity()
        PermissionUtil.storageCameraPermissions(context) { isGranted ->
            if (isGranted) {
                AppDialog.imageAndCameraChooser(context,
                        onCamera = {
                           dispatchTakePictureIntent(fragment)
                        }, onGallery = {
                    pickImage(fragment)
                })
            }
        }
    }



    fun onActivityResult(fragment: Fragment, requestCode: Int, resultCode: Int, data: Intent?,onFile : (File) ->Unit) {

        if(filePickPath ==null && data == null) return@onActivityResult

        val context = fragment.requireContext()

        if (IMAGE_PICKER_CODE == requestCode || FILE_PICKER_CODE == requestCode) {
            GlobalScope.launch(Dispatchers.Main) {

                val dialog = AppDialog.progress(context)

                var filePath = FileUtils.getPath(context, data!!.data)
                var file = File(filePath)
                val fileUri = FileUtils.getUri(file)
                val mimeType = FileUtils.getMimeType(context, fileUri)

                if (mimeType == "image/gif"
                        || mimeType == "image/ief"
                        || mimeType == "image/jpeg"
                        || mimeType == "image/jpeg") {
                    filePath = processForRightAngleImage(filePath)
                    file = File(filePath)
                }

                if(file.size > 500) file = file.reduceFileSize() ?: file


                onFile(file)
                dialog.dismiss()
            }
        }
        else {
            if(filePickPath == null) return@onActivityResult
            GlobalScope.launch(Dispatchers.Main) {

                val dialog = AppDialog.progress(context)
                val filePath = processForRightAngleImage(filePickPath!!)
                var file = File(filePath)
                if(file.size > 500) file = file.reduceFileSize() ?: file
                onFile(file)
                dialog.dismiss()
            }
        }
    }

}

