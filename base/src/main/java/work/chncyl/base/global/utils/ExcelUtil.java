package work.chncyl.base.global.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

public class ExcelUtil {

    /**
     * 导出Excel
     *
     * @param response  HttpServletResponse
     * @param fileName  文件名
     * @param sheetName sheet名称
     * @param clazz     数据类类型
     * @param data      数据列表
     * @param <T>       泛型
     * @throws IOException IO异常
     */
    public static <T> void exportExcel(HttpServletResponse response, String fileName, String sheetName, Class<T> clazz, List<T> data) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        fileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        EasyExcel.write(response.getOutputStream(), clazz)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .sheet(sheetName)
                .doWrite(data);
    }

    /**
     * 导入Excel
     *
     * @param file  文件
     * @param clazz 数据类类型
     * @param <T>   泛型
     * @return 数据列表
     */
    public static <T> List<T> importExcel(MultipartFile file, Class<T> clazz) throws IOException {
        return EasyExcel.read(file.getInputStream())
                .head(clazz)
                .sheet()
                .doReadSync();
    }
}
