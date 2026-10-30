package br.com.zg.acelera.filter

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.annotation.WebFilter
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

@WebFilter("/*")
class CorsFilter implements Filter {

    @Override
    void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        HttpServletResponse res = (HttpServletResponse) response
        HttpServletRequest req = (HttpServletRequest) request

        res.setHeader("Access-Control-Allow-Origin", "*")
        res.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
        res.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization")

        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            res.setStatus(HttpServletResponse.SC_OK)
            return
        }

        chain.doFilter(request, response)
    }
}