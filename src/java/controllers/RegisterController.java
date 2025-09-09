package controllers;

import com.google.gson.Gson;
import dtos.SignUpDTO;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import models.User;
import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;
import utils.AppException;
import utils.HibernateUtil;
import utils.ResponseHandler;
import utils.StatusCode;

@WebServlet(name = "RegisterController", urlPatterns = {"/api/auth/sign-up"})
public class RegisterController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {

            SignUpDTO user = new Gson().fromJson(req.getReader(), SignUpDTO.class);
            if (user.getEmail().isEmpty()) {
                throw new AppException("Email is required!");
            }
            if (user.getName().isEmpty()) {
                throw new AppException("Name is required!");
            }
            if (user.getPassword().isEmpty()) {
                throw new AppException("Password is required!");
            }

            Session session = HibernateUtil.getSessionFactory().openSession();
            session.save(new User(
                    user.getEmail(),
                    user.getName(),
                    user.getPassword()
            ));
            session.beginTransaction().commit();

            ResponseHandler.send(user, StatusCode.CREATED, resp);
        } catch (AppException e) {
            ResponseHandler.sendBadRequest(e.getMessage(), resp);
        } catch (ConstraintViolationException e) {
            ResponseHandler.sendBadRequest("There is a user with the same email!", resp);
        } catch (Exception e) {
            System.out.println(e.getClass());
            ResponseHandler.sendInternalServerError(e.getMessage(), resp);
        }
    }
}
