package com.whitesky.exoplayertest.test

import com.google.android.exoplayer2.upstream.DataSource

class EncryptedDataSourceFactory(
        private val key: String
) : DataSource.Factory {
    override fun createDataSource(): EncryptedDataSource =
            EncryptedDataSource(key)
}