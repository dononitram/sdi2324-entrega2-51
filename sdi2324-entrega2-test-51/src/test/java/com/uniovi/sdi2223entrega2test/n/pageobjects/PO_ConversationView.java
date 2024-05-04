package com.uniovi.sdi2223entrega2test.n.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class PO_ConversationView extends PO_NavView{
    public static void sendMessage(WebDriver driver, String messagep) {
        WebElement message = driver.findElement(By.name("message"));
        message.click();
        message.clear();
        message.sendKeys(messagep);

        By btn = By.className("btn");
        driver.findElements(btn).get(0).click();
    }
}
