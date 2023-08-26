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

package central.security.core.captcha.generator.random;

import central.security.core.captcha.CaptchaView;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.QuadCurve2D;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.util.Map;
import java.util.Random;

/**
 * @author Alan Yeh
 * @since 2023/05/29
 */
@RequiredArgsConstructor
public class RandomCaptchaView implements CaptchaView {

    @Getter
    private final String value;
    private final int width = 108;
    private final int height = 40;

    public static CaptchaView of(String value) {
        return new RandomCaptchaView(value);
    }

    @Override
    public String getContentType() {
        return MediaType.IMAGE_JPEG_VALUE;
    }

    @Override
    public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setHeader(HttpHeaders.PRAGMA, "no-cache");
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-cache");
        response.setHeader(HttpHeaders.EXPIRES, "0");

        try (var buffered = new BufferedOutputStream(response.getOutputStream())) {
            var image = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_RGB);

            // 生成验证码
            this.drawGraphic(this.value, image);

            // 将验证码写到响应流
            ImageIO.write(image, "jpeg", buffered);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 渲染验证码图片逻辑
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    protected static final Random RANDOM = new Random(System.nanoTime());

    // 默认的验证码大小
    protected static final int WIDTH = 108, HEIGHT = 40;
    // 验证码随机字符数组
    protected static final char[] CHAR_RANGE = "3456789ABCDEFGHJKMNPQRSTUVWXYabcdefghjkmnpqrstuvwxy".toCharArray();
    // 验证码字体
    protected static final Font[] RANDOM_FONT = new Font[]{
            new Font(Font.DIALOG, Font.BOLD, 33),
            new Font(Font.DIALOG_INPUT, Font.BOLD, 34),
            new Font(Font.SERIF, Font.BOLD, 33),
            new Font(Font.SANS_SERIF, Font.BOLD, 34),
            new Font(Font.MONOSPACED, Font.BOLD, 34)
    };

    protected void drawGraphic(String randomString, BufferedImage image) {
        // 获取图形上下文
        Graphics2D g = image.createGraphics();

        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        // 图形抗锯齿
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 字体抗锯齿
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // 设定背景色
        g.setColor(getRandColor(210, 250));
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // 绘制小字符背景
        Color color = null;
        for (int i = 0; i < 20; i++) {
            color = getRandColor(120, 200);
            g.setColor(color);
            String rand = String.valueOf(CHAR_RANGE[RANDOM.nextInt(CHAR_RANGE.length)]);
            g.drawString(rand, RANDOM.nextInt(WIDTH), RANDOM.nextInt(HEIGHT));
            color = null;
        }

        // 设定字体
        g.setFont(RANDOM_FONT[RANDOM.nextInt(RANDOM_FONT.length)]);
        // 绘制验证码
        for (int i = 0; i < randomString.length(); i++) {
            // 旋转度数 最好小于45度
            int degree = RANDOM.nextInt(28);
            if (i % 2 == 0) {
                degree = degree * (-1);
            }
            // 定义坐标
            int x = 22 * i, y = 21;
            // 旋转区域
            g.rotate(Math.toRadians(degree), x, y);
            // 设定字体颜色
            color = getRandColor(20, 130);
            g.setColor(color);
            // 将认证码显示到图象中
            g.drawString(String.valueOf(randomString.charAt(i)), x + 8, y + 10);
            // 旋转之后，必须旋转回来
            g.rotate(-Math.toRadians(degree), x, y);
        }
        // 图片中间曲线，使用上面缓存的color
        g.setColor(color);
        // width是线宽,float型
        BasicStroke bs = new BasicStroke(3);
        g.setStroke(bs);
        // 画出曲线
        QuadCurve2D.Double curve = new QuadCurve2D.Double(0d, RANDOM.nextInt(HEIGHT - 8) + 4, WIDTH / 2.0, HEIGHT / 2.0, WIDTH, RANDOM.nextInt(HEIGHT - 8) + 4);
        g.draw(curve);
        // 销毁图像
        g.dispose();
    }

    /*
     * 给定范围获得随机颜色
     */
    protected Color getRandColor(int fc, int bc) {
        Random random = new Random();
        if (fc > 255) {
            fc = 255;
        }

        if (bc > 255) {
            bc = 255;
        }

        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }
}
