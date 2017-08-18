package com.tm.inventory.modules;
import java.util.*;
import org.json.simple.*;
public class Operation implements java.io.Serializable,Comparable<Operation>
{
private String name;
private ArrayList<String> attributesRequired;
public Operation(String name,ArrayList<String> attributesRequired)
{
this.name=name;
this.attributesRequired=attributesRequired;
}
public JSONObject isValid(JSONObject jsonObject)
{
JSONObject errorJSONObject=new JSONObject();
JSONArray jsonArray=new JSONArray();
for(String attribute:attributesRequired)
{
if(jsonObject.get(attribute)==null) jsonArray.add(attribute);
}
if(jsonArray.size()>0)
{
errorJSONObject.put("missing attributes",jsonArray);
return errorJSONObject;
}
else
{
return null;
}
}
public String getName()
{
return this.name;
}
public ArrayList<String> getAttributes()
{
return this.attributesRequired;
}
public boolean equals(Object object)
{
if(!(object instanceof Operation)) return false;
Operation operation=(Operation)object;
return this.name.equals(operation.getName());
}
public int compareTo(Operation operation)
{
return this.name.compareTo(operation.getName());
}
}