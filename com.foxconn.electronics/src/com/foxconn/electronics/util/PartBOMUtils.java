package com.foxconn.electronics.util;

import com.teamcenter.rac.kernel.RevisionRuleEntry;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentBOMWindowType;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentItemType;
import com.teamcenter.rac.kernel.TCComponentRevisionRule;
import com.teamcenter.rac.kernel.TCComponentRevisionRuleType;
import com.teamcenter.rac.kernel.TCComponentSnapshot;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.psebase.common.BOMViewerManager;
import com.teamcenter.rac.ui.common.RACUIUtil;

public class PartBOMUtils
{
    private static TCSession session = RACUIUtil.getTCSession();

    /**
     * 
     * @param revision
     * @return
     * @throws TCException
     */
    public static TCComponentBOMWindow createBomWindow(TCComponentItemRevision revision) throws TCException
    {
        TCComponentRevisionRuleType revisionRuleType = (TCComponentRevisionRuleType) session.getTypeComponent("RevisionRule");
        TCComponentRevisionRule defaultRule = revisionRuleType.getDefaultRule();
        TCComponentBOMWindowType bomWindowType = (TCComponentBOMWindowType) session.getTypeComponent("BOMWindow");
        TCComponentBOMWindow bomWindow = bomWindowType.create(defaultRule);
        bomWindow.setWindowTopLine(null, revision, null, null);
        return bomWindow;
    }

    public static void deleteBOM(TCComponentBOMWindow bomWindow)
    {
    }

    // 创建ITEM
    public static TCComponentItem createItem(String itemId, String itemName, String itemRev, String itemTypeName)
    {
        // TODO Auto-generated method stub
        try
        {
            itemTypeName = itemTypeName.replace("Revision", "");
            TCComponentItemType itemType = (TCComponentItemType) session.getTypeComponent(itemTypeName);
            TCComponentItem item = itemType.create(itemId, itemRev, itemTypeName, itemName, "", null);
            return item;
        }
        catch (TCException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    public static TCComponentItem getItemOrCreate(String itemID, String itemName, String itemTypeName)
    {
        TCComponentItem item = null;
        try
        {
            TCComponentItemType itemType = (TCComponentItemType) session.getTypeComponent(itemTypeName);
            item = itemType.find(itemID);
            if (item == null)
            {
                item = itemType.create(itemID, "", itemTypeName, itemName, "", null);
            }
        }
        catch (TCException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return item;
    }

    /**
     * 指定版本规则
     * 
     * @param revision
     * @param strRevisionRuleName
     * @return
     * @throws TCException
     */
    public static TCComponentBOMWindow createBomWindow(TCComponentItemRevision revision, String strRevisionRuleName) throws TCException
    {
        TCComponentRevisionRule rule = null;
        TCComponentRevisionRuleType revRuleType = (TCComponentRevisionRuleType) revision.getSession().getTypeComponent("RevisionRule");
        TCComponent[] revRules = revRuleType.extent();
        for (TCComponent tcComponent : revRules)
        {
            if (tcComponent.toString().equals(strRevisionRuleName))
            {
                rule = (TCComponentRevisionRule) tcComponent;
            }
        }
        TCComponentBOMWindowType bomWindowType = (TCComponentBOMWindowType) session.getTypeComponent("BOMWindow");
        TCComponentBOMWindow bomWindow = bomWindowType.create(rule);
        bomWindow.setWindowTopLine(null, revision, null, null);
        return bomWindow;
    }

    /**
     * 
     * @param bomWindow
     * @throws TCException
     */
    public static void closeBomWindow(TCComponentBOMWindow bomWindow) throws TCException
    {
        if (bomWindow != null)
        {
            bomWindow.close();
        }
    }

    /**
     * 
     * @param bomWindow
     * @param bomline
     * @param childItem
     * @throws TCException
     */
    public void addChild(TCComponentBOMWindow bomWindow, TCComponentBOMLine bomline, TCComponentItem childItem) throws TCException
    {
        if (bomline != null && childItem != null)
        {
            bomline.add(childItem, childItem.getLatestItemRevision(), null, false);
            bomWindow.save();
        }
    }

    /**
     * 
     * @param bomWindow
     * @param bomline
     * @param childItemRevision
     * @return
     * @throws TCException
     */
    public static TCComponentBOMLine addChild(TCComponentBOMWindow bomWindow, TCComponentBOMLine bomline, TCComponentItemRevision childItemRevision) throws TCException
    {
        if (bomWindow != null && bomline != null && childItemRevision != null)
        {
            bomWindow.refresh();
            TCComponentBOMLine newbomline = bomline.add(childItemRevision.getItem(), childItemRevision, null, false);
            bomWindow.save();
            return newbomline;
        }
        return null;
    }

    /**
     * 
     * @param bomWindow
     * @param bomline
     * @param childItemRevision
     * @return
     * @throws TCException
     */
    public static TCComponentBOMLine addReplaceChild(TCComponentBOMWindow bomWindow, TCComponentBOMLine bomline, TCComponentItemRevision childItemRevision) throws TCException
    {
        if (bomWindow != null && bomline != null && childItemRevision != null)
        {
            bomWindow.refresh();
            // bomline.replace(tccomponentitem, tccomponentitemrevision, tccomponent);
            // bomline.changeToReplace(tccomponentbomline, tccomponentbomline1);
            //
            TCComponentBOMLine newbomline = bomline.add(childItemRevision.getItem(), childItemRevision, null, false);
            bomWindow.save();
            return newbomline;
        }
        return null;
    }

    /**
     * 
     * Robert 2022年6月17日
     * 
     * @param snapshot
     * @return
     * @throws TCException
     */
    public static TCComponentRevisionRule createSnapshotRule(TCComponentSnapshot snapshot) throws TCException
    {
        TCComponentRevisionRuleType revisionRuleType = (TCComponentRevisionRuleType) RACUIUtil.getTCSession().getTypeComponent("RevisionRule");
        TCComponentRevisionRule r = revisionRuleType.create("Snapshot: " + snapshot.toString(), snapshot.toString()); // 名字 与 描述
        RevisionRuleEntry re = r.createEntry(RevisionRuleEntry.OVERRIDEENTRY);
        TCComponent c = re.getTCComponent();
        c.setReferenceProperty("folder", snapshot);
        r.addEntry(re);
        return r;
    }

    /**
     * 
     * Robert 2022年6月17日
     * 
     * @param itemRev
     * @return
     * @throws TCException
     */
    public static TCComponentSnapshot getSnapshot(TCComponentItemRevision itemRev) throws TCException
    {
        TCComponent[] snapshots = itemRev.getRelatedComponents("IMAN_reference");
        if (snapshots != null && snapshots.length > 0)
        {
            for (TCComponent com : snapshots)
            {
                if (com instanceof TCComponentSnapshot)
                {
                    return (TCComponentSnapshot) com;
                }
            }
        }
        return null;
    }

    /**
     * 
     * Robert 2022年6月17日
     * 
     * @param itemRev
     * @return
     * @throws TCException
     */
    public static TCComponentBOMWindow createBomWindowBySnapshot(TCComponentItemRevision itemRev) throws TCException
    {
        TCComponentSnapshot snapshot = getSnapshot(itemRev);
        if (snapshot != null)
        {
            TCComponentRevisionRule rule = createSnapshotRule(snapshot);
            TCComponentBOMWindowType bomWindowType = (TCComponentBOMWindowType) RACUIUtil.getTCSession().getTypeComponent("BOMWindow");
            TCComponentBOMWindow bomWindow = bomWindowType.create(rule);
            bomWindow.setWindowTopLine(null, itemRev, null, null);
            return bomWindow;
        }
        return null;
    }
}
