package com.foxconn.mechanism.createFolderStruct;

import java.util.Vector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.foxconn.mechanism.util.TCUtil;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentFolderType;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;

public class CreateFolderService
{
    public static final String FOLDERTEMPLATE_PREFERENCE = "D9_Create_Folder_Template";
    public TCSession           tcSession                 = TCUtil.getTCSession();
    public final static String FOLDER_RELATION           = "contents";

    public Vector<FolderTemplateModel> getFolderTemplates()
    {
        return Stream.of(TCUtil.getArrayByPreference(tcSession, TCPreferenceService.TC_preference_site, FOLDERTEMPLATE_PREFERENCE))
                     .map(FolderTemplateModel::new)
                     .collect(Collectors.toCollection(Vector::new));
    }

    public void startCreateFolder(String rootFolderName, String templateID) throws TCException
    {
        TCComponentItem item = TCUtil.findItem(templateID);
        TCComponentBOMLine topBomLine = TCUtil.openBomWindow(tcSession, item);
        TCComponentFolder newstufffolder = tcSession.getUser().getNewStuffFolder();
        TCComponentFolder rootFolder = TCUtil.createFolder(tcSession, "Folder", rootFolderName);
        newstufffolder.add(FOLDER_RELATION, rootFolder);
        bacthCreateFolder(topBomLine, rootFolder, true);
    }

    public void bacthCreateFolder(TCComponentBOMLine bomLine, TCComponentFolder parentFolder, boolean isTop) throws TCException
    {
        TCComponentFolder folder = null;
        if (isTop)
        {
            folder = parentFolder;
        }
        else
        {
            folder = TCUtil.createFolder(tcSession, "Folder", bomLine.getProperty("bl_item_object_name"));
            parentFolder.add(FOLDER_RELATION, folder);
        }
        AIFComponentContext[] componmentContext = bomLine.getChildren();
        for (int i = 0; i < componmentContext.length; i++)
        {
            TCComponentBOMLine childBomLine = (TCComponentBOMLine) componmentContext[i].getComponent();
            bacthCreateFolder(childBomLine, folder, false);
        }
    }
}
