package com.branchx.agent.helper.extns

import android.content.Context
import android.graphics.Bitmap
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

fun Bitmap.toFile(context : Context,filename : String) : File{
    val f =  File(context.cacheDir, filename)
    f.createNewFile();
    val bos =  ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos)
    val bitmapdata = bos.toByteArray()
    val fos =  FileOutputStream(f)
    fos.write(bitmapdata)
    fos.flush()
    fos.close()
    return f
}

fun Bitmap.toByteArray(): ByteArray? {
    val stream = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.JPEG, 70, stream)
    return stream.toByteArray()
}