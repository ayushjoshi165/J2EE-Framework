package com.tm.inventory.servlets;
import static com.tm.inventory.utility.ConsoleUtility.*;
import java.util.*;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import com.tm.inventory.processor.*;
public class RequestHandler extends HttpServlet
{
public void doGet(HttpServletRequest request,HttpServletResponse response)
{
processRequest(request,response);
}
public void doPost(HttpServletRequest request,HttpServletResponse response)
{
processRequest(request,response);
}
public void processRequest(HttpServletRequest request,HttpServletResponse response)
{
System.out.println("Request arrived...");
String rurl=request.getRequestURI();
System.out.println("URL: "+rurl);
String [] splits=rurl.split("/");
splits[splits.length-1]=splits[splits.length-1].substring(0,splits[splits.length-1].indexOf("."));
ArrayList<String> requestData=new ArrayList<String>();
for(String k:splits)
{
if(k.trim().length()>0) requestData.add(k);
}
if(requestData.size()<3)
{
processInvalidRequest(request,response);
return;
}
requestData.remove(0);
ServletContext servletContext=getServletContext();
RequestProcessor requestProcessor=(RequestProcessor)servletContext.getAttribute("requestProcessor");
requestProcessor.process(servletContext,request,response,requestData);
}
public void processInvalidRequest(HttpServletRequest request,HttpServletResponse response)
{
try
{
response.sendError(HttpServletResponse.SC_NOT_FOUND);
}catch(IOException ioException)
{
log(ioException.getMessage());
}
}
}