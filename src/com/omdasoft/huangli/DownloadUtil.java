package com.omdasoft.huangli;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;
import net.yihabits.huangli.R;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;

public class DownloadUtil {

	private String basePath; // external storage path
	private String encode = "gb2312";

	public DownloadUtil() {
		this.basePath = initBaseDir();
	}


	public String saveHtml(String url) {
		// 1.remove ? & / from url and get the path
		String dir = initBaseDir() + "/huangli/";
		initDir(dir);
		
		int urlType = 0; // 0 - today; 1 - search; 2 - detail
		if(url.contains("/tools_selectGooday.php")){
			urlType = 0;
		}else if(url.contains("/huangli_search.php")){
			urlType = 1;
		}else if(url.contains("/huangli_details.php")){
			urlType = 2;
		}else{
			urlType = 3;
		}
		String path = "";
		switch(urlType){
		case 0:{
			path = getDayFileName(new Date());
			break;
		}
		case 1:{
			path = parseFileNameByUrl(url);
			break;
		}
		case 2:{
			path = parseFileNameByUrl(url);
			break;
		}
			default:{
				return null;
			}
		}
		path = dir + path;
		File tmp = new File(path);
		if (tmp.exists()){
			return "file://" + path;
		}

		HttpEntity entity = null;
		try {
			HttpClient httpclient = new DefaultHttpClient();

			// loop download all of images from url
			
			HttpGet httpget = new HttpGet(url);
			HttpResponse response = httpclient.execute(httpget);

			int status = response.getStatusLine().getStatusCode();

			if (status == HttpStatus.SC_OK) {
				entity = response.getEntity();
				if (entity != null) {
					String content = EntityUtils.toString(entity, encode);
					Source source = new Source(content);

					// 2.deal with content of page
					switch(urlType){
					case 0:{

						int start = content.indexOf("<table width=\"647\"");
						content = content.substring(start);
						int end = content.indexOf("</table>") + 8;
						int inter = content.substring(end).indexOf("</table>") + 8;
						end = content.substring(end + inter).indexOf("</table>") + end + inter;
						content = content.substring(6, end);
						start = content.indexOf("<table");
						end = content.indexOf("</table>") + 8;
						String table1 = content.substring(start, end);
						content = content.substring(end);
						start = content.indexOf("<table");
						end = content.indexOf("</table>") + 8;
						String table2 = content.substring(start, end);
						content = "<TR><TD  width=\"183\" height=\"159\">" + table1 + "</TD><TD></TD></TR>" + "<TR><TD  nowrap colspan=\"2\">" + table2 + "</TD></TR>";
						
						//deal with imgaes
						content = content.replace("background=\"", "background=\"http://www.99wed.com/tools/");
						content = content.replace("src=\"", "src=\"http://www.99wed.com/tools/");

						content = addHeadEnd(content, "Today", "", encode);
						break;
					}
					case 1:{
						// get page links
						String page = "";
						Element pele = source.getFirstElementByClass("wenzi12");
						if (pele != null) {
							List<Element> tlist = pele.getAllElements(HTMLElementName.A);
							for (Element ele : tlist) {
								if(!ele.getAttributeValue("href").endsWith(".php")){
								page += ele.toString() + "&nbsp;&nbsp;";
								}

							}
//							page = "<tr>" + pele.toString() + "</tr>";
						}

						int start = content.indexOf("<table width=\"586\"");
						content = content.substring(start);
						int end = content.indexOf("</table>") + 8;
						content = content.substring(0,end);

											List<Element> trlist = new Source(content).getAllElements(HTMLElementName.TR);
											content = trlist.get(1).toString();
											content += trlist.get(2);
							
							content = content.replace("href=", "href=http://www.99wed.com/tools/");

						content = addHeadEnd(content, "search", page, encode);
						break;
					}
					case 2:{
						int start = content.indexOf("<table width=\"586\"");
						content = content.substring(start);
						int end = content.indexOf("</table>") + 8;
						content = content.substring(0,end);

											List<Element> trlist = new Source(content).getAllElements(HTMLElementName.TR);
											content = trlist.get(0).toString();
											content += trlist.get(1);

						content = addHeadEnd(content, "detail", "", encode);
						break;
					}
						default:{
							return null;
						}
					}
					// 3.save html page
					save2card(content.getBytes("gb2312"), path);

					// get content
					return "file://" + path;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			if (entity != null) {
				try {
					entity.consumeContent();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return null;
	}


	private String parseFileNameByUrl(String url) {
		String res = "";
		int start = url.lastIndexOf("/") + 1;
		res = url.substring(start);
		res = res.replace("&", "_");
		res = res.replace("=", "_");
		res = res.replace("?", "_");
		res = res.replace("%", "_");
		if(!res.endsWith(".html")){
			res += ".html";
		}
		return res;

	}

	private void save2card(byte[] bytes, String path) {
		try {
			// save to sdcard
			FileOutputStream fos = new FileOutputStream(new File(path));
			IOUtils.write(bytes, fos);

			// release all instances
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public String initBaseDir() {
		File sdDir = Environment.getExternalStorageDirectory();
		File uadDir = null;
		if (sdDir.exists() && sdDir.canWrite()) {

		} else {
			sdDir = Environment.getDataDirectory();

		}
		uadDir = new File(sdDir.getAbsolutePath() + "/download/");
		if (uadDir.exists() && uadDir.canWrite()) {
			
		} else {
			uadDir.mkdir();
			File hDir = new File(sdDir.getAbsolutePath() + "/download/huangli/");
			hDir.mkdir();
		}
		return uadDir.getAbsolutePath();
	}


	private void initDir(String dir) {
		File sdDir = new File(dir);
		if (sdDir.exists() && sdDir.canWrite()) {

		} else {
			sdDir.mkdirs();

		}
	}

	private String addTrTd(String content) {
		return "<TR><TD>" + content + "</TD></TR>";
	}
	
	private String addHeadEnd(String content, String title, String page, String encode) {
		content = "<html><head><title>"
				+ title
				+ "</title><meta http-equiv='Content-Type' content='text/html; charset=" + encode + "'>"
				+ "</head><body>"
				+ "<table id='cTable' style='display:block' border='0'>"
				+ content + "</table><table border='0'>" + page
				+ "</table>" + "<br/><br/></body></html>";
		return content;
	}
	
	private String getDayFileName(Date date) {
		SimpleDateFormat formatter4datetime = new SimpleDateFormat(
				"yyyy-MM-dd");

		String dir = formatter4datetime.format(date);
		return dir + ".html";
	}
	
public static String getDayId(String dateStr) {
		
		SimpleDateFormat formatter4datetime = new SimpleDateFormat(
				"yyyy-MM-dd");

		Date start = null;
		Date date = null;
		try {
			start = formatter4datetime.parse("2004-02-29");
			date  = formatter4datetime.parse(dateStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		long diff = date.getTime() - start.getTime();
		
		int mod = 60*60*1000*24;
		
		return String.valueOf(diff/mod + 1487);
	}
	
	public static String getDayId(Date date) {
		SimpleDateFormat formatter4datetime = new SimpleDateFormat(
				"yyyy-MM-dd");

		Date start = null;
		try {
			start = formatter4datetime.parse("2004-02-29");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		long diff = date.getTime() - start.getTime();
		
		int mod = 60*60*1000*24;
		
		return String.valueOf(diff/mod + 1487);
	}

}
