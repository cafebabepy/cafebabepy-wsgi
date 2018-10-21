package org.cafebabepy.wsgi;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "Hello WSGI World", urlPatterns = {"/*"},
        initParams = {
                @WebInitParam(name = InitParam.MODULE, value = "hello_wsgi_world"),
                @WebInitParam(name = InitParam.FUNCTION, value = "app")
        }
)
public class HelloWorldWSGIServlet extends WSGIServlet {
}
