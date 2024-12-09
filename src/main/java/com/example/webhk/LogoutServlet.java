package com.example.webhk;


import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/LogoutServlet")
public class LogoutServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false); // 获取当前会话
        if (session != null) {
            session.invalidate(); // 销毁会话
        }
        response.sendRedirect("index.jsp"); // 跳转到首页
    }
}
