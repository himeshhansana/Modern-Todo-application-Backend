package controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dtos.CreateTodoDTO;
import dtos.SignUpDTO;
import dtos.SuccessDTO;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import models.Todo;
import models.User;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;
import utils.AppException;
import utils.HibernateUtil;
import utils.ResponseHandler;
import utils.StatusCode;

@WebServlet(name = "TodosController", urlPatterns = {"/api/todos"})
public class TodoController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {

            CreateTodoDTO dto = new Gson().fromJson(req.getReader(), CreateTodoDTO.class);
            if (dto.getTitle().isEmpty()) {
                throw new AppException("Title is required!");
            }
            if (dto.getUserEmail().isEmpty()) {
                throw new AppException("User Email is required!");
            }

            Session session = HibernateUtil.getSessionFactory().openSession();

            User user = (User) session.createCriteria(User.class)
                    .add(Restrictions.eq("email", dto.getUserEmail()))
                    .uniqueResult();
            if (user == null) {
                throw new AppException("User not found!");
            }

            session.save(new Todo(
                    dto.getTitle(),
                    user
            ));
            session.beginTransaction().commit();

            ResponseHandler.send(new SuccessDTO(true), StatusCode.CREATED, resp);
        } catch (AppException e) {
            ResponseHandler.sendBadRequest(e.getMessage(), resp);
        } catch (ConstraintViolationException e) {
            ResponseHandler.sendBadRequest("There is a user with the same email!", resp);
        } catch (Exception e) {
            System.out.println(e.getClass());
            ResponseHandler.sendInternalServerError(e.getMessage(), resp);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {

            String userEmail = req.getParameter("email");
            if (userEmail.isEmpty()) {
                throw new AppException("email param is required!");
            }

            Session session = HibernateUtil.getSessionFactory().openSession();
            List<Todo> todos = session.createCriteria(Todo.class)
                    .add(Restrictions.eq("user.email", userEmail))
                    .list();

            for (Todo t : todos) {
                t.setUser(null);
            }

            ResponseHandler.sendOk(todos, resp);

        } catch (AppException e) {
            ResponseHandler.sendBadRequest(e.getMessage(), resp);
        } catch (Exception e) {
            System.out.println(e.getClass());
            ResponseHandler.sendInternalServerError(e.getMessage(), resp);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try {

            String id = req.getParameter("id");
            if (id.isEmpty()) {
                throw new AppException("Id param is required!");
            }

            JsonObject body = new Gson().fromJson(req.getReader(), JsonObject.class);
            if (!body.has("isCompleted")) {
                throw new AppException("isCompleted is required!");
            }

            Session session = HibernateUtil.getSessionFactory().openSession();
            Todo todo = (Todo) session.createCriteria(Todo.class)
                    .add(Restrictions.eq("id", Integer.parseInt(id)))
                    .uniqueResult();
            if (todo == null) {
                throw new AppException("Todo is not found!");
            }

            todo.setIsCompleted(body.get("isCompleted").getAsBoolean());
            session.update(todo);
            session.beginTransaction().commit();

            todo.setUser(null);

            ResponseHandler.sendOk(todo, resp);

        } catch (AppException e) {
            ResponseHandler.sendBadRequest(e.getMessage(), resp);
        } catch (Exception e) {
            System.out.println(e.getClass());
            ResponseHandler.sendInternalServerError(e.getMessage(), resp);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try {

            String id = req.getParameter("id");
            if (id.isEmpty()) {
                throw new AppException("Id param is required!");
            }

            Session session = HibernateUtil.getSessionFactory().openSession();
            Todo todo = (Todo) session.createCriteria(Todo.class)
                    .add(Restrictions.eq("id", Integer.parseInt(id)))
                    .uniqueResult();
            if (todo == null) {
                throw new AppException("Todo is not found!");
            }

            session.delete(todo);
            session.beginTransaction().commit();

            ResponseHandler.send(new SuccessDTO(true), StatusCode.NO_CONTENT, resp);

        } catch (AppException e) {
            ResponseHandler.sendBadRequest(e.getMessage(), resp);
        } catch (Exception e) {
            System.out.println(e.getClass());
            ResponseHandler.sendInternalServerError(e.getMessage(), resp);
        }
    }
}
