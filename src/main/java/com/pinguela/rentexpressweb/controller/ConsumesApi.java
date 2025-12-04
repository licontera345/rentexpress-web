package com.pinguela.rentexpressweb.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@WebServlet("/consumesapi")
public class ConsumesApi extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final String BASE_URI = "http://localhost:8080/neroveterinaria-rest/api";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Client client = ClientBuilder.newClient();

        WebTarget webTarget = client
                .target(BASE_URI)
                .path("open")
                .path("appointment")
                .path("date")
                .path("2025-01-01")
                .queryParam("language", "es");

        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);

        String jsonResponse = invocationBuilder.get(String.class);
        System.out.println("Raw Response: " + jsonResponse);

        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode rootNode = mapper.readTree(jsonResponse);
            if (rootNode != null && rootNode.isArray()) {
                for (JsonNode appointmentNode : rootNode) {
                    JsonNode idNode = appointmentNode.get("id");
                    if (idNode != null) {
                        int id = idNode.asInt();
                        System.out.println("Appointment id: " + id);
                    }
                }
            } else {
                System.out.println("Response is not a JSON array.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        response.getWriter().append("OK ConsumesApi GET");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if (action == null) {
            handleLogin(request, response);
        } else if ("create_appointment".equals(action)) {
            handleCreateAppointment(request, response);
        } else {
            response.getWriter().append("Acción no soportada");
        }
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            response.getWriter().append("Faltan credenciales");
            return;
        }

        Client client = ClientBuilder.newClient();
        String encodedEmail = URLEncoder.encode(email, StandardCharsets.UTF_8.toString());

        WebTarget webTarget = client
                .target(BASE_URI)
                .path("client")
                .path("athenticate")
                .path(encodedEmail)
                .queryParam("password", password)
                .queryParam("language", "es");

        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);

        Response apiResponse = invocationBuilder.get();
        int status = apiResponse.getStatus();
        String jsonResponse = apiResponse.readEntity(String.class);

        System.out.println("Authenticate status: " + status);
        System.out.println("Raw Response authenticate: " + jsonResponse);

        if (status == Response.Status.OK.getStatusCode()) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                JsonNode rootNode = mapper.readTree(jsonResponse);
                if (rootNode != null) {
                    int id = rootNode.path("id").asInt();
                    String name = rootNode.path("name").asText("");
                    String emailResponse = rootNode.path("email").asText(email);

                    HttpSession session = request.getSession(true);
                    session.setAttribute("neroClientId", id);
                    session.setAttribute("neroClientName", name);
                    session.setAttribute("neroClientEmail", emailResponse);

                    request.setAttribute("neroAppointmentInfo", "Login Nero OK. Ahora puedes crear una cita.");
                    forwardToAppointmentJsp(request, response);
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        response.getWriter().append("Credenciales incorrectas");
    }

    private void handleCreateAppointment(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Integer neroClientId = session != null ? (Integer) session.getAttribute("neroClientId") : null;

        String headquartersIdParam = request.getParameter("headquartersId");
        String dateParam = request.getParameter("date");
        String timeParam = request.getParameter("time");
        String details = request.getParameter("details");

        String errorMessage = null;

        if (neroClientId == null) {
            errorMessage = "No existe un cliente Nero autenticado en sesión.";
        } else if (headquartersIdParam == null || headquartersIdParam.isEmpty() || dateParam == null
                || dateParam.isEmpty() || timeParam == null || timeParam.isEmpty()) {
            errorMessage = "Datos de cita incompletos.";
        }

        if (errorMessage != null) {
            request.setAttribute("neroAppointmentError", errorMessage);
            forwardToAppointmentJsp(request, response);
            return;
        }

        LocalDate date = LocalDate.parse(dateParam);
        LocalTime time = LocalTime.parse(timeParam);
        LocalDateTime dateTime = LocalDateTime.of(date, time);
        String dateTimeIso = dateTime.toString();

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode appointmentJson = mapper.createObjectNode();
        appointmentJson.put("clientId", neroClientId);
        appointmentJson.put("headquartersId", Integer.parseInt(headquartersIdParam));
        appointmentJson.put("dateTime", dateTimeIso);
        appointmentJson.put("details", details);

        Client client = ClientBuilder.newClient();

        WebTarget webTarget = client
                .target(BASE_URI)
                .path("open")
                .path("appointment")
                .queryParam("language", "es");

        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);

        Response apiResponse = invocationBuilder.post(
                Entity.entity(appointmentJson.toString(), MediaType.APPLICATION_JSON));

        int status = apiResponse.getStatus();
        String jsonResponse = apiResponse.readEntity(String.class);

        System.out.println("Create appointment status: " + status);
        System.out.println("Create appointment body: " + jsonResponse);

        if (status == Response.Status.OK.getStatusCode()) {
            Integer appointmentId = null;
            try {
                JsonNode responseNode = mapper.readTree(jsonResponse);
                if (responseNode != null) {
                    if (responseNode.isInt()) {
                        appointmentId = responseNode.asInt();
                    } else if (responseNode.has("id")) {
                        appointmentId = responseNode.get("id").asInt();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            request.setAttribute("neroAppointmentId", appointmentId);
            request.setAttribute("neroAppointmentSuccess", true);
        } else {
            request.setAttribute("neroAppointmentError", "Error al crear la cita en Nero. Código: " + status);
        }

        forwardToAppointmentJsp(request, response);
    }

    private void forwardToAppointmentJsp(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("/public/nero_appointment.jsp");
        dispatcher.forward(request, response);
    }
}
