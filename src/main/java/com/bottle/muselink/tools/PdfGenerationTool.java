package com.bottle.muselink.tools;

import cn.hutool.core.io.FileUtil;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.html2pdf.HtmlConverter;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.jsoup.Jsoup;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PdfGenerationTool {

    private final String FILE_DIR = System.getProperty("user.dir") + "/tmp/pdf";

    // Markdown标题正则
    private static final Pattern HEADER_PATTERN = Pattern.compile("^(#{1,6})\\s+(.+)$");
    // Markdown图片正则
    private static final Pattern IMAGE_PATTERN = Pattern.compile("!\\[(.*?)\\]\\((.*?)\\)");
    // 无序列表
    private static final Pattern UL_ITEM_PATTERN = Pattern.compile("^-\\s+(.+)$", Pattern.MULTILINE);
    // 有序列表
    private static final Pattern OL_ITEM_PATTERN = Pattern.compile("^(\\d+)\\.\\s+(.+)$", Pattern.MULTILINE);

    @Tool(description = "Generate a PDF file with given content")
    public String generatePDF(
            @ToolParam(description = "Name of the file to save the generated PDF") String fileName,
            @ToolParam(description = "Content to be included in the PDF") String content) {

        FileUtil.mkdir(FILE_DIR);
        String filePath = FILE_DIR + "/" + fileName;
        String processContent = "";

        if(!content.startsWith("</")){
            Parser parser = Parser.builder().build();
            Node document = parser.parse(content);
            HtmlRenderer renderer = HtmlRenderer.builder().build();
            processContent = renderer.render(document);
        }

        try (OutputStream os = new FileOutputStream(filePath)) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(processContent, "");
            renderer.layout();
            renderer.createPDF(os);
            return "PDF generated successfully to: " + filePath;
        } catch (Exception e) {
            return "Error generating PDF: " + e.getMessage();
        }
        // String filePath = FILE_DIR + "/" + fileName;
        // try {
        //     // 创建目录
        //     FileUtil.mkdir(FILE_DIR);
        //
        //     HtmlConverter.convertToPdf(content, new FileOutputStream(filePath));
        //
        //     // // 创建 PdfWriter 和 PdfDocument 对象
        //     // PdfWriter writer = new PdfWriter(filePath);
        //     // PdfDocument pdf = new PdfDocument(writer);
        //     // Document document = new Document(pdf);
        //     // // 使用内置中文字体
        //     // PdfFont font = PdfFontFactory.createFont("STSongStd-Light", "UniGB-UCS2-H");
        //     // document.setFont(font);
        //     // // 处理内容，判断是 HTML 还是 Markdown
        //     // processMarkdownContent(content, document, font);
        //     // document.close();
        //
        //     return "PDF generated successfully to: " + filePath;
        // } catch (IOException e) {
        //     return "Error generating PDF: " + e.getMessage();
        // }
    }

    /**
     * 处理Markdown内容
     */
    private void processMarkdownContent(String content, Document document, PdfFont font) {
        String[] lines = content.split("\n");
        StringBuilder textBuffer = new StringBuilder();

        for (String line : lines) {
            Matcher headerMatcher = HEADER_PATTERN.matcher(line);
            if (headerMatcher.matches()) {
                if (textBuffer.length() > 0) {
                    processTextWithImages(textBuffer.toString(), document);
                    textBuffer.setLength(0);
                }

                String headerMarker = headerMatcher.group(1);
                String headerText = headerMatcher.group(2);
                addHeader(document, headerText, headerMarker.length(), font);
                continue;
            }
            Matcher ulMatcher = UL_ITEM_PATTERN.matcher(line);
            if (ulMatcher.matches()) {
                // 处理无序列表项
                addListItem(document, ulMatcher.group(1), false, font);
                continue;
            }

            Matcher olMatcher = OL_ITEM_PATTERN.matcher(line);
            if (olMatcher.matches()) {
                // 处理有序列表项
                addListItem(document, olMatcher.group(2), true, font);
                continue;
            }

            textBuffer.append(line).append("\n");
        }

        if (textBuffer.length() > 0) {
            processTextWithImages(textBuffer.toString(), document);
        }
    }

    /**
     * 处理HTML内容
     */
    private void processHtmlContent(String html, Document document, PdfFont font) {
        org.jsoup.nodes.Document doc = Jsoup.parse(html);

        for (org.jsoup.nodes.Element element : doc.getAllElements()) {
            switch (element.tagName().toLowerCase()) {
                case "h1", "h2", "h3", "h4", "h5", "h6":
                    int level = Integer.parseInt(element.tagName().substring(1));
                    addHeader(document, element.text(), level, font);
                    break;
                case "p":
                    addFormattedText(document, element.text());
                    break;
                case "img":
                    String imageUrl = element.attr("src");
                    try {
                        if (imageUrl.startsWith("data:image")) {
                            String base64Data = imageUrl.substring(imageUrl.indexOf(",") + 1);
                            byte[] imageData = Base64.getDecoder().decode(base64Data);
                            Image image = new Image(ImageDataFactory.create(imageData, true));
                            document.add(image);
                        } else {
                            Image image = new Image(ImageDataFactory.create(new URL(imageUrl)));
                            document.add(image);
                        }
                    } catch (Exception e) {
                        document.add(new Paragraph("无法加载图片: " + imageUrl));
                    }
                    break;
                case "ul":
                    for (org.jsoup.nodes.Element li : element.select("li")) {
                        addListItem(document, li.text(), false, font);
                    }
                    break;
                case "ol":
                    for (org.jsoup.nodes.Element li : element.select("li")) {
                        addListItem(document, li.text(), true, font);
                    }
                    break;
                default:
                    // 其他标签按普通文本处理
                    addFormattedText(document, element.text());
            }
        }
    }

    /**
     * 添加标题
     */
    private void addHeader(Document document, String headerText, int level, PdfFont font) {
        Paragraph header = new Paragraph(headerText);

        float fontSize = 24f;
        switch (level) {
            case 1: fontSize = 24f; break;
            case 2: fontSize = 20f; break;
            case 3: fontSize = 18f; break;
            case 4: fontSize = 16f; break;
            case 5: fontSize = 14f; break;
            case 6: fontSize = 12f; break;
        }

        header.setFont(font)
                .setFontSize(fontSize)
                .setTextAlignment(TextAlignment.LEFT);

        document.add(header);
    }

    /**
     * 处理包含图片的文本
     */
    private void processTextWithImages(String content, Document document) {
        Matcher matcher = IMAGE_PATTERN.matcher(content);

        int lastEnd = 0;

        while (matcher.find()) {
            String textBefore = content.substring(lastEnd, matcher.start());
            if (!textBefore.isEmpty()) {
                // document.add(new Paragraph(textBefore));
                addFormattedText(document, textBefore);
            }

            String imageUrl = matcher.group(2);
            try {
                Image image = new Image(ImageDataFactory.create(new URL(imageUrl)));
                image.setWidth(document.getPdfDocument().getDefaultPageSize().getWidth() * 0.8f);
                image.setAutoScale(true);
                document.add(image);
            } catch (Exception e) {
                document.add(new Paragraph("无法加载图片: " + imageUrl + " (" + e.getMessage() + ")"));
            }

            lastEnd = matcher.end();
        }

        if (lastEnd < content.length()) {
            // document.add(new Paragraph(content.substring(lastEnd)));
            addFormattedText(document, content.substring(lastEnd));
        }
    }

    /**
     * 添加支持多种格式的文本段落（如加粗、列表）
     */
    private void addFormattedText(Document document, String text) {
        Paragraph paragraph = new Paragraph();

        // 替换加粗
        text = text.replaceAll("\\*\\*(.+?)\\*\\*", "<b>$1</b>");
        // 替换斜体
        text = text.replaceAll("\\*(.+?)\\*", "<i>$1</i>");

        // 简单解析 HTML-like 标签
        StringBuilder sb = new StringBuilder();
        boolean inTag = false;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '<') {
                inTag = true;
                continue;
            } else if (c == '>') {
                inTag = false;
                continue;
            }

            if (!inTag) {
                sb.append(c);
            }
        }

        // 这里只是简单拼接，实际可以使用更复杂的解析器或 RichTextElement
        paragraph.add(sb.toString());
        document.add(paragraph);
    }

    private void addListItem(Document document, String text, boolean ordered, PdfFont font) {
        Paragraph p = new Paragraph((ordered ? "• " : "– ") + text);
        p.setFont(font).setMarginLeft(20);
        document.add(p);
    }


}

