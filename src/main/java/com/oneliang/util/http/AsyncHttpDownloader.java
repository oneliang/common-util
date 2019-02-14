package com.oneliang.util.http;

import java.util.List;

import com.oneliang.util.concurrent.ThreadPool;
import com.oneliang.util.http.HttpDownloader.DownloadListener;
import com.oneliang.util.http.HttpUtil.HttpNameValue;

public class AsyncHttpDownloader{

    private static final int DEFAULT_MIN_THREADS=1;
    private ThreadPool threadPool=null;
    private static final HttpDownloader httpDownloader=new HttpDownloader();

    /**
     * constructor
     * @param minThreads
     * @param maxThreads
     */
    public AsyncHttpDownloader(int minThreads,int maxThreads) {
        this.threadPool=new ThreadPool();
        this.threadPool.setMinThreads(minThreads<=0?(minThreads=DEFAULT_MIN_THREADS):minThreads);
        this.threadPool.setMaxThreads(maxThreads<minThreads?(maxThreads=minThreads):maxThreads);
    }

    /**
     * start
     */
    public void start(){
        this.threadPool.start();
    }

    /**
     * download
     * @param httpUrl
     * @param httpHeaderList
     * @param httpParameterList
     * @param timeout
     * @param saveFile full file
     * @param downloadListener
     */
    public void download(final String httpUrl,final List<HttpNameValue> httpHeaderList, final List<HttpNameValue> httpParameterList,final int timeout,final String saveFile,final DownloadListener downloadListener){
        this.threadPool.addRunnable(new Runnable(){
            public void run() {
                httpDownloader.download(httpUrl, httpHeaderList, httpParameterList, timeout, saveFile, downloadListener);
            }
        });
    }
}
