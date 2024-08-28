package work.chncyl.base.global.tools.result;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

import java.io.Serial;
import java.io.Serializable;

public class ApiResult<T> extends ResponseEntity<ApiResult.Result<T>> implements Serializable {
    public ApiResult(Result<T> body, HttpStatus status) {
        super(body, (HttpStatusCode) status);
    }

    public ApiResult(MultiValueMap<String, String> headers, HttpStatus status) {
        super(headers, (HttpStatusCode) status);
    }

    public ApiResult(Result<T> body, MultiValueMap<String, String> headers, HttpStatus status) {
        super(body, headers, status);
    }

    public static <T> ApiResult<T> error(String message, HttpStatus status) {
        Result<T> rtn = new Result<>();
        rtn.setMessage(message);
        rtn.setCode(status.value());
        rtn.setSuccess(false);
        rtn.setError(new ResultError(rtn.getCode(), message, message));
        return new ApiResult<>(rtn, status);
    }

    public static <T> ApiResult<T> error(String message, HttpStatus status, T data) {
        Result<T> rtn = new Result<>();
        rtn.setMessage(message);
        rtn.setCode(status.value());
        rtn.setSuccess(false);
        rtn.setResult(data);
        rtn.setError(new ResultError(rtn.getCode(), message, message));
        return new ApiResult<>(rtn, status);
    }

    public static <T> ApiResult<T> error500(String message, T data) {
        Result<T> rtn = new Result<>();
        rtn.setMessage(message);
        rtn.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        rtn.setSuccess(false);
        rtn.setResult(data);
        rtn.setError(new ResultError(rtn.getCode(), message, message));
        return new ApiResult<>(rtn, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static <T> ApiResult<T> error500(String message) {
        Result<T> rtn = new Result<>();
        rtn.setMessage(message);
        rtn.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        rtn.setSuccess(false);
        rtn.setError(new ResultError(rtn.getCode(), message, message));
        return new ApiResult<>(rtn, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static <T> ApiResult<T> error405(String message) {
        Result<T> rtn = new Result<>();
        rtn.setMessage(message);
        rtn.setCode(405);
        rtn.setSuccess(false);
        rtn.setError(new ResultError(rtn.getCode(), message, message));
        return new ApiResult<>(rtn, HttpStatus.METHOD_NOT_ALLOWED);
    }

    public static <T> ApiResult<T> error404(String message) {
        Result<T> rtn = new Result<>();
        rtn.setMessage(message);
        rtn.setCode(404);
        rtn.setSuccess(false);
        rtn.setError(new ResultError(rtn.getCode(), message, message));
        return new ApiResult<>(rtn, HttpStatus.NOT_FOUND);
    }

    public static ApiResult<Object> error401(String message) {
        Result<Object> rtn = Result.noauth(message);
        return new ApiResult<>(rtn, HttpStatus.UNAUTHORIZED);
    }

    public static <T> ApiResult<T> OK(T data) {
        Result<T> rtn = new Result<>();
        rtn.setCode(HttpStatus.OK.value());
        rtn.setSuccess(true);
        rtn.setResult(data);
        return new ApiResult<>(rtn, HttpStatus.OK);
    }

    @Data
    static class Result<T> implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        private boolean success = true;


        private String message = "";

        private Integer code = Integer.valueOf(0);

        private T result;

        private ApiResult.ResultError error;

        private long timestamp = System.currentTimeMillis();

        @JsonIgnore
        private String onlTable;

        public Result() {
        }

        public Result(Integer code, String message) {
            this.code = code;
            this.message = message;
            setError(new ApiResult.ResultError(code, message, ""));
        }

        @Deprecated
        public static Result<Object> ok() {
            Result<Object> r = new Result<>();
            r.setSuccess(true);
            r.setCode(HttpStatus.OK.value());
            return r;
        }

        @Deprecated
        public static Result<Object> ok(String msg) {
            Result<Object> r = new Result<>();
            r.setSuccess(true);
            r.setCode(HttpStatus.OK.value());
            r.setMessage(msg);
            return r;
        }

        @Deprecated
        public static Result<Object> ok(Object data) {
            Result<Object> r = new Result<>();
            r.setSuccess(true);
            r.setCode(HttpStatus.OK.value());
            r.setResult(data);
            return r;
        }

        public static <T> Result<T> OK() {
            Result<T> r = new Result<>();
            r.setSuccess(true);
            r.setCode(HttpStatus.OK.value());
            return r;
        }

        public static Result<String> OK(String msg) {
            Result<String> r = new Result<>();
            r.setSuccess(true);
            r.setCode(HttpStatus.OK.value());
            r.setMessage(msg);
            r.setResult(msg);
            return r;
        }

        public static <T> Result<T> OK(T data) {
            Result<T> r = new Result<>();
            r.setSuccess(true);
            r.setCode(HttpStatus.OK.value());
            r.setResult(data);
            return r;
        }

        public static <T> Result<T> OK(String msg, T data) {
            Result<T> r = new Result<>();
            r.setSuccess(true);
            r.setCode(HttpStatus.OK.value());
            r.setMessage(msg);
            r.setResult(data);
            return r;
        }

        public static <T> Result<T> error(String msg, T data) {
            Result<T> r = new Result<>();
            r.setSuccess(false);
            r.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            r.setMessage(msg);
            r.setResult(data);
            r.setError(new ApiResult.ResultError(r.getCode(), msg, ""));
            return r;
        }

        public static <T> Result<T> errorNoData(String msg) {
            Result<T> r = new Result<>();
            r.setSuccess(false);
            r.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            r.setMessage(msg);
            r.setError(new ApiResult.ResultError(r.getCode(), msg, ""));
            return r;
        }

        public static Result<Object> error(String msg) {
            return error(HttpStatus.INTERNAL_SERVER_ERROR.value(), msg);
        }

        public static Result<Object> error(int code, String msg) {
            Result<Object> r = new Result<>();
            r.setCode(code);
            r.setMessage(msg);
            r.setError(new ApiResult.ResultError(code, msg, ""));
            r.setSuccess(false);
            return r;
        }

        public Result<T> error500(String message) {
            this.message = message;
            this.code = HttpStatus.INTERNAL_SERVER_ERROR.value();
            this.success = false;
            setError(new ApiResult.ResultError(getCode(), message, message));
            return this;
        }

        public static Result<Object> noauth(String msg) {
            return error(HttpStatus.UNAUTHORIZED.value(), msg);
        }
    }

    @Data
    static class ResultError {

        private Integer code;

        private String details;

        private String message;

        public ResultError(Integer code, String message, String details) {
            this.code = code;
            this.details = details;
            this.message = message;
        }
    }
}