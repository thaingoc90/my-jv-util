package my.library.utils;

import java.util.Date;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * mail.jar
 * 
 * @author ngoctdn
 *
 */
public class MailUtils {

	public static void sendMail(String mailServer, String mailPort,
			String sender, String receiver, String subject, String content)
			throws Exception {
		Properties props = new Properties();
		props.put("mail.smtp.host", mailServer);
		props.put("mail.smtp.port", mailPort);
		Session session = Session.getInstance(props);
		MimeMessage msg = new MimeMessage(session);
		msg.setFrom(new InternetAddress(sender));
		Address addressReceiver = new InternetAddress(receiver);
		msg.setRecipient(Message.RecipientType.TO, addressReceiver);
		msg.setSubject(subject);
		msg.setSentDate(new Date());
		msg.setContent(content, "text/html");
		Transport.send(msg);
	}

	public static void sendAuthMail(String mailServer, String mailPort,
			String sender, String pass, String receiver, String subject,
			String content) throws Exception {
		Properties props = new Properties();
		props.put("mail.smtp.host", mailServer);
		props.put("mail.smtp.port", mailPort);
		final String login = sender;
		final String pwd = pass;
		Authenticator pa = null;
		if (login != null && pwd != null) {
			props.put("mail.smtp.auth", "true");
			pa = new Authenticator() {

				public PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(login, pwd);
				}
			};
		}
		Session session = Session.getInstance(props, pa);
		MimeMessage msg = new MimeMessage(session);
		msg.setFrom(new InternetAddress(sender));
		Address addressReceiver = new InternetAddress(receiver);
		msg.setRecipient(Message.RecipientType.TO, addressReceiver);
		msg.setSubject(subject);
		msg.setSentDate(new Date());
		msg.setText(content);
		msg.saveChanges();
		Transport transport = session.getTransport("smtp");
		transport.connect();
		transport.sendMessage(msg, msg.getAllRecipients());
		transport.close();
	}
}
