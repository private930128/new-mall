package ltd.newbee.mall.controller.admin;

import ltd.newbee.mall.util.ExcelDownloadUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/admin/file")
public class NewBeeMallFileController {

    @Resource
    private ExcelDownloadUtils excelDownloadUtils;

    @RequestMapping(value = "/r/downloadTemplateFile", method = RequestMethod.GET)
    @ResponseBody
    public void downloadTemplateFile(HttpServletResponse response) {
        excelDownloadUtils.exportExcelTemplate(response);
    }

}
