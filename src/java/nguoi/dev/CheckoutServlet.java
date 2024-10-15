/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package nguoi.dev;

import jakarta.servlet.ServletException;
import java.io.IOException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import nguoi.dev.data.dao.DatabaseDao;
import nguoi.dev.data.dao.OrderDao;
import nguoi.dev.data.dao.OrderItemDao;
import nguoi.dev.data.model.Order;
import nguoi.dev.data.model.OrderItem;
import nguoi.dev.data.model.User;
import nguoi.dev.util.StringHelper;

/**
 *
 * @author USER
 */
public class CheckoutServlet extends BaseServlet {
   
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
       HttpSession session = request.getSession();
       User user = (User) session.getAttribute("user");
       
       if(user == null){
           response.sendRedirect("LoginServlet");
       } else{
           proccessCheckout(request, user);
           response.sendRedirect("CartServlet");
       }
    }

    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
    }

    private void proccessCheckout(HttpServletRequest request, User user) {
        OrderDao orderDao = DatabaseDao.getInstance().getOrderDao();
        String code = StringHelper.randomString(10);
        Order order = new Order(code, "pending", user.getId());
        orderDao.insert(order);

        order = orderDao.findByCode(code);
        HttpSession session = request.getSession();
        List<OrderItem> cart = (List<OrderItem>) session.getAttribute("cart");
        
        OrderItemDao orderItemDao = DatabaseDao.getInstance().getOrderItemDao();
        if(cart != null){
            for(OrderItem orderItem : cart){     
                    orderItem.setOrderId(order.getId());
                    orderItemDao.insert(orderItem);
            }
        }
        
        session.setAttribute("message", "Checkout Success");
        session.removeAttribute("cart");
    }


}
