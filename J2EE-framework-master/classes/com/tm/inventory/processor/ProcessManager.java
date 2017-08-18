package com.tm.inventory.processor;
import javax.servlet.*;
import javax.servlet.http.*;
import org.json.simple.*;
public interface ProcessManager extends java.io.Serializable,Comparable<ProcessManager>
{
public void process(ServletContext servletContext,HttpServletRequest request,HttpServletResponse response,String nameOfOperation,JSONObject jsonObject);
}