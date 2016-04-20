package com.oneliang.util.upload;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.oneliang.Constant;
import com.oneliang.util.common.StringUtil;
import com.oneliang.util.log.Logger;

/**
 * for any file upload
 * @author Dandelion
 */
public final class FileUpload {
	private static final Logger logger=Logger.getLogger(FileUpload.class);
	
	private String saveFilePath=null;//save file path
	
	//the line from 0 to end,just like line index
	private static final String HEADER_SEPARATE=": ";
	private static final String FILENAME_START_SIGN="filename=\"";
	private static final String FILENAME_END_SIGN="\"";

	/**
	 * upload the single file,just for InputStream,client upload
	 * @param inputStream
	 * @param filename
	 * @return FileUploadSign
	 * @throws IOException
	 */
	public FileUploadResult upload(final InputStream inputStream,final String filename){
		FileUploadResult fileUploadResult=null;
		OutputStream outputStream=null;
		try{
			outputStream=new FileOutputStream(saveFilePath+filename);
			byte[] buffer=new byte[Constant.Capacity.BYTES_PER_KB];
			int length=-1;
			while((length=inputStream.read(buffer,0,buffer.length))!=-1){
				outputStream.write(buffer,0,length);
				outputStream.flush();
			}
			fileUploadResult=new FileUploadResult();
			fileUploadResult.setSuccess(true);
			fileUploadResult.setFilePath(saveFilePath);
			fileUploadResult.setFilename(filename);
			logger.log("Upload end,original save file is:"+filename);
		}catch(Exception e){
			logger.log("upload error", e);
		}finally{
			if(outputStream!=null){
				try{
					outputStream.flush();
					outputStream.close();
				}catch (Exception e) {
					logger.log("outputStream close error", e);
				}
			}
		}
		return fileUploadResult;
	}

	/**
	 * upload the file,form upload
	 * @param inputStream
	 * @param totalLength
	 * @return List<FileUploadSign>
	 * @throws IOException
	 */
	public List<FileUploadResult> upload(final InputStream inputStream,final int totalLength,String[] saveFilenames){
		List<FileUploadResult> fileUploadResultList=new ArrayList<FileUploadResult>();
		OutputStream outputStream=null;
		try{
			int data=-1;
			boolean firstLine=true;
			boolean formField=true;
			byte[] headByteArray=null;
			ByteArrayOutputStream lineByteArray=new ByteArrayOutputStream();
			String contentDisposition=null;
			String originalFilename=null;
			int fileCount=0;
			int count=0;
			while((data=inputStream.read())!=-1){
				if(data==StringUtil.CR){
					int temp=inputStream.read();
					if(temp==StringUtil.LF){
						//end one line..
						if(firstLine){
							firstLine=false;
							headByteArray=lineByteArray.toByteArray();
							lineByteArray.reset();
						}else{
							String line=new String(lineByteArray.toByteArray(),Constant.Encoding.UTF8);
							String[] header=line.split(HEADER_SEPARATE);
							if(header!=null&&header.length>1){
								if(header[0].equals(Constant.Http.HeaderKey.CONTENT_DISPOSITION)){
									formField=true;
									contentDisposition=header[1];
								}else if(header[0].equals(Constant.Http.HeaderKey.CONTENT_TYPE)){
									formField=false;
									if(contentDisposition!=null&&contentDisposition.indexOf(FILENAME_START_SIGN)>0){
										int startIndex=contentDisposition.indexOf(FILENAME_START_SIGN)+FILENAME_START_SIGN.length();
										int endIndex=contentDisposition.lastIndexOf(FILENAME_END_SIGN);
										originalFilename=contentDisposition.substring(startIndex, endIndex);
									}
								}
							}
							lineByteArray.reset();
						}
						int nextOne=inputStream.read();
						int nextTwo=inputStream.read();
						if(nextOne==StringUtil.CR&&nextTwo==StringUtil.LF){
							//end one form field
							if(!formField){
								String saveFilename=originalFilename;
								if(saveFilenames!=null&&saveFilenames.length>fileCount){
									String tempFilename=saveFilenames[fileCount];
									if(tempFilename!=null){
										saveFilename=tempFilename+originalFilename.substring(originalFilename.lastIndexOf(Constant.Symbol.DOT),originalFilename.length());
									}
								}
								outputStream=new FileOutputStream(saveFilePath+saveFilename);
								int i=0;
								boolean mayBeEndSign=false;
								byte[] byteArray=new byte[headByteArray.length];
								byte[] tempArray=new byte[headByteArray.length];
								ByteArrayInputStream byteArrayInputStream=null;
								while(count<totalLength){
									if(mayBeEndSign){
										data=byteArrayInputStream.read();
										if(byteArrayInputStream.available()==0){
											mayBeEndSign=false;
										}
									}else{
										data=inputStream.read();
									}
									if(data==StringUtil.CR){//may be end
										if(mayBeEndSign){
											temp=byteArrayInputStream.read();
											if(byteArrayInputStream.available()==0){
												mayBeEndSign=false;
											}
										}else{
											temp=inputStream.read();
										}
										if(temp==StringUtil.LF){
											int length=0;
											if(mayBeEndSign){
												int available=byteArrayInputStream.available();
												System.arraycopy(tempArray, tempArray.length-available, byteArray, 0, available);
												length=available;
												length+=inputStream.read(byteArray, available, tempArray.length-available);
											}else{
												length=inputStream.read(byteArray, 0, byteArray.length);
											}
											if(compare(headByteArray, byteArray)){//upload file end
												outputStream.flush();
												outputStream.close();
												logger.log("Upload end,original file is:"+originalFilename+",save file is:"+saveFilename);
												//initial all variable
												FileUploadResult fileUploadResult=new FileUploadResult();
												fileUploadResult.setSuccess(true);
												fileUploadResult.setFilePath(saveFilePath);
												fileUploadResult.setFilename(saveFilename);
												fileUploadResult.setOriginalFilename(originalFilename);
												fileUploadResultList.add(fileUploadResult);
												originalFilename=null;
												fileCount++;
												break;
											}else{
												outputStream.write(StringUtil.CRLF);
												count+=2;
												i+=2;
												System.arraycopy(byteArray, 0, tempArray, 0, tempArray.length);
												byteArrayInputStream=new ByteArrayInputStream(tempArray,0,length);
												if(byteArrayInputStream.available()>0){
													mayBeEndSign=true;
												}
												continue;
											}
										}else{
											outputStream.write(data);
											outputStream.write(temp);
											count+=2;
											i+=2;
										}
									}else{
										outputStream.write(data);
										count++;
										i++;
									}
									if(i%Constant.Capacity.BYTES_PER_KB==0){
										outputStream.flush();
									}
								}
							}else{
								//form field process.skip yet.
							}
						}else{
							lineByteArray.write(nextOne);
							lineByteArray.write(nextTwo);
							count+=2;
							continue;
						}
					}else{
						lineByteArray.write(data);
						lineByteArray.write(temp);
						count+=2;
					}
				}else{
					lineByteArray.write(data);
					count++;
				}
			}
		}catch (Exception e) {
			logger.log("upload error", e);
		}finally{
			if(outputStream!=null){
				try {
					outputStream.flush();
					outputStream.close();
				} catch (Exception e) {
					logger.log("outputStream close error", e);
				}
			}
		}
		return fileUploadResultList;
	}

	private boolean compare(final byte[] a,final byte[] b){
		boolean result=true;
		for(int i=0;i<a.length;i++){
			if(a[i]!=b[i]){
				result=false;
				break;
			}
		}
		return result;
	}

	/**
	 * @return the saveFilePath
	 */
	public String getSaveFilePath() {
		return saveFilePath;
	}

	/**
	 * @param saveFilePath the saveFilePath to set
	 */
	public void setSaveFilePath(String saveFilePath) {
		this.saveFilePath = saveFilePath;
	}
}
