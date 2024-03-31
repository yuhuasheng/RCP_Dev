package com.teamcenter.rac.pse.actions;

import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.bom.BOMMarkupHelper;
import com.teamcenter.rac.pse.actions.ANoteAction;
import com.teamcenter.rac.pse.common.BOMTreeTable;
import com.teamcenter.rac.pse.dialogs.MyANoteDialog;
import com.teamcenter.rac.psebase.AbstractBOMLineViewerApplication;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;

public class MyANoteAction extends ANoteAction {

	private static String packedString;

	public MyANoteAction(
			AbstractBOMLineViewerApplication abstractbomlineviewerapplication,
			String s) {
		super(abstractbomlineviewerapplication, s);
		// TODO Auto-generated constructor stub
		System.out.println("Hello MyANoteAction1...");
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		// super.run();
		System.out.println("Hello MyANoteAction Run...");

		Registry registry = Registry.getRegistry(this);
		AIFComponentContext aaifcomponentcontext[];
		aaifcomponentcontext = getTargetContexts();
		if (aaifcomponentcontext == null) {
			MessageBox.post(registry.getString("noObjectsSelected"),
					registry.getString("error.TITLE"), 1);
			return;
		}
		TCComponentBOMLine tccomponentbomline = null;
		try {
			tccomponentbomline = (TCComponentBOMLine) aaifcomponentcontext[0]
					.getComponent();
		} catch (ClassCastException _ex) {
			MessageBox.post(registry.getString("notABOMLine"),
					registry.getString("error.TITLE"), 1);
			return;
		}
		try {
			if (tccomponentbomline.parent() == null) {
				MessageBox.post(registry.getString("topBomLineNotesError"),
						registry.getString("error.TITLE"), 1);
				return;
			}
			String s;
			String s1;
			if (aaifcomponentcontext.length > 1)
				MessageBox.post(registry.getString("manyLinesNotesWarning"),
						registry.getString("warning.TITLE"), 4);
			BOMTreeTable bomtreetable = ((AbstractBOMLineViewerApplication) application)
					.getViewableTreeTable();
			s = bomtreetable
					.getColumnIdentify(bomtreetable.getEditInPlaceCol());
			s1 = bomtreetable.getColumnName(bomtreetable.getEditInPlaceCol());
			if (tccomponentbomline.isPacked()) {
				if (packedString == null)
					packedString = tccomponentbomline.getSession()
							.getTextService()
							.getTextValue("k_bomline_prop_packed");
				if (tccomponentbomline.getTCProperty(s).getStringValue()
						.equals(packedString)) {
					MessageBox.post(
							registry.getString("packedBomLineNotesError"),
							registry.getString("error.TITLE"), 1);
					return;
				}
			}
			if (!BOMMarkupHelper.allowStructureEdit(AIFUtility
					.getActiveDesktop().getShell(), tccomponentbomline))
				return;

			// huangbin ÷ÿ‘ÿDialog
			MyANoteDialog anotedialog = new MyANoteDialog(parent,
					tccomponentbomline, s1, s);
			anotedialog.run();
		} catch (Exception exception) {
			MessageBox.post(exception);
		}
		return;

	}

	

}
