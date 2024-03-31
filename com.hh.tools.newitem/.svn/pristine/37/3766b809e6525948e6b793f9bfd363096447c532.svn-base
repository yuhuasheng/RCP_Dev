package com.hh.tools.newitem;

import java.util.ArrayList;
import java.util.List;

import com.teamcenter.rac.kernel.*;

public class DocumentTemplate {

    private static String PropertyName_Prefix = Utils.getPrefix2();

    protected static String PN_Dataset = PropertyName_Prefix + "DatasetTemplate";
    protected static String PN_SignatureLocation = PropertyName_Prefix + "SignatureLocation";
    protected static String PN_Layout = PropertyName_Prefix + "Layout";
    protected static String PN_HorOrVer = PropertyName_Prefix + "HorOrVer";
    protected static String PN_FileType = PropertyName_Prefix + "FileType";

    private TCComponentForm form;
    private String layout = null;
    private String style = null;
    private String type = null;
    private TCComponentDataset docTemplate = null;
    private List<SignatureLocation> SignatureLocation = new ArrayList<SignatureLocation>();

    public DocumentTemplate(TCComponentForm form) {
        if (form == null)
            return;
        try {
            this.form = form;
            TCComponent tcc = form.getReferenceProperty(PN_Dataset);
            if (tcc instanceof TCComponentDataset) {
                docTemplate = (TCComponentDataset) tcc;
            }
            this.setLayout(form.getProperty(PN_Layout));
            this.setStyle(form.getProperty(PN_HorOrVer));
            this.setType(form.getProperty(PN_FileType));
            String[] values = form.getTCProperty(PN_SignatureLocation).getStringArrayValue();
            if (values == null || values.length < 1) {
                return;
            }
            for (String value : values) {
                String[] temp = value.split(":");
                if (temp == null || temp.length != 6) {
                	String message = form.getProperty("object_name") + "的签字坐标 " + value
							+ " 配置错误，需要用英文的:间隔6个信息,分别是名称、页、X、Y、角度、字体!";
                    System.err.println(message);
                    continue;
                }
                String name = temp[0];
                int page = Integer.parseInt(temp[1]);
                float x = Float.parseFloat(temp[2]);
                float y = Float.parseFloat(temp[3]);
                float angle = Float.parseFloat(temp[4]);
                float fontsize = Float.parseFloat(temp[5]);

                SignatureLocation location = new SignatureLocation(page, name, x, y, angle, fontsize);
                SignatureLocation.add(location);
            }
        } catch (TCException e) {
            e.printStackTrace();
        }
    }

    public TCComponentDataset getTemplate() {
        return this.docTemplate;
    }

    public List<SignatureLocation> getSignatureLocation() {
        return this.SignatureLocation;
    }

    public TCComponentForm getForm() {
        return this.form;
    }

    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
