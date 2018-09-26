package com.oneliang.util.http;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import com.oneliang.Constants;
import com.oneliang.util.http.HttpDownloader.DownloadListener;
import com.oneliang.util.logging.Logger;
import com.oneliang.util.logging.LoggerManager;

/**
 * @author oneliang
 */
public class DefaultDownloadListener implements DownloadListener {

	private static final Logger logger=LoggerManager.getLogger(DefaultDownloadListener.class);

	public void onStart() {
		logger.debug("download on start");
	}

	public void onProcess(Map<String, List<String>> headerFieldMap, InputStream inputStream, int contentLength, String saveFile) {
		try{
			File file=new File(saveFile);
	        file.createNewFile();
	        FileOutputStream fileOutputStream=new FileOutputStream(file);
	        byte[] buffer = new byte[Constants.Capacity.BYTES_PER_KB];
	        int length=-1;
	        while((length=inputStream.read(buffer,0,buffer.length))!=-1){
	            fileOutputStream.write(buffer,0,length);
	            fileOutputStream.flush();
	        }
	        fileOutputStream.close();
		}catch(Exception exception){
			onFailure(exception);
		}
	}

	public void onFinish() {
		logger.debug("download on finish");
	}

	public void onFailure(Exception exception) {
		logger.error(Constants.Base.EXCEPTION, exception);
	}
}
