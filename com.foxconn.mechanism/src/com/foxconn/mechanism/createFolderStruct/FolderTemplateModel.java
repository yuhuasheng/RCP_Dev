package com.foxconn.mechanism.createFolderStruct;

import java.util.Vector;

public class FolderTemplateModel
{
    private String displayName;
    private String itemId;

    public FolderTemplateModel(String preference)
    {
        String vals[] = preference.split(":");
        this.itemId = vals[0];
        this.displayName = vals[1];
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public void setDisplayName(String displayName)
    {
        this.displayName = displayName;
    }

    public String getItemId()
    {
        return itemId;
    }

    public void setItemId(String itemId)
    {
        this.itemId = itemId;
    }

    public String toString()
    {
        return displayName;
    }

    public static Vector<FolderTemplateModel> getVectors()
    {
        Vector<FolderTemplateModel> list = new Vector<FolderTemplateModel>();
        for (int i = 0; i < 6; i++)
        {
            FolderTemplateModel folderTemplateModel = new FolderTemplateModel("+++++++++==" + i + ":" + "sssssssss_" + i);
            list.add(folderTemplateModel);
        }
        return list;
    }
}
