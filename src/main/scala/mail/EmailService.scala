package mail

import org.apache.commons.mail.SimpleEmail
import org.subethamail.smtp.server.SMTPServer
import org.subethamail.smtp.helper.SimpleMessageListenerAdapter
import org.subethamail.smtp.helper.SimpleMessageListener
import java.io.InputStream
import org.apache.commons.mail.DefaultAuthenticator

object EmailService {

  def sendMessage(message: String) = {
    val email = new SimpleEmail();
    email.setHostName("smtp.googlemail.com");
    email.setSmtpPort(465);
    email.setAuthenticator(new DefaultAuthenticator("*****", "*****"));
    email.setSSLOnConnect(true);
    email.setFrom("andrekuhnen@gmail.com")
    email.addTo("andrekuhnen@gmail.com")
    email.setSubject("Novidades no classificados UFSC")
    email.setMsg(message)
    email.send

  }
}