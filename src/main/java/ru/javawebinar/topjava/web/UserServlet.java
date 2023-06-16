package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.web.user.ProfileRestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.slf4j.LoggerFactory.getLogger;

public class UserServlet extends HttpServlet {
    private static final Logger log = getLogger(UserServlet.class);
    private ProfileRestController profileRestController;
    private ConfigurableApplicationContext context;

    @Override
    public void init() throws ServletException {
        // TODO расшарить контекст из MealServlet
        //  UserServlet - обращу внимание: в UserServlet и MealServlet - два разных Spring-контекста.
        //  Если из них достать одноименные бины, то это будут разные объекты. Если бы в UserServlet тоже
        //  нужен был бы контекст, надо было бы его шарить через ServletContext.
        context = new ClassPathXmlApplicationContext("spring/spring-app.xml");
        profileRestController = context.getBean(ProfileRestController.class);
    }

    @Override
    public void destroy() {
        context.close();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.debug("in doGet method");
        User authUser = profileRestController.get();
        request.setAttribute("authUser", authUser);
        request.setAttribute("users", profileRestController.getAll());
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.debug("in doPost method");
        int authUserId = profileRestController.getByMail(request.getParameter("user")).getId();
        SecurityUtil.setAuthUser(authUserId);
        response.sendRedirect("users");
    }
}
