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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import pl.polsl.mostowska.integrationwebapp.model.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Servlet for performing numerical integration calculations.
 * Handles integration methods (trapezoidal, rectangle) and functions (linear, quadratic),
 * and provides the result in a web interface.
 * Also manages history of integration results and stores them in cookies.
 * 
 * @author Wiktoria Mostowska
 * @version 2.0
 */
@WebServlet(name = "IntegrationServlet", urlPatterns = {"/IntegrationServlet"})
public class IntegrationServlet extends HttpServlet {    
    private static EntityManagerFactory emf;
    /**
     * Initializes the servlet by setting up context attributes for integration models and history.
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
        
        if (getServletContext().getAttribute("rectangleModel") == null) {
        getServletContext().setAttribute("rectangleModel", new Rectangle());
        }
        
        if (getServletContext().getAttribute("trapezoidModel") == null) {
            getServletContext().setAttribute("trapezoidModel", new Trapezoid());
        }
        
        if (emf == null) {
            emf = Persistence.createEntityManagerFactory("pl.polsl.lab_WebJPADemo_war_1.0PU");
        }
    }
    
    /**
     * Creates a function model based on the function name and parameters.
     *
     * @param functionName the name of the function (e.g., "linear", "quadratic").
     * @param a the first parameter of the function (used only for quadratic functions).
     * @param b the second parameter of the function.
     * @param c the third parameter of the function.
     * @return a function model object (LinearFunction or QuadraticFunction).
     */
    private FunctionModel createFunction(String functionName, double a, double b, double c) {
        return switch (functionName) {
            case "linear" -> new LinearFunction(b, c);
            case "quadratic" -> new QuadraticFunction(a, b, c);
            default -> null;
        };
    }
    
    /**
     * Creates the integration parameters based on the bounds and partitions.
     *
     * @param lowerBound the lower bound of the integration.
     * @param upperBound the upper bound of the integration.
     * @param partitions the number of partitions for the integration.
     * @return an IntegrationParameters object containing the integration bounds and partitions.
     */
    private IntegrationParameters createIntegrationParameters(double lowerBound, double upperBound, int partitions) {
        return new IntegrationParameters(lowerBound, upperBound, partitions);
    }
    
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * This method performs integration calculations and returns the result to the client.
     *
     * @param request the servlet request.
     * @param response the servlet response.
     * @throws ServletException if a servlet-specific error occurs.
     * @throws IOException if an I/O error occurs.
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        ResultsEntity resultEntity = new ResultsEntity();
        FunctionEntity functionEntity = new FunctionEntity();
        List<String> history = (List<String>) getServletContext().getAttribute("history");
        try (PrintWriter out = response.getWriter()) {
            IntegrationModel model = (IntegrationModel) getServletContext().getAttribute("rectangleModel");
            String method = request.getParameter("method");
            String functionName = request.getParameter("function");
            double lowerBound = Double.parseDouble(request.getParameter("lowerBound"));
            double upperBound = Double.parseDouble(request.getParameter("upperBound"));
            double b = Double.parseDouble(request.getParameter("b"));
            double c = Double.parseDouble(request.getParameter("c"));
            int partitions = Integer.parseInt(request.getParameter("partitions"));
            String aParam = request.getParameter("a");
            
            if (lowerBound > upperBound) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Upper bound cannot be less than lower bound.");
                return;
            }
            
            double a;
            if (aParam == null || aParam.isEmpty()) {
                if ("quadratic".equals(functionName)) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameter a is required for quadratic functions.");
                    return;
                }
                a = 0;
            } else {
                a = Double.parseDouble(aParam);
            }
            
            FunctionModel function = createFunction(functionName, a, b, c);
            
            if ("trapezoid".equals(method)) {
                model = (IntegrationModel) getServletContext().getAttribute("trapezoidModel");
            } else if ("rectangle".equals(method)) {
                model = (IntegrationModel) getServletContext().getAttribute("rectangleModel");
            }

            if (function != null && model != null) {
                model.setFunction(function);
                IntegrationParameters parameters = createIntegrationParameters(lowerBound, upperBound, partitions);
                model.setParameters(parameters);
                double result = model.calculate();
                //var sameFunc = false;
//                for(FunctionEntity functionFromDB: findObjects()) {
//                    if (functionFromDB.getFunctionName().equals(functionName) && functionFromDB.getA().equals(a) && functionFromDB.getB().equals(b) && functionFromDB.getC().equals(c))
//                    {
//                        functionEntity = functionFromDB;
//                        sameFunc = true;
//                    }
//                }
                //if (sameFunc == false) {
                    functionEntity.setFunctionName(functionName);
                    functionEntity.setA(a);
                    functionEntity.setB(b);
                    functionEntity.setC(c);
                //}
                resultEntity.setFunction(functionEntity);
                resultEntity.setMethod(method);
                resultEntity.setLowerBound(lowerBound);
                resultEntity.setUpperBound(upperBound);
                resultEntity.setPartitions(partitions);
                resultEntity.setResult(result);
                
                functionEntity.getResults().add(resultEntity);
                persistObject(functionEntity);
                
                String record = "Method: " + method + ", Result: " + result +
                        " for bounds [" + lowerBound + ", " + upperBound + "] and " + partitions + " partitions, " +
                        "for function: " + functionName + " f(x) = " + (function instanceof QuadraticFunction ? a + "x² + " : "") + b + "x + " + c;
                
                String encodedRecord = URLEncoder.encode(record, StandardCharsets.UTF_8);
                Cookie resultCookie = new Cookie("lastResult", encodedRecord);
                resultCookie.setMaxAge(24 * 60 * 60);
                response.addCookie(resultCookie);
                history.add(record);
                
                out.println("<!DOCTYPE html>");
                out.println("<html>");
                out.println("<head>");
                out.println("<title>Integration Result</title>");
                out.println("</head>");
                out.println("<body>");
                out.println("<h1>Integration Result at " + request.getContextPath() + "</h1>");
                out.println("<p>" + record + "</p>");
                out.println("<a href='HistoryServlet'>Go to History</a>");
                out.println("<br>");
                out.println("<a href='index.html'>Back</a>");
                out.println("</body>");
                out.println("</html>");
            }
            else {out.println("<h1>Error: Invalid method or function selected.</h1>");}
        } catch (NumberFormatException e) {
            // Handle invalid number format
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid input data format.");
            request.getRequestDispatcher("index.html").forward(request, response);
        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid input data format.");
            request.getRequestDispatcher("index.html").forward(request, response);
        } catch (Exception e) {
            // Handle other exceptions
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred during the integration.");
            request.getRequestDispatcher("index.html").forward(request, response);
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
        return "Servlet for performing numerical integration calculations";
    }// </editor-fold>

    /**
     * Persists an object to the database using JPA.
     *
     * @param object the object to persist.
     */
    void persistObject(Object object) {
        //EntityManagerFactory emf = Persistence.createEntityManagerFactory("pl.polsl.lab_WebJPADemo_war_1.0PU");
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        try {
            em.persist(object);
            em.getTransaction().commit();
        } catch (PersistenceException e) {
            e.printStackTrace(); // replace with proper message for the client
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }
    
    /**
    * Finds a FunctionEntity based on its parameters.
    *
    * @param functionName the name of the function.
    * @param a the first parameter of the function.
    * @param b the second parameter of the function.
    * @param c the third parameter of the function.
    * @return the existing FunctionEntity or null if not found.
    */
    private FunctionEntity findFunctionByParameters(String functionName, double a, double b, double c) {
        //EntityManagerFactory emf = Persistence.createEntityManagerFactory("pl.polsl.lab_WebJPADemo_war_1.0PU");
        EntityManager em = emf.createEntityManager();
        FunctionEntity functionEntity = null;
        try {
            Query query = em.createQuery("SELECT f FROM FunctionEntity f WHERE f.functionName = :functionName AND f.a = :a AND f.b = :b AND f.c = :c");
            query.setParameter("functionName", functionName);
            query.setParameter("a", a);
            query.setParameter("b", b);
            query.setParameter("c", c);

            functionEntity = (FunctionEntity) query.getSingleResult();
        } catch (PersistenceException e) {
            // Function not found or another error occurred
        } finally {
            em.close();
        }
        return functionEntity;
    }
    
    /**
    * Finds and retrieves all objects of type FunctionEntity from the database.
    * This method uses JPA to query the database and return all the results.
    *
    * @return A list of FunctionEntity objects
    */
    public List<FunctionEntity> findObjects() {
        List<FunctionEntity> functionList = null;
        //EntityManagerFactory emf = Persistence.createEntityManagerFactory("pl.polsl.lab_WebJPADemo_war_1.0PU");
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        try {
            Query query = em.createQuery("SELECT f FROM FunctionEntity f");
            functionList = query.getResultList();
        } catch (PersistenceException e) {
            e.printStackTrace(); // replace with proper message for the client
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
        return functionList;
    }
}
