package cc.flyfree.free.module.core.common.misc.msg;

import java.util.Optional;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerProperties;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactory;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import cc.flyfree.free.module.core.common.config.MailProperties;
import freemarker.template.Configuration;
import freemarker.template.Template;
import jakarta.annotation.PostConstruct;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zengzhifei
 * @date 2023/2/20 12:47
 */
@Slf4j
public class MailHelper {
    private final MailProperties properties;
    private JavaMailSenderImpl javaMailSender;
    private Configuration configuration;

    public MailHelper(MailProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    private void init() {
        initJavaMailSender();
        initConfiguration();
    }

    public void sendText(String subject, String text, String... to) {
        String username = properties.getUsername();
        if (StringUtils.isNotEmpty(properties.getFromName())) {
            username = String.format("%s<%s>", properties.getFromName(), username);
        }

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(username);
        simpleMailMessage.setTo(to);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(text);
        javaMailSender.send(simpleMailMessage);
    }

    public void sendHtml(String subject, String text, String... to) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

            InternetAddress internetAddress = new InternetAddress(properties.getUsername(), properties.getFromName());
            mimeMessageHelper.setFrom(internetAddress);
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(text, true);

            javaMailSender.send(mimeMessageHelper.getMimeMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void sendHtml(String subject, String templateName, Object model, String... to) {
        try {
            Template template = configuration.getTemplate(templateName);
            String text = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
            sendHtml(subject, text, to);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void initJavaMailSender() {
        javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost(properties.getHost());
        javaMailSender.setPort(properties.getPort());
        javaMailSender.setProtocol(properties.getProtocol());
        javaMailSender.setUsername(properties.getUsername());
        javaMailSender.setPassword(properties.getPassword());
        javaMailSender.setDefaultEncoding(properties.getDefaultEncoding());
        javaMailSender.setJavaMailProperties(properties.getProperties());

    }

    private void initConfiguration() {
        FreeMarkerProperties freeMarkerProperties = properties.getFreeMarkerProperties();
        freeMarkerProperties = Optional.ofNullable(freeMarkerProperties).orElse(new FreeMarkerProperties());

        try {
            FreeMarkerConfigurationFactory factory = new FreeMarkerConfigurationFactory();
            factory.setTemplateLoaderPaths(freeMarkerProperties.getTemplateLoaderPath());
            factory.setPreferFileSystemAccess(freeMarkerProperties.isPreferFileSystemAccess());
            factory.setDefaultEncoding(freeMarkerProperties.getCharsetName());
            Properties settings = new Properties();
            settings.putAll(freeMarkerProperties.getSettings());
            factory.setFreemarkerSettings(settings);
            configuration = factory.createConfiguration();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
