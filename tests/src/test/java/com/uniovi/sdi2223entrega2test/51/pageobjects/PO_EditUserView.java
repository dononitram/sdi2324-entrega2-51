package com.uniovi.sdi2223entrega2test.51.pageobjects;

import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class PO_EditUserView extends PO_NavView{
    /**
     * Edita el usuario con los datos recibidos como parámetros
     * @param driver Driver en uso
     * @param email Nuevo email
     * @param name Nuevo nombre
     * @param surname Nuevo apellido
     * @param rol Nuevo rol
     */
    public static void editUser(WebDriver driver, String email, String name,
                    String surname, String rol){
        WebElement emailTxt = driver.findElement(By.id("email-input"));
        emailTxt.click();
        emailTxt.clear();
        emailTxt.sendKeys(email);

        WebElement nameTxt = driver.findElement(By.id("name-input"));
        nameTxt.click();
        nameTxt.clear();
        nameTxt.sendKeys(name);

        WebElement lastnameTxt = driver.findElement(By.id("surname"));
        lastnameTxt.click();
        lastnameTxt.clear();
        lastnameTxt.sendKeys(surname);

        WebElement rolDesplegable = driver.findElement(By.id("role"));
        rolDesplegable.click();

        String idRol = "rol"+rol;
        WebElement rolConcreto = driver.findElement(By.id(idRol));// Esto seleccionará la opción
        rolConcreto.click();

        By btn = By.id("button-submit-edit");
        driver.findElement(btn).click();
    }

    /**
     * Reinicia el form para el usuario 1
     * @param driver Driver en uso
     */
    public static void restartForm(WebDriver driver){
        WebElement emailTxt = driver.findElement(By.id("email-input"));
        emailTxt.click();
        emailTxt.clear();
        emailTxt.sendKeys("user01@email.com");

        WebElement nameTxt = driver.findElement(By.id("name-input"));
        nameTxt.click();
        nameTxt.clear();
        nameTxt.sendKeys("User1");

        WebElement lastnameTxt = driver.findElement(By.id("surname"));
        lastnameTxt.click();
        lastnameTxt.clear();
        lastnameTxt.sendKeys("Surname1");
    }

    /**
     * Comprueba todas las posibilidades de error en la edición de usuario llamando a métodos que lo hacen por el
     * @param driver Driver en uso
     */
    public static void editAllWithError(WebDriver driver){
        //Los comentados tienen errores por tildes de la codificación
        //Para depurar antes del equals
        //System.out.println(result.getText()+"-"+checkText);
        //Email
        editEmptyEmail(driver);
        editBadEmail(driver);
        editDuplicatedEmail(driver);
        editBadSpaceEmail(driver);
        //Name
        editEmptyName(driver);
        editBadSpaceName(driver);
        //Surname
        editEmptySurname(driver);
        editBadSpaceSurname(driver);
    }

    /**
     * Mete mal email vacío
     * @param driver Driver en uso
     */
    public static void editEmptyEmail(WebDriver driver){
        restartForm(driver);
        WebElement emailTxt = driver.findElement(By.id("email-input"));
        emailTxt.click();
        emailTxt.clear();
        emailTxt.sendKeys("");

        By btn = By.id("button-submit-edit");
        driver.findElement(btn).click();

        WebElement result = driver.findElement(By.id("error-email"));
        String checkText = PO_HomeView.getP().getString("Error.empty",
                PO_Properties.SPANISH);
        Assertions.assertTrue(result.getText().contains(checkText));
    }
    /**
     * Mete mal email duplicado
     * @param driver Driver en uso
     */
    public static void editDuplicatedEmail(WebDriver driver){
        restartForm(driver);
        WebElement emailTxt = driver.findElement(By.id("email-input"));
        emailTxt.click();
        emailTxt.clear();
        emailTxt.sendKeys("user02@email.com");

        By btn = By.id("button-submit-edit");
        driver.findElement(btn).click();

        WebElement result = driver.findElement(By.id("error-email"));
        String checkText = PO_HomeView.getP().getString("Error.signup.email.duplicate",
                PO_Properties.SPANISH);
        Assertions.assertTrue(result.getText().contains(checkText));
    }
    /**
     * Mete mal email con un espacio al final
     * @param driver Driver en uso
     */
    public static void editBadSpaceEmail(WebDriver driver){
        restartForm(driver);
        WebElement emailTxt = driver.findElement(By.id("email-input"));
        emailTxt.click();
        emailTxt.clear();
        emailTxt.sendKeys("user01@email.com ");

        By btn = By.id("button-submit-edit");
        driver.findElement(btn).click();

        WebElement result = driver.findElement(By.id("error-email"));
        String checkText = PO_HomeView.getP().getString("Error.signup.email.whitespace",
                PO_Properties.SPANISH);

        Assertions.assertTrue(result.getText().contains(checkText));
    }
    /**
     * Mete mal email con estructura incorrecta
     * @param driver Driver en uso
     */
    public static void editBadEmail(WebDriver driver){
        restartForm(driver);
        WebElement emailTxt = driver.findElement(By.id("email-input"));
        emailTxt.click();
        emailTxt.clear();
        emailTxt.sendKeys("user01com");

        By btn = By.id("button-submit-edit");
        driver.findElement(btn).click();

        WebElement result = driver.findElement(By.id("error-email"));
        String checkText = PO_HomeView.getP().getString("Error.signup.email.form",
                PO_Properties.SPANISH);
        Assertions.assertTrue(result.getText().contains(checkText));
    }
    /**
     * Mete mal nombre con espacio al final
     * @param driver Driver en uso
     */
    public static void editBadSpaceName(WebDriver driver){
        restartForm(driver);
        WebElement emailTxt = driver.findElement(By.id("name-input"));
        emailTxt.click();
        emailTxt.clear();
        emailTxt.sendKeys("Luisito ");

        By btn = By.id("button-submit-edit");
        driver.findElement(btn).click();

        WebElement result = driver.findElement(By.id("error-name"));
        String checkText = PO_HomeView.getP().getString("Error.signup.name.whitespace",
                PO_Properties.SPANISH);
        Assertions.assertTrue(result.getText().contains(checkText));
    }
    /**
     * Mete mal nombre vacío
     * @param driver Driver en uso
     */
    public static void editEmptyName(WebDriver driver){
        restartForm(driver);
        WebElement emailTxt = driver.findElement(By.id("name-input"));
        emailTxt.click();
        emailTxt.clear();
        emailTxt.sendKeys("");

        By btn = By.id("button-submit-edit");
        driver.findElement(btn).click();

        WebElement result = driver.findElement(By.id("error-name"));
        String checkText = PO_HomeView.getP().getString("Error.empty",
                PO_Properties.SPANISH);
        Assertions.assertTrue(result.getText().contains(checkText));
    }
    /**
     * Mete mal apellido con espacio al final
     * @param driver Driver en uso
     */
    public static void editBadSpaceSurname(WebDriver driver){
        restartForm(driver);
        WebElement emailTxt = driver.findElement(By.id("surname"));
        emailTxt.click();
        emailTxt.clear();
        emailTxt.sendKeys("Pérez ");

        By btn = By.id("button-submit-edit");
        driver.findElement(btn).click();

        WebElement result = driver.findElement(By.id("error-surname"));
        String checkText = PO_HomeView.getP().getString("Error.signup.surname.whitespace",
                PO_Properties.SPANISH);
        Assertions.assertTrue(result.getText().contains(checkText));
    }
    /**
     * Mete mal apellido vacío
     * @param driver Driver en uso
     */
    public static void editEmptySurname(WebDriver driver){
        restartForm(driver);
        WebElement emailTxt = driver.findElement(By.id("surname"));
        emailTxt.click();
        emailTxt.clear();
        emailTxt.sendKeys("");

        By btn = By.id("button-submit-edit");
        driver.findElement(btn).click();

        WebElement result = driver.findElement(By.id("error-surname"));
        String checkText = PO_HomeView.getP().getString("Error.empty",
                PO_Properties.SPANISH);
        Assertions.assertTrue(result.getText().contains(checkText));
    }
}
