package Servlet;

import Schema.ResponseMessage;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Zhining
 * @description
 * @create 2022-10-02-17-17
 **/

@WebServlet
public class StatisticServlet extends HttpServlet {
  private Gson gson = new Gson();
  @Override
  protected void doGet(
      HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
    res.setContentType("text/plain");
    ResponseMessage msg = new ResponseMessage("statistics data");
    String msgJson = this.gson.toJson(msg);
    PrintWriter out = res.getWriter();
    out.println(msgJson);
    res.setStatus(HttpServletResponse.SC_OK);
  }

}
