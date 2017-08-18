package com.tm.inventory.utility;
public class ConsoleUtility
{
private static final boolean developmentMode=true;
public static void log(Object object)
{
if(developmentMode)
{
System.out.println(object);
}
else
{
//code to add to a file.
}
}
}