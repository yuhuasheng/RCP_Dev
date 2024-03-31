package com.foxconn.tcutils.util;

import java.util.HashMap;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class AjaxResult extends HashMap<String, Object>
{
    private static final long  serialVersionUID = 1L;
    
    
    /**
     *   錯誤編碼  成功
     */
    public static final String STATUS_SUCCESS  = "0000";
    
    /**
     *   錯誤編碼  未查詢到數據
     */
    public static final String STATUS_NO_RESULT  = "2000";
    
    /**
     *   錯誤編碼  網絡連接失敗 
     */
    public static final String STATUS_NET_ERROR  = "4001";
    
    
    /**
     *   錯誤編碼  系统繁忙，请稍后再试
     */
    public static final String STATUS_SERVER_ERROR  = "4002";
    
    
    /**
     *   錯誤編碼  輸入參數錯誤
     */
    public static final String STATUS_PARAM_ERROR  = "4003";
    
    
    
    /**
     *   錯誤編碼  輸入參數缺失
     */
    public static final String STATUS_PARAM_MISS  = "4004";
    
    
    
    /**
     *   錯誤編碼  身份驗證無效
     */
    public static final String STATUS_AUTHOR_INVALID  = "4005";
    
    /**
     *   錯誤編碼  API 停用
     */
    public static final String STATUS_API_STOP  = "4009";
    
    
    /**
     *   錯誤編碼  API不存在
     */
    public static final String STATUS_API_NOT_FOUND  = "4010";
    
    /**
     * 状态码
     */
    public static final String CODE_TAG   = "code";
    /**
     * 返回内容
     */
    public static final String MSG_TAG   = "msg";
    /**
     * 数据对象
     */
    public static final String DATA_TAG   = "data";

    /**
     * 初始化一个新创建的 AjaxResult 对象，使其表示一个空消息。
     */
    public AjaxResult()
    {
    }

    /**
     * 初始化一个新创建的 AjaxResult 对象
     *
     * @param code 状态码
     * @param msg 返回内容
     */
    public AjaxResult(String code, String msg)
    {
        super.put(CODE_TAG, code);
        super.put(MSG_TAG, msg);
    }

    /**
     * 初始化一个新创建的 AjaxResult 对象
     *
     * @param code 状态码
     * @param msg 返回内容
     * @param data 数据对象
     */
    public AjaxResult(String code, String msg, Object data)
    {
        super.put(CODE_TAG, code);
        super.put(MSG_TAG, msg);
        if (data != null)
        {
            super.put(DATA_TAG, data);
        }
    }

    /**
     * 返回成功消息
     *
     * @return 成功消息
     */
    public static AjaxResult success()
    {
        return AjaxResult.success("操作成功");
    }

    /**
     * 返回成功数据
     *
     * @return 成功消息
     */
    public static AjaxResult success(Object data)
    {
        return AjaxResult.success("操作成功", data);
    }

    /**
     * 返回成功消息
     *
     * @param msg 返回内容
     * @return 成功消息
     */
    public static AjaxResult success(String msg)
    {
        return AjaxResult.success(msg, null);
    }

    /**
     * 返回成功消息
     *
     * @param msg 返回内容
     * @param data 数据对象
     * @return 成功消息
     */
    public static AjaxResult success(String msg, Object data)
    {
        return new AjaxResult(AjaxResult.STATUS_SUCCESS, msg, data);
    }


    
    /**
     * 返回错误消息
     *
     * @param code 状态码
     * @param msg 返回内容
     * @return 警告消息
     */
    public static AjaxResult error(String code, String msg)
    {
        return new AjaxResult(code, msg, null);
    }

    public String toString()
    {
        return JSONObject.toJSONString(this);
    }
}
