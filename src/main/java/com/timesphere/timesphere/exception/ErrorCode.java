package com.timesphere.timesphere.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {

    // === 100x: Common ===
    UNCATEGORIZED_EXCEPTION(9999, "Lỗi không xác định", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Khóa dữ liệu không hợp lệ", HttpStatus.BAD_REQUEST),

    // === 110x: Auth & Account ===
    EMAIL_ALREADY_REGISTERED(1101, "Email đã tồn tại.", HttpStatus.BAD_REQUEST),
    EMAIL_INVALID(1102, "Email không hợp lệ, hãy nhập đúng định dạng.", HttpStatus.BAD_REQUEST),
    EMAIL_REQUIRED(1103, "Vui lòng nhập email.", HttpStatus.BAD_REQUEST),
    PASSWORD_REQUIRED(1104, "Vui lòng nhập mật khẩu.", HttpStatus.BAD_REQUEST),
    PASSWORD_TOO_WEAK(1105, "Mật khẩu phải dài tối thiểu 8 ký tự, có chữ hoa, chữ thường, số và ký tự đặc biệt.", HttpStatus.BAD_REQUEST),
    PASSWORD_CONFIRMATION_MISMATCH(1106, "Mật khẩu xác nhận không khớp.", HttpStatus.BAD_REQUEST),
    INVALID_LOGIN_CREDENTIALS(1107, "Email hoặc mật khẩu không đúng.", HttpStatus.UNAUTHORIZED),
    WRONG_PASSWORD(1108, "Mật khẩu hiện tại không đúng.", HttpStatus.BAD_REQUEST),

    // === 120x: User ===
    USER_EXISTED(1201, "Người dùng đã tồn tại.", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1202, "Người dùng không tồn tại.", HttpStatus.NOT_FOUND),
    ROLE_REQUIRED(1203, "Vui lòng chọn vai trò cho người dùng.", HttpStatus.BAD_REQUEST),
    ROLE_NOT_SUPPORTED(1204, "Vai trò không được hỗ trợ.", HttpStatus.BAD_REQUEST),

    // === 130x: Auth Token ===
    UNAUTHENTICATED(1301, "Chưa xác thực người dùng.", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1302, "Không có quyền truy cập.", HttpStatus.FORBIDDEN),
    REFRESH_TOKEN_INVALID(1303, "Refresh token không hợp lệ hoặc đã hết hạn.", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED(1304, "Token đã hết hạn, vui lòng đăng nhập lại.", HttpStatus.UNAUTHORIZED),

    // === 200x: Team & Workspace ===
    TEAM_NOT_FOUND(2001, "Không tìm thấy nhóm.", HttpStatus.NOT_FOUND),
    TEAM_NAME_REQUIRED(2002, "Vui lòng nhập tên nhóm.", HttpStatus.BAD_REQUEST),
    USER_ALREADY_IN_TEAM(2003, "Người dùng đã là thành viên nhóm.", HttpStatus.CONFLICT),
    ONLY_OWNER_CAN_REMOVE(2004, "Chỉ OWNER mới có quyền xoá thành viên.", HttpStatus.FORBIDDEN),
    CANNOT_REMOVE_SELF(2005, "Không thể tự rời nhóm khi bạn là OWNER duy nhất.", HttpStatus.BAD_REQUEST),
    DESCRIPTION_TOO_LONG(2006, "Mô tả nhóm tối đa 500 ký tự.", HttpStatus.BAD_REQUEST),
    USER_NOT_IN_TEAM(2007, "Người dùng không phải là thành viên nhóm.", HttpStatus.NOT_FOUND),
    NOT_JOINED_ANY_TEAM(2008, "Bạn chưa tham gia nhóm nào.", HttpStatus.BAD_REQUEST),
    OWNER_CANNOT_LEAVE(2009, "OWNER không thể tự rời nhóm. Vui lòng chuyển quyền hoặc xoá nhóm.", HttpStatus.BAD_REQUEST),
    CANNOT_KICK_SELF(2010, "Bạn không thể xoá chính mình khỏi nhóm.", HttpStatus.BAD_REQUEST),

    // === 300x: Password Policy ===
    CURRENT_PASSWORD_REQUIRED(3001, "Vui lòng nhập mật khẩu hiện tại.", HttpStatus.BAD_REQUEST),
    NEW_PASSWORD_REQUIRED(3002, "Vui lòng nhập mật khẩu mới.", HttpStatus.BAD_REQUEST),
    CONFIRMATION_PASSWORD_REQUIRED(3003, "Vui lòng nhập xác nhận mật khẩu.", HttpStatus.BAD_REQUEST),
    ;
    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
}