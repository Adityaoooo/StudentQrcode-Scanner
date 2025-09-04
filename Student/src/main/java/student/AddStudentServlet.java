package student;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Base64;

import javax.imageio.ImageIO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

@WebServlet("/addStudent")
@MultipartConfig(maxFileSize = 5*1024*1024)
public class AddStudentServlet extends HttpServlet {

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/students";
    private static final String DB_USER = "postgres";
    private static final String DB_PASS = "1234";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        try {
            String name = request.getParameter("name");
            String gender = request.getParameter("gender");
            String course = request.getParameter("course");
            String dobStr = request.getParameter("dob");
            Part photoPart = request.getPart("photo");

            Date dob = Date.valueOf(dobStr);
            InputStream photoStream = photoPart.getInputStream();

            // unique qr code data
            String qrCodeData = name + "-" + System.currentTimeMillis();

            // ✅ Insert into "physics" table instead of "student"
            Class.forName("org.postgresql.Driver");
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                String sql = "INSERT INTO physics (name, gender, course, dob, photo, qrcode) VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement pst = conn.prepareStatement(sql)) {
                    pst.setString(1, name);
                    pst.setString(2, gender);
                    pst.setString(3, course);
                    pst.setDate(4, dob);
                    pst.setBinaryStream(5, photoStream, (int) photoPart.getSize());
                    pst.setString(6, qrCodeData);
                    pst.executeUpdate();
                }
            }

            // Generate QR
            BufferedImage qrImage = QRCodeGenerator.generateQRCodeImage(qrCodeData, 200, 200);
            ByteArrayOutputStream qrBaos = new ByteArrayOutputStream();
            ImageIO.write(qrImage, "png", qrBaos);
            String qrBase64 = Base64.getEncoder().encodeToString(qrBaos.toByteArray());

            // Encode photo again for showing
            InputStream photoStream2 = photoPart.getInputStream();
            BufferedImage photoImg = ImageIO.read(photoStream2);
            ByteArrayOutputStream photoBaos = new ByteArrayOutputStream();
            ImageIO.write(photoImg, "png", photoBaos);
            String photoBase64 = Base64.getEncoder().encodeToString(photoBaos.toByteArray());

            // set attributes for JSP
            request.setAttribute("qrBase64", qrBase64);
            request.setAttribute("photoBase64", photoBase64);
            request.setAttribute("name", name);
            request.setAttribute("gender", gender);
            request.setAttribute("course", course);
            request.setAttribute("dob", dobStr);

            request.getRequestDispatcher("index.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException("❌ Error while saving student: " + e.getMessage());
        }
    }
}
