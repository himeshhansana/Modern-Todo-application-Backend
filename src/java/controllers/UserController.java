package controllers;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import models.User;
import com.google.gson.Gson;

@WebServlet(name = "UserController", urlPatterns = {"/api/auth/update-name"})
public class UserController extends HttpServlet {

    private SessionFactory sessionFactory;

    @Override
    public void init() throws ServletException {
        try {
            sessionFactory = new Configuration().configure().buildSessionFactory();
        } catch (Exception e) {
            throw new ServletException("Error initializing Hibernate SessionFactory", e);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();
        Gson gson = new Gson();

        try {
            // Parse JSON request body
            StringBuilder requestBody = new StringBuilder();
            String line;
            while ((line = req.getReader().readLine()) != null) {
                requestBody.append(line);
            }

            // Convert JSON to Java object
            UpdateNameRequest updateRequest = gson.fromJson(requestBody.toString(), UpdateNameRequest.class);

            // Validate input
            if (updateRequest.getEmail() == null || updateRequest.getEmail().trim().isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write(gson.toJson(new ResponseMessage("Email is required")));
                return;
            }

            if (updateRequest.getName() == null || updateRequest.getName().trim().isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write(gson.toJson(new ResponseMessage("Name is required")));
                return;
            }

            Session session = sessionFactory.openSession();
            Transaction transaction = null;

            try {
                transaction = session.beginTransaction();

                // Find user by email
                User user = (User) session.get(User.class, updateRequest.getEmail());

                if (user == null) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.write(gson.toJson(new ResponseMessage("User not found")));
                    return;
                }

                // Update user name
                user.setName(updateRequest.getName().trim());
                session.update(user);

                transaction.commit();

                // Return success response
                resp.setStatus(HttpServletResponse.SC_OK);
                out.write(gson.toJson(new ResponseMessage("Username updated successfully")));

            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.write(gson.toJson(new ResponseMessage("Error updating username: " + e.getMessage())));
            } finally {
                session.close();
            }

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write(gson.toJson(new ResponseMessage("Error processing request: " + e.getMessage())));
        }
    }

    @Override
    public void destroy() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
        }
    }

    // Helper classes for request/response
    private static class UpdateNameRequest {

        private String email;
        private String name;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    private static class ResponseMessage {

        private String message;

        public ResponseMessage(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
