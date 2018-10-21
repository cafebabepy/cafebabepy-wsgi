package org.cafebabepy.wsgi;

import org.cafebabepy.runtime.PyObject;
import org.cafebabepy.runtime.Python;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedHashMap;

public class WSGIServlet extends HttpServlet {

    private Python runtime;
    private PyObject function;

    private PyObject server;
    private PyObject startResponse;

    @Override
    public void init(ServletConfig config) {
        String module = config.getInitParameter(InitParam.MODULE);
        String function = config.getInitParameter(InitParam.FUNCTION);

        this.runtime = Python.createRuntime();

        PyObject context = this.runtime.evalModule(module);

        PyObject wsgiModule = this.runtime.evalModule("cafebabepy.wsgi");
        this.server = this.runtime.getattr(wsgiModule, "Server").call();
        this.startResponse = this.runtime.getattr(this.server, "start_response");

        this.function = this.runtime.getattr(context, function);
    }

    @Override
    protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doXXX(req, resp);
    }

    @Override
    protected void doTrace(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doXXX(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doXXX(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doXXX(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doXXX(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doXXX(req, resp);
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doXXX(req, resp);
    }

    private void doXXX(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PyObject environ = getEnviron(req);

        PyObject results = this.function.call(environ, this.startResponse);

        PyObject status = this.runtime.getattr(this.server, "status");
        PyObject headers = this.runtime.getattr(this.server, "response_headers");

        int statusCode = Integer.parseInt(status.toJava(String.class).split(" ")[0]);
        resp.setStatus(statusCode);

        this.runtime.iter(headers, header -> {
            PyObject[] keyAndValue = new PyObject[2];
            int[] i = new int[1];
            this.runtime.iter(header, kv -> keyAndValue[i[0]++] = kv);

            resp.setHeader(
                    keyAndValue[0].toJava(String.class),
                    keyAndValue[1].toJava(String.class)
            );
        });

        OutputStream os = resp.getOutputStream();

        try {
            this.runtime.iter(results, bytes -> {
                try {
                    int[] ints = bytes.toJava(int[].class);
                    for (int i : ints) {
                        os.write(i);
                    }

                } catch (IOException e) {
                    throw new IOExceptionWrapper(e);
                }
            });

        } catch (IOExceptionWrapper e) {
            throw e.e;
        }
    }

    private PyObject getEnviron(HttpServletRequest req) {
        LinkedHashMap<PyObject, PyObject> environ = new LinkedHashMap<>();

        environ.put(this.runtime.str("wsgi.version"), this.runtime.tuple(this.runtime.number(1), this.runtime.number(0)));
        environ.put(this.runtime.str("wsgi.url_scheme"), this.runtime.str("http"));
        environ.put(this.runtime.str("wsgi.input"), this.runtime.None()); // FIXME
        environ.put(this.runtime.str("wsgi.errors"), this.runtime.None()); // FIXME
        environ.put(this.runtime.str("wsgi.multithread"), this.runtime.True());
        environ.put(this.runtime.str("wsgi.multiprocess"), this.runtime.False());
        environ.put(this.runtime.str("wsgi.run_once"), this.runtime.True()); // TODO ???
        environ.put(this.runtime.str("REQUEST_METHOD"), this.runtime.str(req.getMethod().toUpperCase()));
        environ.put(this.runtime.str("QUERY_STRING"), this.runtime.str(req.getQueryString()));
        environ.put(this.runtime.str("SERVER_NAME"), this.runtime.str(req.getServerName()));
        environ.put(this.runtime.str("SERVER_PORT"), this.runtime.number(req.getServerPort()));

        environ.put(this.runtime.str("HTTP_COOKIE"), this.runtime.number(req.getServerPort())); // FIXME

        return this.runtime.dict(environ);
    }

    class IOExceptionWrapper extends RuntimeException {
        final IOException e;

        IOExceptionWrapper(IOException e) {
            super(e);

            this.e = e;
        }
    }
}
