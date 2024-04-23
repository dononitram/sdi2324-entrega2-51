package com.uniovi.sdi2223entrega2test.51.pageobjects;

import com.uniovi.application.util.SeleniumUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class PO_PublicView extends PO_NavView {

    // LOGIN FUNCTIONALITY ----------------------------------------------

    static public void fillLoginForm(WebDriver driver, String emailp, String passwordp) {

        WebElement email = driver.findElement(By.name("username"));
        email.click();
        email.clear();
        email.sendKeys(emailp);

        WebElement password = driver.findElement(By.name("password"));
        password.click();
        password.clear();
        password.sendKeys(passwordp);

        By btn = By.className("btn");
        driver.findElement(btn).click();

    }

    public static void checkLoginIsVisible(WebDriver driver, int language) {
        SeleniumUtils.waitLoadElementsBy(driver, "text", p.getString("login.message", language), getTimeout());
    }

    public static void checkErrorMessageIsVisible(WebDriver driver, int language) {
        SeleniumUtils.waitLoadElementsBy(driver, "text", p.getString("login.error", language), getTimeout());
    }

    public static void checkLogoutMessageIsVisible(WebDriver driver, int language) {
        SeleniumUtils.waitLoadElementsBy(driver, "text", p.getString("login.logout", language), getTimeout());
    }

    public static void checkLogoutNotVisible(WebDriver driver, int language) {
        // Comprueba la opción logout no está en el nav
        try {
            SeleniumUtils.waitLoadElementsBy(driver, "text", p.getString("nav.logout", language), getTimeout());
            assert (false);
        } catch (Exception e) {
            assert (true);
        }
    }

    public static void showSignup(WebDriver driver) {
        List<WebElement> elements = SeleniumUtils.waitLoadElementsBy(driver, "@href", "signup",
                getTimeout());
        elements.get(0).click();
    }

    public static void loginUser(WebDriver driver) {
        fillLoginForm(driver, "user01@email.com", "Us3r@1-PASSW");
    }

    public static void loginAdmin(WebDriver driver) {
        fillLoginForm(driver, "admin@email.com", "@Dm1n1str@D0r");
    }

    public static void loginFailEmpty(WebDriver driver) {
        fillLoginForm(driver, "", "");
    }

    public static void loginFailIncorrect(WebDriver driver) {
        fillLoginForm(driver, "user01@email.com", "WRONGPASS");
    }

    public static void loginSpecificUser(String user, String password, WebDriver driver) {
        fillLoginForm(driver, user, password);
    }

    public static void switchToSignup(WebDriver driver) {
        PO_PrivateView.clickOption(driver, "/signup", "free", "/html/body/div/h2");
    }

    // SIGNUP FUNCTIONALITY ----------------------------------------------

    static public void fillSingupForm(WebDriver driver, String emailp, String namep, String lastnamep, String passwordp, String passwordconfp) {

        WebElement email = driver.findElement(By.name("email"));
        email.click();
        email.clear();
        email.sendKeys(emailp);

        WebElement name = driver.findElement(By.name("name"));
        name.click();
        name.clear();
        name.sendKeys(namep);

        WebElement lastname = driver.findElement(By.name("surname"));
        lastname.click();
        lastname.clear();
        lastname.sendKeys(lastnamep);

        WebElement password = driver.findElement(By.name("password"));
        password.click();
        password.clear();
        password.sendKeys(passwordp);

        WebElement passwordConfirm = driver.findElement(By.name("passwordConfirm"));
        passwordConfirm.click();
        passwordConfirm.clear();
        passwordConfirm.sendKeys(passwordconfp);

        By btn = By.className("btn");
        driver.findElement(btn).click();
    }

    public static void signupSuccess(WebDriver driver) {
        fillSingupForm(driver, "test@test.com", "Test", "Test", "123456789aB=11", "123456789aB=11");
    }

    public static void signupSuccessBis(WebDriver driver) {
        fillSingupForm(driver, "testBis@test.com", "TestBis", "TestBis", "123456789aB=11", "123456789aB=11");
    }

    public static void signupAllError(WebDriver driver) {
        fillSingupForm(driver, "", "", "", "123", "123");
    }

    public static void signupErrorPasswords(WebDriver driver) {
        fillSingupForm(driver, "test@test.com", "Test", "Test", "123456789aB=11", "123456789aB");
    }

    public static void repeatedEmail(WebDriver driver) {
        fillSingupForm(driver, "admin@email.com", "Test", "Test", "123456789aB=11", "123456789aB=11");
    }

    public static void switchToLogin(WebDriver driver) {
        PO_PrivateView.clickOption(driver, "/login", "free", "/html/body/div/h2");
    }

}