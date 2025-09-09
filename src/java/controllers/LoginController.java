package controllers;

import com.google.gson.Gson;
import dtos.SignInDTO;
import dtos.SuccessDTO;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import models.User;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;
import utils.AppException;
import utils.HibernateUtil;
import utils.ResponseHandler;

@WebServlet(name = "LoginController", urlPatterns = {"/api/auth/sign-in"})
public class LoginController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {

            SignInDTO user = new Gson().fromJson(req.getReader(), SignInDTO.class);
            if (user.getEmail().isEmpty()) {
                throw new AppException("Email is required!");
            }
            if (user.getPassword().isEmpty()) {
                throw new AppException("Password is required!");
            }

            Session session = HibernateUtil.getSessionFactory().openSession();
            Criteria users = session.createCriteria(User.class)
                    .add(Restrictions.eq("email", user.getEmail()));

            User foundUser = (User) users.uniqueResult();
            if (foundUser == null) {
                throw new AppException("Invalid email!");
            }

            if (!foundUser.getPassword().equals(user.getPassword())) {
                throw new AppException("Invalid password!");
            }

            ResponseHandler.sendOk(new SuccessDTO(true), resp);
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
