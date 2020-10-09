package com.whitesky.exoplayertest.test

import android.net.Uri
import android.security.keystore.KeyProperties
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.TransferListener
import java.io.File
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.spec.SecretKeySpec

class EncryptedDataSource(private val key: String) : DataSource {
    private var inputStream: CipherInputStream? = null
    private lateinit var uri: Uri

    override fun addTransferListener(transferListener: TransferListener) {}

    override fun open(dataSpec: DataSpec): Long {
        uri = dataSpec.uri
        try {
            val file = File(uri.path)
            val skeySpec = SecretKeySpec(key.toByteArray(), KeyProperties.KEY_ALGORITHM_AES)
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            cipher.init(Cipher.DECRYPT_MODE, skeySpec)
            inputStream = CipherInputStream(file.inputStream(), cipher)
        } catch (e: Exception) {

        }
        return dataSpec.length
    }

    override fun read(buffer: ByteArray, offset: Int, readLength: Int): Int =
            if (readLength == 0) {
                0
            } else {
                inputStream?.read(buffer, offset, readLength) ?: 0
            }

    override fun getUri(): Uri? =
            uri

    override fun close() {
        inputStream?.close()
    }
}