package com.uniovi.sdi2223entrega2test.n.pageobjects;

import com.uniovi.application.util.SeleniumUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class PO_PublicationView extends PO_NavView{

    public static void goToAddPublication (WebDriver driver){
        PO_PrivateView.click(driver, "id", "publicationDropdown", 0);
        PO_PrivateView.click(driver, "id", "addPublication", 0);
    }

    public static void goToListPublicationAsAdmin (WebDriver driver){
        PO_PrivateView.click(driver, "id", "publicationDropdown", 0);
        PO_PrivateView.click(driver, "id", "publicationList", 0);
    }

    public static void goToListPublication (WebDriver driver){
        PO_PrivateView.click(driver, "id", "publicationDropdown", 0);
        PO_PrivateView.click(driver, "id", "misPublicaciones", 0);
    }

    public static void fillForm(WebDriver driver, String name, String description) {
        //Esperamos a que se cargue el formulario de alta de publicación
        SeleniumUtils.waitLoadElementsBy(driver, "class", "form-horizontal",PO_Properties.SPANISH);
        //Rellenamos el campo de título
        WebElement title = driver.findElement(By.name("title"));
        title.click();
        title.clear();
        title.sendKeys(name);
        //Rellenamos el campo de texto
        WebElement text = driver.findElement(By.name("description"));
        text.click();
        text.clear();
        text.sendKeys(description);
        //Pulsamos el botón de Alta.
        By boton = By.className("btn");
        driver.findElement(boton).click();
    }

    public static void checkPublications(WebDriver driver, List<String> titles ){

        for(int i=0 ; i< titles.size(); i++){
            //Pasar de página si ya se comprobaron las primeras 5 publicaciones
            if(i == 5)
                PO_Pagination.clickNextPage(driver);
            //Comprobar que la publicación está en la lista
            SeleniumUtils.textIsPresentOnPage(driver, titles.get(i));
        }
    }

}
