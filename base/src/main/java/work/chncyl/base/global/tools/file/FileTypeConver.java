package work.chncyl.base.global.tools.file;

import com.itextpdf.text.*;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.Pipeline;
import com.itextpdf.tool.xml.XMLWorker;
import com.itextpdf.tool.xml.XMLWorkerFontProvider;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.itextpdf.tool.xml.css.CssFile;
import com.itextpdf.tool.xml.css.StyleAttrCSSResolver;
import com.itextpdf.tool.xml.html.CssAppliers;
import com.itextpdf.tool.xml.html.CssAppliersImpl;
import com.itextpdf.tool.xml.html.Tags;
import com.itextpdf.tool.xml.parser.XMLParser;
import com.itextpdf.tool.xml.pipeline.css.CSSResolver;
import com.itextpdf.tool.xml.pipeline.css.CssResolverPipeline;
import com.itextpdf.tool.xml.pipeline.end.PdfWriterPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipelineContext;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileTypeConver {
    public static String fileToString(String filePath) {
        if (StringUtils.isBlank(filePath)) {
            return null;
        }
        try {
            return new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String fileToString(File file) {
        if (file == null) {
            return null;
        }
        try {
            return new String(Files.readAllBytes(file.toPath()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void markdownToPdf(String markdown, String outputFilePath) throws IOException, DocumentException {
        guaranteeNewDocument(outputFilePath);
        OutputStream os = Files.newOutputStream(Paths.get(outputFilePath));
        markdownToPdf(markdown, os);
    }

    public static void markdownToPdf(String markdown, OutputStream os) throws IOException, DocumentException {
        String html = markdownToHtml(markdown);
        htmlToPdf(html, os);
    }

    public static String markdownToHtml(String markdown) {
        // markdown解析
        Parser parser = Parser.builder().build();
        // html渲染
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(parser.parse(markdown));
    }

    public static void htmlToPdf(String html, OutputStream os) throws IOException, DocumentException {
        Document document = new Document(PageSize.A4);
        try {
            PdfWriter pdfWriter = PdfWriter.getInstance(document, os);
            document.open();
            document.addCreationDate();

            // 构建完整的HTML模板,确保中文正确显示
            String htmlTemplate =
                    "<!DOCTYPE html><html><head>" +
                            "<meta charset='UTF-8'/>" +
                            "<style type='text/css'>" +
                            "body { font-family: SimSun; }" +
                            "* { font-family: SimSun; }" +
                            "</style>" +
                            "</head><body>" + html + "</body></html>";

            // 创建自定义的AsianFontProvider
            class AsianFontProvider extends XMLWorkerFontProvider {
                public AsianFontProvider() throws IOException {
                    super(ResourceFileUtils.getFileFromResource(File.separator + "fonts" + File.separator + "simsun.ttc").getAbsolutePath());
                }

                @Override
                public Font getFont(String fontName, String encoding, boolean embedded, float size, int style, com.itextpdf.text.BaseColor color) {
                    try {
                        // 尝试使用 itext-asian自带的宋体
                        BaseFont baseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
                        return new Font(baseFont, size, style, color);
                    } catch (Exception e) {
                        try {
                            // 如果上面的字体不可用，尝试使用自定义字体
//                            String fontPath = "C:/Windows/Fonts/simsun.ttc";
                            String fontPath = ResourceFileUtils.getFileFromResource(File.separator + "fonts" + File.separator + "simsun.ttc").getAbsolutePath();
                            BaseFont baseFont = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
                            return new Font(baseFont, size, style, color);
                        } catch (Exception ex) {
                            return super.getFont(fontName, encoding, embedded, size, style, color);
                        }
                    }
                }
            }
            // css处理器
            CSSResolver cssResolver = new StyleAttrCSSResolver();
            CssFile cssFile = XMLWorkerHelper.getCSS(new ByteArrayInputStream(
                    "* { font-family: SimSun; }".getBytes(StandardCharsets.UTF_8)));
            cssResolver.addCss(cssFile);

            XMLWorkerFontProvider fontProvider = new AsianFontProvider();
            CssAppliers cssAppliers = new CssAppliersImpl(fontProvider);
            HtmlPipelineContext htmlContext = new HtmlPipelineContext(cssAppliers);
            htmlContext.setTagFactory(Tags.getHtmlTagProcessorFactory());

            Pipeline<?> pipeline = new CssResolverPipeline(cssResolver,
                    new HtmlPipeline(htmlContext,
                            new PdfWriterPipeline(document, pdfWriter)));

            XMLWorker worker = new XMLWorker(pipeline, true);
            XMLParser xmlParser = new XMLParser(worker, StandardCharsets.UTF_8);

            xmlParser.parse(new ByteArrayInputStream(htmlTemplate.getBytes(StandardCharsets.UTF_8)));
        } finally {
            if (document != null) {
                document.close();
            }
        }
    }

    // Word 转 PDF
    public void wordToPdf(String docPath, String pdfPath) throws Exception {
        // 读取Word文档
        FileInputStream fis = new FileInputStream(docPath);
        XWPFDocument document = new XWPFDocument(fis);

        // 创建PDF文档
        Document pdfDocument = new Document(PageSize.A4);
        PdfWriter.getInstance(pdfDocument, new FileOutputStream(pdfPath));
        pdfDocument.open();

        // 设置中文字体
        BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
        Font fontChinese = new Font(bfChinese, 12, Font.NORMAL);

        // 转换段落
        for (XWPFParagraph paragraph : document.getParagraphs()) {
            Paragraph pdfParagraph = new Paragraph();
            for (XWPFRun run : paragraph.getRuns()) {
                String text = run.getText(0);
                if (text != null) {
                    Chunk chunk = new Chunk(text, fontChinese);
                    pdfParagraph.add(chunk);
                }
            }
            pdfDocument.add(pdfParagraph);
        }

        // 转换表格
        for (XWPFTable table : document.getTables()) {
            PdfPTable pdfTable = new PdfPTable(table.getRows().get(0).getTableCells().size());
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    pdfTable.addCell(new PdfPCell(new Phrase(cell.getText(), fontChinese)));
                }
            }
            pdfDocument.add(pdfTable);
        }

        pdfDocument.close();
        fis.close();
    }

    // 4. Excel 转 PDF
    public void excelToPdf(String excelPath, String pdfPath) throws Exception {
        FileInputStream input = new FileInputStream(excelPath);
        XSSFWorkbook workbook = new XSSFWorkbook(input);

        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(pdfPath));
        document.open();

        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            XSSFSheet sheet = workbook.getSheetAt(i);
            PdfPTable table = new PdfPTable(sheet.getRow(0).getLastCellNum());

            // 转换每一行
            for (Row row : sheet) {
                for (Cell cell : row) {
                    table.addCell(cell.toString());
                }
            }

            document.add(table);
        }
        document.close();
        input.close();
    }

    private static void guaranteeNewDocument(String filePath) {
        if (StringUtils.isBlank(filePath)) {
            return;
        }
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
        guaranteeDocumentExist(filePath);
    }

    private static void guaranteeDocumentExist(String filePath) {
        if (StringUtils.isBlank(filePath)) {
            return;
        }
        File file = new File(filePath);
        // 查看文件上级文件夹，不存在则创建
        File parentFile = file.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
