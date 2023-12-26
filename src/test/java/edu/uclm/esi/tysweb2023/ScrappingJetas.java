package edu.uclm.esi.tysweb2023;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.manager.SeleniumManager;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class ScrappingJetas {
    private WebDriver driver1;
    private WebDriverWait wait1;

    @BeforeEach
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        String driverPath = SeleniumManager.getInstance().getDriverPath(options, false).getDriverPath();

        System.setProperty("webdriver.chrome.driver", driverPath);
        driver1 = new ChromeDriver(options);

        wait1 = new WebDriverWait(driver1, Duration.ofSeconds(3));

        Point izquierda = new Point(0, 0);
        driver1.manage().window().setPosition(izquierda);
    }

    @Test
    @Order(1)
    public void testEscenario1() throws Exception {
        Actions actions = new Actions(driver1);
        for (int i = 0; i < 100; i++) {
            driver1.get("https://thispersondoesnotexist.com/");
            WebElement img = driver1.findElement(By.xpath("/html/body/img"));
            actions.contextClick(img);
            actions.sendKeys(Keys.DOWN);
            actions.sendKeys(Keys.DOWN);
            actions.perform();
        }
    }

    private String[] crearCuentaCorriente(WebDriver driver1, WebDriverWait wait1) {
        WebElement link = wait1.until(ExpectedConditions.elementToBeClickable((By.xpath("/html/body/div/oj-module/div[1]/div[2]/div/div/div[2]/div/a;"))));
        link.click();


        String dc = wait1.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div/oj-module/div[1]/div[2]/div/div/div[2]/ul/li/a/span[4]"))).getText();
        String number = wait1.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div/oj-module/div[1]/div[2]/div/div/div[2]/ul/li/a/span[5]"))).getText();

        return new String[]{dc, number};
    }

    @AfterEach
    public void tearDown() {
        driver1.quit();
    }

    private void borrarUsuario(WebDriver driver, WebDriverWait wait, String nombre) {
        driver.get("https://alarcosj.esi.uclm.es/fakeBank");

        WebElement cajaNombre = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div/oj-module/div[1]/div[2]/div/div/div/div[1]/div[1]/input")));
        cajaNombre.click();
        cajaNombre.clear();
        cajaNombre.sendKeys(nombre);

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

        cajaNombre.click();
        cajaNombre.clear();
        cajaNombre.sendKeys(nombre);
        cajaPwd.click();
        cajaPwd.clear();
        cajaPwd.sendKeys(pwd);

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

        cajaNombre.click();
        cajaNombre.clear();
        cajaNombre.sendKeys(nombre);
        cajaEmail.click();
        cajaEmail.clear();
        cajaEmail.sendKeys(email);
        cajaPwd1.click();
        cajaPwd1.clear();
        cajaPwd1.sendKeys(pwd1);
        cajaPwd2.click();
        cajaPwd2.clear();
        cajaPwd2.sendKeys(pwd2);
        boton.click();
    }
}