package ltd.newbee.mall.util;

import com.alibaba.fastjson.JSON;
import ltd.newbee.mall.controller.vo.NewBeeMallOrderItemVO;
import ltd.newbee.mall.controller.vo.NewBeeMallOrderListVO;
import ltd.newbee.mall.manager.NewBeeMallOrderManager;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ExcelDownloadUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelDownloadUtils.class);

    private static final int HEADER_ROW = 0;

    private static final String[] HEAD_LIST = new String[]{"订单号", "商品编号", "商品名称", "供应商报价", "商品数量", "收货人姓名", "收货人电话", "收货人地址", "下单时间", "配送单编号", "配送单状态"};

    @Resource
    private NewBeeMallOrderManager mallOrderManager;

    public void exportExcelTemplate(HttpServletResponse response) {
        excelProcess(response, HEAD_LIST);
    }


    private void excelProcess(HttpServletResponse response, String[] headList) {

        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-disposition",
                "attachment; filename=" + new Date().getTime() + ".xls");
        response.setCharacterEncoding("utf-8");
        OutputStream os = null;
        HSSFWorkbook wb = null;
        try {
            os = response.getOutputStream();
            wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet();
            CellStyle cellStyle = createCellStyle(wb);
            setHeaderRow(sheet, cellStyle, headList);
            // 填充数据
            Map<String, Object> params = new HashMap<>();
            params.put("page", 1);
            params.put("limit", 10);
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
//            params.put("startTime", calendar.getTime());
//            params.put("channelId",1);
            PageQueryUtil pageQueryUtil = new PageQueryUtil(params);
            List<NewBeeMallOrderListVO> list = mallOrderManager.getOrdersByExport(pageQueryUtil);
            LOGGER.info("download list = {}", JSON.toJSON(list));
            int index = 1;
            for (NewBeeMallOrderListVO newBeeMallOrderListVO : list) {
                if (newBeeMallOrderListVO.getOrderStatus() != (byte) 1) {
                    continue;
                }
                for (NewBeeMallOrderItemVO mallOrderItemVO : newBeeMallOrderListVO.getNewBeeMallOrderItemVOS()) {
                    LOGGER.info("download mallOrderItemVO = {}", JSON.toJSON(mallOrderItemVO));
                    HSSFRow row1 = sheet.createRow(index);
                    HSSFCell fen = row1.createCell((short) 0);   //--->创建一个单元格
                    fen.setCellValue(newBeeMallOrderListVO.getOrderId());
                    HSSFCell fen2 = row1.createCell((short) 1);   //--->创建一个单元格
                    fen2.setCellValue(mallOrderItemVO.getGoodsId());
                    HSSFCell fen3 = row1.createCell((short) 2);   //--->创建一个单元格
                    fen3.setCellValue(mallOrderItemVO.getGoodsName());
                    HSSFCell fen4 = row1.createCell((short) 3);   //--->创建一个单元格
                    fen4.setCellValue(mallOrderItemVO.getOriginalPrice());
                    HSSFCell fen5 = row1.createCell((short) 4);   //--->创建一个单元格
                    fen5.setCellValue(mallOrderItemVO.getGoodsCount());
                    HSSFCell fen6 = row1.createCell((short) 5);   //--->创建一个单元格
                    fen6.setCellValue(newBeeMallOrderListVO.getRecipient());
                    HSSFCell fen7 = row1.createCell((short) 6);   //--->创建一个单元格
                    fen7.setCellValue(newBeeMallOrderListVO.getPhone());
                    HSSFCell fen8 = row1.createCell((short) 7);   //--->创建一个单元格
                    fen8.setCellValue(newBeeMallOrderListVO.getUserAddress());
                    HSSFCell fen9 = row1.createCell((short) 8);   //--->创建一个单元格
                    fen9.setCellValue(newBeeMallOrderListVO.getCreateTime());
                    index++;
                }

            }
            wb.write(os);
            dispose(os, wb);
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("[ExcelDownloadUtils." + Thread.currentThread().getStackTrace()[2].getMethodName() + "]" + "文件名编码错误", e);
        } catch (IOException e) {
            LOGGER.error("[ExcelDownloadUtils." + Thread.currentThread().getStackTrace()[2].getMethodName() + "]", e);
        } finally {
            dispose(os, wb);
        }
    }

    private static void setHeaderRow(HSSFSheet sheet, CellStyle cellStyle, String[] list) {
        LOGGER.info("[ExcelDownloadUtils.setHeaderRow] sheet = {}, cellStyle = {}", sheet, cellStyle);
        HSSFRow headRow = sheet.createRow(HEADER_ROW);
        HSSFCell cell;
        for (int i = 0; i < list.length; i++) {
            cell = headRow.createCell(i);
            cell.setCellValue(list[i]);
            cell.setCellStyle(cellStyle);
        }
    }

    private static CellStyle createCellStyle(HSSFWorkbook wb) {
        LOGGER.info("[ExcelDownloadUtils.createCellStyle] wb = {}", wb);
        short fontSize = 14;
        String fontName = "宋体";
        Font font = wb.createFont();
        font.setFontHeightInPoints(fontSize);
        font.setFontName(fontName);
        font.setColor(Font.COLOR_NORMAL);
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setFont(font);
        return cellStyle;

    }

    private static void dispose(OutputStream os, HSSFWorkbook wb) {
        if (wb != null) {
            try {
                wb.close();
            } catch (IOException e) {
                LOGGER.error("[dispose] wb close IOException ", e);
            }
        }

        if (os != null) {
            try {
                os.flush();
                os.close();
            } catch (IOException e) {
                LOGGER.error("[dispose] os close IOException ", e);
            }
        }
    }


}
