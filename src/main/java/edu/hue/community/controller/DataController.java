package edu.hue.community.controller;

import edu.hue.community.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

/**
 * @author 47552
 * @date 2021/10/01
 */
@Controller
public class DataController {

    @Autowired
    private DataService dataService;

    @RequestMapping(path = "/data", method = {RequestMethod.GET})
    public String goToDataPage() {
        return "/site/admin/data";
    }

    /**
     * 统计一段时间内独立用户的数量
     * @param start
     * @param end
     * @param model
     * @return
     */
    @PostMapping("/data/uv")
    public String getUVCount(@DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                             @DateTimeFormat(pattern = "yyyy-MM-dd") Date end,
                             Model model) {
        Long uvCount = dataService.getUVCount(start, end);
        model.addAttribute("uvCount", uvCount);
        model.addAttribute("uvStartDate", start);
        model.addAttribute("uvEndDate", end);
        return "/site/admin/data";
    }

    /**
     * 统计一段时间内活跃用户的数量
     * @param start
     * @param end
     * @param model
     * @return
     */
    @PostMapping("/data/dau")
    public String getDAUCount(@DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                              @DateTimeFormat(pattern = "yyyy-MM-dd") Date end,
                              Model model) {
        Long dauCount = dataService.getDAUCount(start, end);
        model.addAttribute("dauCount", dauCount);
        model.addAttribute("dauStartDate", start);
        model.addAttribute("dauEndDate", end);
        return "/site/admin/data";
    }
}
