package student;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Base64;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/getStudentInfo")
public class StudentInfoServlet extends HttpServlet {

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/students";
    private static final String DB_USER = "postgres";
    private static final String DB_PASS = "1234";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        Gson gson = new Gson();

        String qr = request.getParameter("qr");

        if (qr == null || qr.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Missing QR parameter\"}");
            return;
        }

        try {
            Class.forName("org.postgresql.Driver"); // load driver

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                String sql = "SELECT name, gender, course, dob, photo FROM physics WHERE qrcode=?";
                try (PreparedStatement pst = conn.prepareStatement(sql)) {
                    pst.setString(1, qr);
                    ResultSet rs = pst.executeQuery();

                    if (rs.next()) {
                        JsonObject studentJson = new JsonObject();
                        studentJson.addProperty("name", rs.getString("name"));
                        studentJson.addProperty("gender", rs.getString("gender"));
                        studentJson.addProperty("course", rs.getString("course"));
                        studentJson.addProperty("dob", (rs.getDate("dob") != null) ? rs.getDate("dob").toString() : "");
                        byte[] photoBytes = rs.getBytes("photo");
                        studentJson.addProperty("photoBase64", (photoBytes != null) ? Base64.getEncoder().encodeToString(photoBytes) : "");
                        response.getWriter().write(gson.toJson(studentJson));
                    } else {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        response.getWriter().write("{\"error\":\"Student not found\"}");
                    }
                }
            }

        } catch (ClassNotFoundException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject errorJson = new JsonObject();
            errorJson.addProperty("error", "PostgreSQL Driver not found");
            response.getWriter().write(gson.toJson(errorJson));
            e.printStackTrace();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject errorJson = new JsonObject();
            errorJson.addProperty("error", e.getMessage());
            response.getWriter().write(gson.toJson(errorJson));
            e.printStackTrace();
        }
    }
}
