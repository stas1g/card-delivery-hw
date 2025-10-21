package ru.netology.test;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import static com.codeborne.selenide.Selenide.*;

public class CardDeliveryTest {

    @BeforeAll
    static void setUpAll() {
        // Основные настройки Selenide
        Configuration.browser = "chrome";
        Configuration.headless = true;
        Configuration.timeout = 15000;
        Configuration.browserSize = "1280x800";

       
        Configuration.holdBrowserOpen = false;
        Configuration.reopenBrowserOnFail = true;


        Configuration.browserCapabilities.setCapability("goog:chromeOptions",
                java.util.Map.of(
                        "args", Arrays.asList(
                                "--no-sandbox",
                                "--disable-dev-shm-usage",
                                "--remote-allow-origins=*",
                                "--incognito",
                                "--disable-gpu",
                                "--disable-extensions",
                                "--headless=new"
                        )
                ));
    }

    @BeforeEach
    void setUp() {
        open("http://localhost:9999");
    }

    @AfterEach
    void tearDown() {
        // Обязательно закрываем браузер после каждого теста
        closeWebDriver();
    }

    private String generateDate(int days) {
        return LocalDate.now().plusDays(days).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    @Test
    void shouldSubmitValidDeliveryForm() {
        // Проверяем, что страница загрузилась
        $("[data-test-id=city]").shouldBe(Condition.visible);

        // Очищаем поле даты
        $("[data-test-id=date] input").doubleClick().sendKeys(Keys.BACK_SPACE);

        // Заполняем форму
        $("[data-test-id=city] input").setValue("Москва");
        $("[data-test-id=date] input").setValue(generateDate(3));
        $("[data-test-id=name] input").setValue("Иванов Иван");
        $("[data-test-id=phone] input").setValue("+79270000000");
        $("[data-test-id=agreement]").click();
        $(".button").click();

        // Проверяем уведомление об успехе
        $("[data-test-id=notification]")
                .shouldBe(Condition.visible, Duration.ofSeconds(15))
                .shouldHave(Condition.text("Успешно!"))
                .shouldHave(Condition.text("Встреча успешно забронирована на " + generateDate(3)));
    }

    @Test
    void shouldSubmitWithHyphenInName() {
        $("[data-test-id=city]").shouldBe(Condition.visible);

        $("[data-test-id=date] input").doubleClick().sendKeys(Keys.BACK_SPACE);

        $("[data-test-id=city] input").setValue("Санкт-Петербург");
        $("[data-test-id=date] input").setValue(generateDate(5));
        $("[data-test-id=name] input").setValue("Анна-Мария Петрова-Иванова");
        $("[data-test-id=phone] input").setValue("+79271112233");
        $("[data-test-id=agreement]").click();
        $(".button").click();

        $("[data-test-id=notification]")
                .shouldBe(Condition.visible, Duration.ofSeconds(15))
                .shouldHave(Condition.text("Успешно!"))
                .shouldHave(Condition.text("Встреча успешно забронирована на " + generateDate(5)));
    }

    @Test
    void shouldSubmitWithMinimumDays() {
        $("[data-test-id=city]").shouldBe(Condition.visible);

        $("[data-test-id=date] input").doubleClick().sendKeys(Keys.BACK_SPACE);

        $("[data-test-id=city] input").setValue("Казань");
        $("[data-test-id=date] input").setValue(generateDate(3));
        $("[data-test-id=name] input").setValue("Петр Сидоров");
        $("[data-test-id=phone] input").setValue("+79273334455");
        $("[data-test-id=agreement]").click();
        $(".button").click();

        $("[data-test-id=notification]")
                .shouldBe(Condition.visible, Duration.ofSeconds(15))
                .shouldHave(Condition.text("Успешно!"))
                .shouldHave(Condition.text("Встреча успешно забронирована на " + generateDate(3)));
    }
}