package com.tm.inventory.processor;
import static com.tm.inventory.utility.ConsoleUtility.*;
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import com.tm.inventory.ds.*;
import org.codehaus.jackson.*;
import org.codehaus.jackson.map.*;
import org.json.simple.*;
import org.json.simple.parser.*;
import com.tm.inventory.modules.*;
public class RequestProcessor implements java.io.Serializable
{
private ModuleDataStructure moduleDataStructure;
private String path;
public RequestProcessor(String path)
{
this.path=path;
System.out.println("Loading Modules.....");
moduleDataStructure=new ModuleDataStructure(path+"WEB-INF/data/");
System.out.println("Modules Loaded.....");
}
public void process(ServletContext servletContext,HttpServletRequest request,HttpServletResponse response,ArrayList<String> requestData)
{
String rawRequestData="";
try
{
InputStreamReader inputStreamReader=new InputStreamReader(request.getInputStream());
BufferedReader br=new BufferedReader(inputStreamReader);
StringBuffer sb=new StringBuffer();
String line;
while(true)
{
line=br.readLine();
if(line==null) break;
sb.append(line);
}
rawRequestData=sb.toString();
}catch(IOException ioException)
{
log(ioException.toString());
sendErrorJSON(response,"Unable to parse post data.");
return;
}
System.out.println("JSON: "+rawRequestData);
try
{
JSONObject requestJSONObject=new JSONObject();
JSONParser jsonParser=new JSONParser();
requestJSONObject=(JSONObject)jsonParser.parse(rawRequestData);
String moduleName="";
String operationName="";
if(requestData.size()>0) moduleName=requestData.get(0);
if(requestData.size()>1) operationName=requestData.get(1);
JSONObject jsonObject=moduleDataStructure.isValid(moduleName,operationName,requestJSONObject);
if(jsonObject!=null)
{
sendResponseJSON(response,jsonObject);
return;
}
moduleDataStructure.getProcessManager(moduleName).process(servletContext,request,response,operationName,requestJSONObject);
}catch(ParseException parseException)
{
log(parseException.toString());
sendErrorJSON(response,"Invalid json Object"+ rawRequestData);
return;
}
}
public void sendResponseJSON(HttpServletResponse response,JSONObject jsonObject)
{
try
{
PrintWriter pw=response.getWriter();
response.setContentType("application/json");
pw.print(jsonObject.toString());
}catch(IOException ioException)
{
log(ioException.toString());
}
}
public void sendErrorJSON(HttpServletResponse response,String message)
{
try
{
PrintWriter pw=response.getWriter();
response.setContentType("application/json");
JSONObject jsonObject=new JSONObject();
jsonObject.put("success",false);
jsonObject.put("error",message);
pw.print(jsonObject.toString());
}catch(IOException ioException)
{
log(ioException.toString());
}
}
} 