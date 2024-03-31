package com.teamcenter.rac.workflow.commands.adhoc;

import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.aif.AbstractAIFOperation;
import com.teamcenter.rac.kernel.TCComponentTask;
import com.teamcenter.rac.kernel.TCComponentType;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.kernel.TCTaskState;
import com.teamcenter.rac.util.FilterDocument;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.workflow.commands.complete.CompleteOperation;
import com.teamcenter.rac.workflow.common.TestErrorListener;
import java.awt.Component;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

public class UserRouteTaskSignoffsPanel extends UserAdhocSignoffsPanel {
	protected JTextField ackQuorumField;

	protected JTextField ackPercentQuorumField;

	protected JRadioButton ackRadioButton;

	protected JRadioButton ackPercentRadioButton;

	protected TCComponentTask routeTask;

	protected TCComponentTask acknowTask;

	protected TCComponentTask notifyTask;

	public UserRouteTaskSignoffsPanel(TCSession paramTCSession, Frame paramFrame) {
		super(paramTCSession, paramFrame);
	}

	public UserRouteTaskSignoffsPanel(TCSession paramTCSession) {
		this(paramTCSession, null);
	}

	public UserRouteTaskSignoffsPanel(Frame paramFrame, TCComponentTask paramTCComponentTask) {
		super(paramFrame, paramTCComponentTask);
	}

	public UserRouteTaskSignoffsPanel(TCComponentTask paramTCComponentTask) {
		this(null, paramTCComponentTask);
	}

	public UserRouteTaskSignoffsPanel(AIFDesktop paramAIFDesktop, JPanel paramJPanel,
			TCComponentTask paramTCComponentTask) {
		this(paramAIFDesktop, paramTCComponentTask);
	}

	protected void initializePanel() {
		this.routeTask = this.task;
		this.task = null;
		initializeTasks();
		super.initializePanel();
		JLabel jLabel = new JLabel(this.r.getString("acknowlegeQuorum"));
		jLabel.setToolTipText(this.r.getString("acknowlegeQuorum.TIP"));
		FilterDocument filterDocument = new FilterDocument("0123456789" + this.quorumAllString.toLowerCase() + this.quorumAllString.toUpperCase() + "-");
		filterDocument.setNegativeAccepted(true);
		this.ackQuorumField = new JTextField(3) {
			public void setText(String param1String) {
				if (param1String != null && param1String.equals("-1"))
					param1String = UserRouteTaskSignoffsPanel.this.quorumAllString;
				super.setText(param1String);
			}
		};
		this.ackQuorumField.setDocument(filterDocument);
		this.ackQuorumField.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent param1FocusEvent) {
			}

			public void focusLost(FocusEvent param1FocusEvent) {
				Component component = param1FocusEvent.getOppositeComponent();
				boolean bool = true;
				if (component != null && component instanceof javax.swing.JButton)
					bool = false;
				if (bool)
					UserRouteTaskSignoffsPanel.this.performSetAckQuorumAction();
			}
		});
		this.ackQuorumField.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent param1KeyEvent) {
			}

			public void keyPressed(KeyEvent param1KeyEvent) {
				int i = param1KeyEvent.getKeyCode();
				if (i == 40 || i == 10 || i == 27)
					UserRouteTaskSignoffsPanel.this.signoffTree.requestFocus();
			}

			public void keyReleased(KeyEvent param1KeyEvent) {
			}
		});
		this.ackPercentQuorumField = new JTextField(3);
		this.ackRadioButton = new JRadioButton(this.r.getString("acknowledgeRadioLabel"));
		this.ackPercentRadioButton = new JRadioButton(this.r.getString("acknowledgePercentRadioLabel"));
		loadAckQuorumData();
		this.quorumPanel.add("left", new JLabel("   "));
		this.quorumPanel.add("left", jLabel);
		this.quorumPanel.add("left", this.ackQuorumField);
		this.signoffTree.loadAdhocSignoffOperation(this.acknowTask, 2);
		this.signoffTree.loadAdhocSignoffOperation(this.notifyTask, 3);
		final RouteTaskTreeCellRender render = new RouteTaskTreeCellRender();
		this.signoffTree.setCellRenderer(render);
		this.signoffTree.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent param1MouseEvent) {
				Point point = param1MouseEvent.getPoint();
				int i = UserRouteTaskSignoffsPanel.this.signoffTree.getRowForLocation(param1MouseEvent.getX(), param1MouseEvent.getY());
				if (i > 0) {
					Rectangle rectangle = UserRouteTaskSignoffsPanel.this.signoffTree.getRowBounds(i);
					SignoffTreeNode signoffTreeNode = (SignoffTreeNode) UserRouteTaskSignoffsPanel.this.signoffTree.getPathForRow(i).getLastPathComponent();
					int j = render.getClickedType(rectangle, point, signoffTreeNode.getLevel());
					if (j > 0) {
						TCComponentTask tCComponentTask = UserRouteTaskSignoffsPanel.this.getTypeTask(j);
						UserRouteTaskSignoffsPanel.this.signoffTree.performChangeSignoffType(signoffTreeNode, tCComponentTask, j);
					}
				}
			}
		});
	}

	private void loadAckQuorumData() {
		try
        {
            if(task.getState() == TCTaskState.COMPLETED)
            {
                ackQuorumField.setEnabled(false);
            }
        }
        catch(Exception _ex) { }
        try
        {
            TCProperty tcproperty = acknowTask.getTCProperty("signoff_quorum");
            int i = -1;
            if(tcproperty != null)
            {
                i = tcproperty.getIntValue();
            }
            ackQuorumField.setText((new StringBuilder()).append(i).toString());
        }
        catch(Exception _ex) { }
	}

	private void initializeTasks() {
		TCComponentTask[] arrayOfTCComponentTask = null;
		try {
			arrayOfTCComponentTask = this.routeTask.getSubtasks();
			if (arrayOfTCComponentTask != null && arrayOfTCComponentTask.length > 0) {
				ArrayList arrayList = new ArrayList();
				int b;
				for (b = 0; b < arrayOfTCComponentTask.length; b++)
					arrayList.add(arrayOfTCComponentTask[b]);
				for (b = 0; b < arrayOfTCComponentTask.length; b++) {
					TCComponentTask[] arrayOfTCComponentTask1 = arrayOfTCComponentTask[b].getSubtasks();
					for (int b1 = 0; b1 < arrayOfTCComponentTask1.length; b1++)
						arrayList.add(arrayOfTCComponentTask1[b1]);
				}
				arrayList.add(this.routeTask);
				String str = this.routeTask.getToStringProperty();
				String[] arrayOfString1 = { str, "task_type" };
				String[] arrayOfString2 = { "active_surrogate", "state", "responsible_party", "signoff_quorum", "task_template" };
				TCComponentType.getPropertiesSet(arrayList, arrayOfString1);
				TCComponentType.getTCPropertiesSet(arrayList, arrayOfString2);
			}
			if (arrayOfTCComponentTask != null && arrayOfTCComponentTask.length > 0) {
				for (int b = 0; b < arrayOfTCComponentTask.length; b++) {
					String str = arrayOfTCComponentTask[b].getTaskType();
					if (this.notifyTask == null && str.equals("EPMNotifyTask")) {
						this.notifyTask = arrayOfTCComponentTask[b];
					} else {
						TCComponentTask[] arrayOfTCComponentTask1 = arrayOfTCComponentTask[b].getSubtasks();
						if (arrayOfTCComponentTask1.length > 0)
							for (int b1 = 0; b1 < arrayOfTCComponentTask1.length; b1++) {
								String str1 = arrayOfTCComponentTask1[b1].getTaskType();
								if (str1.equals("EPMSelectSignoffTask")) {
									if (this.task == null && str.equals("EPMReviewTask")) {
										this.task = arrayOfTCComponentTask1[b1];
										break;
									}
									if (this.acknowTask == null && str.equals("EPMAcknowledgeTask")) {
										this.acknowTask = arrayOfTCComponentTask1[b1];
										break;
									}
								}
							}
					}
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	private void performSetAckQuorumAction() {
		this.session.queueOperation(new AbstractAIFOperation(this.r.getString("savingQuorum.MSG")) {
			public void executeOperation() {
				String str = UserRouteTaskSignoffsPanel.this.ackQuorumField.getText();
				if (str != null && UserRouteTaskSignoffsPanel.this.task != null) {
					try {
						UserRouteTaskSignoffsPanel.this.updateAckQuorum();
					} catch (Exception exception) {
						MessageBox.post(UserRouteTaskSignoffsPanel.this.parent, exception);
						UserRouteTaskSignoffsPanel.this.loadAckQuorumData();
					}
				}
			}
		});
	}

	private void updateAckQuorum() throws Exception
    {
        String s = ackQuorumField.getText();
        if(s != null && task != null)
        {
            int i = -1;
            if(!s.equalsIgnoreCase(quorumAllString))
            {
                i = Integer.parseInt(s);
            }
            ackQuorumField.setText((new StringBuilder()).append(i).toString());
            TCProperty tcproperty = acknowTask.getTCProperty("signoff_quorum");
            if(tcproperty != null)
            {
                tcproperty.setIntValue(i);
            }
        }
    }

	void updateQuorum() throws Exception {
		super.updateQuorum();
		updateAckQuorum();
	}

	protected void refreshPanel() {
		super.refreshPanel();
		loadAckQuorumData();
	}

	private TCComponentTask getTypeTask(int paramInt) {
		TCComponentTask tCComponentTask = null;
		if (paramInt == 1) {
			tCComponentTask = this.task;
		} else if (paramInt == 2) {
			tCComponentTask = this.acknowTask;
		} else if (paramInt == 3) {
			tCComponentTask = this.notifyTask;
		}
		return tCComponentTask;
	}

	protected void setAdhocDoneAttribute() {
		if (this.adhocDoneCheckBox.isVisible()) {
			try {
				String str = "Unset";
				boolean bool = this.adhocDoneCheckBox.isSelected();
				if (bool)
					str = "Completed";
				TCProperty tCProperty1 = this.task.getTCProperty("task_result");
				tCProperty1.setStringValue(str);
				TCProperty tCProperty2 = this.acknowTask.getTCProperty("task_result");
				tCProperty2.setStringValue(str);
				TCProperty tCProperty3 = this.notifyTask.getTCProperty("task_result");
				tCProperty3.setStringValue(str);
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
	}

	protected void triggerCompleteAction(boolean paramBoolean) throws TCException {
		try {
			AIFDesktop aIFDesktop = null;
			if (this.parent != null && this.parent instanceof AIFDesktop)
				aIFDesktop = (AIFDesktop) this.parent;
			CompleteOperation completeOperation = new CompleteOperation(aIFDesktop, new TCComponentTask[] { this.notifyTask }, "", null, "Completed");
			TCSession tCSession = this.notifyTask.getSession();
			tCSession.queueOperation(completeOperation);
		} catch (Exception exception) {
			MessageBox messageBox = null;
			messageBox = new MessageBox(exception);
			messageBox.setVisible(true);
			return;
		}
		try {
			this.task.performTaskAction(4, "", null, "Completed", null, new ArrayList(), new ArrayList());
		} catch (TCException tCException) {
			TestErrorListener.showAsyncModeMessageBox(tCException);
		}
		try {
			this.acknowTask.performTaskAction(4, "", null, "Completed", null, new ArrayList(), new ArrayList());
		} catch (TCException tCException) {
			TestErrorListener.showAsyncModeMessageBox(tCException);
		}
	}
}