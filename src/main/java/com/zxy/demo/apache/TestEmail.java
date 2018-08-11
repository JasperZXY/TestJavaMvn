package com.zxy.demo.apache;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

public class TestEmail {
	public static void main(String[] args) throws EmailException {
		Email email = new SimpleEmail();
		email.setHostName("smtp.163.com");
		email.setSmtpPort(465);
		//remenber to change the password
		email.setAuthenticator(new DefaultAuthenticator("yy_jasper@163.com", "password"));
		email.setSSL(true);
		email.setFrom("yy_jasper@163.com");
		email.setSubject("TestMail");
		email.setMsg("This is a test mail ... :-)");
		email.addTo("zhongxianyao@yy.com");
		email.send();
	}

}
