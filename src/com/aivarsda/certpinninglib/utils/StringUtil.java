package com.aivarsda.certpinninglib.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StringUtil 
{
	
	/** Gets the string representation of the InputStream
	 * @param is InputStream
	 * @return The string representation of the InputStream
	 */
	public static String inputStreamToString(InputStream is)
	{
		if(is != null)
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			StringBuilder sb = new StringBuilder();

			String line = null;
			try
			{
				while ((line = reader.readLine()) != null)
				{
					sb.append(line + "\n");
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			finally
			{
				try
				{
					is.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			return sb.toString();
		}
		
		return "";
	}
}
