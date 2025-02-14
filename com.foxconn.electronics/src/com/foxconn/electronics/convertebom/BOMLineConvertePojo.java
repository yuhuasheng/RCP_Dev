package com.foxconn.electronics.convertebom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.foxconn.electronics.convertebom.pojo.MNTMaterialCategory;
import com.foxconn.electronics.domain.Constants;
import com.foxconn.electronics.util.TCUtil;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;

public class BOMLineConvertePojo
{
    private List<BOMLinePojo>   BOMLinePojos = new ArrayList<BOMLinePojo>();// 当前原理图结构pojo
    private Map<String, String> placeMapping;

    public BOMLineConvertePojo(TCComponentBOMLine topBOMLine, Map<String, TCComponentItemRevision> actionMsgMap, Map<String, String> placeMapping) throws Exception
    {
        this.placeMapping = placeMapping;
        unpackageBOMStructure(topBOMLine);// 进行BOM结构解包
        bomStructureConvertPojo(topBOMLine, actionMsgMap);// bom结构转pojo
    }

    public void setPlaceMapping(Map<String, String> locationSMDMap)
    {
    }

    public TCComponentFolder getProjectFolder(TCComponentItem item)
    {
        try
        {
            String projectIds = item.getProperty(Constants.PROJECT_IDS);
            String projectId = Arrays.asList(projectIds.split(",")).get(0);
            TCSession session = (TCSession) AIFUtility.getCurrentApplication().getSession();
            TCComponent[] queryReslut = TCUtil.executeQuery(session, Constants.FIND_PROJECT_FOLDER, new String[] { Constants.SPAS_ID }, new String[] { projectId });
            TCComponentFolder projectFolder = (TCComponentFolder) queryReslut[0];
            return projectFolder;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public List<BOMLinePojo> getBOMStructurePojo() throws Exception
    {
        return BOMLinePojos;
    }

    /**
     * 解包BOM结构
     * 
     * @param designBOMTopLine
     * @throws Exception
     */
    private void unpackageBOMStructure(TCComponentBOMLine topBOMLine) throws Exception
    {
        AIFComponentContext[] children = topBOMLine.getChildren();
        for (int i = 0; i < children.length; i++)
        {
            TCComponentBOMLine bomLine = (TCComponentBOMLine) children[i].getComponent();
            if (bomLine.isPacked())
            {
                bomLine.unpack();
            }
            unpackageBOMStructure(bomLine);
        }
    }

    private void bomStructureConvertPojo(TCComponentBOMLine topBomLine, Map<String, TCComponentItemRevision> actionMsgMap) throws Exception
    {
        AIFComponentContext[] children = topBomLine.getChildren();
        for (int i = 0; i < children.length; i++)
        {
            TCComponentBOMLine bomLine = (TCComponentBOMLine) children[i].getComponent();
            TCComponentItemRevision itemRev = bomLine.getItemRevision();
            if (itemRev == null)
            {
                itemRev = bomLine.getItem().getLatestItemRevision();
            }
            String itemId = bomLine.getProperty(Constants.BL_ITEM_ID);
            String itemRevId = itemRev.getProperty(Constants.ITEM_REV_ID);
            String itemType = itemRev.getType();
            String quantity = bomLine.getProperty(Constants.BL_QUANTITY);
            String packageType = bomLine.getProperty(Constants.BL_PACKAGE_TYPE);
            String insertionType = bomLine.getProperty(Constants.INSERTION_TYPE);
            String side = bomLine.getProperty(Constants.BL_SIDE);
            String bom = bomLine.getProperty(Constants.BL_BOM);
            String location = bomLine.getProperty(Constants.BL_REF_DESIGNATOR);
            String desc = bomLine.getProperty(Constants.BL_DESCRIPTION);
            String materialGroup = itemRev.getProperty(Constants.MATERIAL_GROUP);
            if (StringUtils.isEmpty(side) && StringUtils.isNotEmpty(location) && placeMapping != null && placeMapping.size() > 0)
            {
                String[] locations = location.split(",");
                for (String tempLocation : locations)
                {
                    String placeSide = placeMapping.get(tempLocation);
                    if (StringUtils.isNotEmpty(placeSide))
                    {
                        side = placeSide;
                        break;
                    }
                }
            }
            BOMLinePojo bomLinePojo = new BOMLinePojo();
            bomLinePojo.setItemId(itemId);
            bomLinePojo.setItemRevId(itemRevId);
            bomLinePojo.setItemType(itemType);
            bomLinePojo.setQuantity(quantity);
            bomLinePojo.setPackageType(packageType);
            bomLinePojo.setInsertionType(insertionType);
            bomLinePojo.setLocation(location);
            bomLinePojo.setSide(side);
            bomLinePojo.setBom(bom);
            bomLinePojo.setDesc(desc);
            bomLinePojo.setItemRev(itemRev);
            // 2022/12/13 leo add
            // 單面板1, 大都為PI BD; AI/A--> AI --> SMT ;
            // 單面板2 ; AI --> SMT, 不一定有AI ;
            // 雙面板, 大都為IF BD; (AI) --> SMT/B --> SMT/T --> SMT, AI很少發生
            if (materialGroup != null && (Constants.B1710.equals(materialGroup) || Constants.B1711.equals(materialGroup) || Constants.B1712.equals(materialGroup)))
            {
                bomLinePojo.setMaterialCategory(MNTMaterialCategory.PCB);
                if (actionMsgMap.get(Constants.AI_A1) != null)
                {
                    bomLinePojo.setStepfather(actionMsgMap.get(Constants.AI_A1));
                }
                else if (actionMsgMap.get(Constants.AI1) != null)
                {
                    bomLinePojo.setStepfather(actionMsgMap.get(Constants.AI1));
                }
                else if (actionMsgMap.get(Constants.SMT_B) != null)
                {
                    bomLinePojo.setStepfather(actionMsgMap.get(Constants.SMT_B));
                }
                else if (actionMsgMap.get(Constants.SMT_T) != null)
                {
                    bomLinePojo.setStepfather(actionMsgMap.get(Constants.SMT_T));
                }
                else
                {
                    bomLinePojo.setStepfather(actionMsgMap.get(Constants.SMT));
                }
                if (quantity == null || quantity.length() == 0)
                {
                    bomLinePojo.setQuantity("1");
                }
            }
            if (packageType.equals(Constants.DIP))
            {
                if (insertionType.equals(Constants.AI1))
                {
                    bomLinePojo.setMaterialCategory(MNTMaterialCategory.AI);
                    bomLinePojo.setStepfather(actionMsgMap.get(Constants.AI1));
                }
                else if (insertionType.equals(Constants.AI_A1))
                {
                    bomLinePojo.setMaterialCategory(MNTMaterialCategory.AIA);
                    bomLinePojo.setStepfather(actionMsgMap.get(Constants.AI_A1));
                }
                else if (insertionType.equals(Constants.AI_R1))
                {
                    bomLinePojo.setMaterialCategory(MNTMaterialCategory.AIR);
                    bomLinePojo.setStepfather(actionMsgMap.get(Constants.AI_R1));
                }
                else
                {
                    bomLinePojo.setMaterialCategory(MNTMaterialCategory.MI);
                    bomLinePojo.setStepfather(actionMsgMap.get(Constants.MI1));
                }
            }
            else if (packageType.equals(Constants.SMD))
            {
                if (side.equals(Constants.TOP))
                {
                    if (null != actionMsgMap.get(Constants.SMT_T))
                    {
                        bomLinePojo.setMaterialCategory(MNTMaterialCategory.SMTT);
                        bomLinePojo.setStepfather(actionMsgMap.get(Constants.SMT_T));
                    }
                    else
                    {
                        bomLinePojo.setMaterialCategory(MNTMaterialCategory.SMT);
                        bomLinePojo.setStepfather(actionMsgMap.get(Constants.SMT));
                    }
                }
                else if (side.equals(Constants.BOTTOM))
                {
                    if (null != actionMsgMap.get(Constants.SMT_B))
                    {
                        bomLinePojo.setMaterialCategory(MNTMaterialCategory.SMTB);
                        bomLinePojo.setStepfather(actionMsgMap.get(Constants.SMT_B));
                    }
                    else
                    {
                        bomLinePojo.setMaterialCategory(MNTMaterialCategory.SMT);
                        bomLinePojo.setStepfather(actionMsgMap.get(Constants.SMT));
                    }
                }
                else
                {
                    bomLinePojo.setMaterialCategory(MNTMaterialCategory.SMT);
                    bomLinePojo.setStepfather(actionMsgMap.get(Constants.SMT));
                }
            }
            if (bomLinePojo.getStepfather() != null)
            {
                bomLinePojo.setStepfatherNum(bomLinePojo.getStepfather().getProperty("item_id"));
            }
            else
            {
                System.out.println(itemRevId + "  bomLinePojo.getStepfather() == null");
            }
            BOMLinePojos.add(bomLinePojo);
            bomStructureConvertPojo(bomLine, actionMsgMap);
        }
    }
}
