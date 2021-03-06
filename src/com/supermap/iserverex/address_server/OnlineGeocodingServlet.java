package com.supermap.iserverex.address_server;

import com.supermap.iserverex.utils.JSONUtil;
import com.supermap.services.Interface;
import com.supermap.services.InterfaceContext;
import com.supermap.services.InterfaceContextAware;
import net.sf.json.JSONObject;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
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

    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        String jsoncallback = request.getParameter("jsoncallback");
        try {
            request.setCharacterEncoding("UTF-8");
            String JSON = request.getParameter("params");
            JSON = URLDecoder.decode(JSON, "UTF-8");
            JSONObject data = JSONObject.fromObject(JSON);
            String reqType = data.get("RequestType").toString();
            String paramType = data.get("ParamsType").toString();
            String jsonElements = data.get("Elements").toString();
            String ResponseStr = "";
            switch (reqType) {
//                case "Start":
//                    ResponseStr = onlinegeocoding.StartPOIServer(jsonElements, paramType);
//                    break;
//                case "Stop":
//                    ResponseStr = onlinegeocoding.StopPOISever();
//                    break;
//                case "NewClient":
//                    ResponseStr = onlinegeocoding.NewClientSocket();
//                    break;
                case "POISearch":
                    ResponseStr = onlinegeocoding.POISearch(jsonElements, paramType);
                    break;
//                case "POIStop":
//                    ResponseStr = onlinegeocoding.POI_Client_Stop(jsonElements, paramType);
//                    break;
            }
            PrintWriter writer = response.getWriter();
            System.out.println(ResponseStr);
            writer.println(jsoncallback + "(" +ResponseStr + ")");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Headers", "Content-Type");
            response.setHeader("Access-Control-Allow-Methods", "POST");
            request.setCharacterEncoding("utf-8");
            String JSON = request.getParameter("params");
            System.out.println(JSON);
            JSON = URLDecoder.decode(JSON, "utf-8");
            JSONObject data = JSONObject.fromObject(JSON);
            String reqType = data.get("RequestType").toString();
            String paramType = data.get("ParamsType").toString();
            String jsonElements = data.get("Elements").toString();
            String ResponseStr = "";
            switch (reqType) {
                case "POISearch":
                    ResponseStr = onlinegeocoding.POISearch(jsonElements, paramType);
                    break;
            }
            PrintWriter writer = response.getWriter();
            System.out.println(ResponseStr);
            writer.println(URLEncoder.encode(ResponseStr,"GBK"));
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
