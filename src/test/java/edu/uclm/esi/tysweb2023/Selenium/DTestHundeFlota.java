package edu.uclm.esi.tysweb2023.Selenium;

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

public class DTestHundeFlota {

    private WebDriver driver1;
    private WebDriver driver2;
    private WebDriverWait wait1;
    private WebDriverWait wait2;

    @BeforeEach
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        String driverPath = "C:\\Users\\Raúl\\Downloads\\chromedriver-win64\\chromedriver-win64\\chromedriver.exe";
        System.setProperty("webdriver.chrome.driver", driverPath);

        driver1 = new ChromeDriver(options);
        wait1 = new WebDriverWait(driver1, Duration.ofSeconds(10));
        driver1.manage().window().setPosition(new Point(0, 0));
        driver1.get("http://localhost:4200/Home");

        driver2 = new ChromeDriver(options);
        wait2 = new WebDriverWait(driver2, Duration.ofSeconds(10));
        driver2.manage().window().setPosition(new Point(500, 0));
        driver2.get("http://localhost:4200/Home");
    }

    @ParameterizedTest
    @CsvSource(delimiter = '\t', nullValues = "NULL", value = {
            "Volver a la home"
    })
    public void testJuegoHundeFlota(String mensajeEsperadoFlota) throws InterruptedException {
        // Inicio del juego para el jugador 2
        WebElement linkHundeFlota2 = driver2.findElement(By.xpath("/html/body/app-root/app-home/div/div[2]/div/button"));
        linkHundeFlota2.click();
        wait2.until(ExpectedConditions.urlToBe("http://localhost:4200/Flota"));

        // Espera explícita para permitir la conexión del jugador 1 antes de que el jugador 2 inicie sesión
        Thread.sleep(5000);  // Ajusta este tiempo según sea necesario

        WebElement linkHundeFlota = driver1.findElement(By.xpath("/html/body/app-root/app-home/div/div[2]/div/button"));
        linkHundeFlota.click();
        wait1.until(ExpectedConditions.urlToBe("http://localhost:4200/Flota"));

        // Esperar a que ambos jugadores estén en el juego
        WebElement mensajeAbandono = wait1.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/app-root/app-flota/div/div[2]/div/button")));
        WebElement mensajeAbandono2 = wait2.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/app-root/app-flota/div/div[2]/div/button")));


        WebElement turno1J2 = driver2.findElement(By.xpath("/html/body/app-root/app-flota/div/div[2]/div/div[3]/div[10]"));
        turno1J2.click();

        WebElement turno1J1 = driver1.findElement(By.xpath("/html/body/app-root/app-flota/div/div[2]/div/div[3]/div[4]"));
        turno1J1.click();



        WebElement turno2J1 = driver1.findElement(By.xpath("/html/body/app-root/app-flota/div/div[2]/div/div[7]/div[1]"));
        turno2J1.click();

        WebElement turno2J2 = driver2.findElement(By.xpath("/html/body/app-root/app-flota/div/div[2]/div/div[8]/div[6]"));
        turno2J2.click();

        WebElement turno3J1 = driver1.findElement(By.xpath("/html/body/app-root/app-flota/div/div[2]/div/div[9]/div[4]"));
        turno3J1.click();

        WebElement turno3J2 = driver2.findElement(By.xpath("/html/body/app-root/app-flota/div/div[2]/div/div[9]/div[9]"));
        turno3J2.click();

        WebElement turno4J1 = driver1.findElement(By.xpath("/html/body/app-root/app-flota/div/div[2]/div/div[10]/div[3]"));
        turno4J1.click();

        WebElement turno4J2 = driver2.findElement(By.xpath("/html/body/app-root/app-flota/div/div[2]/div/div[8]/div[1]"));
        turno4J2.click();

        WebElement turno5J1 = driver1.findElement(By.xpath("/html/body/app-root/app-flota/div/div[2]/div/div[5]/div[4]"));
        turno5J1.click();

        WebElement turno5J2 = driver2.findElement(By.xpath("/html/body/app-root/app-flota/div/div[2]/div/div[4]/div[8]"));
        turno5J2.click();

        WebElement turno6J1 = driver1.findElement(By.xpath("/html/body/app-root/app-flota/div/div[2]/div/div[2]/div[2]"));
        turno6J1.click();

        WebElement turno6J2 = driver2.findElement(By.xpath("/html/body/app-root/app-flota/div/div[2]/div/div[1]/div[9]"));
        turno6J2.click();

        WebElement turno7J1 = driver1.findElement(By.xpath("/html/body/app-root/app-flota/div/div[2]/div/div[6]/div[5]"));
        turno7J1.click();

        WebElement turno7J2 = driver2.findElement(By.xpath("/html/body/app-root/app-flota/div/div[2]/div/div[10]/div[5]"));
        turno7J2.click();

        WebElement turno8J1 = driver1.findElement(By.xpath("/html/body/app-root/app-flota/div/div[2]/div/div[2]/div[7]"));
        turno8J1.click();

        WebElement turno8J2 = driver2.findElement(By.xpath("/html/body/app-root/app-flota/div/div[2]/div/div[5]/div[7]"));
        turno8J2.click();

        WebElement turno9J1 = driver1.findElement(By.xpath("/html/body/app-root/app-flota/div/div[2]/div/div[1]/div[6]"));
        turno9J1.click();

        WebElement turno9J2 = driver2.findElement(By.xpath("/html/body/app-root/app-flota/div/div[2]/div/div[2]/div[2]"));
        turno9J2.click();

        WebElement turno10J1 = driver1.findElement(By.xpath("/html/body/app-root/app-flota/div/div[2]/div/div[3]/div[3]"));
        turno10J1.click();

        WebElement turno10J2 = driver2.findElement(By.xpath("/html/body/app-root/app-flota/div/div[2]/div/div[4]/div[1]"));
        turno10J2.click();

        Thread.sleep(6000);

        WebElement iframeFinal1 = driver1.findElement(By.xpath("/html/body/app-root/app-flota/div/div[2]/div[2]"));
        driver1.switchTo().frame(iframeFinal1);


        WebElement iframeFinal2 = driver1.findElement(By.xpath("/html/body/app-root/app-flota/div/div[2]/div[2]"));
        driver1.switchTo().frame(iframeFinal2);
        WebElement home = driver1.findElement(By.xpath("/html/body/app-root/app-flota/div/div[2]/div[2]/div/button"));
        WebElement home2 = driver2.findElement(By.xpath("/html/body/app-root/app-flota/div/div[2]/div[2]/div/button"));


        //String actualMensajeStats1 = mensajeAbandono1.getText();
        //String actualMensajeStats2 = mensajeAbandono2.getText();

        assertEquals(mensajeEsperadoFlota, mensajeAbandono, "Volver a la home");
        assertEquals(mensajeEsperadoFlota, mensajeAbandono2, "Volver a la home");
  }
}