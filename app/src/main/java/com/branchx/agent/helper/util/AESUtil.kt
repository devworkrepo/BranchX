package com.branchx.agent.helper.util

import android.util.Base64
import java.security.spec.AlgorithmParameterSpec
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object AESUtil {

    private const val PADDING = "AES/CBC/PKCS7Padding"
    private const val ALGORITHM = "AES"
    private const val SALT_AND_ENCRYPT_KEY = "628078P3XTO8WH91"

    fun encryptData(
        valueStr: String,
        ivStr: String = SALT_AND_ENCRYPT_KEY,
        keyStr: String = SALT_AND_ENCRYPT_KEY,
    ): String {
        val iv = ivStr.toByteArray()
        val key = keyStr.toByteArray()
        val value = valueStr.toByteArray()

        val ivSpec: AlgorithmParameterSpec = IvParameterSpec(iv)
        val newKey = SecretKeySpec(key, ALGORITHM)
        val cipher = Cipher.getInstance(PADDING)
        cipher.init(Cipher.ENCRYPT_MODE, newKey, ivSpec)
        val result: ByteArray = cipher.doFinal(value)
        return String(Base64.encode(result, Base64.DEFAULT), Charsets.UTF_8)
    }

    fun decryptData(
        valueStr: String,
        ivStr: String = SALT_AND_ENCRYPT_KEY,
        keyStr: String = SALT_AND_ENCRYPT_KEY,
    ): String {
        val iv = ivStr.toByteArray()
        val key = keyStr.toByteArray()
        val value = valueStr.toByteArray(Charsets.UTF_8)

        val ivSpec: AlgorithmParameterSpec = IvParameterSpec(iv)
        val newKey = SecretKeySpec(key, ALGORITHM)
        val cipher = Cipher.getInstance(PADDING)
        cipher.init(Cipher.DECRYPT_MODE, newKey, ivSpec)

        val mValue: ByteArray = Base64.decode(value, Base64.DEFAULT)
        val result = cipher.doFinal(mValue)
        return String(result, Charsets.UTF_8)
    }

}