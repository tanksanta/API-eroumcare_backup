package kr.co.thkc.dispatch;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;

@Slf4j
@Data
public class BaseResponse {

	public BaseResponse() {
		setResult(ResultCode.RC_FAIL);
	}
	private String errorYN;
	private String message;
	private Object data;
	private Integer total;

	public BaseResponse setResult(ResultCode code) {
		return setResult(code, code.getMessage());
	}

	public BaseResponse setResult(ResultCode code, String msg) {
		errorYN = code.getErrorYN();
		message = msg;
		return this;
	}

	
	public BaseResponse setResultData(Object resultData) throws SQLException {
		this.data = resultData;
		return this;
	}

	public BaseResponse setResultMsg(String resultMsg) throws SQLException {
		this.message = resultMsg;
		return this;
	}
}
