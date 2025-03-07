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

import central.lang.reflect.TypeRef;
import central.studio.document.DocumentApplication;
import central.util.Jsonx;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/// Document Controller Test Cases
/// @author Alan Yeh
/// @see TestDocumentController
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = DocumentApplication.class)
public class TestDocumentController {

    private static final String PATH = "/document/api";

/// @see DocumentController#getMetadata
    @Test
    public void case0(@Autowired MockMvc mvc) throws Exception {
        var request = MockMvcRequestBuilders.get(PATH + "/metadata")
                .accept(MediaType.APPLICATION_JSON);

        var response = mvc.perform(request)
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        var body = Jsonx.Default().deserialize(response.getContentAsString(), TypeRef.of(Map.class));
        assertNotNull(body);
    }

/// @see DocumentController#getContent
    @Test
    public void case1(@Autowired MockMvc mvc) throws Exception {
        var request = MockMvcRequestBuilders.get(PATH + "/content")
                .queryParam("category", "api")
                .queryParam("version", "1.0.x")
                .queryParam("article", "document-about")
                .accept(MediaType.APPLICATION_JSON);

        var response = mvc.perform(request)
                .andExpect(status().is2xxSuccessful())
                .andReturn().getResponse();

        var body = response.getContentAsString(StandardCharsets.UTF_8);
        assertNotNull(body);
    }

/// @see DocumentController#getAssets
    @Test
    public void case3(@Autowired MockMvc mvc) throws Exception {
        var request = MockMvcRequestBuilders.get("/document/assets/spec/about/phone.png")
                .accept(MediaType.ALL);

        mvc.perform(request)
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.IMAGE_PNG));
    }
}
