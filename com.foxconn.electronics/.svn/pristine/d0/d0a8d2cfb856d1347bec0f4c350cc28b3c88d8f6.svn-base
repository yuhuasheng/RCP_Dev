package com.foxconn.electronics.util;

import java.awt.BorderLayout;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.engine.EngineOptions;
import com.teamdev.jxbrowser.engine.RenderingMode;
import com.teamdev.jxbrowser.frame.Frame;
import com.teamdev.jxbrowser.view.swing.BrowserView;

public class TCJxbrowser
{
    private Browser browser;

    public TCJxbrowser(String url)
    {
        initJxbrowser();
        browser.navigation().loadUrl(url);
    }

    public void clearCache()
    {
    	browser.profile().cookieStore().deleteAll();
	    browser.profile().httpCache().clear();
        List<Frame> frames = browser.frames();
        for (Frame frame : frames) {
            frame.sessionStorage().clear();
            frame.localStorage().clear();
        }
    }

    public Browser getBrowser()
    {
        return browser;
    }
    
    public void close()
    {
        browser.close();
        browser.engine().close();
    }

    public BrowserView getBrowserView()
    {
        BrowserView view1 = BrowserView.newInstance(browser);
        return view1;
    }

    private void initJxbrowser()
    {
    	System.setProperty("jxbrowser.license.key", "6P835FT5HAPTB03TPIEFPGU5ECGJN8GMGDD79MD7Y52NVP0K0IV6FHYZVQI25H0MLGI2");
        System.setProperty("jxbrowser.logging.level", "INFO");
        EngineOptions.Builder builder = EngineOptions.newBuilder(RenderingMode.HARDWARE_ACCELERATED);
        builder.remoteDebuggingPort(9222);
        EngineOptions option = builder.build();
        Engine engine = Engine.newInstance(option);
        browser = engine.newBrowser();
    }

    public static void main(String[] args)
    {
        // eg:
        JFrame frame1 = new JFrame();
        frame1.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        TCJxbrowser jb = new TCJxbrowser("www.baidu.com");
        frame1.add(jb.getBrowserView(), BorderLayout.CENTER);
        frame1.setSize(1600, 900);
        frame1.setLocationRelativeTo(null);
        frame1.setVisible(true);
    }
}
