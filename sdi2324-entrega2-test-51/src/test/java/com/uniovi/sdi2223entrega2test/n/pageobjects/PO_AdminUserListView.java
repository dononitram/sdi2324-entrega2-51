package com.uniovi.sdi2223entrega2test.n.pageobjects;

import com.uniovi.application.util.SeleniumUtils;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class PO_AdminUserListView extends PO_NavView{
    /**
     * Nos lleva a la ventana de edición del usuario con el email pasado
     * @param driver Driver que estamos usando
     * @param email Correo del usuario que queremos modificar
     */
    public static void editUserByEmail(WebDriver driver, String email){
        String ident = "modify"+email;//Id del enlace a editar
        //Vamos al modify del user 1
        List<WebElement> elements = PO_View.checkElementBy(driver, "id", ident);
        elements.get(0).click();
    }

    /**
     * Método encargado de comprobar si hay el número de usuarios recibidos por parámetro en una
     * tabla Page con vistas de 5 elementos por página
      * @param driver Driver que estamos utilizando
     * @param numberUsers Número de usuarios que hay teóricamente registrados en la base de datos
     */
    public static void correctNumberOfUsers(WebDriver driver, int numberUsers){
        int pages = numberUsers/5;//Num de paginas que habrá (siendo 5 tamaño de página)
        int resto = numberUsers%5;
        int numElements = 0;//Contador de los elementos encontrados
        //Itero tantas veces como páginas
        for(int i = 0; i < pages; i++) {
            List<WebElement> usersList = SeleniumUtils.waitLoadElementsBy(driver, "free", "//tbody/tr",
                    PO_View.getTimeout());
            numElements+=usersList.size();
            //Paso a la siguiente página
            PO_Pagination.clickNextPage(driver);
        }
        if(resto>0) {//Si quedan elementos en la ult página contarlos
            List<WebElement> usersList = SeleniumUtils.waitLoadElementsBy(driver, "free", "//tbody/tr",
                    PO_View.getTimeout());
            numElements += usersList.size();
        }
        Assertions.assertEquals(numberUsers, numElements);
    }

}
