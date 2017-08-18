package com.tm.inventory.modules;
import static com.tm.inventory.utility.ConsoleUtility.*;
import com.tm.inventory.processor.*;
import java.util.*;
import org.json.simple.*;
import org.json.simple.parser.*;
import java.io.*;
public class ModuleDataStructure implements Serializable
{
private HashMap<String,Module> modulesHashMap=new HashMap<String,Module>();
private String path;
private String dataFileName="modules.cfg";
public ModuleDataStructure(String path)
{
this.path=path;
populateDataStructure();
}
private void populateDataStructure()
{
loadModulesFromFile();
System.out.println("--------Modules Loaded---------");
}
public JSONObject isValid(String moduleName,String operationName,JSONObject jsonObject)
{
Module module=modulesHashMap.get(moduleName);
JSONObject errorJSONObject;
if(module==null)
{
errorJSONObject=new JSONObject();
errorJSONObject.put("error","Invalid request"+moduleName);
return errorJSONObject;
}
errorJSONObject=module.isValid(operationName,jsonObject);
if(errorJSONObject!=null) return errorJSONObject;
return null;
}
public void loadModulesFromFile()
{
try
{
String fileName=path+dataFileName;
JSONParser jsonParser=new JSONParser();
JSONObject jsonObject;
jsonObject=(JSONObject)jsonParser.parse(new FileReader(fileName));
JSONArray modulesArray=(JSONArray)jsonObject.get("modules");
String moduleName,operationName;
String [] attributes;
JSONArray operationsArray;
Module module;
String processManager;
Operation operation;
ArrayList<Operation> operations;
ArrayList<String> attributesArrayList;
JSONArray attributesJSONArray;
for(Object object:modulesArray)
{
JSONObject moduleJSONObject=(JSONObject)object;
moduleName=(String)moduleJSONObject.get("name");
processManager=(String)moduleJSONObject.get("processManager");
operationsArray=(JSONArray)moduleJSONObject.get("operations");
operations=new ArrayList<Operation>();
for(Object object2:operationsArray)
{
JSONObject operationJSONObject=(JSONObject)object2;
operationName=(String)operationJSONObject.get("name");
attributesJSONArray=(JSONArray)operationJSONObject.get("attributes");
attributesArrayList=new ArrayList<String>();
for(Object object3:attributesJSONArray)
{
attributesArrayList.add((String)object3);
}
operation=new Operation(operationName,attributesArrayList);
operations.add(operation);
}
module=new Module(moduleName,processManager,operations);
modulesHashMap.put(moduleName,module);
}
}catch(Exception exception)
{
log(exception.toString());
}
}
public ProcessManager getProcessManager(String moduleName)
{
return modulesHashMap.get(moduleName).getProcessManager();
}
}