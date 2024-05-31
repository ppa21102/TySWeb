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
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.Alert;
import java.util.concurrent.TimeUnit;

import java.lang.ref.WeakReference;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CTest4Raya {

	private WebDriver driver1;
	private WebDriverWait wait1;

	@BeforeEach
	public void setUp() {
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--remote-allow-origins=*");

        String driverPath = "C:\\Users\\Raúl\\Downloads\\chromedriver-win64\\chromedriver-win64\\chromedriver.exe";
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

	public void testLogin(String correo, String pwd) {
		WebElement inputmail = driver1.findElement(By.xpath("/html/body/app-root/app-login/div/form/input[1]"));
		inputmail.sendKeys(correo);

		WebElement inputpwd = driver1.findElement(By.xpath("/html/body/app-root/app-login/div/form/input[2]"));
		inputpwd.sendKeys(pwd);

		inputmail.clear();
		inputpwd.clear();

        inputmail.sendKeys("juan@juan.com");
        inputpwd.sendKeys("juan1234");

		WebElement loginbutton = driver1.findElement(By.xpath("/html/body/app-root/app-login/div/form/button"));
		loginbutton.click();

		WebDriverWait wait = new WebDriverWait(driver1, Duration.ofSeconds(10));
		WebElement mensaje = wait.until(ExpectedConditions
				.visibilityOfElementLocated(By.xpath("/html/body/app-root/app-header/div/div[3]/div/p")));

		String actualMensaje = mensaje.getText();
	}

	@ParameterizedTest
	@CsvSource(delimiter = '\t', nullValues = "NULL", value = {
			"juan@juan.com\tjuan1234\t4242 4242 4242 4242\t0524\t000\t45270\t¡Es tu turno!" })
	public void testjuegoRaya(String correo, String pwd, String tarjeta, String fecha, String CVC, String CP,
			String mensajeEsperadoEstadisticas) throws InterruptedException {
		// Login
		testLogin(correo, pwd); // Asumiendo que login() es un método que realiza el inicio de sesión

		// Jugar a 4 en raya
		WebElement link4raya = driver1.findElement(By.xpath("/html/body/app-root/app-home/div/div[1]/div/button"));
		link4raya.click();

		// Esperar a que la URL cambie a la página de estadísticas
		new WebDriverWait(driver1, Duration.ofSeconds(10))
				.until(ExpectedConditions.urlToBe("http://localhost:4200/payments"));

		WebElement linkpago = driver1.findElement(By.xpath("/html/body/app-root/app-payments/div/div/button"));
		linkpago.click();

		Thread.sleep(2000);
		
		WebElement iframeTarjeta = driver1.findElement(By.xpath("/html/body/app-root/app-payments/div/div[2]/form/div[1]/div/iframe"));
		driver1.switchTo().frame(iframeTarjeta);

		// Localizar el campo de número de tarjeta y enviar las teclas
		WebElement inputTarjeta = driver1.findElement(By.name("cardnumber"));
		Actions actions = new Actions(driver1);
		actions.moveToElement(inputTarjeta).click().sendKeys("4242 4242 4242 4242").perform();

		WebElement inputfecha = driver1
				.findElement(By.xpath("/html/body/div[1]/form/div/div[2]/span[2]/span[1]/span/span/input"));
		inputfecha.sendKeys(fecha);
		inputfecha.clear();
		inputfecha.sendKeys("0524");

		WebElement inputCVC = driver1
				.findElement(By.xpath("/html/body/div[1]/form/div/div[2]/span[2]/span[2]/span/span/input"));
		inputCVC.sendKeys(CVC);
		inputCVC.clear();
		inputCVC.sendKeys("000");

		WebElement inputCP = driver1
				.findElement(By.xpath("/html/body/div[1]/form/div/div[2]/span[2]/span[3]/span/span/input"));
		inputCP.sendKeys(CP);
		inputCP.clear();
		inputCP.sendKeys("45270");

        driver1.switchTo().defaultContent();

		WebElement buttonpago = driver1
				.findElement(By.xpath("/html/body/app-root/app-payments/div/div[2]/form/button"));
		buttonpago.click();

		Thread.sleep(2000);
		driver1.switchTo().alert().accept();
		Thread.sleep(2000);

		// Esperar a que un elemento específico de la nueva página esté visible
		WebElement mensajeStats = new WebDriverWait(driver1, Duration.ofSeconds(12)).until(
				ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/app-root/app-raya/div/div[2]/div/p")));

		String actualMensajeStats = mensajeStats.getText();
		assertEquals(mensajeEsperadoEstadisticas, actualMensajeStats, "El mensaje de 4 raya no es el esperado");
	}

}