package com.profit.controller;

import com.profit.base.domain.SyUser;
import com.profit.base.domain.SyUserExample;
import com.profit.base.mapper.SyUserMapper;
import com.profit.commons.constants.ResultCode;
import com.profit.commons.constants.StaticPath;
import com.profit.commons.constants.TemplatePath;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {
    @Resource
    private SyUserMapper syUserMapper;

    @PostMapping("login")
    public String login(@ModelAttribute SyUser userDO) {
        if (StringUtils.isEmpty(userDO.getLoginName()) || StringUtils.isEmpty(userDO.getPassword())) {
            return "redirect:/" + StaticPath.ERROR + "?" + ResultCode.MSG_PARAMETER_INVALID;
        }

        SyUserExample syUserExample = new SyUserExample();
        syUserExample.createCriteria().andLoginNameEqualTo(userDO.getLoginName()).andPasswordEqualTo(userDO.getPassword());
        List<SyUser> list = syUserMapper.selectByExample(syUserExample);
        if (list == null || list.isEmpty()) {
            return "redirect:/" + StaticPath.ERROR + "?" + "用户不存在";
        }

        return "redirect:/" + TemplatePath.MAIN_HTML;
    }

}
