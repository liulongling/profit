package com.profit.controller;

import com.profit.commons.constants.TemplatePath;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class ManagerController {

    @GetMapping("main")
    public String showMain() {
        return TemplatePath.MAIN_HTML;
    }

    @GetMapping("bond")
    public String showBond() {
        return TemplatePath.BOND_HTML;
    }

    @GetMapping("analyse")
    public String showAnalyse() {
        return TemplatePath.PROFIT_HTML;
    }

    @GetMapping("allbond")
    public String allbond() {
        return TemplatePath.ALL_BOND_HTML;
    }
}
