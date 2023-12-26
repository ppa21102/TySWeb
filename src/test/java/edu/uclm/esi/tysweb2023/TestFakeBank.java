package edu.uclm.esi.tysweb2023;

import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.manager.SeleniumManager;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestFakeBank {
    private WebDriver driver1, driver2;
    private WebDriverWait wait1, wait2;

    @BeforeEach
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        String driverPath = SeleniumManager.getInstance().getDriverPath(options, false).getDriverPath();

        System.setProperty("webdriver.chrome.driver", driverPath);
        driver1 = new ChromeDriver(options);
        driver2 = new ChromeDriver(options);

        wait1= new WebDriverWait(driver1, Duration.ofSeconds(3));
        wait2= new WebDriverWait(driver2, Duration.ofSeconds(3));

        Point izquierda = new Point(0, 0);
        driver1.manage().window().setPosition(izquierda);

        Point derecha = new Point(600, 0);
        driver2.manage().window().setPosition(derecha);
    }

    @Test @Order(1)
    public void testEscenario1() throws Exception {
        driver1.get("https://alarcosj.esi.uclm.es/fakeBank");
        driver2.get("https://alarcosj.esi.uclm.es/fakeBank");

        this.acceder(driver1);
        this.acceder(driver2);

        this.borrarUsuario(driver1, wait1, "Pablo1");
        this.borrarUsuario(driver2, wait2, "Pablo2");

        this.registrar(driver1, wait1, "Pablo1", "pablo1@pablo1.com", "Pablo1234", "Pablo1234");
        this.registrar(driver2, wait2, "Pablo2", "pablo2@pablo2.com", "Pablo2341", "Pablo2341");

        this.login(driver1, wait1, "Pablo1", "Pablo1");
        this.login(driver2, wait2, "Pablo2", "Pablo2");

        String[] cuentaMacario1 = this.crearCuentaCorriente(driver1, wait1);
        String[] cuentaMacario2 = this.crearCuentaCorriente(driver2, wait2);

        //this.transferir(driver1, wait1, 50, cuentaMacario1, cuentaMacario2);
    }

    private String[] crearCuentaCorriente(WebDriver driver1, WebDriverWait wait1) {
        WebElement link = wait1.until(ExpectedConditions.elementToBeClickable((By.xpath("/html/body/div/oj-module/div[1]/div[2]/div/div/div[2]/div/a;"))));
        link.click();


        String dc = wait1.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div/oj-module/div[1]/div[2]/div/div/div[2]/ul/li/a/span[4]"))).getText();
        String number = wait1.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div/oj-module/div[1]/div[2]/div/div/div[2]/ul/li/a/span[5]"))).getText();

        return new String[] {dc, number};
    }

    @AfterEach
    public void tearDown() {
        driver1.quit();
        driver2.quit();
    }

    private void borrarUsuario(WebDriver driver, WebDriverWait wait, String nombre) {
        driver.get("https://alarcosj.esi.uclm.es/fakeBank");

        WebElement cajaNombre = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div/oj-module/div[1]/div[2]/div/div/div/div[1]/div[1]/input")));
        cajaNombre.click(); cajaNombre.clear(); cajaNombre.sendKeys(nombre);

        WebElement cajaPwd = driver.findElement(By.xpath("/html/body/div/oj-module/div[1]/div[2]/div/div/div/div[1]/div[2]/input"));
        cajaPwd.click();

        WebElement linkBorrar = driver.findElement(By.xpath("/html/body/div/oj-module/div[1]/div[2]/div/div/div/div[4]/div/a"));
        linkBorrar.click();
    }

    private void login(WebDriver driver, WebDriverWait wait, String nombre, String pwd) {
        WebElement link = wait.until(ExpectedConditions.elementToBeClickable((By.xpath("/html/body/div/div[2]/div/oj-navigation-list/div/div/ul/li[1]/a"))));
        link.click();

        WebElement cajaNombre = driver.findElement(By.xpath("/html/body/div/oj-module/div[1]/div[2]/div/div/div/div[1]/div[1]/input"));
        WebElement cajaPwd = driver.findElement(By.xpath("/html/body/div/oj-module/div[1]/div[2]/div/div/div/div[1]/div[2]/input"));

        cajaNombre.click(); cajaNombre.clear(); cajaNombre.sendKeys(nombre);
        cajaPwd.click(); cajaPwd.clear(); cajaPwd.sendKeys(pwd);

        WebElement boton = driver.findElement(By.xpath("/html/body/div/oj-module/div[1]/div[2]/div/div/div/div[1]/div[3]/button"));
        boton.click();
    }

    private void acceder(WebDriver driver) {
        WebElement linkConfiguracionAvanzada = driver.findElement(By.xpath("/html/body/div/div[2]/button[3]"));
        linkConfiguracionAvanzada.click();

        WebElement linkAcceder = driver.findElement(By.xpath("/html/body/div/div[3]/p[2]/a"));
        linkAcceder.click();
    }

    private void registrar(WebDriver driver, WebDriverWait wait, String nombre, String email, String pwd1, String pwd2) {
        WebElement link = driver.findElement(By.xpath("/html/body/div/oj-module/div[1]/div[2]/div/div/div/div[3]/div/a"));
        link.click();

        WebElement cajaNombre = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div/oj-module/div[1]/div[2]/div/div/div/input[1]")));
        WebElement cajaEmail = driver.findElement(By.xpath("/html/body/div/oj-module/div[1]/div[2]/div/div/div/input[2]"));
        WebElement cajaPwd1 = driver.findElement(By.xpath("/html/body/div/oj-module/div[1]/div[2]/div/div/div/div[1]/input[1]"));
        WebElement cajaPwd2 = driver.findElement(By.xpath("/html/body/div/oj-module/div[1]/div[2]/div/div/div/div[1]/input[2]"));
        WebElement boton = driver.findElement(By.xpath("/html/body/div/oj-module/div[1]/div[2]/div/div/div/button[2]"));

        cajaNombre.click(); cajaNombre.clear(); cajaNombre.sendKeys(nombre);
        cajaEmail.click(); cajaEmail.clear(); cajaEmail.sendKeys(email);
        cajaPwd1.click(); cajaPwd1.clear(); cajaPwd1.sendKeys(pwd1);
        cajaPwd2.click(); cajaPwd2.clear(); cajaPwd2.sendKeys(pwd2);
        boton.click();
    }
}