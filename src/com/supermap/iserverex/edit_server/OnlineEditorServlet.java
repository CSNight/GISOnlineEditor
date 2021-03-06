package com.supermap.iserverex.edit_server;

import com.supermap.services.Interface;
import com.supermap.services.InterfaceContext;
import com.supermap.services.InterfaceContextAware;
import net.sf.json.JSONObject;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.List;

@Interface(componentTypes = {OnlineEditor.class}, optional = false, multiple = false)
public class OnlineEditorServlet extends HttpServlet implements
        InterfaceContextAware {
    private static final long serialVersionUID = 1L;
    private String id;
    private OnlineEditor onlineeditor = null;

    public OnlineEditorServlet() {
        this.id = String.valueOf(System.currentTimeMillis());
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) {
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) {
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
            String ResponseStr = "";
            switch (reqType) {
                case "Add":
                    ResponseStr = onlineeditor.InsertFeature(jsonElements, paramType);
                    break;
                case "Update":
                    ResponseStr = onlineeditor.UpdateFeature(jsonElements, paramType);
                    break;
                case "Delete":
                    ResponseStr = onlineeditor.DeleteFeature(jsonElements, paramType);
                    break;
                case "QueryByID":
                    ResponseStr = onlineeditor.QueryByFeatureIDAndDataset(jsonElements, paramType);
                    break;
                case "QueryBySet":
                    ResponseStr = onlineeditor.QueryByDatasetName(jsonElements, paramType);
                    break;
                case "BorderCheck":
                    ResponseStr = onlineeditor.BorderConflictCheck(jsonElements, paramType);
                    break;
                case "BorderTopCheck":
                    ResponseStr = onlineeditor.BorderTopClassConflictCheck(jsonElements, paramType);
                    break;
            }
            PrintWriter writer = response.getWriter();
            System.out.println(ResponseStr);
            writer.println(ResponseStr);
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
                if (component instanceof OnlineEditor) {
                    this.onlineeditor = (OnlineEditor) component;
                    break;
                }
            }
        }
    }
}
