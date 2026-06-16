package com.example.hospital.controller.command;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;
import java.io.IOException;

/**
 * Інтерфейс для реалізації шаблону Command.
 */
public interface Command {
    /**
     * Виконує логіку команди.
     *
     * @param request  the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @return рядок, що представляє шлях до наступної сторінки (для forward)
     * або рядок, що починається з "redirect:" для перенаправлення.
     * Може повернути null, якщо відповідь генерується безпосередньо (наприклад, для AJAX).
     * @throws IOException      якщо виникає помилка вводу/виводу
     * @throws ServletException якщо виникає помилка сервлета
     */
    String execute(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException;
}