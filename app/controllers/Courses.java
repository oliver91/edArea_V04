package controllers;

import models.Course;
import models.User;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import views.html.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * Created by oliver on 07.11.14.
 */

@Security.Authenticated(Secured.class)
public class Courses extends Controller
{
    public static Result creatingCourse()
    {
        return ok(creatingCourse.render(Form.form(Course.class)));
    }

    public static Result add() {

        Form<Course> creatingCourse_form = Form.form(Course.class).bindFromRequest();
        Course newCourse = new Course(
                                      request().username(),
                                      creatingCourse_form.get().courseName,
                                      null,
                                      creatingCourse_form.get().science,
                                      creatingCourse_form.get().aboutCourse,
                                      true);
        newCourse.save();
        return ok(upload.render(null, ""));
    }

    public static Result showCourses() {
        switch (User.find.byId(request().username()).userType) {   // в зависимости от типа пользователя будет выводится нужная информация

            case 1:
                return ok(courses.render(User.find.byId(request().username()), Course.find.where().like("email", "%" + request().username() + "%").findList()));
            case 2:
                return ok(courses.render(User.find.byId(request().username()), Course.find.where().findList()));
            case 3:
                return ok(courses.render(User.find.byId(request().username()), Course.find.where().like("email", "%" + request().username() + "%").findList()));
            default:
                return ok();
        }
    }

    public static Result showCoursePage(String courseName)
    {
        return ok(course.render(User.find.byId(request().username()), Course.find.byId(courseName)));
    }

    public static Result uploadFile() throws IOException {    // заливка каринки на сервер
        Course currentCourse =  Course.find.where().like("email", "%"+request().username()+"%").like("current", "true").findUnique();
        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart filePart1 = body.getFile("filePart1");


        UUID id1 = UUID.randomUUID();  // уникальное имя лого

        File newFile1 = new File("public/logos/"+id1+".jpg");
        currentCourse.logoPath = "logos/"+id1+".jpg";
        currentCourse.current = false;
        currentCourse.save();
        File file1 = filePart1.getFile();
        InputStream isFile1 = new FileInputStream(file1);
        byte[] byteFile1 = org.apache.commons.io.IOUtils.toByteArray(isFile1);
        org.apache.commons.io.FileUtils.writeByteArrayToFile(newFile1, byteFile1);
        isFile1.close();

        return ok(courses.render(User.find.byId(request().username()), Course.find.where().like("email", "%"+request().username()+"%").findList()));
    }


}
