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
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

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

    	// Comprobar quién empieza
        WebElement turnoJ1 = driver1.findElement(By.xpath("/html/body/app-root/app-flota/div/div[2]/div/p[1]"));
        WebElement turnoJ2 = driver2.findElement(By.xpath("/html/body/app-root/app-flota/div/div[2]/div/p[1]"));
        String textoTurnoJ1 = turnoJ1.getText();
        String textoTurnoJ2 = turnoJ2.getText();

        // Realizar movimientos alternados en diferentes casillas
        if (textoTurnoJ1.equals("¡Es tu turno!")) {
            realizarMovimientos(driver1, driver2);
        } else if (textoTurnoJ2.equals("¡Es tu turno!")) {
            realizarMovimientos(driver2, driver1);
        } else {
            throw new IllegalStateException("No se detectó el turno de ningún jugador.");
        }
        
        Thread.sleep(1000);

        WebElement finPartidaElement1 = driver1.findElement(By.xpath("/html/body/app-root/app-flota/div/div[2]/div[2]"));
        WebElement finPartidaElement2 = driver2.findElement(By.xpath("/html/body/app-root/app-flota/div/div[2]/div[2]"));

        // Verificar si el elemento fin-partida está presente
        boolean elementoPresente1 = finPartidaElement1.isDisplayed();
        boolean elementoPresente2 = finPartidaElement2.isDisplayed();

        // Verificar si ambos jugadores ven el mensaje de fin de partida
        assertEquals(true, elementoPresente1, "El jugador 1 ve el mensaje de fin de partida");
        assertEquals(true, elementoPresente2, "El jugador 2 ve el mensaje de fin de partida");

    }

    private void realizarMovimientos(WebDriver jugadorActual, WebDriver otroJugador) {
        Random rand = new Random();
        Set<String> posicionesClickeadas = new HashSet<>();

        for (int i = 0; i < 10; i++) {
            int filaActual, columnaActual;
            String posicionActual;
            do {
                filaActual = rand.nextInt(10) + 1;
                columnaActual = rand.nextInt(10) + 1;
                posicionActual = filaActual + "," + columnaActual;
            } while (posicionesClickeadas.contains(posicionActual));
            posicionesClickeadas.add(posicionActual);

            WebElement turnoActual = jugadorActual.findElement(By.xpath("/html/body/app-root/app-flota/div/div[2]/div/div[" + columnaActual + "]/div[" + filaActual + "]"));
            turnoActual.click();

            int filaOtro, columnaOtro;
            String posicionOtro;
            do {
                filaOtro = rand.nextInt(10) + 1;
                columnaOtro = rand.nextInt(10) + 1;
                posicionOtro = filaOtro + "," + columnaOtro;
            } while (posicionesClickeadas.contains(posicionOtro));
            posicionesClickeadas.add(posicionOtro);

            WebElement turnoOtroJugador = otroJugador.findElement(By.xpath("/html/body/app-root/app-flota/div/div[2]/div/div[" + columnaOtro + "]/div[" + filaOtro + "]"));
            turnoOtroJugador.click();
        }
        
        int filaFinal, columnaFinal;
        String posicionFinal;
        do {
            filaFinal = rand.nextInt(10) + 1;
            columnaFinal = rand.nextInt(10) + 1;
            posicionFinal = filaFinal + "," + columnaFinal;
        } while (posicionesClickeadas.contains(posicionFinal));
        posicionesClickeadas.add(posicionFinal);

        WebElement turnoFinal = jugadorActual.findElement(By.xpath("/html/body/app-root/app-flota/div/div[2]/div/div[" + columnaFinal + "]/div[" + filaFinal + "]"));
        turnoFinal.click();
    }

    @AfterEach
    public void tearDown() {
        if (driver1 != null) {
            driver1.quit();
        }
        if (driver2 != null) {
            driver2.quit();
        }
    }
}
