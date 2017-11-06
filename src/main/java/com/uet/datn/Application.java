package com.uet.datn;

import com.uet.datn.services.StorageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@SpringBootApplication
public class Application implements CommandLineRunner {

    @Resource
    StorageService storageService;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        storageService.deleteAll();
        storageService.init();
    }

    @Bean
    public FilterRegistrationBean corsFilter() {
        return new FilterRegistrationBean(new Filter() {
            public void doFilter(ServletRequest req, ServletResponse res,
                                 FilterChain chain) throws IOException, ServletException {
                HttpServletRequest request = (HttpServletRequest) req;
                HttpServletResponse response = (HttpServletResponse) res;
                String method = request.getMethod();
                response.setHeader("Access-Control-Allow-Origin", "*");
                response.setHeader("Access-Control-Allow-Methods",
                        "POST,GET,OPTIONS,DELETE,PUT");
                response.setHeader("Access-Control-Max-Age", Long.toString(60 * 60));
                response.setHeader("Access-Control-Allow-Credentials", "true");
                response.setHeader(
                        "Access-Control-Allow-Headers",
                        "Origin,Accept,X-Requested-With,Content-Type,Access-Control-Request-Method," +
                                "Access-Control-Request-Headers,Authorization," +
                                "auth-token,ra_preferred_ip," +
                                "Pragma,Cache-Control,Expires,PageToken");
                if ("OPTIONS".equals(method)) {
                    response.setStatus(HttpStatus.OK.value());
                } else {
                    chain.doFilter(req, res);
                }
            }

            public void init(FilterConfig filterConfig) {
            }

            public void destroy() {
            }
        });
    }
}
