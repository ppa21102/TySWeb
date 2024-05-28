package edu.uclm.esi.tysweb2023;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestRegister {
    private WebDriver driver1;
    private WebDriverWait wait1;


    @BeforeEach
    public void setUp() {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--remote-allow-origins=*");

            // Asegúrate de que la ruta al ChromeDriver es correcta
            String driverPath = "C:\\Users\\Victo\\Downloads\\chromedriver-win64\\chromedriver-win64\\chromedriver.exe"; // Cambia esto por tu ruta real al ChromeDriver
            System.setProperty("webdriver.chrome.driver", driverPath);

            driver1 = new ChromeDriver(options);
            wait1 = new WebDriverWait(driver1, Duration.ofSeconds(3));
            Point izquierda = new Point(0, 0);
            driver1.manage().window().setPosition(izquierda);
            driver1.get("http://localhost:4200/Registro"); // URL de la página de registro
    }

    @AfterEach
    public void tearDown() {
        if (driver1 != null) {
            driver1.quit();
        }
    }

    @ParameterizedTest
    @CsvSource(delimiter = '\t', nullValues = "NULL", value = {
            "juanito\tjuan@juan.com\tjuan1234\tjuan1234\tUsuario creado correctamente."
    })
    public void testRegistro(String nombre, String email, String pwd1, String pwd2, String mensajeEsperado) {
        WebElement inputName = driver1.findElement(By.xpath("/html/body/app-root/app-register/div/form/input[1]"));
        inputName.sendKeys(nombre);

        WebElement inputEmail = driver1.findElement(By.xpath("/html/body/app-root/app-register/div/form/input[2]"));
        inputEmail.sendKeys(email);

        WebElement inputPwd1 = driver1.findElement(By.xpath("/html/body/app-root/app-register/div/form/input[3]"));
        inputPwd1.sendKeys(pwd1);

        WebElement inputPwd2 = driver1.findElement(By.xpath("/html/body/app-root/app-register/div/form/input[4]"));
        inputPwd1.sendKeys(pwd2);



        inputName.clear();
        inputEmail.clear();
        inputPwd1.clear();

        inputName.sendKeys("juanito");
        inputEmail.sendKeys("juan@juan.com");
        inputPwd1.sendKeys("juan1234");
        inputPwd2.sendKeys("juan1234");

        WebElement registerButton = driver1.findElement(By.xpath("/html/body/app-root/app-register/div/form/button"));
        registerButton.click();

        // Usar WebDriverWait para asegurarse de que el mensaje es visible
        WebDriverWait wait = new WebDriverWait(driver1, Duration.ofSeconds(10));
        WebElement mensaje = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/app-root/app-register/div/div/p")));
        String actualMensaje = mensaje.getText();

        assertEquals(mensajeEsperado, actualMensaje, "El mensaje no es el esperado");
    }

}
