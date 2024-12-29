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

package central.data.saas;

import central.bean.Available;
import central.bean.Nonnull;
import central.bean.Remarkable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * Application Route
 * <p>
 * 应用路由
 *
 * @author Alan Yeh
 * @since 2022/09/25
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationRoute implements Serializable, Available, Remarkable {

    @Serial
    private static final long serialVersionUID = -4968804715373126621L;

    /**
     * 上下文路径
     */
    @Nonnull
    private String contextPath;

    /**
     * 服务地址
     */
    @Nonnull
    private String url;

    /**
     * 是否启用
     */
    @Nonnull
    private Boolean enabled;

    /**
     * 备注
     */
    @Nonnull
    private String remark;

    public ApplicationRouteInput.Builder toInput() {
        return ApplicationRouteInput.builder()
                .contextPath(this.getContextPath())
                .url(this.getUrl())
                .enabled(this.getEnabled())
                .remark(this.getRemark());
    }
}
