package controllers;


import models.Notification;
import models.User;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.index;
import views.html.login;
import views.html.registration;

import java.util.ArrayList;
import java.util.List;

public class Application extends Controller
{
    @Security.Authenticated(Secured.class)
    public static Result index()
    {
        List<Notification> notifications = new ArrayList<>();
        notifications = Notification.find.where().like("email_to", "%"+request().username()+"%").findList();
        return ok(index.render(User.find.byId(request().username()), notifications));
    }



    public static Result login()
    {
        return ok(login.render(Form.form(Login.class)));
    }

    public static Result logout() {
        session().clear();
        flash("success", "You've been logged out");
        return redirect(
                routes.Application.login()
        );
    }

    public static Result authenticate()
    {
        Form<Login> loginForm = Form.form(Login.class).bindFromRequest();
        if (loginForm.hasErrors()) {
            return badRequest(login.render(loginForm));
        } else {
            session().clear();
            session("email", loginForm.get().email);

            return redirect(
                    routes.Application.index()
            );
        }
    }

    public static Result registration()
    {
        return ok(registration.render(Form.form(Registration.class)));
    }

    public static Result addUser()
    {
        Form<Registration> reg_form = Form.form(Registration.class).bindFromRequest();

        new User(reg_form.get().email, reg_form.get().name,reg_form.get().userType, reg_form.get().password,
                reg_form.get().birthDate, reg_form.get().country, reg_form.get().city,
                User.find.where().findRowCount()+1).save();


        authenticate();
        return redirect(routes.Application.index());
    }

    public static class Login
    {
        public String email;
        public String password;


        public String validate()
        {
            if (User.authenticate(email, password) == null) {
                return "Invalid user or password";
            }
            return null;
        }
    }

    public static class Registration
    {
        public String name;
        public String email;
        public String password;
        public String birthDate;
        public String country;
        public String city;
        public int id;
        public int userType;
    }

}