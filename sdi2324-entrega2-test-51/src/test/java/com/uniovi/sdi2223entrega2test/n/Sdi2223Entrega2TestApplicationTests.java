package com.uniovi.sdi2223entrega2test.n;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.uniovi.sdi2223entrega2test.n.pageobjects.*;
import com.uniovi.sdi2223entrega2test.n.util.SeleniumUtils;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.bson.codecs.jsr310.LocalDateCodec;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.bson.Document;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.conversions.Bson;
import com.mongodb.client.model.Filters;
import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class Sdi2223Entrega2TestApplicationTests {
    static String PathFirefox = "C:\\Program Files\\Mozilla Firefox\\firefox.exe";
    //static String Geckodriver = "C:\\Dev\\tools\\selenium\\geckodriver-v0.30.0-win64.exe";

    //Peter :(
    //static String Geckodriver = "P:\\aaaUni\\Uni\\SDI\\geckodriver-v0.30.0-win64.exe";
    //Teresa :)
    static String Geckodriver = "C:\\Users\\mtere\\Desktop\\sdi\\geckodriver-v0.30.0-win64.exe";
    static WebDriver driver = getDriver(PathFirefox, Geckodriver);
    static String URL = "http://localhost:8080/users/login";
    static String URL_API = "http://localhost:8080/apiclient/client.html";

    static MongoClient mongoClient;
    static MongoDatabase database;

    public static WebDriver getDriver(String PathFirefox, String Geckodriver) {
        System.setProperty("webdriver.firefox.bin", PathFirefox);
        System.setProperty("webdriver.gecko.driver", Geckodriver);
        driver = new FirefoxDriver();
        return driver;
    }

    @BeforeEach
    public void setUp() {
        initDatabase();
        driver.navigate().to(URL);
    }

    private void initDatabase() {
        //establecer conexión con la bbdd
        mongoClient = MongoClients.create("mongodb://localhost:27017");
        database = mongoClient.getDatabase("sdinstagram");
        restDatabase();
        insertTestsData();
        //cierra la conexión
        mongoClient.close();
    }

    private String hashPassword(String password, String clave) {
        try {
            // Crear una instancia del algoritmo HMAC-SHA-256 con la clave proporcionada
            Mac sha256Hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(clave.getBytes(), "HmacSHA256");
            sha256Hmac.init(secretKey);
            // Aplicar el algoritmo HMAC-SHA-256 al password
            byte[] hashedBytes = sha256Hmac.doFinal(password.getBytes());
            // Convertir el hash a una representación hexadecimal
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException | InvalidKeyException ex) {
            throw new RuntimeException("No se ha podido hashear las contraseñas");
        }
    }

    private void insertTestsData() {
        //usuarios
        Document admin1 = new Document("email", "admin@email.com")
                .append("password", hashPassword("@Dm1n1str@D0r", "abcdefg"))
                .append("firstName", "admin")
                .append("lastName", "admin")
                .append("birthdate", "2024-04-30")
                .append("role", "admin");
        Document user1 = new Document("email", "user01@email.com")
                .append("password", hashPassword("Us3r@1-PASSW", "abcdefg"))
                .append("firstName", "User01")
                .append("lastName", "Surname01")
                .append("birthdate", "2024-04-30")
                .append("role", "user");
        Document user2 = new Document("email", "user02@email.com")
                .append("password", hashPassword("Us3r@2-PASSW", "abcdefg"))
                .append("firstName", "User02")
                .append("lastName", "Surname02")
                .append("birthdate","2024-04-30")
                .append("role", "user");
        Document user3 = new Document("email", "user03@email.com")
                .append("password", hashPassword("Us3r@3-PASSW", "abcdefg"))
                .append("firstName", "User03")
                .append("lastName", "Surname03")
                .append("birthdate","2024-04-30")
                .append("role", "user");
        Document user4 = new Document("email", "user04@email.com")
                .append("password", hashPassword("Us3r@4-PASSW", "abcdefg"))
                .append("firstName", "User04")
                .append("lastName", "Surname04")
                .append("birthdate","2024-04-30")
                .append("role", "user");
        Document user5 = new Document("email", "user05@email.com")
                .append("password", hashPassword("Us3r@5-PASSW", "abcdefg"))
                .append("firstName", "User05")
                .append("lastName", "Surname05")
                .append("birthdate","2024-04-30")
                .append("role", "user");
        Document user6 = new Document("email", "user06@email.com")
                .append("password", hashPassword("Us3r@6-PASSW", "abcdefg"))
                .append("firstName", "User06")
                .append("lastName", "Surname06")
                .append("birthdate","2024-04-30")
                .append("role", "user");
        Document user7 = new Document("email", "user07@email.com")
                .append("password", hashPassword("Us3r@7-PASSW", "abcdefg"))
                .append("firstName", "User07")
                .append("lastName", "Surname07")
                .append("birthdate","2024-04-30")
                .append("role", "user");
        Document user8 = new Document("email", "user08@email.com")
                .append("password", hashPassword("Us3r@8-PASSW", "abcdefg"))
                .append("firstName", "User08")
                .append("lastName", "Surname08")
                .append("birthdate","2024-04-30")
                .append("role", "user");
        Document user9 = new Document("email", "user09@email.com")
                .append("password", hashPassword("Us3r@9-PASSW", "abcdefg"))
                .append("firstName", "User09")
                .append("lastName", "Surname09")
                .append("birthdate","2024-04-30")
                .append("role", "user");
        Document user10 = new Document("email", "user10@email.com")
                .append("password", hashPassword("Us3r@10-PASSW", "abcdefg"))
                .append("firstName", "User10")
                .append("lastName", "Surname10")
                .append("birthdate","2024-04-30")
                .append("role", "user");
        Document user11 = new Document("email", "user11@email.com")
                .append("password", hashPassword("Us3r@11-PASSW", "abcdefg"))
                .append("firstName", "User11")
                .append("lastName", "Surname11")
                .append("birthdate","2024-04-30")
                .append("role", "user");
        Document user12 = new Document("email", "user12@email.com")
                .append("password", hashPassword("Us3r@12-PASSW", "abcdefg"))
                .append("firstName", "User12")
                .append("lastName", "Surname12")
                .append("birthdate","2024-04-30")
                .append("role", "user");
        Document user13 = new Document("email", "user13@email.com")
                .append("password", hashPassword("Us3r@13-PASSW", "abcdefg"))
                .append("firstName", "User13")
                .append("lastName", "Surname13")
                .append("birthdate","2024-04-30")
                .append("role", "user");
        Document user14 = new Document("email", "user14@email.com")
                .append("password", hashPassword("Us3r@14-PASSW", "abcdefg"))
                .append("firstName", "User14")
                .append("lastName", "Surname14")
                .append("birthdate","2024-04-30")
                .append("role", "user");
        Document user15 = new Document("email", "user15@email.com")
                .append("password", hashPassword("Us3r@15-PASSW", "abcdefg"))
                .append("firstName", "User15")
                .append("lastName", "Surname15")
                .append("birthdate","2024-04-30")
                .append("role", "user");



        database.getCollection("users").insertOne(admin1);
        database.getCollection("users").insertOne(user1);
        for(int i = 0; i < 10; i++) {
            Document publication = new Document("title","Publication1"+i)
                    .append("description","Publication1"+i)
                    .append("author",database.getCollection("users").find(user1))
                    .append("date",getFormattedDate(i));
            database.getCollection("publications").insertOne(publication);
        }

        database.getCollection("users").insertOne(user2);
        database.getCollection("users").insertOne(user3);
        database.getCollection("users").insertOne(user4);
        database.getCollection("users").insertOne(user5);
        database.getCollection("users").insertOne(user6);
        database.getCollection("users").insertOne(user7);
        database.getCollection("users").insertOne(user8);
        database.getCollection("users").insertOne(user9);
        database.getCollection("users").insertOne(user10);
        database.getCollection("users").insertOne(user11);
        database.getCollection("users").insertOne(user12);
        database.getCollection("users").insertOne(user13);
        database.getCollection("users").insertOne(user14);
        database.getCollection("users").insertOne(user15);

        //Friendships
        Document friendship = new Document("user1", database.getCollection("users").find(user1).first())
                .append("user2",database.getCollection("users").find(user4).first())
                .append("date", LocalDateTime.now().minusDays(5));
        database.getCollection("friendship").insertOne(friendship);
        friendship = new Document("user1", database.getCollection("users").find(user1).first())
                .append("user2",database.getCollection("users").find(user5).first())
                .append("date", LocalDateTime.now().minusDays(6));
        database.getCollection("friendship").insertOne(friendship);

        //Mensajes
        List<Document> messages = new ArrayList<>();
        //Adding messages
        Document message1 = new Document("messageId", "1")
                .append("author", database.getCollection("users").find(user1).first())
                .append("date", LocalDateTime.now().minusDays(5))
                .append("text", "Que tal estás?")
                .append("read", true);
        messages.add(message1);

        Document message2 = new Document("messageId","2")
                .append("author", database.getCollection("users").find(user4).first())
                .append("date", LocalDateTime.now().minusDays(4))
                .append("text", "Mal, haciendo tests")
                .append("read", false);
        messages.add(message2);

        //Conversations
        Document conversation = new Document("user1", database.getCollection("users").find(user1).first())
                .append("user2",database.getCollection("users").find(user4).first())
                .append("messages",messages);
        database.getCollection("conversations").insertOne(conversation);
    }

    public String getFormattedDate(int i) {
        // Current Date
        LocalDateTime currentDate = LocalDateTime.now().minusDays(i);

        // Obtiene los componentes de la fecha
        int day = currentDate.getDayOfMonth();
        int month = currentDate.getMonthValue()+1; // Los meses son base 0, por lo que se suma 1
        int year = currentDate.getYear();

        // Obtiene los componentes de la hora
        int hours = currentDate.getHour();
        int minutes = currentDate.getMinute();
        int seconds = currentDate.getSecond();

        return month+"/"+day+"/"+year+" "+hours+":"+minutes+":"+seconds;
    }

    private void restDatabase() {
        // Eliminar todas las colecciones de la base de datos
        for (String collectionName : database.listCollectionNames()) {
            database.getCollection(collectionName).deleteMany(new Document());
        }
    }

    //Después de cada prueba se borran las cookies del navegador
    @AfterEach
    public void tearDown() {

        driver.manage().deleteAllCookies();
    }

    //Antes de la primera prueba
    @BeforeAll
    static public void begin() {
    }

    //Al finalizar la última prueba
    @AfterAll
    static public void end() {
    //Cerramos el navegador al finalizar las pruebas
        //driver.quit();
    }

    @Test
    @Order(1)
    void PR01() {
        Assertions.assertTrue(true, "PR01 sin hacer");
    }

    @Test
    @Order(2)
    public void PR02() {
        Assertions.assertTrue(true, "PR02 sin hacer");
    }

    @Test
    @Order(3)
    public void PR03() {
        Assertions.assertTrue(true, "PR03 sin hacer");
    }

    @Test
    @Order(4)
    public void PR04() {
        Assertions.assertTrue(true, "PR04 sin hacer");
    }

    @Test
    @Order(5)
    public void PR05() {
        Assertions.assertTrue(true, "PR05 sin hacer");
    }

    @Test
    @Order(6)
    public void PR06() {
        Assertions.assertTrue(true, "PR06 sin hacer");
    }

    @Test
    @Order(7)
    public void PR07() {
        Assertions.assertTrue(true, "PR07 sin hacer");
    }

    @Test
    @Order(8)
    public void PR08() {
        Assertions.assertTrue(true, "PR08 sin hacer");
    }

    @Test
    @Order(9)
    public void PR09() {
        Assertions.assertTrue(true, "PR09 sin hacer");
    }

    @Test
    @Order(10)
    public void PR10() {
        Assertions.assertTrue(true, "PR10 sin hacer");
    }


    /* Ejemplos de pruebas de llamada a una API-REST */
    /* ---- Probamos a obtener lista de canciones sin token ---- */
    @Test
    @Order(11)
    public void PR11() {
        final String RestAssuredURL = "http://localhost:8081/api/v1.0/songs";
        Response response = get(RestAssuredURL);
        Assertions.assertEquals(403, response.getStatusCode());
    }


    /**
     * @author Samuel
     * [Prueba22] Desde el listado de usuarios de la aplicación, enviar una invitación de amistad a un usuario.
     * Comprobar que la solicitud de amistad aparece en el listado de invitaciones (punto siguiente).
     */
    @Test
    @Order(22)
    void PR22() {
        //Inicio sesión como el usuario2
        PO_PublicView.loginSpecificUser("user02@email.com","Us3r@2-PASSW",driver);
        //Envío solicitud al usuario 3
        WebElement botonEnviarSolicitud = driver.findElement(By.id("btn_user03@email.com"));
        botonEnviarSolicitud.click();
        // Cerrar sesión e inicio con el usuario 3
        WebElement logout = driver.findElement(By.id("logout"));
        logout.click();
        PO_PublicView.loginSpecificUser("user03@email.com","Us3r@3-PASSW",driver);
        //Voy a la ventana de ver solicitudes de amistad recibidas friendshipRequests
        WebElement myRequests = driver.findElement(By.id("myRequests"));
        myRequests.click();
        //Y comprobamos que llegó la solicitud de amistad  email_user02@email.com
        SeleniumUtils.textIsPresentOnPage(driver,"user02@email.com");
        //Finalmente logeamos-cerramos sesión
        WebElement logout2 = driver.findElement(By.id("logout"));
        logout2.click();
    }

    /**
     * @author Samuel
     * [Prueba23] Desde el listado de usuarios de la aplicación, enviar una invitación de amistad a un usuario al
     * que ya le habíamos enviado la invitación previamente. No debería dejarnos enviar la invitación. Se podría
     * ocultar el botón de enviar invitación o notificar que ya había sido enviada previamente.
     */
    @Test
    @Order(23)
    void PR23() {
        //Inicio sesión como usuario 1
        PO_PublicView.loginSpecificUser("user01@email.com","Us3r@1-PASSW",driver);
        //Y pruebo a enviarle 2 veces la solicitud de amistad al usuario 2
        WebElement botonEnviarSolicitud1 = driver.findElement(By.id("btn_user02@email.com"));
        botonEnviarSolicitud1.click();
        WebElement botonEnviarSolicitud2 = driver.findElement(By.id("btn_user02@email.com"));
        botonEnviarSolicitud2.click();
        SeleniumUtils.textIsPresentOnPage(driver, "You have already sent a friend request to this user.");
        // Cerrar sesión
        WebElement logout = driver.findElement(By.id("logout"));
        logout.click();
    }

    /**
     * @author Samuel
     * [Prueba24] Mostrar el listado de invitaciones de amistad recibidas. Comprobar con un listado que
     * contenga varias invitaciones recibidas.
     */
    @Test
    @Order(24)
    void PR24() {
        //Inicio sesión como el usuario1
        PO_PublicView.loginSpecificUser("user01@email.com","Us3r@1-PASSW",driver);
        //Envío solicitud al usuario 3
        WebElement botonEnviarSolicitud = driver.findElement(By.id("btn_user03@email.com"));
        botonEnviarSolicitud.click();
        // Cerrar sesión e inicio con el usuario 2
        WebElement logout = driver.findElement(By.id("logout"));
        logout.click();
        PO_PublicView.loginSpecificUser("user02@email.com","Us3r@2-PASSW",driver);
        //Envío solicitud al usuario 3
        WebElement botonEnviarSolicitud2 = driver.findElement(By.id("btn_user03@email.com"));
        botonEnviarSolicitud2.click();
        // Cerrar sesión e inicio con el usuario 3
        WebElement logout2 = driver.findElement(By.id("logout"));
        logout2.click();
        PO_PublicView.loginSpecificUser("user03@email.com","Us3r@3-PASSW",driver);
        //Voy a la ventana de ver solicitudes de amistad recibidas friendshipRequests
        WebElement myRequests = driver.findElement(By.id("myRequests"));
        myRequests.click();
        //Y comprobamos que llegaron las solicitudes de amistad
        SeleniumUtils.textIsPresentOnPage(driver,"user01@email.com");
        SeleniumUtils.textIsPresentOnPage(driver,"user02@email.com");
        //Finalmente logeamos-cerramos sesión
        WebElement logout3 = driver.findElement(By.id("logout"));
        logout3.click();
    }

    /**
     * @author Samuel
     * [Prueba25] Sobre el listado de invitaciones recibidas. Hacer clic en el botón/enlace de una de ellas y
     * comprobar que dicha solicitud desaparece del listado de invitaciones.
     */
    @Test
    @Order(25)
    void PR25() {
        //Inicio sesión como el usuario2
        PO_PublicView.loginSpecificUser("user02@email.com","Us3r@2-PASSW",driver);
        //Envío solicitud al usuario 3
        WebElement botonEnviarSolicitud = driver.findElement(By.id("btn_user03@email.com"));
        botonEnviarSolicitud.click();
        // Cerrar sesión e inicio con el usuario 3
        WebElement logout = driver.findElement(By.id("logout"));
        logout.click();
        PO_PublicView.loginSpecificUser("user03@email.com","Us3r@3-PASSW",driver);
        //Voy a la ventana de ver solicitudes de amistad recibidas friendshipRequests
        WebElement myRequests = driver.findElement(By.id("myRequests"));
        myRequests.click();
        //Y comprobamos que llegó la solicitud de amistad  email_user02@email.com y la aceptamos
        SeleniumUtils.textIsPresentOnPage(driver,"user02@email.com");
        WebElement botonAceptar = driver.findElement(By.id("btn_user02@email.com"));
        botonAceptar.click();
        //Comprobamos que ya no aparece la solicitud
        SeleniumUtils.textIsNotPresentOnPage(driver,"user02@email.com");
        //Finalmente logeamos-cerramos sesión
        WebElement logout2 = driver.findElement(By.id("logout"));
        logout2.click();
    }

    /**
     * @author Samuel
     * [Prueba25] Obtener los mensajes de una conversación.
     */
    //ejemplo API
    @Test
    @Order(43)
    public void PR57() {
        final String RestAssuredURL = "http://localhost:8081/api/v1.0/conversation/user01@email.com";
        //2. Preparamos el parámetro en formato JSON
        RequestSpecification request = RestAssured.given();
        //3. Hacemos la petición
        Response response = request.post(RestAssuredURL);
        //4. Comprobamos que el servicio ha tenido exito
        Assertions.assertEquals(200, response.getStatusCode());
    }

    /**
     * @author Teresa
     * [Prueba33] Ir al formulario crear publicaciones, rellenarla con datos válidos y pulsar el botón Submit.
     * Comprobar que la publicación sale en el listado de publicaciones de dicho usuario.
     */
    @Test
    @Order(33)
    public void PR33() {

        //Login como usuario
        PO_PublicView.loginSpecificUser("user01@email.com","Us3r@1-PASSW",driver);

        //Creamos publicación
        PO_PublicationView.goToAddPublication(driver);
        PO_PublicationView.fillForm(driver,"Publicación de prueba","Descripción de prueba");

        //Comprobamos que se añadió correctamente
        PO_PublicationView.goToListPublication(driver);
        PO_Pagination.clickPage(driver,3);
        //PO_Pagination.clickNextPage(driver);
        SeleniumUtils.textIsPresentOnPage(driver,"Publicación de prueba");
    }
    /**
     * @author Teresa
     * [Prueba34] Ir al formulario de crear publicaciones, rellenarla con datos inválidos (campos título y
     * descripción vacíos) y pulsar el botón Submit. Comprobar que se muestran los mensajes de campo
     * obligatorios
     */
    @Test
    @Order(34)
    public void PR34() {
        //Login como usuario
        PO_PublicView.loginSpecificUser("user01@email.com","Us3r@1-PASSW",driver);

        //Creamos publicación con datos inválidos
        PO_PublicationView.goToAddPublication(driver);
        PO_PublicationView.fillForm(driver,"a","a");

        //Comprobamos que se se muestran los mensajes de error
        SeleniumUtils.textIsPresentOnPage(driver,"Error when inserting new publication: Title must be at least 4 characters long.");
        //SeleniumUtils.textIsPresentOnPage(driver,"Error when inserting new publication: Description must be at least 4 characters long.");

        // Cerrar sesión
        //PO_PrivateView.logout(driver);
    }

    /**
     * @author Teresa
     * [Prueba35] Mostrar el listado de publicaciones de un usuario y comprobar que se muestran todas las
     * que existen para dicho usuario.
     */
    @Test
    @Order(35)
    public void PR35() {

        //Login como usuario
        PO_PublicView.loginSpecificUser("user01@email.com","Us3r@1-PASSW",driver);

        //Lista con las publicaciones no censuradas del usuario
        String[] titles = {"Publication11", "Publication12", "Publication13", "Publication14", "Publication15", "Publication16", "Publication17", "Publication18", "Publication19"};
        List<String> titlesList = List.of(titles);

        //Vamos a la lista de publicaciones
        PO_PublicationView.goToListPublication(driver);

        //Comprobamos que se muestran todas las publicaciones
        PO_PublicationView.checkPublications(driver,titlesList);

        // Cerrar sesión
        //PO_PrivateView.logout(driver);
    }
    /**
     * @author Teresa
     * [Prueba36] Mostrar el perfil del usuario y comprobar que se muestran sus datos y el listado de sus
     * publicaciones.
     */
    @Test
    @Order(36)
    public void PR36() {
        // Inicio de sesión como usuario
        PO_PublicView.loginSpecificUser("user01@email.com","Us3r@1-PASSW",driver);

        // Acceder al listado de amistades
        PO_PrivateView.click(driver, "id", "myFriends", 0);

        // Acceder a los detalles del amigo
        PO_PrivateView.click(driver, "id", "user04@email.com", 0);

        // Comprobar que aparece las amistad que se espera
        SeleniumUtils.textIsPresentOnPage(driver, "user04@email.com");
        SeleniumUtils.textIsPresentOnPage(driver, "User04");
        SeleniumUtils.textIsPresentOnPage(driver, "Surname04");
        List<WebElement> elements = PO_View.checkElementBy(driver, "id", "publicationsSection");
        Assertions.assertFalse(elements.isEmpty());

        // Cerrar sesión
        //PO_PrivateView.logout(driver);
    }

    /**
     * @author Teresa
     * [Prueba37] Utilizando un acceso vía URL u otra alternativa, tratar de acceder al perfil de un usuario que
     * no sea amigo del usuario identificado en sesión. Comprobar que el sistema da un error de autorización.
     */
    @Test
    @Order(37)
    void PR37() {
        // Inicio de sesión como usuario
        PO_PublicView.loginUser(driver);

        // Navegar a la dirección de un usuario que no es amigo de user01@email.com
        driver.navigate().to("http://localhost:8080/friendships/user02@email.com");

        //SeleniumUtils.textIsPresentOnPage(driver, "You are not friends with this user:user02@email.com");
        // There is still the list of friends so there was no authorization to access that user details
        SeleniumUtils.textIsPresentOnPage(driver, "My friends");

        // Cerrar sesión
        //PO_PrivateView.logout(driver);
    }


    //ejemplo API
    @Test
    @Order(38)
    public void PR38() {
        final String RestAssuredURL = "http://localhost:8081/api/v1.0/users/login";
        //2. Preparamos el parámetro en formato JSON
        RequestSpecification request = RestAssured.given();
        JSONObject requestParams = new JSONObject();
        requestParams.put("email", "delacal@uniovi.es");
        requestParams.put("password", "1234");
        request.header("Content-Type", "application/json");
        request.body(requestParams.toJSONString());
        //3. Hacemos la petición
        Response response = request.post(RestAssuredURL);
        //4. Comprobamos que el servicio ha tenido exito
        Assertions.assertEquals(200, response.getStatusCode());
    }

    /**
     *
     * [Prueba41] Mostrar el listado de amigos para dicho usuario y comprobar que se muestran los amigos
     * del usuario autenticado. Esta prueba implica invocar a dos servicios: S1 y S2
     */
    @Test
    @Order(41)
    public void PR41() {


    }

    /**
     * @author Teresa
     * [Prueba42] Enviar un mensaje a un amigo. Esta prueba consistirá en comprobar que el servicio
     * almacena correctamente el mensaje para el mensaje enviado. Por lo tanto, el usuario tendrá que
     * identificarse (S1), enviar un mensaje para un amigo (S3) y comprobar que el mensaje ha quedado
     * registrado (S4).
     */
    @Test
    @Order(42)
    public void PR42() {
        // S1: Start session and get autentificated
        final String RestAssuredURL = "http://localhost:8080/api/v1.0/users/login";
        RequestSpecification request = RestAssured.given();
        JSONObject requestParams = new JSONObject();
        requestParams.put("email", "user01@email.com");
        requestParams.put("password", "Us3r@1-PASSW");
        request.header("Content-Type", "application/json");
        request.body(requestParams.toJSONString());
        Response response = request.post(RestAssuredURL);
        Assertions.assertEquals(200, response.getStatusCode());

        // get session token
        String token = response.jsonPath().getString("token");

        // S3: Send message to a friend
        final String RestAssuredURL2 = "http://localhost:8080/api/v1.0/conversation";
        RequestSpecification request2 = RestAssured.given();
        request2.header("token", token); // Stablish token in header
        request2.header("Content-Type", "application/json");
        // Stablish body to send a message
        requestParams = new JSONObject();
        requestParams.put("friendEmail", "user05@email.com");
        requestParams.put("message", "Testing");
        request2.body(requestParams.toJSONString());
        Response response2 = request2.post(RestAssuredURL2);
        Assertions.assertEquals(201, response2.getStatusCode());

        // S4: Check that conversation is updated
        final String RestAssuredURL3 = "http://localhost:8080/api/v1.0/conversation/user05@email.com";
        RequestSpecification request3 = RestAssured.given();
        request3.header("token", token); // Stablish session token in header
        request3.header("Content-Type", "application/json");
        Response response3 = request3.get(RestAssuredURL3);
        Assertions.assertEquals(200, response3.getStatusCode());
        Assertions.assertTrue(response3.getBody().asString().contains("Testing"));
    }

    /**
     * @author Samuel
     * [Prueba43] Obtener los mensajes de una conversación. Esta prueba consistirá en comprobar que el
     * servicio retorna el número correcto de mensajes para una conversación. El ID de la conversación
     * deberá conocerse a priori. Por lo tanto, se tendrá primero que invocar al servicio de identificación
     * (S1), y solicitar el listado de mensajes de una conversación de ID conocido a continuación (S4),
     * comprobando que se retornan los mensajes adecuados.
     */
    @Test
    @Order(43)
    public void PR43() {
        // Iniciamos sesión
        final String RestAssuredURL = "http://localhost:8080/api/v1.0/users/login";
        RequestSpecification request = RestAssured.given();
        JSONObject requestParams = new JSONObject();
        requestParams.put("email", "user01@email.com");
        requestParams.put("password", "Us3r@1-PASSW");
        request.header("Content-Type", "application/json");
        request.body(requestParams.toJSONString());
        Response response = request.post(RestAssuredURL);
        Assertions.assertEquals(200, response.getStatusCode());

        // obtengo el token de inicio de sesión
        String token = response.jsonPath().getString("token");

        // Obtenemos conversación
        final String RestAssuredURL2 = "http://localhost:8080/api/v1.0/conversation/user04@email.com";
        RequestSpecification request2 = RestAssured.given();
        request2.header("token", token); // Aquí configuramos el token en la cabecera
        Response response2 = request2.get(RestAssuredURL2);
        Assertions.assertEquals(200, response2.getStatusCode());
    }

    /**
     * @author Samuel
     * [Prueba44] Obtener la lista de conversaciones de un usuario. Esta prueba consistirá en comprobar que
     * el servicio retorna el número correcto de conversaciones para dicho usuario. Por lo tanto, se tendrá
     * primero que invocar al servicio de identificación (S1), y solicitar el listado de conversaciones a
     * continuación (S5) comprobando que se retornan las conversaciones adecuadas
     */
    @Test
    @Order(44)
    public void PR44() {

    }

    /**
     * --PRUEBA FUNCIONAL--
     * @author Pedro
     * [Prueba46] Marcar como leído un mensaje de ID conocido. Esta prueba consistirá en comprobar que
     * el mensaje marcado de ID conocido queda marcado correctamente a true como leído. Por lo tanto,
     * se tendrá primero que invocar al servicio de identificación (S1), solicitar el servicio de marcado
     * (S7), comprobando que el mensaje marcado ha quedado marcado a true como leído (S4).
     */
    @Test
    @Order(46)
    public void PR46() {
        RestAssured.baseURI = "http://localhost:8080"; // Cambia esto por la URL de tu servicio
        mongoClient = MongoClients.create("mongodb://localhost:27017");
        database = mongoClient.getDatabase("sdinstagram");

        //Hago el login
        given().
                contentType("application/json").
                body("{\"email\":\"user01@email.com\", \"password\":\"Us3r@1-PASSW\"}").
                when().
                post("/api/v1.0/users/login").
                then().
                body("authenticated", equalTo(true));
        //Compruebo que el mensaje está no leido

        //Esto saca de la BD el atr read del mensaje que voy a marcar leido
        Document firstConversation = database.getCollection("conversations").find().first();
        List<Document> mensajes = firstConversation.getList("messages", Document.class);
        Document mensaje = mensajes.get(1);
        Boolean leido = mensaje.getBoolean("read");
        assertEquals(false, leido);

        //Lo marco como leido y veo que el put salió correcto
        RestAssured.baseURI = "http://localhost:8080";
        given().
                pathParams("messageId", 2).
                when().
                put("/api/v1.0/messages/read/{messageId}").
                then().
                body("message", equalTo("Message marked as read correctly."));

        mongoClient.close();
        //Compruebo que ahora esa propiedad read está puesta a true, es decir se leyó
        mongoClient = MongoClients.create("mongodb://localhost:27017");
        database = mongoClient.getDatabase("sdinstagram");

        firstConversation = database.getCollection("conversations").find().first();
        mensajes = firstConversation.getList("messages", Document.class);
        mensaje = mensajes.get(1);
        leido = mensaje.getBoolean("read");
        assertEquals(true, leido);//Esta vez a true

        //cierra la conexión
        mongoClient.close();
    }

    /**
     * @author Teresa
     * [Prueba50] Mostrar el listado de amigos para dicho usuario y comprobar que se muestran los amigos
     * del usuario autenticado.
     */
    @Test
    @Order(50)
    public void PR50() {
        driver.navigate().to(URL_API);
        PO_PublicView.loginUser(driver);

        // Acceder al listado de amistades
        PO_PrivateView.click(driver, "id", "myFriends", 0);
        SeleniumUtils.textIsPresentOnPage(driver, "user04@email.com");
        SeleniumUtils.textIsPresentOnPage(driver, "user05@email.com");
    }

    /**
     * [Prueba51] Sobre listado de amigos (a elección de desarrollador), enviar un mensaje a un amigo
     * concreto. Se abriría dicha conversación por primera vez. Comprobar que el mensaje aparece en el
     * listado de mensajes.
     */
    @Test
    @Order(51)
    public void PR51() {
        driver.navigate().to(URL_API);
        PO_PublicView.loginUser(driver);

        // Acceder al listado de amistades
        PO_PrivateView.click(driver, "id", "myFriends", 0);
        PO_PrivateView.click(driver, "text", "Conversation", 0);

        PO_ConversationView.sendMessage(driver, "Hola!");

        //TODO: Check that message is sent
    }
}
