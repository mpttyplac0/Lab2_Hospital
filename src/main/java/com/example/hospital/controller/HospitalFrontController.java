package com.example.hospital.controller;

import com.example.hospital.controller.command.Command;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

@WebServlet("/app/*")
public class HospitalFrontController extends HttpServlet {
    private static final Logger logger = LogManager.getLogger(HospitalFrontController.class);

    @Override
    public void init() throws ServletException {
        super.init();
        logger.info("HospitalFrontController initialized.");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        String commandName = request.getParameter("command");
        logger.debug("Received command: {}", commandName);

        Command command = CommandNameMapper.getCommand(commandName);
        logger.debug("Executing command: {}", command.getClass().getSimpleName());

        String page = null;
        try {
            page = command.execute(request, response);
        } catch (Exception e) { // Обробка ServiceException та інших можливих помилок
            logger.error("Error executing command {}", command.getClass().getSimpleName(), e);
            request.setAttribute("errorMessage", "Виникла помилка під час обробки вашого запиту: " + e.getMessage());
            page = "/WEB-INF/jsp/error.jsp";
        }

        if (page != null) {
            if (page.startsWith("redirect:")) {
                String redirectPath = page.substring("redirect:".length());
                logger.debug("Redirecting to: {}", request.getContextPath() + redirectPath);
                response.sendRedirect(request.getContextPath() + redirectPath);
            } else {
                logger.debug("Forwarding to: {}", page);
                RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(page);
                dispatcher.forward(request, response);
            }
        } else {
            logger.warn("Command {} returned null page. Response might not have been committed.", command.getClass().getSimpleName());
        }
    }

    @Override
    public void destroy() {
        logger.info("HospitalFrontController destroyed.");
        super.destroy();
    }
}