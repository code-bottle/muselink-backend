package com.bottle.muselink.tools;

import cn.hutool.core.io.FileUtil;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.font.FontProvider;

import com.itextpdf.styledxmlparser.resolver.resource.IResourceRetriever;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.regex.Pattern;


public class PdfGenerationTool {

    private final String FILE_DIR = System.getProperty("user.dir") + "/tmp/pdf";

    @Tool(description = "Generate a PDF file with given content")
    public String generatePDF(
            @ToolParam(description = "Name of the file to save the generated PDF") String fileName,
            @ToolParam(description = "Content to be included in the PDF") String content) {

        FileUtil.mkdir(FILE_DIR);
        String filePath = FILE_DIR + "/" + fileName;
        String processContent = content;

        // 如果 content 是 Markdown，先转成 HTML
        if (!content.startsWith("<")) {
            Parser parser = Parser.builder().build();
            Node document = parser.parse(content);
            HtmlRenderer renderer = HtmlRenderer.builder().build();
            processContent = renderer.render(document);
        }
        // 参考 https://jingling.im/posts/java-itext7-html-to-pdf-guide
        try (OutputStream outputStream = new FileOutputStream(filePath)) {
            // 转换过程
            ConverterProperties properties = new ConverterProperties();
            FontProvider fontProvider = new FontProvider();
            // 添加系统所有字体，支持中文等多语言
            fontProvider.addSystemFonts();
            properties.setFontProvider(fontProvider);

            // 设置自定义资源解析器，支持远程图片
            properties.setResourceRetriever(new CustomResourceRetriever());
            // 设置 baseUri 为 null 表示允许远程资源加载
            properties.setBaseUri(null);

            PdfWriter pdfWriter = new PdfWriter(outputStream);
            PdfDocument pdfDocument = new PdfDocument(pdfWriter);

            // 从HTML字符串转换
            HtmlConverter.convertToPdf(processContent, pdfDocument, properties);

            return "PDF generated successfully to: " + filePath;
        } catch (Exception e) {
            e.printStackTrace();
            return "Error generating PDF: " + e.getMessage();
        }
    }

    /**
     * 自定义资源获取器 - 处理网络图片下载
     */
    static class CustomResourceRetriever implements IResourceRetriever {
        // 使用线程安全的 HTTP 客户端
        private static final CloseableHttpClient httpClient = HttpClients.createDefault();

        @Override
        public InputStream getInputStreamByUrl(URL url) throws IOException {
            return new ByteArrayInputStream(getByteArrayByUrl(url));
        }

        @Override
        public byte[] getByteArrayByUrl(URL url) throws IOException {
            HttpGet request = new HttpGet(url.toString());

            try (CloseableHttpResponse response = httpClient.execute(request);
                 ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

                // 检查HTTP状态
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode != 200) {
                    throw new IOException("HTTP error: " + statusCode + " for URL: " + url);
                }

                HttpEntity entity = response.getEntity();
                if (entity == null) {
                    throw new IOException("Empty response from: " + url);
                }

                try (InputStream inputStream = entity.getContent()) {
                    byte[] buffer = new byte[8192];  // 使用8KB缓冲区提高效率
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                }
                return outputStream.toByteArray();
            }
        }

    }
}

