package org.foo.app;

//import org.eclipse.jetty.server.Server;
//import org.eclipse.jetty.servlet.FilterHolder;
//import org.eclipse.jetty.servlet.ServletContextHandler;
//import org.eclipse.jetty.servlets.CrossOriginFilter;
//
//public class CorsConfig {
//
//    public static void main(String[] args) throws Exception {
//        Server server = new Server(8080);
//
//        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
//        context.setContextPath("/");
//        server.setHandler(context);
//
//        // Enable CORS
//        FilterHolder cors = context.addFilter(CrossOriginFilter.class, "/*", null);
//        cors.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,POST,HEAD,OPTIONS");
//        cors.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "http://example.com"); // or "*"
//        cors.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "Content-Type");
//
//        // Add your servlet or request handlers here
//
//        server.start();
//        server.join();
//    }
//}

//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//import org.springframework.web.filter.CorsFilter;
//
//@Configuration
//public class CorsConfig {
//
//    @Bean
//    public CorsFilter corsFilter() {
//        CorsConfiguration corsConfig = new CorsConfiguration();
//        corsConfig.addAllowedOrigin("http://localhost:3000"); // Allow requests from this origin
//        corsConfig.addAllowedHeader("*"); // Allow all headers
//        corsConfig.addAllowedMethod("*"); // Allow all HTTP methods
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", corsConfig);
//
//        return new CorsFilter(source);
//    }
//}
//
