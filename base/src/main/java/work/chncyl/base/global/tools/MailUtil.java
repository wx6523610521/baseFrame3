package work.chncyl.base.global.tools;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class MailUtil implements CommandLineRunner {
    private static final Map<String, String> hostMap = new HashMap<>();

    static {
        // 126
        hostMap.put("smtp.126", "smtp.126.com");
        // qq
        hostMap.put("smtp.qq", "smtp.qq.com");
        // 163
        hostMap.put("smtp.163", "smtp.163.com");
        // sina
        hostMap.put("smtp.sina", "smtp.sina.com.cn");
        // tom
        hostMap.put("smtp.tom", "smtp.tom.com");
        // 263
        hostMap.put("smtp.263", "smtp.263.net");
        // yahoo
        hostMap.put("smtp.yahoo", "smtp.mail.yahoo.com");
        // hotmail
        hostMap.put("smtp.hotmail", "smtp.live.com");
        // gmail
        hostMap.put("smtp.gmail", "smtp.gmail.com");
        hostMap.put("smtp.port.gmail", "465");
    }

    MailProperties mailProperties = new MailProperties();// 配置项

    @Value("${spring.mail.username}")
    private String from;

    /**
     * 发送普通邮件
     *
     * @param toMailAddr 收信人地址
     * @param subject    email主题
     * @param message    发送email信息
     */
    public static void sendCommonMail(String toMailAddr, String subject, String message) throws MessagingException, UnsupportedEncodingException {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();

        javaMailSender.setDefaultEncoding("utf-8");

        javaMailSender.setHost("smtp.qq.com");              // 设置邮箱服务器
        javaMailSender.setPort(465);                        // 设置端口
        javaMailSender.setUsername("123456789@qq.com");    // 设置用户名
        javaMailSender.setPassword("<你的密码/授权码>");      // 设置密码（记得替换为你实际的密码、授权码）
        javaMailSender.setProtocol("smtps");                // 设置协议
        /*
        javaMailSender.setJavaMailProperties(mailProperties.getProperties()); // 设置配置项

        // 创建一个邮件消息
        MimeMessage message = javaMailSender.createMimeMessage();

        // 创建 MimeMessageHelper
        MimeMessageHelper helper = new MimeMessageHelper(message, false);

        // 发件人邮箱和名称
        helper.setFrom("747692844@qq.com", "springdoc");
        // 收件人邮箱
        helper.setTo("admin@springboot.io");
        // 邮件标题
        helper.setSubject("Hello");
        // 邮件正文，第二个参数表示是否是HTML正文
        helper.setText("Hello <strong> World</strong>！", true);
        // 发送
        javaMailSender.send(message);*/
    }

    public static String getHost(String email) throws Exception {
        Pattern pattern = Pattern.compile("\\w+@(\\w+)(\\.\\w+){1,2}");
        Matcher matcher = pattern.matcher(email);
        String key = "unSupportEmail";
        if (matcher.find()) {
            key = "smtp." + matcher.group(1);
        }
        if (hostMap.containsKey(key)) {
            return hostMap.get(key);
        } else {
            throw new Exception("unSupportEmail");
        }
    }

    public static int getSmtpPort(String email) throws Exception {
        Pattern pattern = Pattern.compile("\\w+@(\\w+)(\\.\\w+){1,2}");
        Matcher matcher = pattern.matcher(email);
        String key = "unSupportEmail";
        if (matcher.find()) {
            key = "smtp.port." + matcher.group(1);
        }
        if (hostMap.containsKey(key)) {
            return Integer.parseInt(hostMap.get(key));
        } else {
            // 默认smtp端口
            return 25;
        }
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println(mailProperties);
    }
}
