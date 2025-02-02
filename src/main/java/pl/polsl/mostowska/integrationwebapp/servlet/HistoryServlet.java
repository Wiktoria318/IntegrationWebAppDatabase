package pl.polsl.mostowska.integrationwebapp.servlet;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Query;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import pl.polsl.mostowska.integrationwebapp.model.*;

/**
 * Servlet for displaying the calculation history of numerical integrations.
 * It retrieves the calculation history from the context and the last result from cookies.
 * 
 * @author Wiktoria Mostowska
 * @version 2.0
 */
@WebServlet(name = "HistoryServlet", urlPatterns = {"/HistoryServlet"})
public class HistoryServlet extends HttpServlet {
    private static EntityManagerFactory emf;
    /**
     * Initializes the servlet by setting up context attributes for history.
     * If they are not already present in the context, they are created.
     *
     * @throws ServletException if an error occurs during servlet initialization.
     */
    @Override
    public void init() throws ServletException {
        super.init();
    
        if (getServletContext().getAttribute("history") == null) {
            getServletContext().setAttribute("history", new ArrayList<String>());
        }
        
        if (emf == null) {
            emf = Persistence.createEntityManagerFactory("pl.polsl.lab_WebJPADemo_war_1.0PU");
        }
    }
    /**
     * Processes the HTTP request for both GET and POST methods.
     * It retrieves the calculation history from the context and displays it, 
     * along with the last result from the cookies.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        // Retrieve the history of calculations stored in the servlet context
        List<String> history = (List<String>) getServletContext().getAttribute("history");
        
        try (PrintWriter out = response.getWriter()) {
            // Retrieve the last result from the cookie
            Cookie[] cookies = request.getCookies();
            String lastResult = "No previous result available.";
            
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("lastResult".equals(cookie.getName())) {
                        lastResult = URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8);
                    }
                }
            }

            // Display the history to the user
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Calculation History</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Calculation History at " + request.getContextPath() + "</h1>");
            out.println("<p>Last Result from Cookies: " + lastResult + "</p>");
            // If history is empty, notify the user
//            if (history == null || history.isEmpty()) {
//                out.println("<p>No history available.</p>");
//            } else {
//                out.println("<ul>");
//                for (String record : history) {
//                    out.println("<li>" + record + "</li>");
//                }
//                out.println("</ul>");
//            }
            
            List<ResultsEntity> results = findObjects();
            if (results.isEmpty()) {
                out.println("<p>No history available in the database.</p>");
            } else {
                out.println("<p>History from database</p>");
                out.println("<table border='1'>");
                out.println("<tr><th>Method</th><th>Result</th><th>Bounds</th><th>Partitions</th><th>Function</th></tr>");
                for (ResultsEntity resultFromDB : results) {
                    out.println("<tr>");
                    out.println("<td>" + resultFromDB.getMethod() + "</td>");
                    out.println("<td>" + resultFromDB.getResult() + "</td>");
                    out.println("<td>[" + resultFromDB.getLowerBound() + ", " + resultFromDB.getUpperBound() + "]</td>");
                    out.println("<td>" + resultFromDB.getPartitions() + "</td>");
                    out.println("<td>" + resultFromDB.getFunction().getFunctionName() + " f(x) = "
                            + (resultFromDB.getFunction().getFunctionName().equals("quadratic") ? resultFromDB.getFunction().getA() + "x² + " : "")
                        + resultFromDB.getFunction().getB() + "x + " + resultFromDB.getFunction().getC() + "</td>");
                    out.println("</tr>");
                }
                out.println("</table>");
            }
            out.println("<a href='index.html'>Back</a>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    /**
     * Finds and retrieves all objects of type ResultsEntity from the database.
     * This method uses JPA to query the database and return all the results.
     *
     * @return A list of ResultsEntity objects
     */
    public List<ResultsEntity> findObjects() {
        List<ResultsEntity> resultList = null;
        //EntityManagerFactory emf = Persistence.createEntityManagerFactory("pl.polsl.lab_WebJPADemo_war_1.0PU");
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        try {
            Query query = em.createQuery("SELECT r FROM ResultsEntity r");
            resultList = query.getResultList();
        } catch (PersistenceException e) {
            e.printStackTrace(); // replace with proper message for the client
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
        return resultList;
    }
}
