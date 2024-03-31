package com.teamcenter.rac.commands.project;

import com.teamcenter.rac.aif.AbstractAIFOperation;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.common.Activator;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentProject;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.project.ProjectDataPanel;
import com.teamcenter.rac.util.ButtonLayout;
import com.teamcenter.rac.util.HorizontalLayout;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.OSGIUtil;
import com.teamcenter.rac.util.PropertyLayout;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.rac.util.SplitPane;
import com.teamcenter.rac.util.iTextField;
import com.teamcenter.services.rac.core.ProjectLevelSecurityService;
import com.teamcenter.services.rac.core._2009_10.ProjectLevelSecurity;
//import com.teamcenter.services.rac.core._2018_11.ProjectLevelSecurity;
import com.teamcenter.soa.client.model.ModelObject;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
//import org.apache.log4j.Logger;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

public class UserProjectSelectionPanel extends JPanel {
//  private static final Logger logger = Logger.getLogger(UserProjectSelectionPanel.class);

	private TCSession session;

	private JList candidateList;

	private JList selectedList;

	private JButton addButton;

	private JButton addAll;

	private JButton removeButton;

	private JButton removeAll;

	private JCheckBox showAllUserProjects;

	private Registry reg;

	private TCComponentProject[] defaultProjects = null;

	private TCComponentProject[] projectsToDisable = null;

	private TCComponentProject owningProjObj = null;

	private boolean removeFromProj = false;

	private TCComponent[] targets = null;

	private static final String TC_SHOW_ALL_USER_PROJECTS = "TC_show_all_user_projects";

	private static final String TC_PROJECT_VALIDATE_CONDITIONS = "TC_project_validate_conditions";

	private static String show_all_user_projects = null;

	private static boolean prefValueRead = false;

	private static final String TC_ALLOW_REMOVE_OWNING_PROJECT = "TC_allow_remove_owning_project";

	private static boolean prefAllowRemoveOwingProject = false;

	private static final String TC_USE_STRICT_PROGRAM_PROJECT_HIERARCHY = "TC_use_strict_program_project_hierarchy";

	private static final String PROP_OBJECT_STRING = "object_string";

	private TCComponentProject[] userProjects = null;

	private TCComponentProject[] grpMemberProjects = null;

	private static final String TC_ASSIGN_TO_SOURCE_OBJECT = "TC_assign_to_source_object_projects_on_save_as";

	private TCComponent source = null;
	
	// recompile : START
	private static Object[] _projectArr;
	// recompile : END

	public UserProjectSelectionPanel(TCSession paramTCSession, String paramString,
			TCComponentProject[] paramArrayOfTCComponentProject1, TCComponentProject[] paramArrayOfTCComponentProject2,
			boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3,
			TCComponent[] paramArrayOfTCComponent) {
		
		System.out.println("==========UserProjectSelectionPanel 1==========");
		
		this.session = paramTCSession;
		this.defaultProjects = paramArrayOfTCComponentProject1;
		this.projectsToDisable = paramArrayOfTCComponentProject2;
		this.removeFromProj = paramBoolean3;
		this.targets = paramArrayOfTCComponent;
		this.reg = Registry.getRegistry(this);
		TCComponent tCComponent = (TCComponent) AIFUtility.getTargetComponent();
		if (paramArrayOfTCComponent == null && tCComponent != null)
			paramArrayOfTCComponent = new TCComponent[] { tCComponent };
		this.owningProjObj = null;
		try {
			if (paramBoolean2) {
				this.owningProjObj = this.session.getCurrentProject();
			} else if (paramArrayOfTCComponent != null) {
				ArrayList arrayList = new ArrayList();
				TCComponent[] arrayOfTCComponent;
				int i = (arrayOfTCComponent = paramArrayOfTCComponent).length;
				int b;
				for (b = 0; b < i; b++) {
					TCComponent tCComponent1 = arrayOfTCComponent[b];
					arrayList.add(tCComponent1);
				}
				ProjectDataPanel.loadPropertiesInBulk(arrayList, new String[] { "owning_project" });
				TCProperty tCProperty = paramArrayOfTCComponent[0].getTCProperty("owning_project");
				if (tCProperty != null) {
					this.owningProjObj = (TCComponentProject) tCProperty.getReferenceValue();
					for (b = 1; b < paramArrayOfTCComponent.length; b++) {
						TCProperty tCProperty1 = paramArrayOfTCComponent[b].getTCProperty("owning_project");
						if (tCProperty1 != null && !tCProperty1.toString().equals(tCProperty.toString())) {
							this.owningProjObj = null;
							break;
						}
					}
				} else {
					TCComponentItemRevision tCComponentItemRevision = null;
					if (paramArrayOfTCComponent[0] instanceof TCComponentBOMLine)
						tCComponentItemRevision = ((TCComponentBOMLine) paramArrayOfTCComponent[0]).getItemRevision();
					if (tCComponentItemRevision != null) {
						tCProperty = tCComponentItemRevision.getTCProperty("owning_project");
						if (tCProperty != null)
							this.owningProjObj = (TCComponentProject) tCProperty.getReferenceValue();
					}
				}
			}
		} catch (TCException tCException) {
			tCException.printStackTrace();
			MessageBox.post(tCException);
			return;
		}
		setLayout(new BorderLayout());
		SplitPane splitPane = buildSelectionPanel(paramBoolean1);
		String str = paramString;
		Object[] arrayOfObject = { TCComponentProject.getDisplayName(this.session) };
		if (str == null)
			str = MessageFormat.format(this.reg.getString("selectProject.TITLE", "Select Projects to Assign Object to"), arrayOfObject);
		setBorder(BorderFactory.createTitledBorder(str));
		add(splitPane, "Center");
		JPanel jPanel = new JPanel(new PropertyLayout());
		JLabel jLabel = new JLabel(MessageFormat.format(this.reg.getString("OwningProject.LABEL", "Owning Project"), arrayOfObject));
		iTextField iTextField = new iTextField(20, false);
		iTextField.setEnabled(false);
		// recompile : START
		JLabel jLabel2 = new JLabel("        ÕˆÊäÈë¹Ø¼ü×Ö");
		iTextField iTextField2 = new iTextField(20, false);
		iTextField2.setEnabled(true);
		iTextField2.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent arg0) {
				try {
					Document doc = arg0.getDocument();
					String text = doc.getText(0, doc.getLength()).trim();
					filterListValue(text);
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				try {
					Document doc = arg0.getDocument();
					String text = doc.getText(0, doc.getLength()).trim();
					filterListValue(text);
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void changedUpdate(DocumentEvent arg0) {
				try {
					Document doc = arg0.getDocument();
					String text = doc.getText(0, doc.getLength()).trim();
					filterListValue(text);
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			}
		});
		// recompile : END
		jPanel.add("1.1.right.center", jLabel);
		jPanel.add("1.2.left.center", iTextField);
		// recompile : START
		jPanel.add("1.3.right.left", jLabel2);
		jPanel.add("1.4.left.right", iTextField2);
		// recompile : END
		if (this.owningProjObj != null) {
			iTextField.setText(this.owningProjObj.toString());
		} else {
			iTextField.setText("");
		}
		add(jPanel, "North");
	}

	public UserProjectSelectionPanel(TCSession paramTCSession, String paramString, TCComponentProject[] paramArrayOfTCComponentProject1, TCComponentProject[] paramArrayOfTCComponentProject2, boolean paramBoolean1, boolean paramBoolean2) {
		this(paramTCSession, paramString, paramArrayOfTCComponentProject1, paramArrayOfTCComponentProject2, true, paramBoolean1, paramBoolean2, null);
	}

	public UserProjectSelectionPanel(TCSession paramTCSession, String paramString, TCComponentProject[] paramArrayOfTCComponentProject, boolean paramBoolean1, boolean paramBoolean2) {
		this(paramTCSession, paramString, paramArrayOfTCComponentProject, null, true, paramBoolean1, paramBoolean2, null);
	}

	public UserProjectSelectionPanel(TCSession paramTCSession, String paramString, TCComponentProject[] paramArrayOfTCComponentProject, boolean paramBoolean1, boolean paramBoolean2, TCComponent[] paramArrayOfTCComponent) {
		this(paramTCSession, paramString, paramArrayOfTCComponentProject, null, true, paramBoolean1, paramBoolean2, paramArrayOfTCComponent);
	}

	public UserProjectSelectionPanel(TCSession paramTCSession, String paramString, TCComponentProject[] paramArrayOfTCComponentProject, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3) {
		this(paramTCSession, paramString, paramArrayOfTCComponentProject, null, paramBoolean1, paramBoolean2, paramBoolean3, null);
	}

	public UserProjectSelectionPanel(TCSession paramTCSession, boolean paramBoolean) {
		this(paramTCSession, null, null, null, true, paramBoolean, true, null);
	}

	public UserProjectSelectionPanel(TCSession paramTCSession, boolean paramBoolean1, boolean paramBoolean2) {
		this(paramTCSession, null, null, null, paramBoolean1, paramBoolean2, true, null);
	}

	private SplitPane buildSelectionPanel(boolean paramBoolean) {
		DefaultListModel defaultListModel = new DefaultListModel();
		this.candidateList = new JList(defaultListModel);
		this.candidateList.setVisibleRowCount(10);
		this.candidateList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent param1ListSelectionEvent) {
				validateButtons();
			}
		});
		this.candidateList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent param1MouseEvent) {
				if (param1MouseEvent.getClickCount() == 2)
					addButton.doClick();
			}
		});
		this.showAllUserProjects = new JCheckBox();
		this.showAllUserProjects.setText(this.reg.getString("showAllUserProjectsCheckbox.LABEL"));
		this.showAllUserProjects.setToolTipText(this.reg.getString("showAllUserProjectsCheckbox.TIP"));
		this.showAllUserProjects.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent param1ActionEvent) {
				if (showAllUserProjects.isSelected()) {
					if (userProjects == null) {
						show_all_user_projects = "true";
						userProjects = getProjects();
						show_all_user_projects = "false";
					}
					session.queueOperation(new AbstractAIFOperation() {
						public void executeOperation() {
							updateCadidateProjectsList(userProjects);
						}
					});
				}
				if (!showAllUserProjects.isSelected()) {
					if (grpMemberProjects == null)
						grpMemberProjects = getProjects();
					session.queueOperation(new AbstractAIFOperation() {
						public void executeOperation() {
							updateCadidateProjectsList(grpMemberProjects);
						}
					});
				}
			}
		});
		JPanel jPanel1 = new JPanel(new BorderLayout());
		Object[] arrayOfObject = { TCComponentProject.getDisplayName(this.session) };
		jPanel1.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(), MessageFormat
				.format(this.reg.getString("projectList.TITLE", "Projects for Selection"), arrayOfObject)));
		if (!this.removeFromProj && !getPrefValShowAllUserProjects())
			jPanel1.add(this.showAllUserProjects, "North");
		jPanel1.add(new JScrollPane(this.candidateList), "Center");
		JPanel jPanel2 = new JPanel();
		jPanel2.setLayout(new ButtonLayout(2));
		this.addButton = createButton(">", MessageFormat.format(this.reg.getString("addButton.TIP"), arrayOfObject));
		this.addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent param1ActionEvent) {
				DefaultListModel defaultListModel = (DefaultListModel) selectedList.getModel();
				int i = defaultListModel.getSize();
				moveElements(candidateList.getSelectedValues(), candidateList, selectedList);
				if (i <= 0 && defaultListModel.getSize() > 0)
					firePropertyChange("ProjectSelectionChanged", null, defaultListModel.getElementAt(0));
			}
		});
		jPanel2.add(this.addButton);
		this.addAll = createButton(">>", MessageFormat.format(this.reg.getString("addAllButton.TIP"), arrayOfObject));
		this.addAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent param1ActionEvent) {
				DefaultListModel defaultListModel = (DefaultListModel) selectedList.getModel();
				int i = defaultListModel.getSize();
				moveElements(((DefaultListModel) candidateList.getModel()).toArray(), candidateList, selectedList);
				selectedList.clearSelection();
				if (i <= 0 && defaultListModel.getSize() > 0)
					firePropertyChange("ProjectSelectionChanged", null, defaultListModel.getElementAt(0));
			}
		});
		jPanel2.add(this.addAll);
		this.removeButton = createButton("<",
				MessageFormat.format(this.reg.getString("removeButton.TIP"), arrayOfObject));
		this.removeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent param1ActionEvent) {
				DefaultListModel defaultListModel = (DefaultListModel) selectedList.getModel();
				int i = defaultListModel.getSize();
				moveElements(selectedList.getSelectedValues(), selectedList, candidateList);
				if (i > 0 && defaultListModel.getSize() <= 0)
					firePropertyChange("ProjectSelectionChanged", null, null);
			}
		});
		jPanel2.add(this.removeButton);
		this.removeAll = createButton("<<",
				MessageFormat.format(this.reg.getString("removeAllButton.TIP"), arrayOfObject));
		this.removeAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent param1ActionEvent) {
				DefaultListModel defaultListModel = (DefaultListModel) selectedList.getModel();
				int i = defaultListModel.getSize();
				moveElements(((DefaultListModel) selectedList.getModel()).toArray(), selectedList, candidateList);
				if (owningProjObj == null)
					candidateList.clearSelection();
				if (i > 0 && defaultListModel.getSize() <= 0)
					firePropertyChange("ProjectSelectionChanged", null, null);
			}
		});
		jPanel2.add(this.removeAll);
		defaultListModel = new DefaultListModel();
		this.selectedList = new JList(defaultListModel);
		this.selectedList.setVisibleRowCount(10);
		this.selectedList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent param1ListSelectionEvent) {
				validateButtons();
			}
		});
		this.selectedList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent param1MouseEvent) {
				if (param1MouseEvent.getClickCount() == 2)
					removeButton.doClick();
			}
		});
		JPanel jPanel3 = new JPanel(new BorderLayout());
		jPanel3.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(),
				MessageFormat.format(this.reg.getString("selectedProject.TITLE", "Selected Projects"), arrayOfObject)));
		jPanel3.add(new JScrollPane(this.selectedList), "Center");
		Dimension dimension1 = jPanel1.getPreferredSize();
		Dimension dimension2 = jPanel3.getPreferredSize();
		Dimension dimension3 = new Dimension(
				((dimension1.width > dimension2.width) ? dimension1.width : dimension2.width) * 4 / 5,
				((dimension1.height > dimension2.height) ? dimension1.height : dimension2.height) * 4 / 5);
		jPanel1.setPreferredSize(dimension3);
		jPanel3.setPreferredSize(dimension3);
		JPanel jPanel4 = new JPanel(new HorizontalLayout(3, 3, 3, 3, 3));
		jPanel4.add("unbound.bind", jPanel1);
		jPanel4.add("right.bind.center.center", jPanel2);
		SplitPane splitPane = new SplitPane(0);
		splitPane.setLeftComponent(jPanel4);
		splitPane.setRightComponent(jPanel3);
		splitPane.setDividerLocation(0.5D);
		if (paramBoolean)
			startLoadProjectOperation();
		return splitPane;
	}

	private JButton createButton(String paramString1, String paramString2) {
		JButton jButton = new JButton(paramString1);
		jButton.setMargin(new Insets(0, 0, 0, 0));
		jButton.setFocusPainted(false);
		jButton.setEnabled(true);
		jButton.setToolTipText(paramString2);
		return jButton;
	}

	private void validateButtons() {
		enableButton(this.addButton);
		enableButton(this.addAll);
		enableButton(this.removeButton);
		enableButton(this.removeAll);
	}

	private void moveElements(Object[] paramArrayOfObject, JList paramJList1, JList paramJList2) {
		if (paramArrayOfObject == null || paramArrayOfObject.length <= 0)
			return;
		paramJList2.clearSelection();
		if (paramArrayOfObject.length == 1) {
			addElement(paramArrayOfObject[0], paramJList2);
		} else {
			DefaultListModel defaultListModel = (DefaultListModel) paramJList2.getModel();
			int i = defaultListModel.getSize();
			int j = paramArrayOfObject.length;
			ArrayList arrayList1 = new ArrayList();
			ArrayList arrayList2 = new ArrayList();
			for (int b1 = 0; b1 < j; b1++) {
				if (!defaultListModel.contains(paramArrayOfObject[b1]))
					arrayList1.add(paramArrayOfObject[b1]);
				arrayList2.add(paramArrayOfObject[b1]);
			}
			j = arrayList1.size();
			paramArrayOfObject = arrayList2.toArray();
			Object[] arrayOfObject1 = new Object[i + j];
			defaultListModel.copyInto(arrayOfObject1);
			for (int k = 0; k < j; k++)
				arrayOfObject1[i + k] = paramArrayOfObject[k];
			defaultListModel.removeAllElements();
			Object[] arrayOfObject2;
			int m = (arrayOfObject2 = arrayOfObject1).length;
			for (int b2 = 0; b2 < m; b2++) {
				Object object = arrayOfObject2[b2];
				defaultListModel.addElement(object);
			}
		}
		removeElements(paramArrayOfObject, paramJList1);
		validateButtons();
	}

	private boolean getPrefValShowAllUserProjects() {
		if (!prefValueRead)
			try {
				TCPreferenceService tCPreferenceService = this.session.getPreferenceService();
				show_all_user_projects = tCPreferenceService.getString(4, "TC_show_all_user_projects");
				prefValueRead = true;
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		return (show_all_user_projects == null || show_all_user_projects.equalsIgnoreCase("")
				|| show_all_user_projects.equalsIgnoreCase("true"));
	}

	private static int getValidateProjectConditions() {
		TCPreferenceService tCPreferenceService = (TCPreferenceService) OSGIUtil.getService(Activator.getDefault(),
				TCPreferenceService.class);
		return tCPreferenceService.getInt(4, "TC_project_validate_conditions", 0);
	}

	private boolean assignSourceObject() {
		return this.session.getPreferenceService().isTrue(4, "TC_assign_to_source_object_projects_on_save_as");
	}

	private boolean allowRemoveOwningProject() {
		String str = this.session.getPreferenceService().getString(4, "TC_allow_remove_owning_project");
		prefAllowRemoveOwingProject = Boolean.valueOf(str).booleanValue();
		return prefAllowRemoveOwingProject;
	}

	private void removeElements(Object[] paramArrayOfObject, JList paramJList) {
		if (paramArrayOfObject == null || paramArrayOfObject.length <= 0)
			return;
		DefaultListModel defaultListModel = (DefaultListModel) paramJList.getModel();
		Object[] arrayOfObject;
		int i = (arrayOfObject = paramArrayOfObject).length;
		for (int b = 0; b < i; b++) {
			Object object = arrayOfObject[b];
			defaultListModel.removeElement(object);
		}
		paramJList.validate();
	}

	private void addElement(Object paramObject, JList paramJList) {
		DefaultListModel defaultListModel = (DefaultListModel) paramJList.getModel();
		if (defaultListModel.contains(paramObject))
			return;
		Object[] arrayOfObject = defaultListModel.toArray();
		TCComponentProject tCComponentProject = (TCComponentProject) paramObject;
		String str = tCComponentProject.toString().toUpperCase();
		int i = 0;
		if (arrayOfObject != null) {
			i = binaryFindProjIndexByName(arrayOfObject, str);
			if (i < 0)
				i = 0;
		}
		defaultListModel.insertElementAt(paramObject, i);
		paramJList.addSelectionInterval(i, i);
		paramJList.ensureIndexIsVisible(i);
		paramJList.repaint();
	}

	private void updateCadidateProjectsList(final TCComponentProject[] projects) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				DefaultListModel defaultListModel1 = (DefaultListModel) candidateList.getModel();
				defaultListModel1.removeAllElements();
				DefaultListModel defaultListModel2 = (DefaultListModel) selectedList.getModel();
				TCComponentProject[] arrayOfTCComponentProject;
				int i = (arrayOfTCComponentProject = projects).length;
				for (int b = 0; b < i; b++) {
					TCComponentProject tCComponentProject = arrayOfTCComponentProject[b];
					if (!defaultListModel1.contains(tCComponentProject)
							&& !defaultListModel2.contains(tCComponentProject))
						defaultListModel1.addElement(tCComponentProject);
				}
			}
		});
	}

	private void addProjectsToList(TCComponent[] paramArrayOfTCComponent1,
			TCComponentProject[] paramArrayOfTCComponentProject, final TCComponent[] sourceProjs) {
		final TCComponent[] projects = paramArrayOfTCComponent1;
		final TCComponentProject[] defaultProjects = paramArrayOfTCComponentProject;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (projects != null) {
					DefaultListModel defaultListModel = (DefaultListModel) candidateList.getModel();
					TCComponent[] arrayOfTCComponent;
					int i = (arrayOfTCComponent = projects).length;
					for (int b = 0; b < i; b++) {
						TCComponent tCComponent = arrayOfTCComponent[b];
						if (!defaultListModel.contains(tCComponent))
							defaultListModel.addElement(tCComponent);
					}
				}
				if (defaultProjects != null) {
					DefaultListModel defaultListModel = (DefaultListModel) selectedList.getModel();
					TCComponentProject[] arrayOfTCComponentProject;
					int i = (arrayOfTCComponentProject = defaultProjects).length;
					for (int b = 0; b < i; b++) {
						TCComponentProject tCComponentProject = arrayOfTCComponentProject[b];
						if (!defaultListModel.contains(tCComponentProject))
							defaultListModel.addElement(tCComponentProject);
					}
				}
				if (sourceProjs != null) {
					DefaultListModel defaultListModel = (DefaultListModel) selectedList.getModel();
					TCComponent[] arrayOfTCComponent;
					int i = (arrayOfTCComponent = sourceProjs).length;
					for (int b = 0; b < i; b++) {
						TCComponent tCComponent = arrayOfTCComponent[b];
						if (!defaultListModel.contains(tCComponent))
							defaultListModel.addElement(tCComponent);
					}
				}
				validateButtons();
			}
		});
	}

	public TCComponentProject[] getSelectedProjects() {
		Object[] arrayOfObject = ((DefaultListModel) this.selectedList.getModel()).toArray();
		int i = arrayOfObject.length;
		ArrayList arrayList = new ArrayList();
		if (this.defaultProjects != null && this.defaultProjects.length > 0) {
			HashMap hashMap = new HashMap();
			int b;
			for (b = 0; b < this.defaultProjects.length; b++)
				hashMap.put(this.defaultProjects[b].getUid(), this.defaultProjects[b]);
			for (b = 0; b < i; b++) {
				TCComponentProject tCComponentProject = (TCComponentProject) arrayOfObject[b];
				if (hashMap.get(tCComponentProject.getUid()) == null)
					arrayList.add(tCComponentProject);
			}
		} else {
			for (int b = 0; b < i; b++) {
				TCComponentProject tCComponentProject = (TCComponentProject) arrayOfObject[b];
				arrayList.add(tCComponentProject);
			}
		}
		return (TCComponentProject[]) arrayList.toArray(new TCComponentProject[arrayList.size()]);
	}

	public boolean getProjectSelected() {
		int i = ((DefaultListModel) this.selectedList.getModel()).size();
		if (this.defaultProjects != null)
			i -= this.defaultProjects.length;
		return (i > 0);
	}

	private void startLoadProjectOperation() {
		this.session.queueOperation(new AbstractAIFOperation() {
			public void executeOperation() {
				setCursor(Cursor.getPredefinedCursor(3));
				Object[] arrayOfObject = { TCComponentProject.getDisplayName(session) };
				session.setStatus(MessageFormat.format(reg.getString("loadingProjects.MESSAGE", "loading projects"),
						arrayOfObject));
				TCComponentProject[] arrayOfTCComponentProject = getProjects();
				TCComponent[] arrayOfTCComponent = null;
				try {
					if (source != null && assignSourceObject()) {
						arrayOfTCComponent = source.getReferenceListProperty("project_list");
						if (arrayOfTCComponent == null) {
							TCComponent tCComponent = source.getUnderlyingComponent();
							arrayOfTCComponent = tCComponent.getReferenceListProperty("project_list");
						}
						if (arrayOfTCComponent != null) {
							ArrayList arrayList1 = new ArrayList();
							if (arrayOfTCComponentProject != null && arrayOfTCComponentProject.length > 0) {
								TCComponentProject[] arrayOfTCComponentProject1;
								int j = (arrayOfTCComponentProject1 = arrayOfTCComponentProject).length;
								for (int b1 = 0; b1 < j; b1++) {
									TCComponentProject tCComponentProject = arrayOfTCComponentProject1[b1];
									arrayList1.add(tCComponentProject);
								}
							}
							ArrayList arrayList2 = new ArrayList();
							TCComponent[] arrayOfTCComponent1;
							int i = (arrayOfTCComponent1 = arrayOfTCComponent).length;
							for (int b = 0; b < i; b++) {
								TCComponent tCComponent = arrayOfTCComponent1[b];
								if (arrayList1.contains(tCComponent)) {
									arrayList2.add(tCComponent);
									arrayList1.remove(tCComponent);
								}
							}
							arrayOfTCComponent = (TCComponent[]) arrayList2.toArray(new TCComponent[arrayList2.size()]);
							arrayOfTCComponentProject = (TCComponentProject[]) arrayList1
									.toArray(new TCComponentProject[arrayList1.size()]);
						}
					}
				} catch (TCException tCException) {
					MessageBox.post(tCException);
				}
				addProjectsToList(arrayOfTCComponentProject, defaultProjects, arrayOfTCComponent);
				session.setReadyStatus();
				setCursor(Cursor.getPredefinedCursor(0));
			}
		});
	}

	private TCComponentProject[] getProjects() {
		TCComponentProject[] arrayOfTCComponentProject = null;
		try {
			TCComponentProject[] arrayOfTCComponentProject1;
			com.teamcenter.services.rac.core._2018_11.ProjectLevelSecurity.UserGroupRoleInfo userGroupRoleInfo = new com.teamcenter.services.rac.core._2018_11.ProjectLevelSecurity.UserGroupRoleInfo();
			userGroupRoleInfo.tcUser = this.session.getUser();
			if (getPrefValShowAllUserProjects() || this.showAllUserProjects.isSelected()) {
				userGroupRoleInfo.tcGroup = null;
				userGroupRoleInfo.tcRole = null;
			} else {
				userGroupRoleInfo.tcGroup = this.session.getGroup();
				userGroupRoleInfo.tcRole = this.session.getRole();
			}
			int i = getValidateProjectConditions();
			if (i == 1 || i == 2) {
				arrayOfTCComponentProject1 = getProjectsForLoginUser(userGroupRoleInfo, true, true, false, false,
						false);
			} else {
				arrayOfTCComponentProject1 = getProjectsForLoginUser(userGroupRoleInfo, true, true, true, false, false);
			}
			ArrayList arrayList1 = new ArrayList();
			for (int b = 0; arrayOfTCComponentProject1 != null && b < arrayOfTCComponentProject1.length; b++)
				arrayList1.add(arrayOfTCComponentProject1[b]);
			ArrayList arrayList2 = new ArrayList();
			if (this.removeFromProj) {
				TCComponent tCComponent = (TCComponent) AIFUtility.getTargetComponent();
				if (this.targets == null && tCComponent != null
						&& !(tCComponent instanceof com.teamcenter.rac.kernel.TCComponentQuery))
					this.targets = new TCComponent[] { tCComponent };
				if (this.targets != null) {
					ArrayList arrayList = new ArrayList();
					TCComponent[] arrayOfTCComponent;
					int j = (arrayOfTCComponent = this.targets).length;
					for (int b3 = 0; b3 < j; b3++) {
						TCComponent tCComponent1 = arrayOfTCComponent[b3];
						arrayList.add(tCComponent1);
					}
					ProjectDataPanel.loadPropertiesInBulk(arrayList, new String[] { "project_list" });
					int b2;
					for (b2 = 0; b2 < this.targets.length; b2++) {
						TCComponent tCComponent1 = this.targets[b2];
						TCComponent[] arrayOfTCComponent1 = tCComponent1.getReferenceListProperty("project_list");
						if (arrayOfTCComponent1 == null) {
							TCComponent tCComponent2 = tCComponent1.getUnderlyingComponent();
							arrayOfTCComponent1 = tCComponent2.getReferenceListProperty("project_list");
						}
						for (int b4 = 0; arrayOfTCComponent1 != null && b4 < arrayOfTCComponent1.length; b4++) {
							if (!arrayList2.contains(arrayOfTCComponent1[b4])
									&& arrayList1.contains(arrayOfTCComponent1[b4]))
								arrayList2.add(arrayOfTCComponent1[b4]);
						}
						if (tCComponent1 instanceof TCComponentFolder)
							processFolders(arrayList1, arrayList2, tCComponent1);
					}
					if (!allowRemoveOwningProject())
						for (b2 = 0; b2 < this.targets.length; b2++) {
							TCComponent tCComponent1 = this.targets[b2].getReferenceProperty("owning_project");
							if (tCComponent1 != null)
								arrayList2.remove(tCComponent1);
						}
				} else {
					arrayList2 = arrayList1;
				}
				for (int b1 = 0; this.defaultProjects != null && b1 < this.defaultProjects.length; b1++) {
					if (arrayList2.contains(this.defaultProjects[b1]))
						arrayList2.remove(this.defaultProjects[b1]);
				}
			} else if (showStrictHierarchicalMode()) {
				populateProjectsForHierarchicalMode(this.session, arrayList1, arrayList2);
			} else {
				arrayList2 = arrayList1;
			}
			if (arrayList2.size() > 0) {
				arrayOfTCComponentProject = (TCComponentProject[]) arrayList2
						.toArray(new TCComponentProject[arrayList2.size()]);
			} else {
				arrayOfTCComponentProject = null;
			}
		} catch (TCException tCException) {
			MessageBox.post(tCException);
		}
		return arrayOfTCComponentProject;
	}

	private void processFolders(ArrayList paramArrayList1, ArrayList paramArrayList2, TCComponent paramTCComponent)
			throws TCException {
		TCComponentFolder tCComponentFolder = (TCComponentFolder) paramTCComponent;
		TCComponent[] arrayOfTCComponent = tCComponentFolder.getReferenceListProperty("contents");
		if (arrayOfTCComponent != null) {
			TCComponent[] arrayOfTCComponent1;
			int i = (arrayOfTCComponent1 = arrayOfTCComponent).length;
			for (int b = 0; b < i; b++) {
				TCComponent tCComponent = arrayOfTCComponent1[b];
				if (tCComponent != null) {
					if (tCComponent instanceof TCComponentFolder)
						processFolders(paramArrayList1, paramArrayList2, tCComponent);
					tCComponent.clearCache();
					TCComponent[] arrayOfTCComponent2 = tCComponent.getReferenceListProperty("project_list");
					for (int b1 = 0; arrayOfTCComponent2 != null && b1 < arrayOfTCComponent2.length; b1++) {
						if (!paramArrayList2.contains(arrayOfTCComponent2[b1])
								&& paramArrayList1.contains(arrayOfTCComponent2[b1]))
							paramArrayList2.add(arrayOfTCComponent2[b1]);
					}
				}
			}
		}
	}

	private int binaryFindProjIndexByName(Object[] paramArrayOfObject, String paramString) {
		int i = 0;
		int j = paramArrayOfObject.length - 1;
		while (i <= j) {
			int k = (i + j) / 2;
			TCComponentProject tCComponentProject = (TCComponentProject) paramArrayOfObject[k];
			String str = tCComponentProject.toString().toUpperCase();
			int m = str.compareTo(paramString);
			if (m < 0) {
				i = k + 1;
				continue;
			}
			if (m > 0) {
				j = k - 1;
				continue;
			}
			return k;
		}
		return -1;
	}

	public void clearSelection() {
		if (this.removeAll != null)
			this.removeAll.doClick();
	}

	public void disableProjectSelection(TCComponentProject[] paramArrayOfTCComponentProject) {
		this.projectsToDisable = paramArrayOfTCComponentProject;
	}

	private void enableButton(JButton paramJButton) {
		Object[] arrayOfObject = null;
		if (paramJButton == this.addButton) {
			arrayOfObject = this.candidateList.getSelectedValues();
		} else if (paramJButton == this.addAll) {
			arrayOfObject = ((DefaultListModel) this.candidateList.getModel()).toArray();
		} else if (paramJButton == this.removeButton) {
			arrayOfObject = this.selectedList.getSelectedValues();
		} else if (paramJButton == this.removeAll) {
			arrayOfObject = ((DefaultListModel) this.selectedList.getModel()).toArray();
		}
		boolean bool = true;
		if (arrayOfObject == null || arrayOfObject.length <= 0)
			bool = false;
		if (arrayOfObject != null && this.projectsToDisable != null) {
			Object[] arrayOfObject1;
			int i = (arrayOfObject1 = arrayOfObject).length;
			for (int b = 0; b < i; b++) {
				Object object = arrayOfObject1[b];
				if (binaryFindProjIndexByName(this.projectsToDisable, object.toString().toUpperCase()) >= 0) {
					bool = false;
					break;
				}
			}
		}
		paramJButton.setEnabled(bool);
	}

	public void setSelectedProjects(TCComponentProject[] paramArrayOfTCComponentProject) {
		this.defaultProjects = paramArrayOfTCComponentProject;
	}

	public void loadProjects() {
		startLoadProjectOperation();
	}

	public TCComponentProject[] getProjectsForLoginUser(
			com.teamcenter.services.rac.core._2018_11.ProjectLevelSecurity.UserGroupRoleInfo paramUserGroupRoleInfo,
			boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4,
			boolean paramBoolean5) {
		ProjectLevelSecurityService projectLevelSecurityService = ProjectLevelSecurityService.getService(this.session);
		com.teamcenter.services.rac.core._2018_11.ProjectLevelSecurity.UserProjectsResponse userProjectsResponse = projectLevelSecurityService
				.getUserProjects2(
						new com.teamcenter.services.rac.core._2018_11.ProjectLevelSecurity.UserGroupRoleInfo[] {
								paramUserGroupRoleInfo },
						paramBoolean1, paramBoolean2, paramBoolean3, paramBoolean4, paramBoolean5);
		if (userProjectsResponse.serviceData.sizeOfPartialErrors() != 0) {
			String[] arrayOfString = userProjectsResponse.serviceData.getPartialError(0).getMessages();
			for (int b = 0; b < arrayOfString.length; b++)
				System.err.println(arrayOfString[b]);
			MessageBox.post(AIFUtility.getActiveDesktop(), new TCException(arrayOfString));
			return null;
		}
		if (userProjectsResponse.userProjectList.length > 0) {
			Arrays.parallelSort((userProjectsResponse.userProjectList[0]).projects, new LexicographicComparator());
			return (userProjectsResponse.userProjectList[0]).projects;
		}
		return null;
	}

	@Deprecated
	public static final TCComponentProject[] getProjectsForUser(TCComponentUser paramTCComponentUser,
			boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, TCSession paramTCSession) {
		ProjectLevelSecurity.UserProjectsInfoInput userProjectsInfoInput = new ProjectLevelSecurity.UserProjectsInfoInput();
		userProjectsInfoInput.user = paramTCComponentUser;
		userProjectsInfoInput.activeProjectsOnly = paramBoolean1;
		userProjectsInfoInput.privilegedProjectsOnly = paramBoolean2;
		userProjectsInfoInput.programsOnly = paramBoolean3;
		userProjectsInfoInput.clientId = "a";
		ProjectLevelSecurityService projectLevelSecurityService = ProjectLevelSecurityService
				.getService(paramTCSession);
		ProjectLevelSecurity.UserProjectsInfoResponse userProjectsInfoResponse = projectLevelSecurityService
				.getUserProjects(new ProjectLevelSecurity.UserProjectsInfoInput[] { userProjectsInfoInput });
		if (userProjectsInfoResponse.serviceData.sizeOfPartialErrors() != 0) {
			String[] arrayOfString = userProjectsInfoResponse.serviceData.getPartialError(0).getMessages();
			for (int b = 0; b < arrayOfString.length; b++)
				System.err.println(arrayOfString[b]);
			MessageBox.post(AIFUtility.getActiveDesktop(), new TCException(arrayOfString));
			return null;
		}
		if (userProjectsInfoResponse.userProjectInfos.length > 0) {
			ProjectLevelSecurity.UserProjectsInfo userProjectsInfo = userProjectsInfoResponse.userProjectInfos[0];
			ProjectLevelSecurity.ProjectInfo[] arrayOfProjectInfo = userProjectsInfo.projectsInfo;
			int i = arrayOfProjectInfo.length;
			TCComponentProject[] arrayOfTCComponentProject = new TCComponentProject[i];
			for (int b = 0; b < i; b++)
				arrayOfTCComponentProject[b] = (arrayOfProjectInfo[b]).project;
			return arrayOfTCComponentProject;
		}
		return null;
	}

	public void setSource(TCComponent paramTCComponent) {
		this.source = paramTCComponent;
	}

	public static boolean showStrictHierarchicalMode() {
		TCPreferenceService tCPreferenceService = (TCPreferenceService) OSGIUtil.getService(Activator.getDefault(),
				TCPreferenceService.class);
		Boolean bool = tCPreferenceService.getLogicalValue("TC_use_strict_program_project_hierarchy");
		return (bool != null && bool.booleanValue());
	}

	public static void populateProjectsForHierarchicalMode(TCSession paramTCSession, ArrayList paramArrayList1,
			ArrayList paramArrayList2) throws TCException {
		TCComponentProject tCComponentProject = paramTCSession.getCurrentProject();
		paramArrayList2.add(tCComponentProject);
		ModelObject[] arrayOfModelObject = tCComponentProject.getTCProperty("fnd0Children").getModelObjectArrayValue();
		for (int b = 0; b < arrayOfModelObject.length; b++) {
			if (paramArrayList1.contains(arrayOfModelObject[b]))
				paramArrayList2.add(arrayOfModelObject[b]);
		}
	}

	class LexicographicComparator extends Object implements Comparator<TCComponentProject> {
		public int compare(TCComponentProject param1TCComponentProject1, TCComponentProject param1TCComponentProject2) {
			int i = 0;
			try {
				i = param1TCComponentProject1.getProperty("object_string")
						.compareTo(param1TCComponentProject2.getProperty("object_string"));
			} catch (Exception exception) {
			}
			return i;
		}
	}
	
	// recompile : START
	private void filterListValue(String textVaule) {
//		System.out.println(textVaule);
		
		DefaultListModel listModel = (DefaultListModel)candidateList.getModel();
		if (null == _projectArr || 0 == _projectArr.length) {
			_projectArr = listModel.toArray();
		}
		
//		System.out.println(_projectArr.length);
		if (_projectArr.length > 0) {
			List<TCComponentProject> projectLst = Arrays.asList(_projectArr).stream().map(obj -> (TCComponentProject)obj).filter(obj -> {
				String projectInfo = obj.getProjectID() + "-" + obj.getProjectName();
//				System.out.println(projectInfo + "========" + projectInfo.contains(textVaule));
				return projectInfo.contains(textVaule);
			}).collect(Collectors.toList());
						
			listModel.removeAllElements();
			projectLst.forEach(obj -> {
				listModel.addElement(obj);
			});						
		}		
	}
	// recompile : END
	
}
