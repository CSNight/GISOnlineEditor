package com.supermap.spscsi.dataimport;

import com.supermap.services.Interface;
import com.supermap.services.InterfaceContext;
import com.supermap.services.InterfaceContextAware;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.util.List;

@Interface(componentTypes = {PostOffice.class}, optional = false, multiple = false)
public class PostOfficeServlet extends HttpServlet implements
        InterfaceContextAware {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String id = null;
    private PostOffice postoffice = null;

    public PostOfficeServlet() {
        this.id = String.valueOf(System.currentTimeMillis());
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        PrintWriter out;

        response.setContentType("text/html;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        out = response.getWriter();

        File f = new File(PostOfficeServlet.class.getResource("").getPath()
                + "template.html");
        InputStreamReader isr = new InputStreamReader(new FileInputStream(f),
                "UTF-8");
        BufferedReader reader = new BufferedReader(isr);
        String str = null;

        while ((str = reader.readLine()) != null) {
            out.println(str);
        }
        reader.close();
        out.close();
    }

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String path = request.getParameter("path");
        path = URLDecoder.decode(path, "UTF-8");
        String datasetname = request.getParameter("datasetname");
        datasetname = URLDecoder.decode(datasetname, "UTF-8");
        String geofield = request.getParameter("geofield");
        geofield = URLDecoder.decode(geofield, "UTF-8");
        String workpath = request.getParameter("workpath");
        workpath = URLDecoder.decode(workpath, "UTF-8");
        String workname = request.getParameter("workname");
        workname = URLDecoder.decode(workname, "UTF-8");
        String functiontype = request.getParameter("functiontype");
        functiontype = URLDecoder.decode(functiontype, "UTF-8");
        String Result = "";
        if (functiontype == "poi") {
            Result = postoffice.importPOIData(path, datasetname);
        } else if (functiontype == "workspace") {
            Result = postoffice.CreateWorkspace(workpath, workname);
        } else {
            Result = postoffice.importBoundryData(path, datasetname, geofield);
        }
        PrintWriter out;

        response.setContentType("text/html;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        out = response.getWriter();
        String title = "入库结果";

        out.println("<html>");
        out.println("<head>");
        out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
        out.println("<TITLE>" + title + "</TITLE>");
        out.println("</head>");

        out.println("<body>");
        out.println("<p align=\"center\">");
        out.println(Result);
        out.println("</p>");
        out.println("</body>");
        out.println("</html>");
    }

    public void setInterfaceContext(InterfaceContext context) {
        this.getServletContext().setAttribute(this.id + "InterfaceContext",
                context);
        List<Object> components = context.getComponents(Object.class);
        if (components != null) {
            for (Object component : components) {
                if (component instanceof PostOffice) {
                    this.postoffice = (PostOffice) component;
                    break;
                }
            }
        }
    }
}

