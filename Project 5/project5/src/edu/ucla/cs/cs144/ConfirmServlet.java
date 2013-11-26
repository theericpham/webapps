package edu.ucla.cs.cs144;

import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.text.SimpleDateFormat;
import java.util.Date;

import edu.ucla.cs.cs144.*;

public class ConfirmServlet extends HttpServlet implements Servlet {
	public ConfirmServlet() {}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(true);
		if (session.isNew()) request.setAttribute("valid", "false");
		session.invalidate();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(true);
		if (session.isNew()) request.setAttribute("valid", "false");
		else request.setAttribute("valid", "true");

		if (request.isSecure()) request.setAttribute("secure", "true");
		else request.setAttribute("secure", "false");

		String cardNumber = request.getParameter("card-number");
		session.setAttribute("card-number", cardNumber);

		SimpleDateFormat sdf = new SimpleDateFormat("MMMM  d, yyyy hh:mm aaa");
		Date d = new Date();
		String purchaseTime = sdf.format(d);
		session.setAttribute("purchase-time", purchaseTime);

		request.getRequestDispatcher("/WEB-INF/confirm.jsp").forward(request, response);
	}
}
