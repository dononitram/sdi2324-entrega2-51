package com.uniovi.sdi2223entrega2test.n.pageobjects;

import com.uniovi.sdi2223entrega2test.n.util.SeleniumUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class PO_FriendsView extends PO_NavView{
    public static List<WebElement> checkAcceptButton(WebDriver driver, int language) {
        return PO_View.checkElementBy(driver, "text", p.getString("request.accept", language));
    }

    /**
     * Conduce a los detalles del usuario con el email si sois amigos desde el home
     * @param driver Driver en uso
     * @param email Email de quien quiere verse sus detalles
     */
    public static void goToDetailsOf(WebDriver driver, String email) {
        String builtId = "detailsLink"+email;
        SeleniumUtils.waitLoadElementsBy(driver,"id","friendshipDropdown", PO_View.getTimeout()).get(0).click();
        SeleniumUtils.waitLoadElementsBy(driver,"id","friendshipList", PO_View.getTimeout()).get(0).click();
        SeleniumUtils.waitLoadElementsBy(driver, "id", builtId, PO_View.getTimeout()).get(0).click();
    }


    /**
     * Método que comprueba si al recomendar se incrementa el número de recomendaciones y que el botón se deshabilita
     * @param driver Driver en uso
     * @param publicationId Id de la publi que vamos a recomendar
     */
    public static void checkRecomendationsClick(WebDriver driver, String publicationId) {
        String templateForRecomendationCount = "publicationNumber"+publicationId;
        //Guardo el num de recomendaciones iniciales
        WebElement recomIniciales = SeleniumUtils.waitLoadElementsBy(driver, "free", "/html/body/div[1]/div[4]/div/div[1]/div/div/p[2]", PO_View.getTimeout()).get(0);
        int numInicial = Integer.valueOf(recomIniciales.getText());
        int numEsperado = numInicial++;
        //Vuelvo a mirar el num de recomendaciones y compruebo que es 1 + lo anterior
        SeleniumUtils.textIsPresentOnPage(driver, String.valueOf(numEsperado));
    }
}
