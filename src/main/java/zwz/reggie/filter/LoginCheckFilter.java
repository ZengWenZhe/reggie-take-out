package zwz.reggie.filter;

import com.alibaba.fastjson.JSON;
import zwz.reggie.common.BaseContext;
import zwz.reggie.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// 自定义过滤器: 检查用户是否已经完成登录
//  urlPatterns = "/*": 拦截所有请求
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {

    //  路径匹配，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // 1、获取本次请求的URI----------------------------------
        String requestURI = request.getRequestURI();

        log.info("拦截到的请求:{}",requestURI);
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",  // 移动端 发送短信
                "/user/login"     //  移动端登录
        };
        // 2、判断本次请求是否需要处理(该次访问是否处于登录状态)------------------
        boolean checkLogin = isMatch(urls, requestURI);
        // 3、如果不需要处理，直接放行

        if (checkLogin){    // 直接放行请求
            log.info("本次请求 {} 不需要处理，直接放行",requestURI);
            filterChain.doFilter(request,response);
            return;
        }
        // 4-1、判断登录状态(session中含有employee的登录信息)，如果已经登录，则直接放行---------------
        Long empId = (Long) request.getSession().getAttribute("employee");
        if (empId != null){
            log.info("用户已经登录，用户id为:{}",empId);

            // 自定义元数据对象处理器 MyMetaObjectHandler中需要使用 登录用户id
            //   通过ThreadLocal set和get用户id
            BaseContext.setCurrentId(empId);

            filterChain.doFilter(request,response);
            return;
        }

        // 4-2、判断 移动端(消费者端)登录状态(session中含有employee的登录信息)，如果已经登录，则直接放行
        Long userId = (Long) request.getSession().getAttribute("user");
        if (userId != null){
            log.info("用户已经登录，用户id为:{}",userId);

            // 自定义元数据对象处理器 MyMetaObjectHandler中需要使用 登录用户id
            //   通过ThreadLocal set和get用户id
            BaseContext.setCurrentId(userId);

            filterChain.doFilter(request,response);
            return;
        }
        // 5、如果未登录，则返回未登录的结果
        // 通过输出流 向客户端页面响应数据
        // 返回结果需要是 backend/js/request.js 中写好的返回结果
        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(Result.error("NOTLOGIN")));
        return;

    }

    public boolean isMatch(String[] urls,String requestURI){
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match){
                return true;
            }
        }
        return false;
    }
}
