package com.tm.inventory.modules;
import static com.tm.inventory.utility.ConsoleUtility.*;
import java.lang.reflect.*;
import java.util.*;
import org.json.simple.*;
import com.tm.inventory.processor.*;
public class Module implements java.io.Serializable,Comparable<Module>
{
private String name;
private HashMap<String,Operation> operationsHashMap;
private String processManagerClassName;
private ProcessManager processManager;
public Module(String name,String processManagerClassName,ArrayList<Operation> operations)
{
this.name=name;
this.operationsHashMap=new HashMap<String,Operation>();
for(Operation operation:operations)
{
operationsHashMap.put(operation.getName(),operation);
}
this.processManagerClassName=processManagerClassName;
try
{
Class c=Class.forName(processManagerClassName);
Method m=c.getMethod("getInstance");
this.processManager=(ProcessManager)m.invoke(null);
}catch(ClassNotFoundException classNotFoundException)
{
log(classNotFoundException.toString());
}
catch(Exception exception)
{
log(exception.toString());
}
}
public JSONObject isValid(String operationName,JSONObject jsonObject)
{
JSONObject errorJSONObject;
Operation operation=operationsHashMap.get(operationName);
if(operation==null)
{
errorJSONObject=new JSONObject();
errorJSONObject.put("error","Invalid request"+operationName);
return errorJSONObject;
}
errorJSONObject=operation.isValid(jsonObject);
if(errorJSONObject!=null) return errorJSONObject;
return null;
}
public String getName()
{
return this.name;
}
public HashMap<String,Operation> getOperations()
{
return this.operationsHashMap;
}
public boolean equals(Object object)
{
if(!(object instanceof Module)) return false;
Module module1=(Module)object;
return this.name.equals(module.getName());
}
public int compareTo(Module module)
{
return this.name.compareTo(module.getName());
}
public ProcessManager getProcessManager()
{
return this.processManager;
}
}