package com.uniovi.sdi2223entrega2test.n;

import com.uniovi.application.services.InsertSampleDataService;
import com.uniovi.application.util.SeleniumUtils;
import org.junit.jupiter.api.*;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.uniovi.application.pageobjects.*;
import org.springframework.context.ApplicationContext;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

/*
    Avisos:
    Abrir pruebas con la ventana grande para poder cargar los botones del nav correctamente
 */
@SpringBootTest
class ApplicationTests {

    private static final int LANGUAGE = PO_Properties.SPANISH;

    static String PathFirefox = "C:\\Program Files\\Mozilla Firefox\\firefox.exe";
    static String Geckodriver = "C:\\Dev\\tools\\selenium\\geckodriver-v0.30.0-win64.exe";

    @Autowired
    private ApplicationContext applicationContext;

    static WebDriver driver = getDriver(PathFirefox, Geckodriver);
    InsertSampleDataService sampleDataService;

    public static WebDriver getDriver(String PathFirefox, String Geckodriver) {
        System.setProperty("webdriver.firefox.bin", PathFirefox);
        System.setProperty("webdriver.gecko.driver", Geckodriver);
        driver = new FirefoxDriver();
        return driver;
    }

    //Antes de cada prueba se trata de acceder a home
    @BeforeEach
    public void setUp(){
        driver.navigate().to("http://localhost:8090/home");
    }

    //Después de cada prueba se borran las cookies del navegador
    @AfterEach
    public void tearDown(){
        driver.manage().deleteAllCookies();
        //Reiniciamos BBDD
        InsertSampleDataService insertSampleDataService = applicationContext.getBean(InsertSampleDataService.class);
        insertSampleDataService.init();
    }

    //Antes de la primera prueba
    @BeforeAll
    static public void begin() {

    }

    //Al finalizar la última prueba
    @AfterAll
    static public void end() {
        //Cerramos el navegador al finalizar las pruebas
        driver.quit();
    }

    /**
     * @author Samuel
     * [Prueba1] Registro de Usuario con datos válidos
     */
    @Test
    @Order(1)
    void PR1() {
        //Hacemos click es registrarnos
        PO_PrivateView.click(driver, "id", "registrarse", 0);
        PO_PublicView.signupSuccess(driver);
        SeleniumUtils.textIsPresentOnPage(driver,"Iniciar sesion");
    }

    /**
     * @author Samuel
     * [Prueba1] Registro de Usuario con datos inválidos (email vacío, nombre vacío, apellidos vacíos y
     * contraseña incorrecta (débil)).
     */
    @Test
    @Order(2)
    void PR2() {
        //Hacemos click es registrarnos
        PO_PrivateView.click(driver, "id", "registrarse", 0);
        PO_PublicView.signupAllError(driver);
        SeleniumUtils.textIsPresentOnPage(driver,"Este campo es obligatorio.");
        SeleniumUtils.textIsPresentOnPage(driver,"La contrasena debe ser fuerte, con una longitud minima de 12 " +
                "caracteres y debe incluir una combinacion de letras mayusculas y minusculas, numeros y caracteres especiales.");
    }

    /**
     * @author Samuel
     * [Prueba1] Registro de Usuario con datos inválidos (email vacío, nombre vacío, apellidos vacíos y
     * contraseña incorrecta (débil)).
     */
    @Test
    @Order(3)
    void PR3() {
        //Hacemos click es registrarnos
        PO_PrivateView.click(driver, "id", "registrarse", 0);
        PO_PublicView.signupErrorPasswords(driver);
        SeleniumUtils.textIsPresentOnPage(driver,"Estas contrasenas no coinciden.");
    }

    /**
     * @author Samuel
     * [Prueba1] Registro de Usuario con datos inválidos (email vacío, nombre vacío, apellidos vacíos y
     * contraseña incorrecta (débil)).
     */
    @Test
    @Order(4)
    void PR4() {
        //Hacemos click es registrarnos
        PO_PrivateView.click(driver, "id", "registrarse", 0);
        PO_PublicView.repeatedEmail(driver);
        SeleniumUtils.textIsPresentOnPage(driver,"Ya existe un usuario con este correo electronico.");
    }

    /**
     * @author Donato
     * [Prueba5] Inicio de sesión con datos válidos
     * (administrador).
     */
    @Test
    @Order(5)
    void PR5() {
        PO_PublicView.loginAdmin(driver);
        // comprobamos que estamos en el path /user/list
        Assertions.assertEquals("http://localhost:8090/user/list", driver.getCurrentUrl());

    }

    /**
     * @author Donato
     * [Prueba6] Inicio de sesión con datos válidos
     * (usuario estándar).
     */
    @Test
    @Order(6)
    void PR6() {
        PO_PublicView.loginUser(driver);
        // comprobamos que estamos en el path /user/list/users
        Assertions.assertEquals("http://localhost:8090/user/list/users", driver.getCurrentUrl());

    }

    /**
     * @author Donato
     * [Prueba7] Inicio de sesión con datos inválidos
     * (usuario estándar, campo email y contraseña vacíos).
     */
    @Test
    @Order(7)
    void PR7() {
        PO_PublicView.loginFailEmpty(driver);
        PO_PublicView.checkLoginIsVisible(driver, LANGUAGE);
    }

    /**
     * @author Donato
     * [Prueba8] Inicio de sesión con datos válidos
     * (usuario estándar, email existente, pero contraseña  incorrecta).
     */
    @Test
    @Order(8)
    void PR8() {
        PO_PublicView.loginFailIncorrect(driver);
        PO_PublicView.checkErrorMessageIsVisible(driver, LANGUAGE);
    }

    /**
     * @author Donato
     * [Prueba9] Hacer clic en la opción de salir de sesión y comprobar que se muestra el mensaje “Ha cerrado
     * sesión correctamente” y se redirige a la página de inicio de sesión (Login).
     */
    @Test
    @Order(9)
    void PR9() {
        //Inicio sesión como admin
        PO_PublicView.loginAdmin(driver);
        //Cerramos sesión
        PO_PrivateView.logout(driver);
        //Comprobamos que se muestra el mensaje “Ha cerrado sesión correctamente” y se redirige a la página de inicio de sesión (Login)
        PO_PublicView.checkLogoutMessageIsVisible(driver, LANGUAGE);
    }

    /**
     * @author Donato
     * [Prueba10] Comprobar que el botón cerrar sesión no está visible si el usuario no está autenticado.
     *
     * (Este test es flaky, ya que podría dar un falso positivo si el resultado es que simplemente no carga correctamente)
     */
    @Test
    @Order(10)
    void PR10() {
        //Comprobamos que el botón cerrar sesión no está visible si el usuario no está autenticado
        PO_PublicView.checkLogoutNotVisible(driver, LANGUAGE);
    }

    /**
     * @author Pedro
     * [Prueba11] Mostrar el listado de usuarios y comprobar que se muestran todos los que existen en el sistema,
     * incluyendo el usuario actual y los usuarios administradores.
     */
    @Test
    @Order(11)
    void PR11() {
        //Inicio sesión como admin
        PO_PublicView.loginAdmin(driver);
        //PO_HomeView.checkWelcomeToPage(driver, PO_Properties.SPANISH);
        //Vamos a la lista de usuarios
        PO_PrivateView.click(driver, "id", "userDropdown", 0);
        PO_PrivateView.click(driver, "id", "userList", 0);
        //Compruebo usuarios 16 users y 1 admin (propio usuario)
        PO_AdminUserListView.correctNumberOfUsers(driver,17);
        // Cerrar sesión
        PO_PrivateView.logout(driver);
    }

    /**
     * @author Pedro
     * [Prueba12] Autenticarse como administrador, editar un usuario estándar, cambiando su rol a
     * administrador, email, nombre y apellidos, comprobar que los datos se han actualizados correctamente.
     * Salir de sesión como administrador y autenticarse como el usuario modificado y acceder a la funcionalidad
     * de listado de usuarios del sistema para probar el nuevo rol de administrador.
     */
    @Test
    @Order(12)
    void PR12() {
        //Inicio sesión como administrador
        PO_PublicView.loginAdmin(driver);
        //PO_HomeView.checkWelcomeToPage(driver, PO_Properties.SPANISH);
        //Vamos a la lista de usuarios donde se puede editar
        PO_PrivateView.click(driver, "id", "userDropdown", 0);
        PO_PrivateView.click(driver, "id", "userList", 0);
        //Voy a la ventana de edición del usuario 1
        PO_AdminUserListView.editUserByEmail(driver,"user01@email.com");
        //Editamos el usuario
        PO_EditUserView.editUser(driver,"Paco@email.es","Paco","Pérez","ROLE_ADMIN");
        // Cerrar sesión
        PO_PrivateView.logout(driver);
        //Entramos como ese usuario
        PO_PublicView.loginSpecificUser("Paco@email.es", "Us3r@1-PASSW", driver);
        //Vamos a list de solo admins para ver si es admin ahora
        PO_PrivateView.click(driver, "id", "userDropdown", 0);
        PO_PrivateView.click(driver, "id", "userList", 0);
        // Una vez comprobado que es admin vamos a cerrar sesión
        PO_PrivateView.logout(driver);
    }
    /**
     * @author Pedro
     * [Prueba13] Editar un usuario introduciendo datos inválidos (email existente asignado a otro usuario del
     * sistema, nombre y apellidos vacíos), comprobar que se devuelven los mensajes de error correctamente y
     * que el usuario no se actualiza.
     */
    @Test
    @Order(13)
    void PR13() {
        //Inicio sesión como administrador
        PO_PublicView.loginAdmin(driver);
        //PO_HomeView.checkWelcomeToPage(driver, PO_Properties.SPANISH);
        //Vamos a la lista de usuarios donde se puede editar
        PO_PrivateView.click(driver, "id", "userDropdown", 0);
        PO_PrivateView.click(driver, "id", "userList", 0);
        //Voy a la ventana de edición del usuario 1
        PO_AdminUserListView.editUserByEmail(driver,"user01@email.com");
        //Editamos el usuario de todas las formas posibles erroneas
        PO_EditUserView.editAllWithError(driver);
        // Cerrar sesión
        PO_PrivateView.logout(driver);
    }

    /**
     * @author Samuel
     * [Prueba14] Ir a la lista de usuarios, borrar el primer usuario de la lista, comprobar que la lista se actualiza
     * y dicho usuario desaparece.
     */
    @Test
    @Order(14)
    void PR14() {
        //Iniciamos sesión como administrador y comprobar inicio correcto
        PO_PublicView.loginAdmin(driver);
        PO_View.checkElementBy(driver, "text", "admin@email.com");

        // Acceder al listado de invitaciones de amistad
        PO_PrivateView.click(driver, "id", "userDropdown", 0);
        PO_PrivateView.click(driver, "id", "userList", 0);

        // Seleccionamos al primer usuario (user01@email.com)
        PO_PrivateView.click(driver, "id", "checkbox_user01@email.com", 0);

        // Eliminamos el usuario
        PO_PrivateView.click(driver, "id", "deleteButton", 0);
        WebDriverWait wait = new WebDriverWait(driver, 10);
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        alert.accept();

        // Comprobamos que no aparezca el usuario
        SeleniumUtils.waitTextIsNotPresentOnPage(driver, "user01@email.com",PO_View.getTimeout());

        // Cerrar sesión
        PO_PrivateView.logout(driver);
    }

    /**
     * @author Samuel
     * [Prueba15] Ir a la lista de usuarios, borrar el último usuario de la lista, comprobar que la lista se actualiza
     * y dicho usuario desaparece.
     */
    @Test
    @Order(15)
    void PR15() {
        //Iniciamos sesión como administrador y comprobar inicio correcto
        PO_PublicView.loginAdmin(driver);
        PO_View.checkElementBy(driver, "text", "admin@email.com");


        // Acceder al listado de invitaciones de amistad
        PO_PrivateView.click(driver, "id", "userDropdown", 0);
        PO_PrivateView.click(driver, "id", "userList", 0);

        //Vamos a la última página
        PO_PrivateView.click(driver, "id", "last_page", 0);

        // Seleccionamos al último usuario (d@uniovi.es)
        PO_PrivateView.click(driver, "id", "checkbox_user16@email.com", 0);

        // Eliminamos el usuario
        PO_PrivateView.click(driver, "id", "deleteButton", 0);
        WebDriverWait wait = new WebDriverWait(driver, 10);
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        alert.accept();

        //Vamos a la última página
        PO_PrivateView.click(driver, "id", "last_page", 0);

        // Comprobamos que no aparezca el usuario
        SeleniumUtils.waitTextIsNotPresentOnPage(driver, "user16@email.com",PO_View.getTimeout());

        // Cerrar sesión
        PO_PrivateView.logout(driver);
    }

    /**
     * @author Samuel
     * [Prueba16] Ir a la lista de usuarios, borrar 3 usuarios, comprobar que la lista se actualiza y dichos usuarios
     * desaparecen.
     */
    @Test
    @Order(16)
    void PR16() {
        //Iniciamos sesión como administrador y comprobar inicio correcto
        PO_PublicView.loginAdmin(driver);
        PO_View.checkElementBy(driver, "text", "admin@email.com");


        // Acceder al listado de invitaciones de amistad
        PO_PrivateView.click(driver, "id", "userDropdown", 0);
        PO_PrivateView.click(driver, "id", "userList", 0);

        // Seleccionamos 3 usuarios
        PO_PrivateView.click(driver, "id", "checkbox_user04@email.com", 0);
        PO_PrivateView.click(driver, "id", "checkbox_user02@email.com", 0);
        PO_PrivateView.click(driver, "id", "checkbox_user03@email.com",0);

        // Eliminamos los usuarios
        PO_PrivateView.click(driver, "id", "deleteButton", 0);
        WebDriverWait wait = new WebDriverWait(driver, 10);
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        alert.accept();

        // Comprobamos que no aprezcan los usuarios
        SeleniumUtils.waitTextIsNotPresentOnPage(driver, "user04@email.com",PO_View.getTimeout());
        SeleniumUtils.waitTextIsNotPresentOnPage(driver, "user02@email.com",PO_View.getTimeout());
        SeleniumUtils.waitTextIsNotPresentOnPage(driver, "user03@email.com",PO_View.getTimeout());

        // Cerrar sesión
        PO_PrivateView.logout(driver);
    }

    /**
     * @author David
     * [Prueba17] Mostrar el listado de usuarios y comprobar que se muestran todos los que existen en el sistema,
     * excepto el propio usuario y aquellos que sean administradores.
     */
    @Test
    @Order(17)
    void PR17() {
        //Login como usuario
        PO_PublicView.loginSpecificUser("user01@email.com","Us3r@1-PASSW",driver);
        //Vamos a la lista de usuarios
        PO_PrivateView.click(driver, "id", "seeUsersNetwork", 0);

        //Por cada pagina
        for(int i=0; i<3 ; i++){
            //Comprobamos que no aparezcan el usuario autenticado ni el admin
            SeleniumUtils.textIsNotPresentOnPage(driver, "User1 ");
            SeleniumUtils.textIsNotPresentOnPage(driver, "admin");
            if(i < 2)
                PO_Pagination.clickNextPage(driver);
        }

        // Cerrar sesión
        PO_PrivateView.logout(driver);
    }

    /**
     * @author Donato
     * [Prueba18] Hacer una búsqueda con el campo vacío y comprobar que se muestra la página que
     * corresponde con el listado usuarios existentes en el sistema.
     */
    @Test
    @Order(18)
    void PR18() {

        int timeout = 4;

        PO_PublicView.loginAdmin(driver);

        // Comprobamos los usuarios de la primera página sin filtro
        List<WebElement> users = driver.findElements(By.xpath("//table//tbody//tr"));
        List<String> usernames = users.stream().map(WebElement::getText).toList();

        SeleniumUtils.waitLoadElementsByXpath(driver, "/html/body/div/form/button", timeout);

        // Buscamos
        driver.findElement(By.xpath("/html/body/div/form/button")).click();

        // Comprobamos que no se ha filtrado nada
        List<WebElement> usersFiltered = driver.findElements(By.xpath("//table//tbody//tr"));
        List<String> usernamesFiltered = usersFiltered.stream().map(WebElement::getText).toList();

        Assertions.assertEquals(usernames, usernamesFiltered);

    }

    /**
     * @author Donato
     * [Prueba19] Hacer una búsqueda escribiendo en el campo un texto que no exista y comprobar que se
     * muestra la página que corresponde, con la lista de usuarios vacía.
     */
    @Test
    @Order(19)
    void PR19() {

        PO_PublicView.loginAdmin(driver);

        // Buscamos un usuario que no existe
        driver.findElement(By.xpath("/html/body/div/form/div/input")).sendKeys("usuarioNoExistente");
        driver.findElement(By.xpath("/html/body/div/form/button")).click();

        // Comprobamos que no hay elementos
        List<WebElement> usersFiltered = driver.findElements(By.xpath("//table//tbody//tr"));
        Assertions.assertEquals(0, usersFiltered.size());


    }

    /**
     * @author Donato
     * [Prueba20] Hacer una búsqueda con un texto específico y comprobar que se muestra la página que
     * corresponde, con la lista de usuarios en los que el texto especificado sea parte de su nombre, apellidos o
     * de su email.
     */
    @Test
    @Order(20)
    void PR20() {

        PO_PublicView.loginAdmin(driver);

        // Buscamos un usuario que existe

        driver.findElement(By.xpath("/html/body/div/form/div/input")).sendKeys("user01");
        driver.findElement(By.xpath("/html/body/div/form/button")).click();

        // Comprobamos que hay elementos
        List<WebElement> usersFiltered = driver.findElements(By.xpath("//table//tbody//tr"));
        Assertions.assertEquals(1, usersFiltered.size());

        // Comprobamos que el usuario es el esperado
        Assertions.assertTrue(usersFiltered.get(0).getText().contains("user01"));

    }

    /**
     * @author Pedro
     * [Prueba21] Desde el listado de usuarios de la aplicación, enviar una invitación de amistad a un usuario.
     * Comprobar que la solicitud de amistad aparece en el listado de invitaciones (punto siguiente).
     */
    @Test
    @Order(21)
    void PR21() {
        //Inicio sesión como el usuario2
        PO_PublicView.loginSpecificUser("user02@email.com","Us3r@2-PASSW",driver);
        //Vamos a la lista de usuarios donde se puede solicitar amistad
        PO_PrivateView.click(driver, "id", "seeUsersNetwork", 0);
        //Envío solicitud al usuario 3
        WebElement botonEnviarSolicitud = driver.findElement(By.id("sendButtonuser03@email.com"));
        botonEnviarSolicitud.click();
        // Cerrar sesión e inicio con el usuario 3
        PO_PrivateView.logout(driver);
        PO_PublicView.loginSpecificUser("user03@email.com","Us3r@3-PASSW",driver);
        //Voy a la ventana de ver solicitudes de amistad recibidas friendshipRequests
        driver.findElement(By.id("friendshipDropdown")).click();
        driver.findElement(By.id("friendshipRequests")).click();
        //Y comprobamos que llegó la solicitud de amistad  email_user02@email.com
        SeleniumUtils.waitLoadElementsBy(driver,"id", "email_user02@email.com", PO_View.getTimeout());
        //Finalmente logeamos-cerramos sesión
        PO_PrivateView.logout(driver);
    }

    /**
     * @author Pedro
     * [Prueba22] Desde el listado de usuarios de la aplicación, enviar una invitación de amistad a un usuario al
     * que ya le habíamos enviado la invitación previamente. No debería dejarnos enviar la invitación. Se podría
     * ocultar el botón de enviar invitación o notificar que ya había sido enviada previamente.
     */
    @Test
    @Order(22)
    void PR22() {
        //Inicio sesión como usuario 1
        PO_PublicView.loginSpecificUser("user01@email.com","Us3r@1-PASSW",driver);
        //Vamos a la lista de usuarios donde se puede solicitar amistad
        PO_PrivateView.click(driver, "id", "seeUsersNetwork", 0);
        //Y compruebo que el user5 por ejemplo pone ya enviado en el botón
        SeleniumUtils.textIsPresentOnPage(driver, "Ya enviada");
        // Cerrar sesión
        PO_PrivateView.logout(driver);
    }

    /**
     * @author Teresa
     * [Prueba23] Mostrar el listado de invitaciones de amistad recibidas. Comprobar con un listado que
     * contenga varias invitaciones recibidas.
     */
    @Test
    @Order(23)
    void PR23() {
        // Inicio de sesión como usuario
        PO_PublicView.loginUser(driver);
        PO_View.checkElementBy(driver, "text", "user01@email.com");
        //PO_HomeView.checkWelcomeToPage(driver, PO_Properties.SPANISH);

        // Acceder al listado de invitaciones de amistad
        PO_PrivateView.click(driver, "id", "friendshipDropdown", 0);
        PO_PrivateView.click(driver, "id", "friendshipRequests", 0);

        // Comprobar que aparecen las invitaciones de amistad recibidas
        String expected = "user10@email.com";
        List<WebElement> elements = PO_View.checkElementBy(driver, "id", "email_"+expected);
        Assertions.assertEquals(1, elements.size());
        Assertions.assertEquals(expected, elements.get(0).getText());

        expected = "user05@email.com";
        elements = PO_View.checkElementBy(driver, "id", "email_"+expected);
        Assertions.assertEquals(1, elements.size());
        Assertions.assertEquals(expected, elements.get(0).getText());

        // Cerrar sesión
        PO_PrivateView.logout(driver);
    }

    /**
     * @author Teresa
     * [Prueba24] Sobre el listado de invitaciones recibidas. Hacer clic en el botón/enlace de una de ellas y
     * comprobar que dicha solicitud desaparece del listado de invitaciones.
     */
    @Test
    @Order(24)
    void PR24() {
        // Inicio de sesión como usuario
        PO_PublicView.loginUser(driver);
        PO_View.checkElementBy(driver, "text", "user01@email.com");

        // Acceder al listado de invitaciones de amistad
        PO_PrivateView.click(driver, "id", "friendshipDropdown", 0);
        PO_PrivateView.click(driver, "id", "friendshipRequests", 0);

        // Buscar la invitación de amistad que se quiere aceptar
        String expected = "email_user10@email.com";
        List<WebElement> elements = PO_View.checkElementBy(driver, "id", expected);
        Assertions.assertEquals(1, elements.size());
        Assertions.assertEquals("user10@email.com", elements.get(0).getText());

        // Clicar en el botón "Aceptar" de la primera invitación de amistad
        elements = PO_FriendsView.checkAcceptButton(driver, LANGUAGE);
        elements.get(0).click();
        //PO_PrivateView.click(driver, "id", "acceptButton_user10@email.com", 0);

        // Comprobar que la invitación ya no aparece en la lista
        SeleniumUtils.waitTextIsNotPresentOnPage(driver, expected,PO_View.getTimeout());

        // Cerrar sesión
        PO_PrivateView.logout(driver);
    }

    /**
     * @author Teresa
     * [Prueba25] Mostrar el listado de amigos de un usuario. Comprobar que el listado contiene los amigos que
     * deben ser.
     */
    @Test
    @Order(25)
    void PR25() {
        // Inicio de sesión como usuario
        PO_PublicView.loginUser(driver);
        PO_View.checkElementBy(driver, "text", "user01@email.com");

        // Acceder al listado de amistades
        PO_PrivateView.click(driver, "id", "friendshipDropdown", 0);
        PO_PrivateView.click(driver, "id", "friendshipList", 0);

        // Comprobar que aparecen las amistades correspondientes
        String expected = "user04@email.com";
        List<WebElement> elements = PO_View.checkElementBy(driver, "id", "email_"+expected);
        Assertions.assertEquals(1, elements.size());
        Assertions.assertEquals(expected, elements.get(0).getText());

        expected = "user03@email.com";
        elements = PO_View.checkElementBy(driver, "id", "email_"+expected);
        Assertions.assertEquals(1, elements.size());
        Assertions.assertEquals(expected, elements.get(0).getText());

        expected = "user02@email.com";
        elements = PO_View.checkElementBy(driver, "id", "email_"+expected);
        Assertions.assertEquals(1, elements.size());
        Assertions.assertEquals(expected, elements.get(0).getText());

        // Cerrar sesión
        PO_PrivateView.logout(driver);
    }

    /**
     * @author Teresa
     * [Prueba26] Mostrar el listado de amigos de un usuario. Comprobar que se incluye la información
     * relacionada con la última publicación de cada usuario y la fecha de inicio de amistad
     */
    @Test
    @Order(26)
    void PR26() {
        // Inicio de sesión como usuario
        PO_PublicView.loginUser(driver);
        PO_View.checkElementBy(driver, "text", "user01@email.com");

        // Acceder al listado de amistades
        PO_PrivateView.click(driver, "id", "friendshipDropdown", 0);
        PO_PrivateView.click(driver, "id", "friendshipList", 0);

        // Comprobar que aparecen las amistades correspondientes
        String expected = "user04@email.com";
        List<WebElement> elements = PO_View.checkElementBy(driver, "id", "email_"+expected);
        Assertions.assertEquals(1, elements.size());
        Assertions.assertEquals(expected, elements.get(0).getText());

        // Comprobar que se incluye la información sobre la fecha de inicio de amistad
        expected = "date_user04@email.com";
        elements = PO_View.checkElementBy(driver, "id", expected);
        Assertions.assertEquals(1, elements.size());
        Assertions.assertEquals(LocalDate.now().minusDays(2).toString(), elements.get(0).getText());

        // Comprobar que se incluye la información sobre la última publicación del amigo
        expected = "pub_user04@email.com";
        elements = PO_View.checkElementBy(driver, "id", expected);
        Assertions.assertEquals(1, elements.size());
        Assertions.assertEquals("Publication 49", elements.get(0).getText());

        // Cerrar sesión
        PO_PrivateView.logout(driver);
    }

    /**
     * @author David
     * [Prueba27] Ir al formulario crear publicaciones, rellenarla con datos válidos y pulsar el botón Submit.
     * Comprobar que la publicación sale en el listado de publicaciones de dicho usuario
     */
    @Test
    @Order(27)
    void PR27() {

        //Login como usuario
        PO_PublicView.loginSpecificUser("user01@email.com","Us3r@1-PASSW",driver);

        //Creamos publicación
        PO_PublicationView.goToAddPublication(driver);
        PO_PublicationView.fillForm(driver,"Publicación de prueba","Descripción de prueba");

        //Comprobamos que se añadió correctamente
        PO_PublicationView.goToListPublication(driver);
        PO_Pagination.clickLastPage(driver);
        SeleniumUtils.textIsPresentOnPage(driver,"Publicación de prueba");
    }

    /**
     * @author David
     * [Prueba28] Ir al formulario de crear publicaciones, rellenarla con datos inválidos (campos título y
     * descripción vacíos) y pulsar el botón Submit. Comprobar que se muestran los mensajes de campo
     * obligatorios.
     */
    @Test
    @Order(28)
    void PR28() {

        //Login como usuario
        PO_PublicView.loginSpecificUser("user01@email.com","Us3r@1-PASSW",driver);

        //Creamos publicación con datos inválidos
        PO_PublicationView.goToAddPublication(driver);
        PO_PublicationView.fillForm(driver,"a","a");

        //Comprobamos que se se muestran los mensajes de error
        SeleniumUtils.textIsPresentOnPage(driver,"El titulo debe tener mas de 3 caracteres");
        SeleniumUtils.textIsPresentOnPage(driver,"La descripcion debe tener mas de 10 caracteres");

        // Cerrar sesión
        PO_PrivateView.logout(driver);
    }

    /**
     * @author David
     * [Prueba29] Mostrar el listado de publicaciones de un usuario y comprobar que se muestran todas las que
     * existen para dicho usuario
     */
    @Test
    @Order(29)
    void PR29() {

        //Login como usuario
        PO_PublicView.loginSpecificUser("user01@email.com","Us3r@1-PASSW",driver);

        //Lista con las publicaciones no censuradas del usuario
        String[] titles = {"Publication 12", "Publication 13", "Publication 14", "Publication 15", "Publication 16", "Publication 17", "Publication 18", "Publication 19"};
        List<String> titlesList = List.of(titles);

        //Vamos a la lista de publicaciones
        PO_PublicationView.goToListPublication(driver);

        //Comprobamos que se muestran todas las publicaciones
        PO_PublicationView.checkPublications(driver,titlesList);

        // Cerrar sesión
        PO_PrivateView.logout(driver);
    }

    /**
     * @author Teresa
     * [Prueba30] Mostrar el perfil del usuario y comprobar que se muestran sus datos y el listado de sus
     * publicaciones.
     */
    @Test
    @Order(30)
    void PR30() {
        // Inicio de sesión como usuario
        PO_PublicView.loginUser(driver);
        PO_View.checkElementBy(driver, "text", "user01@email.com");

        // Acceder al listado de amistades
        PO_PrivateView.click(driver, "id", "friendshipDropdown", 0);
        PO_PrivateView.click(driver, "id", "friendshipList", 0);

        // Acceder a los detalles del amigo
        PO_PrivateView.click(driver, "id", "name_user04@email.com", 0);

        // Comprobar que aparece las amistad que se espera
        SeleniumUtils.textIsPresentOnPage(driver, "user04@email.com");
        SeleniumUtils.textIsPresentOnPage(driver, "User4");
        SeleniumUtils.textIsPresentOnPage(driver, "Surname4");
        List<WebElement> elements = PO_View.checkElementBy(driver, "id", "publicationsSection");
        Assertions.assertFalse(elements.isEmpty());

        // Cerrar sesión
        PO_PrivateView.logout(driver);
    }

    /**
     * @author Teresa
     * [Prueba31] Utilizando un acceso vía URL u otra alternativa, tratar de acceder al perfil de un usuario que
     * no sea amigo del usuario identificado en sesión. Comprobar que el sistema da un error de autorización.
     */
    @Test
    @Order(31)
    void PR31() {
        // Inicio de sesión como usuario
        PO_PublicView.loginUser(driver);
        PO_View.checkElementBy(driver, "text", "user01@email.com");

        // Navegar a la dirección de un usuario que no es amigo de user01@email.com
        driver.navigate().to("http://localhost:8090/friendship/details/user05@email.com");

        // Redirige a /home porque no tiene permisos
        //PO_HomeView.checkWelcomeToPage(driver, PO_Properties.SPANISH);
        PO_View.checkElementBy(driver, "text", "Bienvenido a la pagina principal");

        // Cerrar sesión
        PO_PrivateView.logout(driver);
    }

    /**
     * @author teresa
     * [Prueba32] Visualizar al menos tres páginas en español/inglés/español (comprobando que algunas de
     * las etiquetas cambian al idioma correspondiente). Ejemplo, Página principal/Opciones Principales de
     * Usuario/Listado de Usuarios.
     */
    @Test
    @Order(32)
    void PR32() {
        // Inicio de sesión como usuario
        PO_PublicView.loginUser(driver);
        PO_View.checkElementBy(driver, "text", "user01@email.com");
        PO_PrivateView.click(driver, "id", "home", 0);

        // Comprobar el mensaje en español
        PO_InternationalizationView.checkMessage(driver, PO_Properties.SPANISH, "welcome.message");

        // Cambiar a ingles
        PO_PrivateView.click(driver, "id", "btnLanguage", 0);
        PO_PrivateView.click(driver, "id", "btnEnglish", 0);
        // Comprobar el mensaje en ingles
        PO_InternationalizationView.checkMessage(driver, PO_Properties.ENGLISH, "welcome.message");

        // Pasar a español
        PO_PrivateView.click(driver, "id", "btnLanguage", 0);
        PO_PrivateView.click(driver, "id", "btnSpanish", 0);
        // Comprobar el mensaje en español
        PO_InternationalizationView.checkMessage(driver, PO_Properties.SPANISH, "welcome.message");

        // ************************************************************************************
        // Cambiar a la ventana de invitaciones de amistad
        PO_PrivateView.click(driver, "id", "friendshipDropdown", 0);
        PO_PrivateView.click(driver, "id", "friendshipRequests", 0);

        // Comprobar el mensaje en español
        PO_InternationalizationView.checkMessage(driver, PO_Properties.SPANISH, "friendship.requests.message");

        // Cambiar a ingles
        PO_PrivateView.click(driver, "id", "btnLanguage", 0);
        PO_PrivateView.click(driver, "id", "btnEnglish", 0);
        // Comprobar el mensaje en ingles
        PO_InternationalizationView.checkMessage(driver, PO_Properties.ENGLISH, "friendship.requests.message");

        // Pasar a español
        PO_PrivateView.click(driver, "id", "btnLanguage", 0);
        PO_PrivateView.click(driver, "id", "btnSpanish", 0);
        // Comprobar el mensaje en español
        PO_InternationalizationView.checkMessage(driver, PO_Properties.SPANISH, "friendship.requests.message");

        // ************************************************************************************
        // Cambiar a la ventana de listado de usuarios
        PO_PrivateView.click(driver, "id", "seeUsersNetwork", 0);

        // Comprobar el mensaje en español
        PO_InternationalizationView.checkMessage(driver, PO_Properties.SPANISH, "nav.users.list");

        // Cambiar a ingles
        PO_PrivateView.click(driver, "id", "btnLanguage", 0);
        PO_PrivateView.click(driver, "id", "btnEnglish", 0);
        // Comprobar el mensaje en ingles
        PO_InternationalizationView.checkMessage(driver, PO_Properties.ENGLISH, "nav.users.list");

        // Pasar a español
        PO_PrivateView.click(driver, "id", "btnLanguage", 0);
        PO_PrivateView.click(driver, "id", "btnSpanish", 0);
        // Comprobar el mensaje en español
        PO_InternationalizationView.checkMessage(driver, PO_Properties.SPANISH, "nav.users.list");

        // Cerrar sesión
        PO_PrivateView.logout(driver);

    }

    /**
     * @author teresa
     * [Prueba33] Visualizar al menos tres páginas en inglés/francés o idioma elegido/Inglés (comprobando
     * que algunas de las etiquetas cambian al idioma correspondiente). Ejemplo, Página principal/Opciones
     * Principales de Usuario/Listado de Usuarios
     */
    @Test
    @Order(33)
    void PR33() {
        // Inicio de sesión como usuario
        PO_PublicView.loginUser(driver);
        PO_View.checkElementBy(driver, "text", "user01@email.com");
        PO_PrivateView.click(driver, "id", "home", 0);

        // Cambiar a ingles
        PO_PrivateView.click(driver, "id", "btnLanguage", 0);
        PO_PrivateView.click(driver, "id", "btnEnglish", 0);

        // Comprobar el mensaje en ingles
        PO_InternationalizationView.checkMessage(driver, PO_Properties.ENGLISH, "welcome.message");

        // Cambiar a aleman
        PO_PrivateView.click(driver, "id", "btnLanguage", 0);
        PO_PrivateView.click(driver, "id", "btnGerman", 0);
        // Comprobar el mensaje en aleman
        PO_InternationalizationView.checkMessage(driver, PO_Properties.GERMAN, "welcome.message");

        // Pasar a ingles
        PO_PrivateView.click(driver, "id", "btnLanguage", 0);
        PO_PrivateView.click(driver, "id", "btnEnglish", 0);
        // Comprobar el mensaje en ingles
        PO_InternationalizationView.checkMessage(driver, PO_Properties.ENGLISH, "welcome.message");

        // ************************************************************************************
        // Cambiar a la ventana de invitaciones de amistad
        PO_PrivateView.click(driver, "id", "friendshipDropdown", 0);
        PO_PrivateView.click(driver, "id", "friendshipRequests", 0);

        // Comprobar el mensaje en ingles
        PO_InternationalizationView.checkMessage(driver, PO_Properties.ENGLISH, "friendship.requests.message");

        // Cambiar a aleman
        PO_PrivateView.click(driver, "id", "btnLanguage", 0);
        PO_PrivateView.click(driver, "id", "btnGerman", 0);
        // Comprobar el mensaje en aleman
        //PO_InternationalizationView.checkMessage(driver, PO_Properties.GERMAN, "friendship.requests.message");

        // Pasar a ingles
        PO_PrivateView.click(driver, "id", "btnLanguage", 0);
        PO_PrivateView.click(driver, "id", "btnEnglish", 0);
        // Comprobar el mensaje en ingles
        PO_InternationalizationView.checkMessage(driver, PO_Properties.ENGLISH, "friendship.requests.message");

        // ************************************************************************************
        // Cambiar a la ventana de listado de usuarios
        PO_PrivateView.click(driver, "id", "seeUsersNetwork", 0);

        // Comprobar el mensaje en ingles
        //PO_View.checkElementBy(driver, "text", "Friendship list");
        PO_InternationalizationView.checkMessage(driver, PO_Properties.ENGLISH, "nav.users.list");

        // Cambiar a aleman
        PO_PrivateView.click(driver, "id", "btnLanguage", 0);
        PO_PrivateView.click(driver, "id", "btnGerman", 0);
        // Comprobar el mensaje en aleman
        PO_InternationalizationView.checkMessage(driver, PO_Properties.GERMAN, "nav.users.list");

        // Pasar a ingles
        PO_PrivateView.click(driver, "id", "btnLanguage", 0);
        PO_PrivateView.click(driver, "id", "btnEnglish", 0);
        // Comprobar el mensaje en ingles
        PO_InternationalizationView.checkMessage(driver, PO_Properties.ENGLISH, "nav.users.list");

        // Cerrar sesión
        PO_PrivateView.logout(driver);
    }

    /**
     * @author Donato
     * [Prueba34] Intentar acceder sin estar autenticado a la opción de listado de usuarios. Se deberá volver al
     * formulario de login.
     */
    @Test
    @Order(34)
    void PR34() {

        driver.navigate().to("http://localhost:8090/user/list");

        PO_PublicView.checkLoginIsVisible(driver, LANGUAGE);
    }

    /**
     * @author Donato
     * [Prueba35] Intentar acceder sin estar autenticado a la opción de listado de invitaciones de amistad recibida
     * de un usuario estándar. Se deberá volver al formulario de login.
     */
    @Test
    @Order(35)
    void PR35() {

        driver.navigate().to("http://localhost:8090/friendship/list");

        PO_PublicView.checkLoginIsVisible(driver, LANGUAGE);
    }

    /**
     * @author Donato
     * [Prueba36] Estando autenticado como usuario estándar intentar acceder a una opción disponible solo
     * para usuarios administradores (Añadir menú de auditoria (visualizar logs)). Se deberá indicar un mensaje
     * de acción prohibida.
     */
    @Test
    @Order(36)
    void PR36() {

        PO_PublicView.loginUser(driver);

        driver.navigate().to("http://localhost:8090/log");

        PO_HomeView.checkAccessDeniedMessage(driver, LANGUAGE);
    }

    /**
     * @author Donato
     * [Prueba37] Estando autenticado como usuario administrador visualizar todos los logs generados en una
     * serie de interacciones. Esta prueba deberá generar al menos dos interacciones de cada tipo y comprobar
     * que el listado incluye los logs correspondientes.
     */
    @Test
    @Order(37)
    void PR37() {

        // Realizamos al menos dos interacciones de cada tipo

        PO_PublicView.loginFailIncorrect(driver);
        PO_PublicView.loginFailIncorrect(driver);

        PO_PublicView.switchToSignup(driver);
        PO_PublicView.signupSuccess(driver);

        PO_PublicView.switchToSignup(driver);
        PO_PublicView.signupSuccessBis(driver);

        PO_PublicView.loginUser(driver);
        PO_PrivateView.logout(driver);

        PO_PublicView.loginAdmin(driver);
        PO_PrivateView.logout(driver);

        PO_PublicView.loginAdmin(driver);
        PO_PrivateView.switchToLog(driver);

        // Obtenemos las filas de la tabla de registros

        List<WebElement> rows = driver.findElements(By.xpath("//table//tbody//tr"));

        HashMap<String,Integer> actions = new HashMap<>();

        WebElement element;

        // Recorremos las filas y contamos las acciones

        for(WebElement row:rows){
            element = row.findElement(By.xpath(".//td[2]"));
            if(!actions.containsKey(element.getText())){
                actions.put(element.getText(), 1);
            }else{
                actions.put(element.getText(),actions.get(element.getText())+1);
            }
        }

        // Comprobamos que hemos realizado al menos una interacción de cada tipo

        Assertions.assertTrue(actions.get("PET")>=2);
        Assertions.assertTrue(actions.get("LOGIN_EX")>=2);
        Assertions.assertTrue(actions.get("LOGIN_ERR")>=2);
        Assertions.assertTrue(actions.get("LOGOUT")>=2);
        Assertions.assertTrue(actions.get("ALTA")>=2);
    }

    /**
     * @author Donato
     * [Prueba38]
     * Estando autenticado como usuario administrador, ir a visualización de logs y filtrar por un
     * tipo, pulsar el botón/enlace borrar logs y comprobar que se eliminan los logs del tipo seleccionado, de la
     * base de datos.
     */
    @Test
    @Order(38)
    void PR38() {

        PO_PublicView.loginAdmin(driver);

        PO_PrivateView.switchToLog(driver);

        PO_PrivateView.filterByLoginEx(driver);

        PO_PrivateView.deleteLogs(driver);

        // Contamos los logs que tienen LOGIN_EX
        List<WebElement> rows = driver.findElements(By.xpath("//table//tbody//tr"));
        int count = 0;
        for(WebElement row:rows){
            if(row.findElement(By.xpath(".//td[2]")).getText().equals("LOGIN_EX")){
                count++;
            }
        }

        // Comprobamos que no hay logs de tipo LOGIN_EX
        Assertions.assertEquals(0, count);
    }

    /**
     * @author Pedro
     * [Prueba39] Acceder a las publicaciones de un amigo y recomendar una publicación. Comprobar que el
     * número de recomendaciones se ha incrementado en uno y que no aparece el botón/enlace recomendar.
     */
    @Test
    @Order(39)
    void PR39() {
        //Inicio sesión como usuario 1
        PO_PublicView.loginSpecificUser("user01@email.com","Us3r@1-PASSW",driver);
        //Vamos a la lista de publicaciones o detalles de un amigo (del user4)
        PO_FriendsView.goToDetailsOf(driver,"user04@email.com");
        //Y compruebo que recomiendo una publicación correctamente
        PO_FriendsView.checkRecomendationsClick(driver,"42");
        // Cerrar sesión
        PO_PrivateView.logout(driver);
    }

    /**
     * @author Pedro
     * [Prueba40] Utilizando un acceso vía URL u otra alternativa, tratar de recomendar una publicación de un
     * usuario con el que no se mantiene una relación de amistad.
     * (Se ejecuta bien sola [causa desconocida])
     */
    @Test
    @Order(40)
    void PR40() {
        //Inicio sesión como usuario 1
        PO_PublicView.loginSpecificUser("user01@email.com", "Us3r@1-PASSW", driver);
        //Y busco en la barra de navegación que sería lo necesario para recomendar una publicación
        //En este caso la publicación 60 (id:57) es del usuario 6 que no es amigo del usuario 1
        String urlRecomendIlegal = "http://localhost:8090/publications/recomend/57";
        //Y vamos a dicha URL
        driver.navigate().to(urlRecomendIlegal);
        //Ahora deberíamos estar en /home ya que es donde redirije un error de validación por lado de servidor
        SeleniumUtils.waitLoadElementsBy(driver, "id", "welcome", PO_View.getTimeout());
        //Y salimos de sesión
        PO_PrivateView.logout(driver);
    }

    /**
     * @author Samuel
     * [Prueba41] Como administrador, cambiar el estado de una publicación y comprobar que el estado ha
     * cambiado.
     */
    @Test
    @Order(41)
    void PR41() {
        //Iniciamos sesión como administrador y comprobar inicio correcto
        PO_PublicView.loginAdmin(driver);
        PO_View.checkElementBy(driver, "text", "admin@email.com");

        //Acceder al listado de invitaciones de publicaciones
        PO_PrivateView.click(driver, "id", "publicationDropdown", 0);
        PO_PrivateView.click(driver, "id", "publicationList", 0);

        //Actualizamos para cambiar el estado a MODERADA
        PO_PrivateView.click(driver, "id","actualizar_Publication 10",0);

        //Introducimos "Publicación 10" en el campo de busqueda y realizamos la busqueda
        WebElement campo_busqueda = driver.findElement(By.id("espacio_busqueda"));
        campo_busqueda.click();
        campo_busqueda.clear();
        campo_busqueda.sendKeys("Publication 10");
        PO_PrivateView.click(driver, "id", "botón_buscar", 0);

        //Comprobamos que tenga estado MODERADA
        PO_View.checkElementBy(driver, "text", "MODERADA");

        // Cerrar sesión
        PO_PrivateView.logout(driver);
    }

    /**
     * @author Samuel
     * [Prueba42] Como usuario estándar, comprobar que NO aparece en el listado propio de publicaciones una
     * publicación censurada.
     */
    @Test
    @Order(42)
    void PR42() {
        //Iniciamos sesión como usuario estándar y comprobar inicio correcto
        PO_PublicView.loginUser(driver);
        PO_View.checkElementBy(driver, "text", "user01@email.com");

        //Acceder a mis publicaciones
        PO_PrivateView.click(driver, "id", "publicationDropdown", 0);
        PO_PrivateView.click(driver, "id", "misPublicaciones", 0);

        //Comprobamos que no tenga publicaciones con estado CENSURADA
        SeleniumUtils.waitTextIsNotPresentOnPage(driver, "Publication 10",PO_View.getTimeout());
        SeleniumUtils.waitTextIsNotPresentOnPage(driver, "Publication 11",PO_View.getTimeout());

        // Cerrar sesión
        PO_PrivateView.logout(driver);
    }

    /**
     * @author Samuel
     * [Prueba43] Como usuario estándar, comprobar que, en el listado de publicaciones de un amigo, NO
     * aparece una publicación moderada.
     */
    @Test
    @Order(43)
    void PR43() {
        //Iniciamos sesión como usuario estándar y comprobar inicio correcto
        PO_PublicView.loginUser(driver);
        PO_View.checkElementBy(driver, "text", "user01@email.com");

        // Acceder al listado de amistades
        PO_PrivateView.click(driver, "id", "friendshipDropdown", 0);
        PO_PrivateView.click(driver, "id", "friendshipList", 0);

        // Acceder a los detalles del amigo
        PO_PrivateView.click(driver, "id", "name_user04@email.com", 0);

        //Comprobamos que no aparecen las publicaciones moderadas(Publication 49 y Publication 48)
        SeleniumUtils.waitTextIsNotPresentOnPage(driver, "Publication 48",PO_View.getTimeout());
        SeleniumUtils.waitTextIsNotPresentOnPage(driver, "Publication 49",PO_View.getTimeout());
    }

    /**
     * @author Samuel
     * [Prueba44] Como usuario estándar, intentar acceder la opción de cambio del estado de una publicación y
     * comprobar que se redirecciona al usuario hacia el formulario de login.
     */
    @Test
    @Order(44)
    void PR44() {
        //Iniciamos sesión como usuario estándar y comprobar inicio correcto
        PO_PublicView.loginUser(driver);
        PO_View.checkElementBy(driver, "text", "user01@email.com");

        //Acceder a mis publicaciones
        PO_PrivateView.click(driver, "id", "publicationDropdown", 0);
        PO_PrivateView.click(driver, "id", "misPublicaciones", 0);

        //Actualizamos para cambiar el estado a MODERADA (clicamos dos veces porque no sabemos porque funciona mal)
        PO_PrivateView.click(driver, "id","actualizar_Publication 12",0);
        PO_PrivateView.click(driver, "id","actualizar_Publication 12",0);

        //Comprobamos que se redirecciona al formulario de login
        PO_View.checkElementBy(driver, "text", "Iniciar sesion");
    }

    /**
     * @author Samuel
     * [Prueba45] Hacer una búsqueda con el campo vacío y comprobar que se muestra la página que
     * corresponde con el listado publicaciones.
     */
    @Test
    @Order(45)
    void PR45() {
        //Iniciamos sesión como administrador y comprobar inicio correcto
        PO_PublicView.loginAdmin(driver);
        PO_View.checkElementBy(driver, "text", "admin@email.com");

        // Acceder al listado de publicaciones
        PO_PrivateView.click(driver, "id", "publicationDropdown", 0);
        PO_PrivateView.click(driver, "id", "publicationList", 0);

        //Introducimos "" en el campo de busqueda y realizamos la busqueda
        WebElement campo_busqueda = driver.findElement(By.id("espacio_busqueda"));
        campo_busqueda.click();
        campo_busqueda.clear();
        campo_busqueda.sendKeys("");
        PO_PrivateView.click(driver, "id", "botón_buscar", 0);

        //Comprobamos que estan todas las publicaciones
        PO_View.checkElementBy(driver, "text", "Publication 10");
        PO_View.checkElementBy(driver, "text", "Publication 11");
        PO_View.checkElementBy(driver, "text", "Publication 12");
        PO_View.checkElementBy(driver, "text", "Publication 13");
        PO_View.checkElementBy(driver, "text", "Publication 14");

        // Cerrar sesión
        PO_PrivateView.logout(driver);
    }

    /**
     * @author Samuel
     * [Prueba46] Hacer una búsqueda escribiendo en el campo un texto que no exista y comprobar que se
     * muestra la página que corresponde, con la lista de publicaciones vacía.
     */
    @Test
    @Order(46)
    void PR46() {
        //Iniciamos sesión como administrador y comprobar inicio correcto
        PO_PublicView.loginAdmin(driver);
        PO_View.checkElementBy(driver, "text", "admin@email.com");

        // Acceder al listado de invitaciones de amistad
        PO_PrivateView.click(driver, "id", "publicationDropdown", 0);
        PO_PrivateView.click(driver, "id", "publicationList", 0);

        //Introducimos "" en el campo de busqueda y realizamos la busqueda
        WebElement campo_busqueda = driver.findElement(By.id("espacio_busqueda"));
        campo_busqueda.click();
        campo_busqueda.clear();
        campo_busqueda.sendKeys("no exite cadena");
        PO_PrivateView.click(driver, "id", "botón_buscar", 0);

        //Comprobamos que no aparece ninguna publicación
        SeleniumUtils.waitTextIsNotPresentOnPage(driver, "Publication 10",PO_View.getTimeout());
        SeleniumUtils.waitTextIsNotPresentOnPage(driver, "Publication 11",PO_View.getTimeout());
        SeleniumUtils.waitTextIsNotPresentOnPage(driver, "Publication 12",PO_View.getTimeout());
        SeleniumUtils.waitTextIsNotPresentOnPage(driver, "Publication 13",PO_View.getTimeout());
        SeleniumUtils.waitTextIsNotPresentOnPage(driver, "Publication 14",PO_View.getTimeout());

        // Cerrar sesión
        PO_PrivateView.logout(driver);
    }

    /**
     * @author Samuel
     * [Prueba47] Hacer una búsqueda de publicaciones censuradas, escribiendo el cuadro de búsqueda
     * “Censurada” y comprobar que se muestra la página que corresponde, con la lista de publicaciones
     * censuradas o que en el texto especificado sea parte de título, estado o del email.
     */
    @Test
    @Order(47)
    void PR47() {
        //Iniciamos sesión como administrador y comprobar inicio correcto
        PO_PublicView.loginAdmin(driver);
        PO_View.checkElementBy(driver, "text", "admin@email.com");

        // Acceder al listado de invitaciones de amistad
        PO_PrivateView.click(driver, "id", "publicationDropdown", 0);
        PO_PrivateView.click(driver, "id", "publicationList", 0);

        //Introducimos "" en el campo de busqueda y realizamos la busqueda
        WebElement campo_busqueda = driver.findElement(By.id("espacio_busqueda"));
        campo_busqueda.click();
        campo_busqueda.clear();
        campo_busqueda.sendKeys("Censurada");
        PO_PrivateView.click(driver, "id", "botón_buscar", 0);

        //Comprobamos que este la publicaciones censuradas y que las otras no
        PO_View.checkElementBy(driver, "text", "Publication 10");
        PO_View.checkElementBy(driver, "text", "Publication 11");
        SeleniumUtils.waitTextIsNotPresentOnPage(driver, "Publication 12",PO_View.getTimeout());
        SeleniumUtils.waitTextIsNotPresentOnPage(driver, "Publication 13",PO_View.getTimeout());
        SeleniumUtils.waitTextIsNotPresentOnPage(driver, "Publication 14",PO_View.getTimeout());

        // Cerrar sesión
        PO_PrivateView.logout(driver);
    }

    /**
     * @author David
     * [Prueba48] Desde el formulario de crear publicaciones, crear una publicación con datos válidos y una foto
     * adjunta. Comprobar que en el listado de publicaciones aparecer la foto adjunta junto al resto de datos de
     * la publicación.
     */
    @Test
    @Order(48)
    void PR48() {
        //Login como usuario
        PO_PublicView.loginSpecificUser("user01@email.com","Us3r@1-PASSW",driver);

        //Obtener ruta de la imagen
        String currentDirectory = System.getProperty("user.dir");
        String imgPath = currentDirectory + "\\src\\test\\resources\\testImg.png";

        //Crear publicación con foto
        PO_PublicationView.goToAddPublication(driver);
        driver.findElement(By.id("image")).sendKeys(imgPath);
        PO_PublicationView.fillForm(driver,"Publicación de prueba con foto","Descripción de prueba");

        // Comprobar en la lista de publicaciones que aparece la foto de la nueva publicación
        PO_PublicationView.goToListPublication(driver);
        PO_Pagination.clickLastPage(driver);
        SeleniumUtils.textIsPresentOnPage(driver,"Publicación de prueba con foto");
        WebElement img;
        try{
            img = driver.findElement(By.id("customImg"));
        }catch (Exception e){
            img = null;
        }
        Assertions.assertNotNull(img);
    }

    /**
     * @author David
     * [Prueba49] Crear una publicación con datos válidos y sin una foto adjunta. Comprobar que la publicación
     * se ha creado con éxito, ya que la foto no es obligatoria.
     */
    @Test
    @Order(49)
    void PR49() {

        //Login como usuario
        PO_PublicView.loginSpecificUser("user01@email.com","Us3r@1-PASSW",driver);

        //Creamos publicación
        PO_PublicationView.goToAddPublication(driver);
        PO_PublicationView.fillForm(driver,"Publicación de prueba sin foto","Descripción de prueba");

        //Comprobamos que se añadió correctamente
        PO_PublicationView.goToListPublication(driver);
        PO_Pagination.clickLastPage(driver);
        SeleniumUtils.textIsPresentOnPage(driver,"Publicación de prueba sin foto");

    }



    /* Ejemplos de pruebas de llamada a una API-REST */
    /* ---- Probamos a obtener lista de canciones sin token ---- */
    @Test
    @Order(11)
    public void PR11_1() {
        final String RestAssuredURL = "http://localhost:8081/api/v1.0/songs";
        Response response = RestAssured.get(RestAssuredURL);
        Assertions.assertEquals(403, response.getStatusCode());
    }

    @Test
    @Order(38)
    public void PR38_1() {
        final String RestAssuredURL = "http://localhost:8081/api/v1.0/users/login";
        //2. Preparamos el parámetro en formato JSON
        RequestSpecification request = RestAssured.given();
        JSONObject requestParams = new JSONObject();
        requestParams.put("email", "prueba1@prueba1.com");
        requestParams.put("password", "prueba1");
        request.header("Content-Type", "application/json");
        request.body(requestParams.toJSONString());
        //3. Hacemos la petición
        Response response = request.post(RestAssuredURL);
        //4. Comprobamos que el servicio ha tenido exito
        Assertions.assertEquals(200, response.getStatusCode());
    }
}
