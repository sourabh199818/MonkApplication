package org.example.utilis;

import org.example.dto.ResponseModel;

public class CustomMessages {
    public static final int INTERNAL_SERVER_ERROR = 500;
    public final static int GET_DATA_SUCCESS = 200;
    public final static int ALREADY_EXIST = 300;
    public final static int GET_DATA_ERROR = 400;
    public final static int NO_DATA_FOUND = 201;
    public final static int UNAUTHORIZED_ERROR = 401;




    public final static int ADD_SUCCESSFULLY = 301;
    public final static int UPDATE_SUCCESSFULLY = 302;
    public final static int DELETE_SUCCESSFULLY = 303;
    public final static int STATUS_UPDATED_SUCCESSFULLY = 304;



    public final static String SUCCESS = "success";

    public final static String FAILED = "failed";
    public final static String UNAUTHORIZED = "unauthorized";

    public final static String METHOD_GET = "GET";
    public final static String METHOD_POST = "POST";
    public final static String METHOD_DELETE = "DELETE";
    public final static String METHOD_PUT = "PUT";

    public final static String FAILURE = "Internal server error";

    public static ResponseModel makeResponseModel(Object data, String message, int code, String status) {
        ResponseModel responseModel = new ResponseModel();
        responseModel.setData(data);
        responseModel.setMessage(message);
        responseModel.setCode(code);
        responseModel.setStatus(status);
        return responseModel;
    }
}
