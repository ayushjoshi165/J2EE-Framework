package com.tm.inventory.servlets;
import javax.servlet.*;
import javax.servlet.http.*;
import com.tm.inventory.ds.*;
import com.tm.inventory.processor.*;
public class DataStructureInitializer extends HttpServlet
{
public void init()
{
ServletContext servletContext=getServletContext();
ApplicationDataStructure applicationDataStructure;
applicationDataStructure=(ApplicationDataStructure)servletContext.getAttribute("aplicationDataStructure");
String path=servletContext.getRealPath("/");
if(applicationDataStructure==null)
{
applicationDataStructure=new ApplicationDataStructure();
servletContext.setAttribute("applicationDataStructure",applicationDataStructure);
RequestProcessor requestProcessor=new RequestProcessor(path);
servletContext.setAttribute("requestProcessor",requestProcessor);
}
}
}