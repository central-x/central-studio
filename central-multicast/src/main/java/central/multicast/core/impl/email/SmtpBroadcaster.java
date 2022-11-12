/*
 * MIT License
 *
 * Copyright (c) 2022-present Alan Yeh <alan@yeh.cn>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package central.multicast.core.impl.email;

import central.api.client.multicast.body.MailBody;
import central.api.client.multicast.body.StandardBody;
import central.multicast.core.Broadcaster;
import central.multicast.core.impl.email.option.AvailableEnum;
import central.pluglet.annotation.Control;
import central.pluglet.control.ControlType;
import central.util.Listx;
import central.util.Objectx;
import central.validation.Label;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.validation.constraints.*;
import lombok.SneakyThrows;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.MediaType;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Properties;

/**
 * SMTP 协议广播
 *
 * @author Alan Yeh
 * @since 2022/11/03
 */
public class SmtpBroadcaster implements Broadcaster<MailBody>, InitializingBean {

    @Control(label = "说明", type = ControlType.LABEL, required = false, defaultValue = "　　通过 SMTP 协议发送邮件。")
    private String label;

    @Label("服务器地址")
    @NotBlank
    @Size(min = 1, max = 255)
    @Control(label = "服务器地址", comment = "SMTP 服务器地址")
    private String host;

    @Label("SSL")
    @NotNull
    @Control(label = "SSL", type = ControlType.RADIO, defaultValue = "enabled", comment = "是否使用 SSL 认证")
    private AvailableEnum useSsl;

    @Label("端口")
    @NotNull
    @Min(0)
    @Max(65535)
    @Control(label = "端口", type = ControlType.NUMBER)
    private Integer port;

    @Label("邮箱帐号")
    @NotBlank
    @Size(min = 1, max = 255)
    @Control(label = "邮箱帐号")
    private String username;

    @Label("邮箱密码")
    @NotBlank
    @Size(min = 1, max = 255)
    @Control(label = "邮箱密码", type = ControlType.PASSWORD)
    private String password;

    @Label("发件人")
    @NotBlank
    @Size(min = 1, max = 255)
    @Control(label = "发件人")
    private String name;

    /**
     * 邮件会话
     */
    private Session session;

    @Override
    public void afterPropertiesSet() throws Exception {
        Properties props = new Properties();
        // 消息发送协议写死
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.smtp.host", this.host);
        props.setProperty("mail.smtp.auth", String.valueOf(this.useSsl == AvailableEnum.ENABLED));
        props.setProperty("mail.smtp.port", this.port.toString());
        if (this.useSsl == AvailableEnum.ENABLED) {
            props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.setProperty("mail.smtp.socketFactory.fallback", "false");
            props.setProperty("mail.smtp.socketFactory.port", this.port.toString());
        }
        props.setProperty("mail.smtp.user", this.username);
        props.setProperty("mail.smtp.password", this.password);

        session = Session.getDefaultInstance(props);
    }

    @Override
    @SneakyThrows
    public void standardPublish(StandardBody body) {
        var mail = new MimeMessage(session);

        // 设置发件人帐号和名称
        mail.setFrom(new InternetAddress(this.username, this.name, "UTF-8"));
        // 设置接收人帐号名称
        for (var recipient : body.getRecipients()) {
            mail.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress(recipient.getAddress(), recipient.getName(), "UTF-8"));
        }

        mail.setSubject(body.getSubject());
        mail.setContent(body.getContent(), new MediaType(MediaType.TEXT_HTML, StandardCharsets.UTF_8).toString());
        mail.setSentDate(new Date());
        mail.saveChanges();

        // 发送邮件
        try (var transport = session.getTransport()) {
            transport.connect(this.username, this.password);
            transport.sendMessage(mail, mail.getAllRecipients());
        }
    }

    @Override
    @SneakyThrows
    public void customPublish(MailBody body) {
        var mail = new MimeMessage(session);

        // 设置发件人帐号和名称
        mail.setFrom(new InternetAddress(Objectx.getOrDefault(body.getFrom(), this.username), this.name, "UTF-8"));
        // 设置接收人帐号和名称
        for (var recipient : body.getTo()) {
            mail.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress(recipient.getAddress(), recipient.getName(), "UTF-8"));
        }
        // 设置抄送人帐号和名称
        if (Listx.isNotEmpty(body.getCc())) {
            for (var recipient : body.getCc()) {
                mail.addRecipient(MimeMessage.RecipientType.CC, new InternetAddress(recipient.getAddress(), recipient.getName(), "UTF-8"));
            }
        }
        // 设置密送人帐号和名称
        if (Listx.isNotEmpty(body.getBcc())) {
            for (var recipient : body.getBcc()) {
                mail.addRecipient(MimeMessage.RecipientType.BCC, new InternetAddress(recipient.getAddress(), recipient.getName(), "UTF-8"));
            }
        }

        mail.setSubject(body.getSubject());
        mail.setContent(body.getContent(), new MediaType(MediaType.TEXT_HTML, StandardCharsets.UTF_8).toString());
        mail.setSentDate(new Date());
        mail.saveChanges();

        // 发送邮件
        try (var transport = session.getTransport()) {
            transport.connect(this.username, this.password);
            transport.sendMessage(mail, mail.getAllRecipients());
        }
    }
}
