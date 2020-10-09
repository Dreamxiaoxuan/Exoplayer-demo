package com.xiaoxuan.exoplayerdemo.test2;

import java.io.IOException;

import com.google.android.exoplayer2.upstream.AssetDataSource;
import com.google.android.exoplayer2.upstream.ContentDataSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;

import android.content.Context;
import android.net.Uri;

public class MyDataSource implements DataSource
{
    private static final String SCHEME_ASSET = "asset";
    
    private static final String SCHEME_CONTENT = "content";
    
    private final Context context;
    
    private final TransferListener listener;
    
    private final DataSource baseDataSource;
    
    // Lazily initialized.
    private DataSource fileDataSource;
    
    private DataSource assetDataSource;
    
    private DataSource contentDataSource;
    
    private DataSource dataSource;
    
    private String mStrKey = "0123456789abcdef";
    
    /**
     * Constructs a new instance, optionally configured to follow cross-protocol redirects.
     *
     * @param context A context.
     * @param listener An optional listener.
     * @param userAgent The User-Agent string that should be used when requesting remote data.
     * @param allowCrossProtocolRedirects Whether cross-protocol redirects (i.e. redirects from HTTP to HTTPS and vice
     *            versa) are enabled when fetching remote data.
     */
    public MyDataSource(Context context, TransferListener listener, String userAgent,
        boolean allowCrossProtocolRedirects)
    {
        this(context, listener, userAgent, DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
            DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS, allowCrossProtocolRedirects);
    }
    
    /**
     * Constructs a new instance, optionally configured to follow cross-protocol redirects.
     *
     * @param context A context.
     * @param listener An optional listener.
     * @param userAgent The User-Agent string that should be used when requesting remote data.
     * @param connectTimeoutMillis The connection timeout that should be used when requesting remote data, in
     *            milliseconds. A timeout of zero is interpreted as an infinite timeout.
     * @param readTimeoutMillis The read timeout that should be used when requesting remote data, in milliseconds. A
     *            timeout of zero is interpreted as an infinite timeout.
     * @param allowCrossProtocolRedirects Whether cross-protocol redirects (i.e. redirects from HTTP to HTTPS and vice
     *            versa) are enabled when fetching remote data.
     */
    public MyDataSource(Context context, TransferListener listener, String userAgent, int connectTimeoutMillis,
        int readTimeoutMillis, boolean allowCrossProtocolRedirects)
    {
        this(context, listener, new DefaultHttpDataSource(userAgent, null, listener, connectTimeoutMillis,
            readTimeoutMillis, allowCrossProtocolRedirects, null));
    }
    
    /**
     * Constructs a new instance that delegates to a provided {@link DataSource} for URI schemes other than file, asset
     * and content.
     *
     * @param context A context.
     * @param listener An optional listener.
     * @param baseDataSource A {@link DataSource} to use for URI schemes other than file, asset and content. This
     *            {@link DataSource} should normally support at least http(s).
     */
    public MyDataSource(Context context, TransferListener listener, DataSource baseDataSource)
    {
        this.context = context.getApplicationContext();
        this.listener = listener;
        this.baseDataSource = Assertions.checkNotNull(baseDataSource);
    }
    
    @Override
    public void addTransferListener(TransferListener transferListener)
    {
        
    }
    
    // 解密的地方
    @Override
    public long open(DataSpec dataSpec)
        throws IOException
    {
        Assertions.checkState(dataSource == null);
        // Choose the correct source for the scheme.
        String scheme = dataSpec.uri.getScheme();
        // 如果URI是一个本地文件路径或本地文件的引用。
        if (Util.isLocalFileUri(dataSpec.uri))
        {
            // 如果路径尾包含aitrip的文件名，使用解密类
            if (dataSpec.uri.getPath().endsWith(".ld6"))
            {
                Aes128DataSource aes128DataSource =
                    new Aes128DataSource(getAssetDataSource(), mStrKey.getBytes(), mStrKey.getBytes());
                dataSource = aes128DataSource;
            }
            else
            {// 否则，正常解析mp3
                if (dataSpec.uri.getPath().startsWith("/android_asset/"))
                {
                    dataSource = getAssetDataSource();
                }
                else
                {
                    dataSource = getFileDataSource();
                }
            }
        }
        else if (SCHEME_ASSET.equals(scheme))
        {
            dataSource = getAssetDataSource();
        }
        else if (SCHEME_CONTENT.equals(scheme))
        {
            dataSource = getContentDataSource();
        }
        else
        {
            dataSource = baseDataSource;
        }
        // Open the source and return.
        return dataSource.open(dataSpec);
    }
    
    @Override
    public int read(byte[] buffer, int offset, int readLength)
        throws IOException
    {
        return dataSource.read(buffer, offset, readLength);
    }
    
    @Override
    public Uri getUri()
    {
        return dataSource == null ? null : dataSource.getUri();
    }
    
    @Override
    public void close()
        throws IOException
    {
        if (dataSource != null)
        {
            try
            {
                dataSource.close();
            }
            finally
            {
                dataSource = null;
            }
        }
    }
    
    private DataSource getFileDataSource()
    {
        if (fileDataSource == null)
        {
            fileDataSource = new FileDataSource(listener);
        }
        return fileDataSource;
    }
    
    private DataSource getAssetDataSource()
    {
        if (assetDataSource == null)
        {
            assetDataSource = new AssetDataSource(context, listener);
        }
        return assetDataSource;
    }
    
    private DataSource getContentDataSource()
    {
        if (contentDataSource == null)
        {
            contentDataSource = new ContentDataSource(context, listener);
        }
        return contentDataSource;
    }
}
