package Servlet;

import Constants.ServiceConstant;
import Schema.LiftRide;
import Schema.ResortsReq;
import Schema.ResponseMessage;
import Schema.TotalVertival;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet
public class SkierServlet extends HttpServlet {
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
      case ServiceConstant.GET_SKI_DAY_VERT: {
        PrintWriter out = res.getWriter();
        TotalVertival totalVertival = new TotalVertival(34507);
        String msgJson = this.gson.toJson(totalVertival);
        out.println(msgJson);
        res.setStatus(HttpServletResponse.SC_OK);
        break;
      }
      case ServiceConstant.GET_TOTAL_VERT: {
        PrintWriter out = res.getWriter();
        msg = new ResponseMessage("get the total vertical for the skier for specified seasons at the specified resort");
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
    if(urlPath.length == 8
        && urlPath[2].equals("seasons")
        && urlPath[4].equals("days")
        && urlPath[6].equals("skiers")) {
      return ServiceConstant.GET_SKI_DAY_VERT;
    } else if(urlPath.length == 3
        && urlPath[2].equals("vertical")) {
      return ServiceConstant.GET_TOTAL_VERT;
    }
    return ServiceConstant.SERVICE_NOT_FOUND;
  }

  private String postUrlType(String[] urlPath) {
    if(urlPath.length == 3 && urlPath[2].equals("seasons")) {
      return ServiceConstant.WRITE_LIFT_TO_SKIER;
    }
    return ServiceConstant.SERVICE_NOT_FOUND;
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
    res.setContentType("application/json");
    String urlPath = req.getPathInfo();
    String[] urlParts = urlPath.split("/");
    String resortId = urlParts[1];
    String seasonId = urlParts[3];
    String dayId = urlParts[5];
    String skierId = urlParts[7];
    if(urlParts.length <= 1
        || !isInteger(resortId)
        || !isInteger(seasonId)
        || !isInteger(dayId)
        || !isInteger(skierId)) {
      res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      ResponseMessage msg = new ResponseMessage("Invalid inputs");
      PrintWriter out = res.getWriter();
      String msgJson = this.gson.toJson(msg);
      out.println(msgJson);
      return;
    }

    if(!findResortById(resortId)
        || !findSeasonById(seasonId)
        || !findDayById(dayId)
        || !findSkierById(skierId)) {
      res.setStatus(HttpServletResponse.SC_NOT_FOUND);
      ResponseMessage msg = new ResponseMessage("Data Not Found");
      PrintWriter out = res.getWriter();
      String msgJson = this.gson.toJson(msg);
      out.println(msgJson);
      return;
    }

    StringBuilder sb = new StringBuilder();
    String s;
    while ((s = req.getReader().readLine()) != null) {
      sb.append(s);
    }
    LiftRide json = (LiftRide) gson.fromJson(sb.toString(), LiftRide.class);
    int time = Integer.valueOf(json.getTime());
    int liftId = Integer.valueOf(json.getLiftID());

    ResponseMessage msg = new ResponseMessage("Write a new lift ride for the skier with time-"+time+" and liftID-"+liftId);
    PrintWriter out = res.getWriter();
    String msgJson = this.gson.toJson(msg);
    out.println();
    res.setStatus(HttpServletResponse.SC_CREATED);
  }

  private boolean findResortById(String resortId) {
    return true;
  }
  private boolean findSeasonById(String seasonId) {
    return true;
  }
  private boolean findDayById(String dayId) {
    return true;
  }
  private boolean findSkierById(String skierId) {
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

