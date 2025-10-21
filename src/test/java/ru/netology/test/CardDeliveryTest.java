package ru.netology.test;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.*;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class CardDeliveryTest {

    @BeforeAll
    static void setUpAll() {
        // Настройки для CI
        Configuration.browser = "chrome";
        Configuration.headless = true;
        Configuration.browserSize = "1280x800";
        Configuration.timeout = 10000;

        // Критически важные настройки для Chrome в CI
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--headless");
        options.addArguments("--window-size=1280,800");
        options.addArguments("--disable-gpu");
        options.addArguments("--remote-allow-origins=*");

        Configuration.browserCapabilities = options;
    }

    @BeforeEach
    void setUp() {
        // Добавляем задержку перед открытием страницы
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        open("http://localhost:9999");
    }

    @AfterEach
    void tearDown() {
        Selenide.closeWebDriver();
    }

    @Test
    void shouldSubmitValidDeliveryForm() {
        // Заполняем форму с небольшими задержками
        $("[data-test-id=city] input").setValue("Москва");
        sleep(500);

        $("[data-test-id=date] input").doubleClick().sendKeys(getFutureDate(3));
        sleep(500);

        $("[data-test-id=name] input").setValue("Иван Иванов");
        sleep(500);

        $("[data-test-id=phone] input").setValue("+79270000000");
        sleep(500);

        $("[data-test-id=agreement]").click();
        sleep(500);

        $("button.button").click();

        // Увеличиваем время ожидания для CI
        $("[data-test-id=notification]")
                .shouldBe(visible, Duration.ofSeconds(20))
                .shouldHave(text("Успешно!"));
    }

    @Test
    void shouldSubmitWithMinimumDays() {
        $("[data-test-id=city] input").setValue("Санкт-Петербург");
        $("[data-test-id=date] input").doubleClick().sendKeys(getFutureDate(5));
        $("[data-test-id=name] input").setValue("Анна Петрова");
        $("[data-test-id=phone] input").setValue("+79271111111");
        $("[data-test-id=agreement]").click();
        $("button.button").click();

        $("[data-test-id=notification]")
                .shouldBe(visible, Duration.ofSeconds(20))
                .shouldHave(text("Успешно!"));
    }

    @Test
    void shouldSubmitWithHyphenInName() {
        $("[data-test-id=city] input").setValue("Казань");
        $("[data-test-id=date] input").doubleClick().sendKeys(getFutureDate(7));
        $("[data-test-id=name] input").setValue("Анна-Мария Сидорова");
        $("[data-test-id=phone] input").setValue("+79272222222");
        $("[data-test-id=agreement]").click();
        $("button.button").click();

        $("[data-test-id=notification]")
                .shouldBe(visible, Duration.ofSeconds(20))
                .shouldHave(text("Успешно!"));
    }

    private String getFutureDate(int days) {
        return LocalDate.now().plusDays(days).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}