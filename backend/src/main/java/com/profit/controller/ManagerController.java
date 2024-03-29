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

    @GetMapping("allbond")
    public String allbond() {
        return TemplatePath.ALL_BOND_HTML;
    }


    @GetMapping("login")
    public String login() {
        return TemplatePath.USER_LOGIN;
    }

    @GetMapping("transactionLogs")
    public String transactionLogs() {
        return TemplatePath.TRANSACTION_LOGS;
    }

    @GetMapping("profitAnlysis")
    public String profitAnlysis() {
        return TemplatePath.PROFIT_ANLYSIS;
    }
}