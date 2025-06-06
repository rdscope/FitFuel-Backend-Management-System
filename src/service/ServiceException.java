package service;
//  Service 層專用自訂例外，用於封裝 DAO 層 SQLException 與業務邏輯錯誤
public class ServiceException extends Exception {
    public ServiceException(String message) {
        super(message);
    }
    // 呼叫父類別 Exception 的建構子，傳入錯誤訊息。

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
    // 呼叫父類別 Exception 的建構子，傳入錯誤訊息 + 原始例外（Cause）。
}
