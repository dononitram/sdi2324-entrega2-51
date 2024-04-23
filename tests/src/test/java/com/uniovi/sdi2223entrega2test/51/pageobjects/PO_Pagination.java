package com.uniovi.sdi2223entrega2test.51.pageobjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class PO_Pagination extends PO_View{
    /**
     * Pasa a la primera página del elemento page
     * @param driver
     */
    public static void clickFirstPage(WebDriver driver) {
        List<WebElement> elements = PO_View.checkElementBy(driver, "id", "first_page");
        elements.get(0).click();
    }
    /**
     * Pasa a la página anterior del elemento page (cuidado cuando no tengamos)
     * @param driver
     */
    public static void clickPreviousPage(WebDriver driver) {
        List<WebElement> elements = PO_View.checkElementBy(driver, "id", "previous_page");
        elements.get(0).click();
    }
    /**
     * Selecciona la página actual del elemento page
     * @param driver
     */
    public static void clickActualPage(WebDriver driver) {
        List<WebElement> elements = PO_View.checkElementBy(driver, "id", "actual_page");
        elements.get(0).click();
    }
    /**
     * Pasa a la página siguiente del elemento page (cuidado cuando no tengamos)
     * @param driver
     */
    public static void clickNextPage(WebDriver driver) {
        List<WebElement> elements = PO_View.checkElementBy(driver, "id", "next_page");
        elements.get(0).click();
    }
    /**
     * Pasa a la última página del elemento page
     * @param driver
     */
    public static void clickLastPage(WebDriver driver) {
        List<WebElement> elements = PO_View.checkElementBy(driver, "id", "last_page");
        elements.get(0).click();
    }

}
