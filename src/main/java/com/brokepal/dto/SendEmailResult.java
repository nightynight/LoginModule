package com.brokepal.dto;

public final class SendEmailResult {
    private String result;
    private String errorInfo;

    private SendEmailResult(){
        this.result = "succeed";
    }

    private SendEmailResult(String errorInfo){
        this.result = "fail";
        this.errorInfo = errorInfo;
    }

    /**
     * 得到一个成功对象，result值为“succeed”，errorInfo值为null
     * @return
     */
    public static SendEmailResult newSucceedInstance(){
        return new SendEmailResult();
    }

    /**
     * 得到一个成功对象，result值为“failed”，errorInfo值为错误信息
     * @param errorInfo
     * @return
     */
    public static SendEmailResult newFailedInstance(String errorInfo){
        return new SendEmailResult(errorInfo);
    }

    public String getResult() {
        return result;
    }

    public String getErrorInfo() {
        return errorInfo;
    }
}
