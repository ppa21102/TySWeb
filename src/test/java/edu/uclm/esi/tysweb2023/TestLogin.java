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

public class TestLogin {
    private WebDriver driver1;
    private WebDriverWait wait1;

    @BeforeEach
    public void setUp(){
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");


        String driverPath = "C:\\Users\\Victo\\Downloads\\chromedriver-win64\\chromedriver-win64\\chromedriver.exe";
        System.setProperty("webdriver.chrome.driver", driverPath);

        driver1 = new ChromeDriver(options);
        wait1 = new WebDriverWait(driver1, Duration.ofSeconds(3));
        Point izquierda = new Point(0, 0);
        driver1.manage().window().setPosition(izquierda);
        driver1.get("http://localhost:4200/Login"); // URL de la página de registro
    }

    @AfterEach
    public void tearDown() {
        if (driver1 != null) {
            driver1.quit();
        }
    }

    @ParameterizedTest
    @CsvSource(delimiter = '\t', nullValues = "NULL", value = {
            "victor@victor.com\tVictor14\tBienvenido Victor"
    })
    public void testLogin(String correo, String pwd, String mensajeEsperado){
        WebElement inputmail = driver1.findElement(By.xpath("/html/body/app-root/app-login/div/form/input[1]"));
        inputmail.sendKeys(correo);

        WebElement inputpwd = driver1.findElement(By.xpath("/html/body/app-root/app-login/div/form/input[2]"));
        inputpwd.sendKeys(pwd);


        inputmail.clear();
        inputpwd.clear();

        inputmail.sendKeys("victor@victor.com");
        inputpwd.sendKeys("Victor14");

        WebElement loginbutton = driver1.findElement(By.xpath("/html/body/app-root/app-login/div/form/button"));
        loginbutton.click();

        WebDriverWait wait = new WebDriverWait(driver1, Duration.ofSeconds(10));
        WebElement mensaje = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/app-root/app-header/div/div[3]/div/p")));

        String actualMensaje = mensaje.getText();
        assertEquals(mensajeEsperado, actualMensaje, "El mensaje no es el esperado");
    }

}
