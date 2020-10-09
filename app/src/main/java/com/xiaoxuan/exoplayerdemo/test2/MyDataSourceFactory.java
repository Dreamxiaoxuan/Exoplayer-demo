package com.xiaoxuan.exoplayerdemo.test2;

import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.TransferListener;

import android.content.Context;

public class MyDataSourceFactory implements DataSource.Factory
{
    private final Context context;
    
    private final TransferListener listener;
    
    private final DataSource.Factory baseDataSourceFactory;
    
    /**
     * @param context A context.
     * @param userAgent The User-Agent string that should be used.
     */
    public MyDataSourceFactory(Context context, String userAgent)
    {
        this(context, userAgent, null);
    }
    
    /**
     * @param context A context.
     * @param userAgent The User-Agent string that should be used.
     * @param listener An optional listener.
     */
    public MyDataSourceFactory(Context context, String userAgent, TransferListener listener)
    {
        this(context, listener, new DefaultHttpDataSourceFactory(userAgent, listener));
    }
    
    public MyDataSourceFactory(Context context, TransferListener listener, DataSource.Factory baseDataSourceFactory)
    {
        this.context = context.getApplicationContext();
        this.listener = listener;
        this.baseDataSourceFactory = baseDataSourceFactory;
    }
    
    @Override
    public MyDataSource createDataSource()
    {
        return new MyDataSource(context, listener, baseDataSourceFactory.createDataSource());
    }
}
