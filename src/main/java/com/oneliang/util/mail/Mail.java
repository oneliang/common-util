package com.oneliang.util.mail;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.URLName;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import com.oneliang.util.common.StringUtil;

public final class Mail{
	
	private static final String MAIL_SMTP_HOST="mail.smtp.host";
	private static final String MAIL_SMTP_AUTH="mail.smtp.auth";
	private static final String SMTP="smtp";
	private static final String POP3="pop3";
	private static final String FOLDER_INBOX="INBOX";
	
	/**
	 * debug mode
	 */
	public static boolean DEBUG=false;
	
	/**
	 * send mail
	 * @param sendMailInformation
	 * @throws Exception
	 */
	public static void send(final SendMailInformation sendMailInformation) throws Exception {
		String from=sendMailInformation.getFromAddress();
		String user=sendMailInformation.getUser();
		String host=sendMailInformation.getHost();
		Properties properties = new Properties();
		// set host
		properties.put(MAIL_SMTP_HOST, host);
		// set authenticator true
		properties.put(MAIL_SMTP_AUTH, true);
		Session session = Session.getDefaultInstance(properties);
		// debug mode
		session.setDebug(DEBUG);
		MimeMessage message = new MimeMessage(session);
		// from address
		message.setFrom(new InternetAddress(from));
		// to address
		List<ToAddress> toAddressList=sendMailInformation.getToAddressList();
		for(ToAddress to:toAddressList){
			ToAddress.Type type=to.getType();
			InternetAddress address=new InternetAddress(to.getAddress());
			switch(type){
			case TO:
				message.addRecipient(Message.RecipientType.TO, address);
				break;
			case BCC:
				message.addRecipient(Message.RecipientType.BCC, address);
				break;
			case CC:
				message.addRecipient(Message.RecipientType.CC, address);
				break;
			}
		}
		// set subject
		message.setSubject(sendMailInformation.getSubject());
		// send date
		message.setSentDate(new Date());
		// 向multipart对象中添加邮件的各个部分内容，包括文本内容和附件
		Multipart multipart = new MimeMultipart();

		// set text content
		BodyPart bodyPart = new MimeBodyPart();
//		bodyPart.setText(sendMailInformation.getText());
		bodyPart.setContent(sendMailInformation.getContent(), "text/html;charset=UTF-8");
		multipart.addBodyPart(bodyPart);

		List<String> accessoryPathList=sendMailInformation.getAccessoryPathList();
		for(String accessoryPath:accessoryPathList){
			if(StringUtil.isNotBlank(accessoryPath)){
				// add body part
				BodyPart messageBodyPart = new MimeBodyPart();
				DataSource source = new FileDataSource(accessoryPath);
				// set accessories file
				messageBodyPart.setDataHandler(new DataHandler(source));
				// set accessories name
				messageBodyPart.setFileName(MimeUtility.encodeText(source.getName()));
				multipart.addBodyPart(messageBodyPart);
			}
		}
		// set content
		message.setContent(multipart);
		// save mail
		message.saveChanges();
		// get transport
		Transport transport = session.getTransport(SMTP);
		// connection
		transport.connect(host, user,sendMailInformation.getPassword());
		// send mail
		MailcapCommandMap commandMap = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
		commandMap.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
		commandMap.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
		commandMap.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
		commandMap.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
		commandMap.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
		CommandMap.setDefaultCommandMap(commandMap);
		transport.sendMessage(message, message.getAllRecipients());
		transport.close();
	}
	
	/**
	 * receive mail
	 * @param receiveMailInformation
	 * @return List<MailMessage>
	 * @throws Exception
	 */
	public static List<MailMessage> receive(final ReceiveMailInformation receiveMailInformation) throws Exception{
		List<MailMessage> mailMessageList=new ArrayList<MailMessage>();
		String user=receiveMailInformation.getUser();
		String host=receiveMailInformation.getHost();
		Properties properties = new Properties();
		properties.put(MAIL_SMTP_HOST, host);
		properties.put(MAIL_SMTP_AUTH, true);
		Session session = Session.getDefaultInstance(properties);
		URLName urlName = new URLName(POP3, host, 110, null,user, receiveMailInformation.getPassword());
		Store store=session.getStore(urlName);
		store.connect();
		Folder folder=store.getFolder(FOLDER_INBOX);
		folder.open(Folder.READ_ONLY);
		Message[] messages=folder.getMessages();
		for(Message message:messages){
			mailMessageList.add(new MailMessage(message));
		}
		return mailMessageList;
	}
	
	/**
	 * test
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		//send
		SendMailInformation mailBean=new SendMailInformation();
		mailBean.setFromAddress("13422349169@163.com");
		mailBean.setHost("smtp.163.com");
		mailBean.setUser("13422349169");
		mailBean.setPassword("borqs654321");
		mailBean.setSubject("test");
		mailBean.setContent("test");
		mailBean.addToAddress(new ToAddress("stephen8558@gmail.com"));
		Mail.send(mailBean);
		//receive
//		ReceiveMailInformation receiveMailInformation=new ReceiveMailInformation();
//		receiveMailInformation.setHost("mail.kidgrow.cn");
//		receiveMailInformation.setUser("noreply@kidgrow.cn");
//		receiveMailInformation.setPassword("t6g4f3");
//		List<MailMessage> mailMessageList=Mail.receive(receiveMailInformation);
//		String path="C:\\temp";
//		FileUtil.createDirectory(path);
//		for(MailMessage mailMessage:mailMessageList){
//			System.out.println(mailMessage.getFromAddress());
//			mailMessage.saveAccessories(path);
//		}
	}
}
