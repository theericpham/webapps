package edu.ucla.cs.cs144;

import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.ucla.cs.cs144.*;

public class PayServlet extends HttpServlet implements Servlet {
	public PayServlet() {}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(true);
		if (session.isNew()) {
			// Session is invalid
			request.setAttribute("valid", "false");
		}
		else {
			request.setAttribute("valid", "true");
			Item item = (Item) session.getAttribute("item");
			request.setAttribute("item", item);
		}
		request.getRequestDispatcher("/WEB-INF/pay.jsp").forward(request, response);
	}
}