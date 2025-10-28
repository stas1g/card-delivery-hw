package ru.netology.test;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class CardDeliveryTest {

    @BeforeEach
    void setUp() {
        open("http://localhost:9999");
    }

    @Test
    @DisplayName("Should successfully submit delivery form with valid data")
    void shouldSubmitValidDeliveryForm() {
        String meetingDate = getFutureDate(3);

        $("[data-test-id=city] input").setValue("Москва");
        $("[data-test-id=date] input").doubleClick().sendKeys(Keys.DELETE, meetingDate);
        $("[data-test-id=name] input").setValue("Иван Иванов");
        $("[data-test-id=phone] input").setValue("+79270000000");
        $("[data-test-id=agreement]").click();
        $("button.button").click();

        $("[data-test-id=notification]")
                .shouldBe(visible, Duration.ofSeconds(15))
                .shouldHave(exactText("Успешно!\nВстреча успешно запланирована на " + meetingDate));
    }

    @Test
    @DisplayName("Should submit form with minimum days ahead")
    void shouldSubmitWithMinimumDays() {
        String meetingDate = getFutureDate(5);

        $("[data-test-id=city] input").setValue("Санкт-Петербург");
        $("[data-test-id=date] input").doubleClick().sendKeys(Keys.DELETE, meetingDate);
        $("[data-test-id=name] input").setValue("Анна Петрова");
        $("[data-test-id=phone] input").setValue("+79271111111");
        $("[data-test-id=agreement]").click();
        $("button.button").click();

        $("[data-test-id=notification]")
                .shouldBe(visible, Duration.ofSeconds(15))
                .shouldHave(exactText("Успешно!\nВстреча успешно запланирована на " + meetingDate));
    }

    @Test
    @DisplayName("Should submit form with hyphen in name")
    void shouldSubmitWithHyphenInName() {
        String meetingDate = getFutureDate(7);

        $("[data-test-id=city] input").setValue("Казань");
        $("[data-test-id=date] input").doubleClick().sendKeys(Keys.DELETE, meetingDate);
        $("[data-test-id=name] input").setValue("Анна-Мария Сидорова");
        $("[data-test-id=phone] input").setValue("+79272222222");
        $("[data-test-id=agreement]").click();
        $("button.button").click();

        $("[data-test-id=notification]")
                .shouldBe(visible, Duration.ofSeconds(15))
                .shouldHave(exactText("Успешно!\nВстреча успешно запланирована на " + meetingDate));
    }

    private String getFutureDate(int days) {
        return LocalDate.now().plusDays(days).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }
}