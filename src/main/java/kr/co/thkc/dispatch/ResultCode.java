package kr.co.thkc.dispatch;

import lombok.Getter;

@Getter
public enum ResultCode {
	// Server
//	RC_ERR_UNKNOWN(9999, "UNKNOWN_ERROR"), // INIT
//	RC_ERR_INTERNAL_SERVER(-1, "INTERNAL_SERVER_ERROR"),
	RC_OK("N", "SUCCESS"),
	RC_FAIL("Y", "FAILED");

	private final String errorYN;
	private final String message;
	private ResultCode(String errorYN, String message) {
		this.errorYN = errorYN;
		this.message = message;
	}
//	static String getMessage(int code) {
//		ResultCode newCode =
//	}
}
