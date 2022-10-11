package Servlet;

import Constants.ServiceConstant;
import Schema.ResortsReq;
import Schema.ResponseMessage;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet
public class ResortServlet extends HttpServlet {
  private Gson gson = new Gson();
  @Override
  protected void doGet(
      HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
    res.setContentType("application/json");
    String urlPath = req.getPathInfo();
    String[] urlParts = new String[0];
    if(urlPath!=null) {
      urlParts = urlPath.split("/");
    }
    String service = getUrlType(urlParts);
    ResponseMessage msg;
    switch (service) {
      case ServiceConstant.GET_SKI_LIST: {
        PrintWriter out = res.getWriter();
        msg = new ResponseMessage("get a list of ski resorts in the database");
        String msgJson = this.gson.toJson(msg);
        out.println(msgJson);
        res.setStatus(HttpServletResponse.SC_OK);
        break;
      }
      case ServiceConstant.GET_UNIQUE_SKIER_NUM: {
        PrintWriter out = res.getWriter();
        msg = new ResponseMessage("get a number of unique skiers at resort/season/day");
        String msgJson = this.gson.toJson(msg);
        out.println(msgJson);
        res.setStatus(HttpServletResponse.SC_OK);
        break;
      }
      case ServiceConstant.GET_SEASON_SKI_LIST: {
        PrintWriter out = res.getWriter();
        msg = new ResponseMessage("get a list of season for the specified day");
        String msgJson = this.gson.toJson(msg);
        out.println(msgJson);
        res.setStatus(HttpServletResponse.SC_OK);
        break;
      }
      default: {
        PrintWriter out = res.getWriter();
        msg = new ResponseMessage("No service found");
        String msgJson = this.gson.toJson(msg);
        out.println(msgJson);
        res.setStatus(HttpServletResponse.SC_NOT_FOUND);
      }
    }

  }

  private String getUrlType(String[] urlPath) {
    if (urlPath.length == 0) {
      return ServiceConstant.GET_SKI_LIST;
    } else if(urlPath.length == 8
        && urlPath[2].equals("seasons")
        && urlPath[4].equals("day")
        && urlPath[6].equals("skiers")) {
      return ServiceConstant.GET_UNIQUE_SKIER_NUM;
    } else if(urlPath.length == 3
        && urlPath[2].equals("seasons")) {
      return ServiceConstant.GET_SEASON_SKI_LIST;
    }
    return ServiceConstant.SERVICE_NOT_FOUND;
  }

  private String postUrlType(String[] urlPath) {
    if(urlPath.length == 3 && urlPath[2].equals("seasons")) {
      return ServiceConstant.ADD_SEASON_FOR_RESORT;
    }
    return ServiceConstant.SERVICE_NOT_FOUND;
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
    res.setContentType("application/json");
    String urlPath = req.getPathInfo();
    String[] urlParts = urlPath.split("/");
    String resortId = urlParts[1];
    if(urlParts.length <= 2 || !isInteger(resortId)) {
      res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      ResponseMessage msg = new ResponseMessage("Invalid inputs");
      PrintWriter out = res.getWriter();
      String msgJson = this.gson.toJson(msg);
      out.println(msgJson);
      return;
    }

    if(!findResortById(resortId)) {
      res.setStatus(HttpServletResponse.SC_NOT_FOUND);
      res.getWriter().println(new ResponseMessage("Resort not found"));
      return;
    }
    StringBuilder sb = new StringBuilder();
    String s;
    while ((s = req.getReader().readLine()) != null) {
      sb.append(s);
    }
    ResortsReq json = (ResortsReq) gson.fromJson(sb.toString(), ResortsReq.class);
    int year = Integer.valueOf(json.getYear());

    ResponseMessage msg = new ResponseMessage("Add a new season for a resort on year "+year);
    PrintWriter out = res.getWriter();
    String msgJson = this.gson.toJson(msg);
    out.println();
    res.setStatus(HttpServletResponse.SC_CREATED);
  }

  private boolean findResortById(String resortId) {
    return true;
  }

  private boolean isInteger(String s) {
    try{
      int num = Integer.parseInt(s);
    } catch (NumberFormatException e) {
      return false;
    }
    return true;
  }


}
