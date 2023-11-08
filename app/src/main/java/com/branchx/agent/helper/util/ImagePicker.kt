package com.branchx.agent.helper.util


import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.provider.MediaStore
import android.widget.ImageView
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.branchx.agent.R
import com.branchx.agent.helper.extns.makeToast
import com.branchx.agent.helper.extns.reduceFileSize
import com.branchx.agent.helper.extns.sizeInKb
import com.branchx.agent.helper.extns.toFile
import com.branchx.agent.ui.dialog.AppDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*


class ImagePicker(private val activity: Activity, isFragment: Boolean = false) {

    lateinit var mfile: File
    var dialog: Dialog? = null

    companion object {
        const val FILE_NAME = "norton_document"
        const val IMAGE_CAPTURE_CODE = 42
        const val GALLERY_PIC_IMAGE_CODE = 43


        suspend fun readFileFromInputStream(context: Context?, data: Intent?): File? {
            return withContext(Dispatchers.IO) {
                val inputStream: InputStream =
                    context?.contentResolver?.openInputStream(data?.data!!)!!
                val bmp = BitmapFactory.decodeStream(inputStream)
                inputStream.close()
                val timestamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                val photoFile = bmp.toFile(context, ImagePicker.FILE_NAME + "_$timestamp.jpg")

                if (photoFile.sizeInKb > 512)
                    photoFile.reduceFileSize()
                else photoFile
            }
        }

        suspend fun readBitmapFromInputStream(context: Context?, data: Intent?): Bitmap {
            return withContext(Dispatchers.IO) {
                val inputStream: InputStream =
                    context?.contentResolver?.openInputStream(data?.data!!)!!
                val bmp = BitmapFactory.decodeStream(inputStream)
                inputStream.close()
                bmp
            }
        }
    }

    fun openCameraGallery() {
        dialog = AppDialog.openCameraGallery(activity)
        dialog?.let {
            val ivCamera: ImageView = it.findViewById(R.id.iv_camera)
            val ivGallery: ImageView = it.findViewById(R.id.iv_gallery)
            ivCamera.setOnClickListener {
                openCameraIntent()
                dialog?.dismiss()
            }
            ivGallery.setOnClickListener {
                openGalleryIntent()
                dialog?.dismiss()
            }
        }
    }

    private fun openGalleryIntent() {
        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
            type = "image/*"
            activity.startActivityForResult(this, GALLERY_PIC_IMAGE_CODE)
        }
    }

    private fun openCameraIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        mfile = getPhotoFile()
        val fileProvider =
            FileProvider.getUriForFile(activity, "app.tramo_norton.com.fileprovider", mfile)
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
        if (takePictureIntent.resolveActivity(activity.packageManager) != null) {
            activity.startActivityForResult(takePictureIntent, IMAGE_CAPTURE_CODE)
        } else {
            activity.makeToast("Unable to open camera")
        }
    }

    private fun getPhotoFile(): File {
        val timestamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDirectory = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(FILE_NAME + "_$timestamp", ".jpg", storageDirectory)
    }

    fun handleOnActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        response: (imageFile: File?) -> Unit
    ) {

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == IMAGE_CAPTURE_CODE) {
                if (mfile.sizeInKb > 512)
                    response(mfile.reduceFileSize())
                else response(mfile)

            } else if (requestCode == GALLERY_PIC_IMAGE_CODE) {
                val inputStream: InputStream =
                    activity.contentResolver.openInputStream(data?.data!!)!!
                val bmp = BitmapFactory.decodeStream(inputStream)
                inputStream.close()
                val timestamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                mfile = bmp.toFile(activity, FILE_NAME + "_$timestamp")

                if (mfile.sizeInKb > 512)
                    response(mfile.reduceFileSize())
                else response(mfile)

            }
        }
    }
}

class ImagePickerFragment(val fragment: Fragment) {
    private val context = fragment.requireContext()
    lateinit var mfile: File
    var dialog: Dialog? = null

    companion object {
        const val FILE_NAME = "norton_document"
        const val IMAGE_CAPTURE_CODE = 42
        const val GALLERY_PIC_IMAGE_CODE = 43


        suspend fun readFileFromInputStream(context: Context?, data: Intent?): File? {
            return withContext(Dispatchers.IO) {
                val inputStream: InputStream =
                    context?.contentResolver?.openInputStream(data?.data!!)!!
                val bmp = BitmapFactory.decodeStream(inputStream)
                inputStream.close()
                val timestamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                val photoFile = bmp.toFile(context, ImagePicker.FILE_NAME + "_$timestamp.jpg")

                if (photoFile.sizeInKb > 512)
                    photoFile.reduceFileSize()
                else photoFile
            }
        }

        suspend fun readBitmapFromInputStream(context: Context?, data: Intent?): Bitmap {
            return withContext(Dispatchers.IO) {
                val inputStream: InputStream =
                    context?.contentResolver?.openInputStream(data?.data!!)!!
                val bmp = BitmapFactory.decodeStream(inputStream)
                inputStream.close()
                bmp
            }
        }
    }

    fun openCameraGallery() {
        dialog = AppDialog.openCameraGallery(context)
        dialog?.let {
            val ivCamera: ImageView = it.findViewById(R.id.iv_camera)
            val ivGallery: ImageView = it.findViewById(R.id.iv_gallery)
            ivCamera.setOnClickListener {
                openCameraIntent()
                dialog?.dismiss()
            }
            ivGallery.setOnClickListener {
                openGalleryIntent()
                dialog?.dismiss()
            }
        }
    }

    private fun openGalleryIntent() {
        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
            type = "image/*"
            fragment.startActivityForResult(this, GALLERY_PIC_IMAGE_CODE)
        }
    }

    private fun openCameraIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        mfile = getPhotoFile()
        val fileProvider =
            FileProvider.getUriForFile(context, "com.branchx.agent.fileprovider", mfile)
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
        if (takePictureIntent.resolveActivity(context.packageManager) != null) {
            fragment.startActivityForResult(takePictureIntent, IMAGE_CAPTURE_CODE)
        } else {
            context.makeToast("Unable to open camera")
        }
    }

    private fun getPhotoFile(): File {
        val timestamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDirectory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(FILE_NAME + "_$timestamp", ".jpg", storageDirectory)
    }

    fun handleOnActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        response: (imageFile: File?) -> Unit
    ) {

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == IMAGE_CAPTURE_CODE) {
                if (mfile.sizeInKb > 512)
                    response(mfile.reduceFileSize())
                else response(mfile)

            } else if (requestCode == GALLERY_PIC_IMAGE_CODE) {
                val inputStream: InputStream =
                    context.contentResolver.openInputStream(data?.data!!)!!
                val bmp = BitmapFactory.decodeStream(inputStream)
                inputStream.close()
                val timestamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                mfile = bmp.toFile(context, FILE_NAME + "_$timestamp")

                if (mfile.sizeInKb > 512)
                    response(mfile.reduceFileSize())
                else response(mfile)

            }
        }
    }

}

