package com.pinguela.rentexpressweb.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter; 
import java.util.ArrayList;
import java.util.List;

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
import jakarta.ws.rs.core.Response.Status;

@WebServlet("/consumesapi")
public class ConsumesApi extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final String BASE_URI = "http://10.63.11.79:8080/neroveterinaria-rest/api";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        if ("show_appointment_form".equals(action)) {
            showAppointmentForm(request, response);
        } else {
            response.getWriter().append("Ruta GET no manejada o de prueba.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        if (action == null) {
            handleLogin(request, response);
        } else if ("create_appointment".equals(action)) {
            handleCreateAppointment(request, response);
        } else {
            response.getWriter().append("Acción no soportada");
        }
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            request.setAttribute("loginError", "Debe ingresar email y contraseña.");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/public/nero_login.jsp");
            dispatcher.forward(request, response);
            return;
        }

        Client client = ClientBuilder.newClient();
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode loginJson = mapper.createObjectNode();
        loginJson.put("email", email);
        loginJson.put("password", password);
        String jsonBody = loginJson.toString();
        
        System.out.println("JSON de Login Enviado: " + jsonBody);

        WebTarget webTarget = client
                .target(BASE_URI)
                .path("open")
                .path("client")
                .path("authenticate") 
                .queryParam("language", "es");

        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
        Response apiResponse = invocationBuilder.post(
                Entity.entity(jsonBody, MediaType.APPLICATION_JSON)
        );
        
        int status = apiResponse.getStatus();
        String jsonResponse = apiResponse.readEntity(String.class);

        System.out.println("Authenticate status: " + status);
        System.out.println("Raw Response authenticate: " + jsonResponse);

        if (status == Status.OK.getStatusCode()) {
            try {
                JsonNode rootNode = mapper.readTree(jsonResponse);
                if (rootNode != null) {
                    int id = rootNode.path("id").asInt(); 
                    String name = rootNode.path("name").asText("");
                    String lastName1 = rootNode.path("lastName1").asText("");
                    String emailResponse = rootNode.path("email").asText(email);

                    if (id > 0) {
                        HttpSession session = request.getSession(true);
                        session.setAttribute("neroClientId", id);
                        session.setAttribute("neroClientName", name);
                        session.setAttribute("neroClientLastName1", lastName1);
                        session.setAttribute("neroClientEmail", emailResponse);

                        response.sendRedirect(request.getContextPath() + "/consumesapi?action=show_appointment_form");
                        return;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        request.setAttribute("loginError", "Credenciales incorrectas o error en la autenticación con Nero API.");
        RequestDispatcher dispatcher = request.getRequestDispatcher("/public/nero_login.jsp");
        dispatcher.forward(request, response);
    }

    private void showAppointmentForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        Integer neroClientId = session != null ? (Integer) session.getAttribute("neroClientId") : null;

        if (neroClientId == null || neroClientId <= 0) {
            response.sendRedirect(request.getContextPath() + "/public/nero_login.jsp");
            return;
        }

        List<HeadquartersDTO> headquarters = loadHeadquarters();
        request.setAttribute("headquarters", headquarters);

        forwardToAppointmentJsp(request, response);
    }

    private List<HeadquartersDTO> loadHeadquarters() {
        List<HeadquartersDTO> list = new ArrayList<>();
        
        try {
            Client client = ClientBuilder.newClient();
            WebTarget webTarget = client
                    .target(BASE_URI)
                    .path("open")
                    .path("headquarters")
                    .queryParam("language", "es");

            Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
            String jsonResponse = invocationBuilder.get(String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(jsonResponse);

            if (rootNode != null && rootNode.isArray()) {
                for (JsonNode hqNode : rootNode) {
                    HeadquartersDTO dto = new HeadquartersDTO();
                    dto.setId(hqNode.path("id").asInt());
                    dto.setName(hqNode.path("name").asText(""));
                    dto.setLocalityName(hqNode.path("localityName").asText(""));
                    list.add(dto);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    private void handleCreateAppointment(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        Integer neroClientId = session != null ? (Integer) session.getAttribute("neroClientId") : null;
        String neroClientName = session != null ? (String) session.getAttribute("neroClientName") : ""; 
        String neroClientLastName1 = session != null ? (String) session.getAttribute("neroClientLastName1") : ""; 

        String headquartersIdParam = request.getParameter("headquartersId");
        String dateParam = request.getParameter("date");
        String timeParam = request.getParameter("time");
        String details = request.getParameter("details");

        String errorMessage = null;
        if (neroClientId == null || neroClientId <= 0) {
            errorMessage = "No existe un cliente Nero autenticado.";
        } else if (headquartersIdParam == null || headquartersIdParam.isEmpty() || 
                   dateParam == null || dateParam.isEmpty() || 
                   timeParam == null || timeParam.isEmpty()) {
            errorMessage = "Datos de cita incompletos (Sede, Fecha u Hora).";
        }

        if (errorMessage != null) {
            request.setAttribute("neroAppointmentError", errorMessage);
            List<HeadquartersDTO> headquarters = loadHeadquarters();
            request.setAttribute("headquarters", headquarters);
            forwardToAppointmentJsp(request, response);
            return;
        }

        LocalDate date = LocalDate.parse(dateParam);
        LocalTime time = LocalTime.parse(timeParam);
        LocalDateTime localDateTime = LocalDateTime.of(date, time);
        
        String finalDateTimeIso = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.000'Z'"));

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode appointmentJson = mapper.createObjectNode();
        
        appointmentJson.put("id", 0); 
        appointmentJson.put("dateTime", finalDateTimeIso); 
        appointmentJson.put("clientId", neroClientId); 
        appointmentJson.put("headquartersId", Integer.parseInt(headquartersIdParam));
        appointmentJson.put("statusAppointmentId", 1);         
        appointmentJson.put("userId", 1); 
        appointmentJson.put("animalId", 249);         
        appointmentJson.put("clientName", neroClientName);
        appointmentJson.put("clientLastName1", neroClientLastName1);
        appointmentJson.put("animalName", "MASCOTA_POR_DEFECTO");
        appointmentJson.put("headquartersName", ""); 
        appointmentJson.put("statusAppointmentName", ""); 
        appointmentJson.put("userName", ""); 
        appointmentJson.put("userLastName1", ""); 
        appointmentJson.put("details", (details != null && !details.trim().isEmpty()) ? details.trim() : "");
        
        System.out.println("JSON CITA FINAL ENVIADO: " + appointmentJson.toString());

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

        System.out.println("Status Code: " + status);

        if (status == Status.OK.getStatusCode() || status == 201) {
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
            request.setAttribute("neroAppointmentError", 
                "Error al crear la cita en Nero. Código: " + status + ". Respuesta: " + jsonResponse);
        }

        List<HeadquartersDTO> headquarters = loadHeadquarters();
        request.setAttribute("headquarters", headquarters);
        
        forwardToAppointmentJsp(request, response);
    }

    private void forwardToAppointmentJsp(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("/public/nero_appointment.jsp");
        dispatcher.forward(request, response);
    }

    public static class HeadquartersDTO {
        private int id;
        private String name;
        private String localityName;
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getLocalityName() { return localityName; }
        public void setLocalityName(String localityName) { this.localityName = localityName; }
    }
}