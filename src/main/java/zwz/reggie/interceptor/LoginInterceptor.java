package zwz.reggie.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        Object employee = session.getAttribute("employee");
        if(employee!=null){
            return true;
        }
       else
            session.setAttribute("employee","请先登录");
            System.out.println("请求转发");
            //request.getRequestDispatcher("D:\\Java\\reggie-take-out\\src\\main\\resources\\backend\\page\\login\\login.html").forward(request,response);
            response.sendRedirect("backend/page/login/login.html");
            return false;
        }

    }

