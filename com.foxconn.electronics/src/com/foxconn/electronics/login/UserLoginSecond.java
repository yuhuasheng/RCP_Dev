package com.foxconn.electronics.login;

import com.foxconn.electronics.util.TCJxbrowser;
import com.foxconn.tcutils.util.TCUtil;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.services.IUserLoginAuthenticator;
import com.teamcenter.rac.ui.common.RACUIUtil;
import com.teamdev.jxbrowser.browser.Browser;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class UserLoginSecond implements IUserLoginAuthenticator
{
    boolean                    userLoginAuthenticationStatus = false;
    private Browser            browser;
    private TCJxbrowser        jb;
    private Shell              shell;
    private static OSSUserPojo userPojo;
    private String             errorMsg                      = "CANCLE.LABEL";
    private ResourceBundle     reg;

    public UserLoginSecond()
    {
    }

    @Override
    public void runUserLoginAuthentication()
    {
        userOSSLogin();
        if (!userLoginAuthenticationStatus)
        {
            Display display = Display.getCurrent();
            if (display == null)
            {
                display = new Display();
            }
            MessageDialog.openError(display.getActiveShell(), "User Login Authenticator", errorMsg);
        }
    }

    @Override
    public boolean runUserLoginAuthentication2()
    {
        reg = ResourceBundle.getBundle("com.foxconn.electronics.login.login_locale");
        TCSession tcSesson = RACUIUtil.getTCSession();
        if (tcSesson != null && tcSesson.getUser() != null)
        {
            userOSSLogin();
            if (!userLoginAuthenticationStatus)
            {
                Display display = Display.getCurrent();
                if (display == null)
                {
                    display = new Display();
                }
                MessageDialog.openError(display.getActiveShell(), "User Login Authenticator", reg.getString(errorMsg));
            }
        }
        return userLoginAuthenticationStatus;
    }

    /**
     * 打开 相信登录的窗口
     * 
     * @param display
     * @param url
     */
    public void openOSSLoginWindow(Display display, String url)
    {
        shell = new Shell(display.getActiveShell(), SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL | SWT.SHELL_TRIM);
        shell.setSize(680, 660);
        shell.setText(reg.getString("TITLE.LABEL"));
        shell.setLayout(new FillLayout());
        TCUtil.centerShell(shell);
        Composite mainComposite = new Composite(shell, SWT.EMBEDDED | SWT.NO_BACKGROUND);
        Frame frame = SWT_AWT.new_Frame(mainComposite);
        frame.setLayout(new BorderLayout());
        jb = new TCJxbrowser(url);
        browser = jb.getBrowser();
        frame.add(jb.getBrowserView());
        frame.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosed(WindowEvent var1)
            {
                super.windowClosed(var1);
                browser.close();
            }
        });
        shell.open();
        shell.layout();
        while (!shell.isDisposed())
        {
            if (!display.readAndDispatch())
            {
                display.sleep();
            }
        }
    }

    /**
     * 在单独的线程中轮询 获取相信登录结果 及用户信息
     * 
     * @param display
     * @param key
     */
    private void verificationLogin(Display display, String key)
    {
        Long startTime = System.currentTimeMillis();
        new Thread(() -> {
            while (true)
            {
                Long currenTime = System.currentTimeMillis();
                // 设置 5分钟超时
                if ((currenTime - startTime) / 1000 > 5 * 60)
                {
                    System.out.println("timeout exit !");
                    closeOSSLoginWindow(display);// 超时关闭相信登录界面
                    break;
                }
                try
                {
                    Thread.sleep(500);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                if (browser != null && !browser.isClosed())
                {
                    String url = TCUtil.getPreference(RACUIUtil.getTCSession(), TCPreferenceService.TC_preference_site, "D9_SpringCloud_URL");
                    String userJson = HttpRequest.get(url + "/tc-integrate-civet/sso/getSsoState?tcKey=" + key).execute().body();
                    //String userJson = HttpRequest.get("http://10.203.163.43/tc-integrate-civet/sso/getSsoState?tcKey=" + key).execute().body();
                    System.out.println(key + " -------- >> " + userJson);
                    if (StrUtil.isNotBlank(userJson))
                    {
                        try
                        {
                            userPojo = JSONUtil.toBean(userJson, OSSUserPojo.class);
                            if (ObjectUtil.isNull(userPojo))
                            {
                                throw new TCException("獲取用戶失敗");
                            }
                            IEclipsePreferences rooNode = Platform.getPreferencesService().getRootNode();
                            rooNode.put("ssoUserPojo", userJson);
                            System.out.println("userPojo :: " + userPojo.getEmp_no());
                            TCSession tcSesson = RACUIUtil.getTCSession();
                            TCComponentUser user = tcSesson.getUser();
                            // 請求二級賬號和一級賬號是否對於
                            String result = HttpUtil.get(url + "/tc-service/issueManagement/getUserAccountByUid?uid=" + user.getUid());
                            // String url = "http://10.205.56.204:8888/issueManagement/getUserAccountByUid?uid="+user.getUid();
                            // String result = HttpUtil.get(url);
                            JSONObject parseObj = JSONUtil.parseObj(result);
                            String code = parseObj.getStr("code");
                            if (!"0000".equals(code))
                            {
                                throw new TCException("獲取用戶失敗");
                            }
                            JSONArray jsonArray = parseObj.getJSONArray("data");
                            if (CollUtil.isNotEmpty(jsonArray))
                            {
                                // 判斷是不是管理員
                                List<Object> collect2 = jsonArray.parallelStream().filter(item -> {
                                    JSONObject object = JSONUtil.parseObj(item);
                                    return "PLM".equals(object.get("dept")) && object.getStr("no").equals(userPojo.getEmp_no().toLowerCase());
                                }).collect(Collectors.toList());
                                if (CollUtil.isNotEmpty(collect2))
                                {
                                    // 管理員直接登錄
                                    JSONObject obj = JSONUtil.parseObj(collect2.get(0));
                                    userPojo.setDept(obj.getStr("bu", "") + "+" + obj.getStr("platform", "") + "+" + obj.getStr("dept", ""));
                                    userLoginAuthenticationStatus = true;
                                }
                                else
                                {
                                    // 非管理員判斷是否配置二級賬號
                                    List<Object> collect = jsonArray.parallelStream().filter(item -> {
                                        JSONObject object = JSONUtil.parseObj(item);
                                        return !"PLM".equals(object.get("dept"));
                                    }).collect(Collectors.toList());
                                    if (CollUtil.isEmpty(collect))
                                    {
                                        // 沒有配置二級賬號
                                        userLoginAuthenticationStatus = true;
                                    }
                                    else
                                    {
                                        // 配置了二級賬號，判斷二級賬號是否對應
                                        List<Object> collect3 = collect.parallelStream().filter(item -> {
                                            JSONObject object = JSONUtil.parseObj(item);
                                            return object.getStr("no").equals(userPojo.getEmp_no().toLowerCase());
                                        }).collect(Collectors.toList());
                                        if (CollUtil.isEmpty(collect3))
                                        {
                                            userLoginAuthenticationStatus = false;
                                            errorMsg = "SYSTEM.LABEL";
                                        }
                                        else
                                        {
                                            JSONObject obj = JSONUtil.parseObj(collect3.get(0));
                                            userPojo.setDept(obj.getStr("bu", "") + "+" + obj.getStr("platform", "") + "+" + obj.getStr("dept", ""));
                                            userLoginAuthenticationStatus = true;
                                        }
                                    }
                                }
                            }
                            else
                            {
                                // 沒有二級賬號，直接登錄進去
                                userLoginAuthenticationStatus = true;
                            }
                        }
                        catch (Exception e)
                        {
                            userLoginAuthenticationStatus = false;
                            errorMsg = "VERIFICATION.LABEL";
                        }
                        finally
                        {
                            closeOSSLoginWindow(display);
                            break;
                        }
                    }
                }
                else
                {
                    System.out.println("browser != null  " + (browser != null));
                    if (browser != null)
                    {
                        System.out.println("browser.isClosed()  " + (!browser.isClosed()));
                    }
                }
            }
        }).start();
    }

    private void closeOSSLoginWindow(Display display)
    {
        browser.close();
        jb.close();
        display.syncExec(() -> {
            shell.dispose();
        });
    }

    private void userOSSLogin()
    {
        Display display = Display.getCurrent();
        if (display == null)
        {
            display = new Display();
        }
        String uuid = UUID.randomUUID().toString();
        if (StringUtils.isNotEmpty(uuid))
        {
            verificationLogin(display, uuid);
            String url = TCUtil.getPreference(RACUIUtil.getTCSession(), TCPreferenceService.TC_preference_site, "D9_SpringCloud_URL");
            openOSSLoginWindow(display, "https://sso.foxconn.com/connect/authorize?client_id=TcSS&redirect_uri="+  URLUtil.encode(url) +"%2Ftc-integrate-civet%2FtcSSO&response_type=code&scope=openid%20profile%20foxconn&state=" + uuid);
            System.out.println("display   display   userLoginAuthenticationStatus : " + userLoginAuthenticationStatus);
        }
        else
        {
            System.out.println("uuid is null :: " + uuid);
        }
    }

    public static OSSUserPojo getOSSUserInfo()
    {
        return userPojo;
    }
}
