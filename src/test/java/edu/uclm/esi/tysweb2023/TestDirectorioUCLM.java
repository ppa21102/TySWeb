package edu.uclm.esi.tysweb2023;


import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.openqa.selenium.WebDriver;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

public class TestDirectorioUCLM {

    private WebDriver edge;

    @BeforeAll
    public void setUp() {
        System.setProperty("webdriver.edge.driver", "C:\\Users\\pablo\\Downloads\\edgedriver_win64\\msedgedriver.exe");
        EdgeOptions options = new EdgeOptions();
        options.addArguments("--remote-allow-origins=*");
        edge = new EdgeDriver(options);
    }

    @Test
    @Order(1)
    public void buscarAlRector() {
        edge.get("https://directorio.uclm.es/");
        WebElement caja = edge.findElement(By.id("CPH_CajaCentro_tb_busqueda"));
        caja.click();
        caja.sendKeys("garde");
        WebElement boton = edge.findElement(By.xpath("/html/body/form/div[3]/div[2]/div[2]/div/div[1]/div[2]/div[2]/a"));
        boton.click();
        WebElement tabla = edge.findElement(By.id("CPH_CajaCentro_gv_personas"));
        WebElement tBody = tabla.findElement(By.tagName("tbody"));
        List<WebElement> ttrr = tBody.findElements(By.tagName("tr"));
        assertTrue(ttrr.size() == 2);
    }

    @AfterEach
    public void tearDown() {
        edge.quit();
    }
}