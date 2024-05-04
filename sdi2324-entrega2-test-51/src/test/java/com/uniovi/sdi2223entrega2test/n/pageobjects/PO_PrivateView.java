package com.uniovi.sdi2223entrega2test.n.pageobjects;

import com.uniovi.sdi2223entrega2test.n.util.SeleniumUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class PO_PrivateView extends PO_NavView {

    public static void deleteOneUser(){

    }

    static public void click(WebDriver driver, String criteria, String xpath, int pos) {
        List<WebElement> elements = PO_View.checkElementBy(driver, criteria, xpath);
        elements.get(pos).click();
    }

    public static void logout(WebDriver driver) {
        PO_PrivateView.clickOption(driver, "/logout", "text", p.getString("login.logout", PO_Properties.SPANISH));
    }

    public static void switchToLog(WebDriver driver) {
        PO_PrivateView.clickOption(driver, "/log/list", "free", "/html/body/div/h2");
    }

    public static void deleteLogs(WebDriver driver) {
        SeleniumUtils.waitLoadElementsBy(driver, "free", "/html/body/div/form/button[2]", timeout).get(0).click();
    }

    public static void filterByLoginEx(WebDriver driver) {
        SeleniumUtils.waitLoadElementsBy(driver, "free", "/html/body/div/form/div/select", timeout).get(0).click();
        SeleniumUtils.waitLoadElementsBy(driver, "free", "/html/body/div/form/div/select/option[4]", timeout).get(0).click();
        SeleniumUtils.waitLoadElementsBy(driver, "free", "/html/body/div/form/button[1]", timeout).get(0).click();
    }

    public static void searchBy(WebDriver driver, String searchText){
        WebElement searchTextInput = driver.findElement(By.id("search"));
        searchTextInput.click();
        searchTextInput.clear();
        searchTextInput.sendKeys(searchText);

        WebElement searchButton = driver.findElement(By.id("search-button"));
        searchButton.click();
    }
}