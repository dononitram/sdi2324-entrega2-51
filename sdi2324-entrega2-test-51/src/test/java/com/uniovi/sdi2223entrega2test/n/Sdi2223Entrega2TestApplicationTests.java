package com.uniovi.sdi2223entrega2test.n;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.uniovi.sdi2223entrega2test.n.pageobjects.PO_PrivateView;
import com.uniovi.sdi2223entrega2test.n.pageobjects.PO_PublicView;
import com.uniovi.sdi2223entrega2test.n.pageobjects.PO_View;
import com.uniovi.sdi2223entrega2test.n.util.SeleniumUtils;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
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
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class Sdi2223Entrega2TestApplicationTests {
    static String PathFirefox = "C:\\Program Files\\Mozilla Firefox\\firefox.exe";
    static String Geckodriver = "C:\\Dev\\tools\\selenium\\geckodriver-v0.30.0-win64.exe";
    static WebDriver driver = getDriver(PathFirefox, Geckodriver);
    static String URL = "http://localhost:8080/users/login";

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
        Document user1 = new Document("email", "user01@email.com")
                .append("password", hashPassword("Us3r@1-PASSW", "abcdefg"))
                .append("name", "User01")
                .append("surname", "Surname01")
                .append("birthdate", "2024-04-30")
                .append("role", "user");
        Document user2 = new Document("email", "user02@email.com")
                .append("password", hashPassword("Us3r@2-PASSW", "abcdefg"))
                .append("name", "User02")
                .append("surname", "Surname02")
                .append("birthdate","2024-04-30")
                .append("role", "user");
        Document user3 = new Document("email", "user03@email.com")
                .append("password", hashPassword("Us3r@3-PASSW", "abcdefg"))
                .append("name", "User03")
                .append("surname", "Surname03")
                .append("birthdate","2024-04-30")
                .append("role", "user");
        Document user4 = new Document("email", "user04@email.com")
                .append("password", hashPassword("Us3r@4-PASSW", "abcdefg"))
                .append("name", "User04")
                .append("surname", "Surname04")
                .append("birthdate","2024-04-30")
                .append("role", "user");
        Document user5 = new Document("email", "user05@email.com")
                .append("password", hashPassword("Us3r@5-PASSW", "abcdefg"))
                .append("name", "User05")
                .append("surname", "Surname05")
                .append("birthdate","2024-04-30")
                .append("role", "user");
        Document user6 = new Document("email", "user06@email.com")
                .append("password", hashPassword("Us3r@6-PASSW", "abcdefg"))
                .append("name", "User06")
                .append("surname", "Surname06")
                .append("birthdate","2024-04-30")
                .append("role", "user");
        Document user7 = new Document("email", "user07@email.com")
                .append("password", hashPassword("Us3r@7-PASSW", "abcdefg"))
                .append("name", "User07")
                .append("surname", "Surname07")
                .append("birthdate","2024-04-30")
                .append("role", "user");
        Document user8 = new Document("email", "user08@email.com")
                .append("password", hashPassword("Us3r@8-PASSW", "abcdefg"))
                .append("name", "User08")
                .append("surname", "Surname08")
                .append("birthdate","2024-04-30")
                .append("role", "user");
        Document user9 = new Document("email", "user09@email.com")
                .append("password", hashPassword("Us3r@9-PASSW", "abcdefg"))
                .append("name", "User09")
                .append("surname", "Surname09")
                .append("birthdate","2024-04-30")
                .append("role", "user");
        Document user10 = new Document("email", "user10@email.com")
                .append("password", hashPassword("Us3r@10-PASSW", "abcdefg"))
                .append("name", "User10")
                .append("surname", "Surname10")
                .append("birthdate","2024-04-30")
                .append("role", "user");
        Document user11 = new Document("email", "user11@email.com")
                .append("password", hashPassword("Us3r@11-PASSW", "abcdefg"))
                .append("name", "User11")
                .append("surname", "Surname11")
                .append("birthdate","2024-04-30")
                .append("role", "user");
        Document user12 = new Document("email", "user12@email.com")
                .append("password", hashPassword("Us3r@12-PASSW", "abcdefg"))
                .append("name", "User12")
                .append("surname", "Surname12")
                .append("birthdate","2024-04-30")
                .append("role", "user");
        Document user13 = new Document("email", "user13@email.com")
                .append("password", hashPassword("Us3r@13-PASSW", "abcdefg"))
                .append("name", "User13")
                .append("surname", "Surname13")
                .append("birthdate","2024-04-30")
                .append("role", "user");
        Document user14 = new Document("email", "user14@email.com")
                .append("password", hashPassword("Us3r@14-PASSW", "abcdefg"))
                .append("name", "User14")
                .append("surname", "Surname14")
                .append("birthdate","2024-04-30")
                .append("role", "user");
        Document user15 = new Document("email", "user15@email.com")
                .append("password", hashPassword("Us3r@15-PASSW", "abcdefg"))
                .append("name", "User15")
                .append("surname", "Surname15")
                .append("birthdate","2024-04-30")
                .append("role", "user");


        database.getCollection("users").insertOne(user1);
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

        //publicaciones
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
        Assertions.assertTrue(false, "PR02 sin hacer");
    }

    @Test
    @Order(3)
    public void PR03() {
        Assertions.assertTrue(false, "PR03 sin hacer");
    }

    @Test
    @Order(4)
    public void PR04() {
        Assertions.assertTrue(false, "PR04 sin hacer");
    }

    @Test
    @Order(5)
    public void PR05() {
        Assertions.assertTrue(false, "PR05 sin hacer");
    }

    @Test
    @Order(6)
    public void PR06() {
        Assertions.assertTrue(false, "PR06 sin hacer");
    }

    @Test
    @Order(7)
    public void PR07() {
        Assertions.assertTrue(false, "PR07 sin hacer");
    }

    @Test
    @Order(8)
    public void PR08() {
        Assertions.assertTrue(false, "PR08 sin hacer");
    }

    @Test
    @Order(9)
    public void PR09() {
        Assertions.assertTrue(false, "PR09 sin hacer");
    }

    @Test
    @Order(10)
    public void PR10() {
        Assertions.assertTrue(false, "PR10 sin hacer");
    }


    /* Ejemplos de pruebas de llamada a una API-REST */
    /* ---- Probamos a obtener lista de canciones sin token ---- */
    @Test
    @Order(11)
    public void PR11() {
        final String RestAssuredURL = "http://localhost:8081/api/v1.0/songs";
        Response response = RestAssured.get(RestAssuredURL);
        Assertions.assertEquals(403, response.getStatusCode());
    }

    /**
     * @author Pedro
     * [Prueba22] Desde el listado de usuarios de la aplicación, enviar una invitación de amistad a un usuario.
     * Comprobar que la solicitud de amistad aparece en el listado de invitaciones (punto siguiente).
     */
    @Test
    @Order(22)
    void PR22() {
        //Inicio sesión como el usuario2
        PO_PublicView.loginSpecificUser("user02@email.com","Us3r@2-PASSW",driver);
        //Vamos a la lista de usuarios donde se puede solicitar amistad
        driver.findElement(By.id("mylistUsersSocial")).click();
        //Envío solicitud al usuario 3
        WebElement botonEnviarSolicitud = driver.findElement(By.id("btn_user02@email.com"));
        botonEnviarSolicitud.click();
        // Cerrar sesión e inicio con el usuario 3
        PO_PrivateView.logout(driver);
        PO_PublicView.loginSpecificUser("user03@email.com","Us3r@3-PASSW",driver);
        //Voy a la ventana de ver solicitudes de amistad recibidas friendshipRequests
        driver.findElement(By.id("myRequests")).click();
        //Y comprobamos que llegó la solicitud de amistad  email_user02@email.com
        SeleniumUtils.textIsPresentOnPage(driver,"btn_user02@email.com");
        //Finalmente logeamos-cerramos sesión
        PO_PrivateView.logout(driver);
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
        //Vamos a la lista de usuarios donde se puede solicitar amistad
        driver.findElement(By.id("mylistUsersSocial")).click();
        //Y pruebo a enviarle 2 veces la solicitud de amistad al usuario 2
        WebElement botonEnviarSolicitud = driver.findElement(By.id("btn_user02@email.com"));
        botonEnviarSolicitud.click();
        botonEnviarSolicitud.click();
        SeleniumUtils.textIsPresentOnPage(driver, "You have already sent a friend request to this user.");
        // Cerrar sesión
        PO_PrivateView.logout(driver);
    }

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
}
