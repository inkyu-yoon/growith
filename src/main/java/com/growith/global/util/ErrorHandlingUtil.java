package com.growith.global.util;

import com.google.gson.Gson;
import com.growith.global.Response;
import com.growith.global.exception.ErrorCode;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

public class ErrorHandlingUtil {
    public static void sendErrorMessage(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType("application/json;charset=UTF-8");

        Response<String> errorMessage = Response.error(errorCode.getMessage());

        Gson gson = new Gson();
        String responseBody = gson.toJson(errorMessage);

        response.getWriter().write(responseBody);
    }

    public static void sendAlert(HttpServletResponse response) throws IOException {

        CookieUtil.setCookie(response, "jwt", "deleted", 0);

        response.setStatus(ErrorCode.TOKEN_NOT_FOUND.getHttpStatus().value());
        response.setContentType("application/json;charset=UTF-8");

        Response<String> errorMessage = Response.error(ErrorCode.TOKEN_NOT_FOUND.getMessage());

        Gson gson = new Gson();
        String responseBody = gson.toJson(errorMessage);

        response.getWriter().write(responseBody);
    }
}
