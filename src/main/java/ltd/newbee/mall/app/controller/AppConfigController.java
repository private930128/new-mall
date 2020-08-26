package ltd.newbee.mall.app.controller;

import ltd.newbee.mall.manager.NewBeeMallConfigManager;
import ltd.newbee.mall.util.Result;
import ltd.newbee.mall.util.ResultGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * Created by zhanghenan on 2020/2/17.
 */
@Controller
@RequestMapping("/app/config")
public class AppConfigController {

    @Resource
    private NewBeeMallConfigManager newBeeMallConfigManager;

    @RequestMapping(value = "/getCategoryConfig", method = RequestMethod.GET)
    @ResponseBody
    public Result getCategoryConfig() {
        return ResultGenerator.genSuccessDateResult(newBeeMallConfigManager.listCategoryConfig());
    }
}
