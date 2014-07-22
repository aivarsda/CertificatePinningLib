package com.aivarsda.certpinninglib.logging;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.os.Environment;

public class Log
{
	static final boolean		IS_LOGGABLE	= true;
	public final static String 	LOG_PATH 	= "log_PinningCertificate.txt";

	public static void i(String tag, String string)
	{
		if (IS_LOGGABLE)
		{
			android.util.Log.i(tag, string);
			appendLog(string);
		}
	}

	public static void e(String tag, String string)
	{
		if (IS_LOGGABLE)
		{
			android.util.Log.e(tag, string);
			appendLog(string);
		}
	}

	public static void d(String tag, String string)
	{
		if (IS_LOGGABLE)
		{
			android.util.Log.d(tag, string);
			appendLog(string);
		}
	}

	public static void v(String tag, String string)
	{
		if (IS_LOGGABLE)
		{
			android.util.Log.v(tag, string);
			appendLog(string);
		}
	}

	public static void w(String tag, String string)
	{
		if (IS_LOGGABLE) 
		{
			android.util.Log.w(tag, string);
			appendLog(string);
		}
	}

	private static void appendLog(String text)
	{
		File sdcard = Environment.getExternalStorageDirectory();
		File logFile = new File(sdcard,Log.LOG_PATH);
		if (!logFile.exists())
		{
			try
			{
				logFile.createNewFile();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		try
		{
			BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
			buf.append(text);
			buf.newLine();
			buf.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
