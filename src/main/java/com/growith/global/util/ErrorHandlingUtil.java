package com.growith.global.util;

import com.google.gson.Gson;
import com.growith.global.Response;
import com.growith.global.exception.ErrorCode;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class ErrorHandlingUtil {
    public static void sendErrorMessage(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType("application/json;charset=UTF-8");

        Response<String> errorMessage = Response.error(errorCode.getMessage());

        Gson gson = new Gson();
        String responseBody = gson.toJson(errorMessage);

        response.getWriter().write(responseBody);
    }
}
