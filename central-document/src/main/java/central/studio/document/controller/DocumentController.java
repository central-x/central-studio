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

package central.studio.document.controller;

import central.starter.webmvc.render.FileRender;
import central.studio.document.core.Library;
import central.studio.document.core.Metadata;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.InternalResourceView;

import java.io.IOException;

/// Document Controller
///
/// 文档控制器
///
/// @author Alan Yeh
@Controller
@RequestMapping("/document")
public class DocumentController {

    @Setter(onMethod_ = @Autowired)
    private Library library;

    /// 首页
    @GetMapping("/")
    public View index() {
        return new InternalResourceView("/document/index.html");
    }

    /// 获取文档库元数据
    @GetMapping("/api/metadata")
    @ResponseBody
    public Metadata getMetadata() {
        return this.library.getMetadata();
    }

    /// 获取文档内容
    @GetMapping("/api/content")
    @ResponseBody
    public String getContent(String category, String version, String article) throws IOException {
        return this.library.getContent(category, version, article);
    }

    /// 获取文档资源
    ///
    /// @param category 分类标识
    /// @param version  版本标识
    /// @param article  文章标识
    /// @param name     资源名称
    @GetMapping({"/assets/{category}/{article}/{name:.+}", "/assets/{category}/{version}/{article}/{name:.+}"})
    public void getAssets(@PathVariable(value = "category") String category,
                          @PathVariable(value = "version", required = false) String version,
                          @PathVariable(value = "article") String article,
                          @PathVariable(value = "name") String name,
                          HttpServletRequest request, HttpServletResponse response) throws IOException {
        var inputStream = this.library.getAsset(category, version, article, name);

        // 返回文件
        new FileRender(request, response)
                .setInputStream(inputStream)
                .setFileName(name)
                .render();
    }
}
