package com.oneliang.util.mail;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

import com.oneliang.Constant;
import com.oneliang.util.common.StringUtil;
import com.oneliang.util.common.TimeUtil;
import com.oneliang.util.log.Logger;

public class MailMessage{

	private static final Logger logger=Logger.getLogger(MailMessage.class);

	private static final String TYPE_TEXT_PLAIN="text/plain";
	private static final String TYPE_TEXT_HTML="text/html";
	private static final String TYPE_MULTIPART="multipart/*";
	private static final String TYPE_MESSAGE_RFC822="message/rfc822";
	private static final String HEADER_DISPOSITION_NOTIFICATION_TO="Disposition-Notification-TO";
	
	private Part part=null;
	
	/**
	 * constructor
	 * @param part
	 */
	public MailMessage(final Part part) {
		this.part=part;
	}
	
	/**
	 * get from address
	 * @return String
	 * @throws Exception
	 */
	public String getFromAddress() throws Exception {
		MimeMessage mimeMessage=(MimeMessage)this.part;
		InternetAddress[] address = (InternetAddress[])mimeMessage.getFrom();
		String from=StringUtil.nullToBlank(address[0].getAddress());
		String personal=StringUtil.nullToBlank(address[0].getPersonal());
		String fromAddress = personal + "<" + from + ">";
		return fromAddress;
	}
	
	/**
	 * get subject
	 * @return String
	 * @throws Exception
	 */
	public String getSubject() throws Exception{
		MimeMessage mimeMessage=(MimeMessage)this.part;
		String subject=MimeUtility.decodeText(mimeMessage.getSubject());
		return subject;
	}

	/**
	 * send date
	 * @return String
	 * @throws Exception
	 */
	public String getSendDate() throws Exception{
		MimeMessage mimeMessage=(MimeMessage)this.part;
		Date sendDate=mimeMessage.getSentDate();
		return TimeUtil.dateToString(sendDate, TimeUtil.YEAR_MONTH_DAY_HOUR_MINUTE_SECOND);
	}
	
	/**
	 * get mail body text
	 * @return String
	 * @throws Exception
	 */
	public String getMailBodyText() throws Exception {
		return getMailBodyText(this.part);
	}
	
	/**
	 * get mail body text
	 * @param part
	 * @throws Exception
	 */
	private String getMailBodyText(final Part part) throws Exception {
		StringBuilder bodyText=new StringBuilder();
		String contentType = part.getContentType();
		int nameIndex = contentType.indexOf("name");
		boolean nameSign = false;
		if (nameIndex != -1) {
			nameSign = true;
		}
		if (part.isMimeType(TYPE_TEXT_PLAIN) && !nameSign) {
			bodyText.append((String) part.getContent());
		} else if (part.isMimeType(TYPE_TEXT_HTML) && !nameSign) {
			bodyText.append((String) part.getContent());
		} else if (part.isMimeType(TYPE_MULTIPART)) {
			Multipart multipart = (Multipart) part.getContent();
			int count = multipart.getCount();
			for (int i = 0; i < count; i++) {
				bodyText.append(this.getMailBodyText(multipart.getBodyPart(i)));
			}
		} else if (part.isMimeType(TYPE_MESSAGE_RFC822)) {
			bodyText.append(this.getMailBodyText((Part)part.getContent()));
		}
		return bodyText.toString();
	}
	
	/**
	 * isNeedReply
	 * @return boolean
	 * @throws Exception
	 */
	public boolean isNeedReply() throws Exception {
		boolean isNeedReply = false;
		String needReply[] = part.getHeader(HEADER_DISPOSITION_NOTIFICATION_TO);
		if (needReply != null) {
			isNeedReply = true;
		}
		return isNeedReply;
	}
	
	/**
	 * isNew
	 * @return is new
	 * @throws Exception
	 */
	public boolean isNew() throws Exception {
		boolean isNew = false;
		Flags flags = ((MimeMessage) this.part).getFlags();
		Flags.Flag[] flag = flags.getSystemFlags();
		for (int i = 0; i < flag.length; i++) {
			if (flag[i] == Flags.Flag.SEEN) {
				isNew = true;
				break;
			}
		}
		return isNew;
	}
	
	/**
	 * isContainAccessories
	 * @return boolean
	 * @throws Exception
	 */
	public boolean isContainAccessories() throws Exception{
		return isContainAccessories(this.part);
	}
	
	/**
	 * isContainAccessories
	 * @param part
	 * @return boolean
	 * @throws Exception
	 */
	private boolean isContainAccessories(final Part part) throws Exception {
		boolean isContainAccessories = false;
		if (part.isMimeType(TYPE_MULTIPART)) {
			Multipart multipart = (Multipart) part.getContent();
			int count = multipart.getCount();
			for (int i = 0; i < count; i++) {
				BodyPart bodyPart = multipart.getBodyPart(i);
				String dispostion = bodyPart.getDisposition();
				if ((dispostion != null)
						&& (dispostion.equals(Part.ATTACHMENT) || dispostion
								.equals(Part.INLINE))) {
					isContainAccessories = true;
				} else if (bodyPart.isMimeType(TYPE_MULTIPART)) {
					isContainAccessories = isContainAccessories(bodyPart);
				} else {
					String contentType = bodyPart.getContentType();
					if (contentType.toLowerCase().indexOf("appliaction") != -1) {
						isContainAccessories = true;
					}
					if (contentType.toLowerCase().indexOf("name") != -1) {
						isContainAccessories = true;
					}
				}
			}
		} else if (part.isMimeType(TYPE_MESSAGE_RFC822)) {
			isContainAccessories = isContainAccessories((Part) part.getContent());
		}
		return isContainAccessories;
	}
	
	/**
	 * saveAccessories
	 * @throws Exception
	 */
	public void saveAccessories(String path) throws Exception{
		this.saveAccessories(this.part,path);
	}
	
	/**
	 * saveAccessories
	 * @param part
	 * @throws Exception
	 */
	private void saveAccessories(final Part part,final String path) throws Exception{
		String filename = null;
		if (part.isMimeType(TYPE_MULTIPART)) {
			Multipart multipart = (Multipart) part.getContent();
			for (int i = 0; i < multipart.getCount(); i++) {
				BodyPart bodyPart = multipart.getBodyPart(i);
				String dispostion = bodyPart.getDisposition();
				if ((dispostion != null)
						&& (dispostion.equals(Part.ATTACHMENT) || dispostion
								.equals(Part.INLINE))) {
					filename = bodyPart.getFileName();
					if (filename!=null&&filename.toLowerCase().indexOf(Constant.Encoding.GB2312.toLowerCase()) != -1) {
						filename = MimeUtility.decodeText(filename);
					}
					this.saveFile(path, filename, bodyPart.getInputStream());
				} else if (bodyPart.isMimeType(TYPE_MULTIPART)) {
					this.saveAccessories(bodyPart,path);
				} else {
					filename = bodyPart.getFileName();
					if (filename != null
							&& (filename.toLowerCase().indexOf(Constant.Encoding.GB2312.toLowerCase()) != -1)) {
						filename = MimeUtility.decodeText(filename);
					}
					this.saveFile(path, filename, bodyPart.getInputStream());
				}
			}

		} else if (part.isMimeType(TYPE_MESSAGE_RFC822)) {
			this.saveAccessories((Part)part.getContent(),path);
		}
	}
	
	/**
	 * saveFile
	 * @param filename
	 * @param inputStream
	 * @throws IOException
	 */
	private void saveFile(final String path,final String filename,final InputStream inputStream) throws IOException {
		String tempFilename=filename;
		if(StringUtil.isNotBlank(tempFilename)){
			int lastIndex=tempFilename.lastIndexOf(File.separator);
			if(lastIndex>-1){
				tempFilename=tempFilename.substring(lastIndex);
			}
			File file = new File(path + File.separator + tempFilename);
			BufferedOutputStream bufferedOutputStream = null;
			BufferedInputStream bufferedInputStream = null;
			try {
				bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));
				bufferedInputStream = new BufferedInputStream(inputStream);
				byte[] buffer = new byte[Constant.Capacity.BYTES_PER_KB];
				int length=-1;
				while((length=bufferedInputStream.read(buffer,0,buffer.length))!=-1){
					bufferedOutputStream.write(buffer,0,length);
					bufferedOutputStream.flush();
				}
			} catch (Exception e) {
				logger.error(Constant.Base.EXCEPTION, e);
			} finally {
				if(bufferedInputStream!=null){
					bufferedInputStream.close();
				}
				if(bufferedOutputStream!=null){
					bufferedOutputStream.close();
				}
			}
		}
	}
	
	/**
	 * @return the part
	 */
	public Part getPart() {
		return part;
	}

	/**
	 * @param part the part to set
	 */
	public void setPart(Part part) {
		this.part = part;
	}
}
