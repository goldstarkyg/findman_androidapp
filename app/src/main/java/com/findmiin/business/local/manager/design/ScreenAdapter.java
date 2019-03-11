package com.findmiin.business.local.manager.design;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * Created by JonIC on 2017-01-23.
 */
public class ScreenAdapter
{
    static Context m_Context = null;
    static int 		m_nDefaultSizeCx = 800;
    static int 		m_nDefaultSizeCy = 1280;

    static int 		m_nScreenSizeCx = 800;
    static int 		m_nScreenSizeCy = 1280;
    static float 	m_fRatioX = 1.0f;
    static float 	m_fRatioY = 1.0f;
    static float 	m_fDensity = 1.0f;


    public static void setApplicationContext( Context context )
    {
        m_Context = context;
        initLayoutParmas( context );
    }

    public static void setDefaultSize( int cx, int cy )
    {
        m_nDefaultSizeCx = cx;
        m_nDefaultSizeCy = cy;

        calcAdapterRatio();
    }
    private static void initLayoutParmas(Context context)
    {
        WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        m_nScreenSizeCx = wm.getDefaultDisplay().getWidth();
        m_nScreenSizeCy = wm.getDefaultDisplay().getHeight();

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        m_fDensity = metrics.density;

        calcAdapterRatio();
    }

    private static void calcAdapterRatio()
    {
        m_fRatioX = (float)m_nScreenSizeCx /  m_nDefaultSizeCx;
        m_fRatioY = (float)m_nScreenSizeCy /  m_nDefaultSizeCy;
    }

    public static int computeWidth( int defaultWidth )
    {
        if( defaultWidth < 0 )
            defaultWidth = 0;

//        return (int)(defaultWidth * m_fRatioX);
        return (int)(defaultWidth * m_fRatioY);
    }

    public static int computeHeight( int defaultHeight )
    {
        if( defaultHeight < 0 )
            defaultHeight = 0;

        return (int)(defaultHeight * m_fRatioY);
    }

    public static int getDeviceWidth()
    {
        return m_nScreenSizeCx;
    }

    public static int getDeviceHeight()
    {
        return m_nScreenSizeCy;
    }

    public static int dipToPx( int cx_dip )
    {
        return (int)(m_fDensity * cx_dip);
    }

    public static int inchToPx(float inch)
    {
        return (int)((inch * m_fDensity) * 160 + 0.5f);
    }

    public static int miliToPx(int mili)
    {
        return (int)((mili * m_fDensity / 254) * 160 + 0.5f);
    }
}

