package com.supermap.iserverex.address_server;

import com.supermap.services.Interface;
import com.supermap.services.InterfaceContext;
import com.supermap.services.InterfaceContextAware;
import net.sf.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.List;

@Interface(componentTypes = {OnlineGeocoding.class}, optional = false, multiple = false)
public class OnlineGeocodingServlet extends HttpServlet implements
        InterfaceContextAware {
    private static final long serialVersionUID = 1L;
    private String id;
    private OnlineGeocoding onlinegeocoding = null;

    public OnlineGeocodingServlet() {
        this.id = String.valueOf(System.currentTimeMillis());
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Headers", "Content-Type");
            response.setHeader("Access-Control-Allow-Methods", "POST");
            request.setCharacterEncoding("UTF-8");
            String JSON = request.getParameter("params");
            JSON = URLDecoder.decode(JSON, "UTF-8");
            JSONObject data = JSONObject.fromObject(JSON);
            String reqType = data.get("RequestType").toString();
            String paramType = data.get("ParamsType").toString();
            String jsonElements = data.get("Elements").toString();
            String ReponseStr = "";
            PrintWriter writer = response.getWriter();
            System.out.println(ReponseStr);
            writer.println(ReponseStr);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setInterfaceContext(InterfaceContext context) {
        this.getServletContext().setAttribute(this.id + "InterfaceContext",
                context);
        List<Object> components = context.getComponents(Object.class);
        if (components != null) {
            for (Object component : components) {
                if (component instanceof OnlineGeocoding) {
                    this.onlinegeocoding = (OnlineGeocoding) component;
                    break;
                }
            }
        }
    }
}
