package com.hh.tools.newitem;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.SimpleFormatter;

import javax.imageio.ImageIO;
import javax.swing.AbstractCellEditor;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.common.Activator;
import com.teamcenter.rac.common.TCTable;
import com.teamcenter.rac.common.TCTypeRenderer;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentListOfValues;
import com.teamcenter.rac.kernel.TCComponentListOfValuesType;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.stylesheet.PropertyArray;
import com.teamcenter.rac.stylesheet.PropertyLOVCombobox;
import com.teamcenter.rac.util.DateButton;
import com.teamcenter.rac.util.HyperLink;
import com.teamcenter.rac.util.combobox.iComboBox;

public class TableUtil {

    public static void setColumnSize(JTable table, int i, int preferedWidth, int maxWidth, int minWidth) {
    	//表格的列模型  
        TableColumnModel cm = table.getColumnModel();
        //得到第i个列对象   
        TableColumn column = cm.getColumn(i);
        column.setPreferredWidth(preferedWidth);
        //column.setMaxWidth(maxWidth);
        column.setMinWidth(minWidth);
    }

    // 隐藏列
    public static void hideTableColumn(TCTable table, int column) {
        TableColumn tc = table.getTableHeader().getColumnModel().getColumn(
                column);
        tc.setMaxWidth(0);
        tc.setPreferredWidth(0);
        tc.setWidth(0);
        tc.setMinWidth(0);
        table.getTableHeader().getColumnModel().getColumn(column)
                .setMaxWidth(0);
        table.getTableHeader().getColumnModel().getColumn(column)
                .setMinWidth(0);
    }

    public static void hideTableColumn(JTable table, int column) {
        TableColumn tc = table.getTableHeader().getColumnModel().getColumn(
                column);
        tc.setMaxWidth(0);
        tc.setPreferredWidth(0);
        tc.setWidth(0);
        tc.setMinWidth(0);
        table.getTableHeader().getColumnModel().getColumn(column)
                .setMaxWidth(0);
        table.getTableHeader().getColumnModel().getColumn(column)
                .setMinWidth(0);
    }

    public static class ComboBoxCellEditor extends AbstractCellEditor implements TableCellEditor {
        //~ Static fields/initializers -------------------------------------------------------------------------------------

        private static final long serialVersionUID = 1L;

        //~ Instance fields ------------------------------------------------------------------------------------------------

        protected PropertyLOVCombobox combobox;

        //~ Constructors ---------------------------------------------------------------------------------------------------


        public ComboBoxCellEditor(TCComponentListOfValues lov) {
            combobox = new PropertyLOVCombobox();
            combobox.setBackground(Color.white);
            try {
//				  TCSession session = (TCSession) AIFUtility.getCurrentApplication().getSession();
//				  TCComponentListOfValuesType lovType = (TCComponentListOfValuesType) session.getTypeComponent("ListOfValues");
//					TCComponentListOfValues lov = lovType.findLOVByName(lovname);
//					String[] displayValues= lov.getListOfValues().getLOVDisplayValues();
//					for(int i=0;i<displayValues.length;i++) {
//						combobox.addItem(displayValues[i]);
//						System.out.println( "comboBox addItem == "+displayValues[i]);
//					}
                combobox.setLOVComponent(lov);
                combobox.setLovName(lov.getProperty(lov.PROP_LOV_NAME));

            } catch (Exception e) {
                e.printStackTrace();
            }

            //combobox.setLovName(lovname);

        }


        public ComboBoxCellEditor(String lovname) {
            combobox = new PropertyLOVCombobox();
            combobox.setBackground(Color.white);
            try {
                TCSession session = (TCSession) AIFUtility.getCurrentApplication().getSession();
                TCComponentListOfValuesType lovType = (TCComponentListOfValuesType) session.getTypeComponent("ListOfValues");

                TCComponentListOfValues lov = lovType.findLOVByName(lovname);
                String[] displayValues = lov.getListOfValues().getLOVDisplayValues();
                for (int i = 0; i < displayValues.length; i++) {
                    combobox.addItem(displayValues[i]);
                    System.out.println("comboBox addItem == " + displayValues[i]);
                }
//					combobox.setLOVComponent(lovname);
//					combobox.setLovName(lovname);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //combobox.setLovName(lovname);

        }


        //~ Methods --------------------------------------------------------------------------------------------------------

        @Override
        public Object getCellEditorValue() {
            return combobox.getSelectedObject();
        }

        //~ ----------------------------------------------------------------------------------------------------------------

        @Override
        public Component getTableCellEditorComponent(
                JTable table,
                Object value,
                boolean isSelected,
                int row,
                int column) {
            return combobox;

        }
    } // end class CheckBoxCellEditor


    public static class ComboBoxCellEditor1 extends AbstractCellEditor implements TableCellEditor {
        //~ Static fields/initializers -------------------------------------------------------------------------------------

        private static final long serialVersionUID = 1L;

        //~ Instance fields ------------------------------------------------------------------------------------------------

        protected PropertyLOVCombobox combobox;

        //~ Constructors ---------------------------------------------------------------------------------------------------


        public ComboBoxCellEditor1(TCComponentListOfValues lov) {
            combobox = new PropertyLOVCombobox();
            combobox.setBackground(Color.white);
            try {
//				  TCSession session = (TCSession) AIFUtility.getCurrentApplication().getSession();
//				  TCComponentListOfValuesType lovType = (TCComponentListOfValuesType) session.getTypeComponent("ListOfValues");
//					TCComponentListOfValues lov = lovType.findLOVByName(lovname);
//					String[] displayValues= lov.getListOfValues().getLOVDisplayValues();
//					for(int i=0;i<displayValues.length;i++) {
//						combobox.addItem(displayValues[i]);
//						System.out.println( "comboBox addItem == "+displayValues[i]);
//					}
                combobox.setLOVComponent(lov);
                combobox.setLovName(lov.getProperty(lov.PROP_LOV_NAME));

            } catch (Exception e) {
                e.printStackTrace();
            }

            //combobox.setLovName(lovname);

        }

        public ComboBoxCellEditor1(String[] arrays) {
            combobox = new PropertyLOVCombobox();
            combobox.setBackground(Color.white);
            try {
//				  TCSession session = (TCSession) AIFUtility.getCurrentApplication().getSession();
//				  TCComponentListOfValuesType lovType = (TCComponentListOfValuesType) session.getTypeComponent("ListOfValues");
//					TCComponentListOfValues lov = lovType.findLOVByName(lovname);
//					String[] displayValues= lov.getListOfValues().getLOVDisplayValues();
                for (int i = 0; i < arrays.length; i++) {
                    combobox.addItem(arrays[i]);
                    System.out.println("comboBox addItem == " + arrays[i]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            //combobox.setLovName(lovname);

        }


        public ComboBoxCellEditor1(String lovname) {
            combobox = new PropertyLOVCombobox();
            combobox.setBackground(Color.white);
            try {
                TCSession session = (TCSession) AIFUtility.getCurrentApplication().getSession();
                TCComponentListOfValuesType lovType = (TCComponentListOfValuesType) session.getTypeComponent("ListOfValues");
                TCComponentListOfValues lov = lovType.findLOVByName(lovname);
                String[] displayValues = lov.getListOfValues().getLOVDisplayValues();
                for (int i = 0; i < displayValues.length; i++) {
                    combobox.addItem(displayValues[i]);
                    System.out.println("comboBox addItem == " + displayValues[i]);
                }
//					combobox.setLOVComponent(lovname);
//					combobox.setLovName(lovname);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //combobox.setLovName(lovname);

        }

        //~ Methods --------------------------------------------------------------------------------------------------------

        @Override
        public Object getCellEditorValue() {
            return combobox.getSelectedObject();
        }

        //~ ----------------------------------------------------------------------------------------------------------------


        @Override
        public Component getTableCellEditorComponent(
                JTable table,
                Object value,
                boolean isSelected,
                int row,
                int column) {
            return combobox;

        }


        public PropertyLOVCombobox getCombobox() {
            return combobox;
        }
    } // end class CheckBoxCellEditor


    public static class ComboBoxTableCellRenderer extends PropertyLOVCombobox implements TableCellRenderer {

        public ComboBoxTableCellRenderer() {
            super();
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            System.out.println("value == " + value);

            this.setText((value == null) ? "" : value.toString());
            return this;
        }
    }


//	public static class TextButtonCellEditor extends AbstractCellEditor implements TableCellEditor {
//		
//		private static final long serialVersionUID = 1L;
//		
//		private PropertyArray propertyArray;
//		
//		public TextButtonCellEditor() {
//			// TODO Auto-generated constructor stub
//			propertyArray.setLayout(new BorderLayout());
//			propertyArray.setBackground(Color.white);
//		}
//
//		@Override
//		public Object getCellEditorValue() {
//			// TODO Auto-generated method stub
//			if (propertyArray.getToolTipText() == null) {
//				return "";
//			}
//			return propertyArray.getToolTipText();
//		}
//
//		@Override
//		public Component getTableCellEditorComponent(JTable table,
//				Object value, boolean isSelected, int row, int column) {
//			// TODO Auto-generated method stub
//			return propertyArray;
//		}
//		
//	}

    public static class DateButtonCellEditor extends AbstractCellEditor implements TableCellEditor {
        //~ Static fields/initializers -------------------------------------------------------------------------------------

        private static final long serialVersionUID = 1L;

        //~ Instance fields ------------------------------------------------------------------------------------------------

        protected DateButton button;

        //~ Constructors ---------------------------------------------------------------------------------------------------

        public DateButtonCellEditor() {

            button = new DateButton(null, "yyyy-MM-dd", false, true);
            button.setHorizontalAlignment(SwingConstants.CENTER);
            button.setBackground(Color.white);
        }

        //~ Methods --------------------------------------------------------------------------------------------------------

        @Override
        public Object getCellEditorValue() {
            if (button.getDate() == null) {
                return "";
            }
            return button.getText();
        }


        //~ ----------------------------------------------------------------------------------------------------------------

        @Override
        public Component getTableCellEditorComponent(
                JTable table,
                Object value,
                boolean isSelected,
                int row,
                int column) {
            return button;

        }
    } // end class CheckBoxCellEditor

    public static class DateButtonTableCellRenderer extends DateButton implements TableCellRenderer {

        public DateButtonTableCellRenderer() {
            super();
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            System.out.println("datebutton value == " + value);
            Date date = this.getDate();
            System.out.println("datebutton date == " + date);
            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    "yyyy-MM-dd");
            String dateValue = "";
            if (date != null) {
                dateValue = dateFormat.format(date);

            }
            System.out.println("date dateValue == " + dateValue);
            this.setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    public static class PSEButtonCellEditor extends AbstractCellEditor
            implements TableCellEditor {
        private static final long serialVersionUID = 1L;
        protected JButton button;
        public TCTable table = null;

        public PSEButtonCellEditor(String text, final TCTable table) {
            this.table = table;
            this.button = new JButton(text);
            this.button.setHorizontalAlignment(0);
            this.button.setBackground(Color.white);
            this.button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    System.out.println("click");
                    int row = table.getSelectedRow();
                    Object ob = table.getValueAt(row, 2);
                    System.out.println("ob == " + ob);
                    if ((ob != null) && (ob instanceof TCComponentItemRevision)) {
                        System.out.println("is Rev");
                        TCComponentItemRevision itemRev = (TCComponentItemRevision) ob;
                        try {
                            TCComponentItemRevision newItemRev = itemRev.getItem().getLatestItemRevision();
                            String s = "com.teamcenter.rac.pse.PSEPerspective";
                            Activator.getDefault().openPerspective(s);
                            Activator.getDefault().openComponents(s, new InterfaceAIFComponent[]{newItemRev});
                        } catch (TCException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            });
        }

        public Object getCellEditorValue() {
            return this.button.getText();
        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            return this.button;
        }
    }

    public static class PSEButtonTableCellRenderer extends JButton implements TableCellRenderer {
        String text = "";

        public PSEButtonTableCellRenderer(String text) {
            this.text = text;
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            System.out.println("value == " + value);
            setText(this.text);
            return this;
        }
    }

    public static class QADButtonCellEditor extends AbstractCellEditor implements TableCellEditor {
        //~ Static fields/initializers -------------------------------------------------------------------------------------

        private static final long serialVersionUID = 1L;

        //~ Instance fields ------------------------------------------------------------------------------------------------

        protected JButton button;
        protected TCTable table;

        //~ Constructors ---------------------------------------------------------------------------------------------------


        //~ Methods --------------------------------------------------------------------------------------------------------

        @Override
        public Object getCellEditorValue() {
            return button.getText();
        }

        //~ ----------------------------------------------------------------------------------------------------------------

        @Override
        public Component getTableCellEditorComponent(
                JTable table,
                Object value,
                boolean isSelected,
                int row,
                int column) {
            return button;

        }
    } // end class CheckBoxCellEditor

    public static class QADButtonCellEditor1 extends AbstractCellEditor implements TableCellEditor {
        //~ Static fields/initializers -------------------------------------------------------------------------------------

        private static final long serialVersionUID = 1L;

        //~ Instance fields ------------------------------------------------------------------------------------------------

        protected JButton button;
        protected TCTable table;

        //~ Constructors ---------------------------------------------------------------------------------------------------

        //~ Methods --------------------------------------------------------------------------------------------------------

        @Override
        public Object getCellEditorValue() {
            return button.getText();
        }

        //~ ----------------------------------------------------------------------------------------------------------------

        @Override
        public Component getTableCellEditorComponent(
                JTable table,
                Object value,
                boolean isSelected,
                int row,
                int column) {
            return button;

        }
    } // end class CheckBoxCellEditor

    public static class QADButtonCellEditor2 extends AbstractCellEditor implements TableCellEditor {
        //~ Static fields/initializers -------------------------------------------------------------------------------------

        private static final long serialVersionUID = 1L;

        //~ Instance fields ------------------------------------------------------------------------------------------------

        protected JButton button;
        protected TCTable table;

        //~ Constructors ---------------------------------------------------------------------------------------------------


        //~ Methods --------------------------------------------------------------------------------------------------------

        @Override
        public Object getCellEditorValue() {
            return button.getText();
        }

        //~ ----------------------------------------------------------------------------------------------------------------

        @Override
        public Component getTableCellEditorComponent(
                JTable table,
                Object value,
                boolean isSelected,
                int row,
                int column) {
            return button;

        }
    } // end class CheckBoxCellEditor

    public static class QADButtonTableCellRenderer extends JButton implements TableCellRenderer {
        String text = "";

        public QADButtonTableCellRenderer(String text) {

            super();
            this.text = text;
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            System.out.println("value == " + value);
            this.setText(text);
            return this;
        }
    }

    public static class ButtonCellEditor extends AbstractCellEditor implements TableCellEditor {
        //~ Static fields/initializers -------------------------------------------------------------------------------------

        private static final long serialVersionUID = 1L;

        //~ Instance fields ------------------------------------------------------------------------------------------------

        protected JButton button;

        //~ Constructors ---------------------------------------------------------------------------------------------------

        public ButtonCellEditor(String text) {

            button = new JButton(text);
            button.setHorizontalAlignment(SwingConstants.CENTER);
            button.setBackground(Color.white);
            button.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    // TODO Auto-generated method stub
                    System.out.println("click");
                }
            });
        }

        //~ Methods --------------------------------------------------------------------------------------------------------

        @Override
        public Object getCellEditorValue() {
            return button.getText();
        }

        public JButton getButton() {
            return this.button;
        }

        //~ ----------------------------------------------------------------------------------------------------------------

        @Override
        public Component getTableCellEditorComponent(
                JTable table,
                Object value,
                boolean isSelected,
                int row,
                int column) {
            return button;

        }
    } // end class CheckBoxCellEditor

    public static class ButtonTableCellRenderer extends JButton implements TableCellRenderer {
        String text = "";

        public ButtonTableCellRenderer(String text) {

            super();
            this.text = text;
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            System.out.println("value == " + value);
            this.setText(text);
            return this;
        }
    }

    public static class JTextAreaCellEditor extends AbstractCellEditor implements TableCellEditor {
        //~ Static fields/initializers -------------------------------------------------------------------------------------

        private static final long serialVersionUID = 1L;

        //~ Instance fields ------------------------------------------------------------------------------------------------

        protected JTextArea textArea;
        JScrollPane jScrollPane = new JScrollPane();
        //~ Constructors ---------------------------------------------------------------------------------------------------

        public JTextAreaCellEditor() {

            textArea = new JTextArea();
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);

            jScrollPane.setViewportView(textArea);
            jScrollPane.setAutoscrolls(true);
        }

        //~ Methods --------------------------------------------------------------------------------------------------------


        public JTextArea getButton() {
            return this.textArea;
        }

        //~ ----------------------------------------------------------------------------------------------------------------

        @Override
        public Component getTableCellEditorComponent(
                JTable table,
                Object value,
                boolean isSelected,
                int row,
                int column) {
            System.out.println("getTableCellEditorComponent value == " + value);
            textArea.setText(value == null ? "" : value.toString());
            return jScrollPane;

        }

        @Override
        public Object getCellEditorValue() {
            // TODO Auto-generated method stub
            return textArea.getText();
        }
    } // end class CheckBoxCellEditor

    public static class JTextAreaCellRenderer extends JTextArea implements TableCellRenderer {
        String text = "";

        public JTextAreaCellRenderer() {

            super();
            // setLineWrap(true);
            //this.text = text;
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            System.out.println("isSelected == " + isSelected);
            System.out.println("hasFocus == " + hasFocus);
            System.out.println("row == " + row);
            System.out.println("column == " + column);
            System.out.println("value == " + value);
            setText(value == null ? "" : value.toString());
            this.setWrapStyleWord(true);
            this.setLineWrap(true);
            JScrollPane jsp = new JScrollPane(this);
            return jsp;
        }
    }

    public static class CheckBoxCellEditor extends AbstractCellEditor implements TableCellEditor {
        //~ Static fields/initializers -------------------------------------------------------------------------------------

        private static final long serialVersionUID = 1L;

        //~ Instance fields ------------------------------------------------------------------------------------------------

        protected JCheckBox checkBox;

        //~ Constructors ---------------------------------------------------------------------------------------------------

        public CheckBoxCellEditor() {
            checkBox = new JCheckBox();

            checkBox.setHorizontalAlignment(SwingConstants.CENTER);
            checkBox.setBackground(Color.white);

        }

        //~ Methods --------------------------------------------------------------------------------------------------------

        @Override
        public Object getCellEditorValue() {
            return Boolean.valueOf(checkBox.isSelected());
        }

        //~ ----------------------------------------------------------------------------------------------------------------

        @Override
        public Component getTableCellEditorComponent(
                JTable table,
                Object value,
                boolean isSelected,
                int row,
                int column) {
            System.out.println("isSelected == " + isSelected);
            System.out.println("value == " + value);
            if (value == null || "".equals(value.toString().trim())) {
                value = false;
            }
            checkBox.setSelected(((Boolean) value).booleanValue());

            return checkBox;

        }
    } // end class CheckBoxCellEditor

    public static class CWCheckBoxRenderer extends JCheckBox implements TableCellRenderer {
        //~ Static fields/initializers -------------------------------------------------------------------------------------

        private static final long serialVersionUID = 1L;

        //~ Instance fields ------------------------------------------------------------------------------------------------

        Border border = new EmptyBorder(1, 2, 1, 2);

        //~ Constructors ---------------------------------------------------------------------------------------------------

        public CWCheckBoxRenderer() {
            super();
            setOpaque(true);
            setHorizontalAlignment(SwingConstants.CENTER);
        }

        //~ Methods --------------------------------------------------------------------------------------------------------

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column) {
            if (value instanceof Boolean) {
                setSelected(((Boolean) value).booleanValue());

                // setEnabled(table.isCellEditable(row, column));
                setForeground(table.getForeground());
                setBackground(table.getBackground());

            }

            return this;
        }
    } // end class CWCheckBoxRenderer


//	public static class PropertyArrayCellEditor extends AbstractCellEditor implements TableCellEditor {
//		  //~ Static fields/initializers -------------------------------------------------------------------------------------
//
//		  private static final long serialVersionUID = 1L;
//
//		  //~ Instance fields ------------------------------------------------------------------------------------------------
//
//		  protected PropertyArray propertyArray;
//
//		  //~ Constructors ---------------------------------------------------------------------------------------------------
//
//		  
//		  public PropertyArrayCellEditor() {
//			  propertyArray = new PropertyArray();
//
//			 
//		  }
//		 
//
//		  //~ Methods --------------------------------------------------------------------------------------------------------
//
//		  @Override 
//		  public Object getCellEditorValue() {
//		    return propertyArray.getEditableValue();
//		  }
//
//		  //~ ----------------------------------------------------------------------------------------------------------------
//
//		  @Override 
//		  public Component getTableCellEditorComponent(
//		    JTable  table,
//		    Object  value,
//		    boolean isSelected,
//		    int     row,
//		    int     column) {
//		    return propertyArray;
//
//		  }
//
//
//		 
//		} // end class CheckBoxCellEditor
//
//	public static class  PropertyArrayTableCellRenderer   extends   JPanel   implements   TableCellRenderer{   
//	      
//	  public   PropertyArrayTableCellRenderer()   {   
//	                          super();   
//	  }   
//
//	  public   Component   getTableCellRendererComponent(JTable   table,   Object   value,   
//	                                              boolean   isSelected,   boolean   hasFocus,   int   row,   int   column)   {   
//		  			System.out.println("PropertyArray ");
//	                 
//	                 return   this;   
//	  }
//
//	   
//	}

    public static class TCTableCellListener implements PropertyChangeListener, Runnable {
        private TCTable table;
        private Action action;
        private int row;
        private int column;
        private Object oldValue;
        private Object newValue;

        /**
         * Create a TableCellListener.
         *
         * @param table  the table to be monitored for data changes
         * @param action the Action to invoke when cell data is changed
         */
        public TCTableCellListener(TCTable table, Action action) {
            this.table = table;
            this.action = action;
            this.table.addPropertyChangeListener(this);
        }

        /**
         * Create a TableCellListener with a copy of all the data relevant to
         * the change of data for a given cell.
         *
         * @param row      the row of the changed cell
         * @param column   the column of the changed cell
         * @param oldValue the old data of the changed cell
         * @param newValue the new data of the changed cell
         */
        private TCTableCellListener(TCTable table, int row, int column, Object oldValue, Object newValue) {
            this.table = table;
            this.row = row;
            this.column = column;
            this.oldValue = oldValue;
            this.newValue = newValue;
        }

        /**
         * Get the column that was last edited
         *
         * @return the column that was edited
         */
        public int getColumn() {
            return column;
        }

        /**
         * Get the new value in the cell
         *
         * @return the new value in the cell
         */
        public Object getNewValue() {
            return newValue;
        }

        /**
         * Get the old value of the cell
         *
         * @return the old value of the cell
         */
        public Object getOldValue() {
            return oldValue;
        }

        /**
         * Get the row that was last edited
         *
         * @return the row that was edited
         */
        public int getRow() {
            return row;
        }

        /**
         * Get the table of the cell that was changed
         *
         * @return the table of the cell that was changed
         */
        public TCTable getTable() {
            return table;
        }

        //
        // Implement the PropertyChangeListener interface
        //
        @Override
        public void propertyChange(PropertyChangeEvent e) {
            // A cell has started/stopped editing
            if ("tableCellEditor".equals(e.getPropertyName())) {
                if (table.isEditing()) {
                    //System.out.printf("tableCellEditor is editing..%n");
                    processEditingStarted();
                } else {
                    //System.out.printf("tableCellEditor editing stopped..%n");
                    processEditingStopped();
                }
            }
        }

        /*
         * Save information of the cell about to be edited
         */
        private void processEditingStarted() {
            // The invokeLater is necessary because the editing row and editing
            // column of the table have not been set when the "tableCellEditor"
            // PropertyChangeEvent is fired.
            // This results in the "run" method being invoked
            SwingUtilities.invokeLater(this);
        }

        /*
         * See above.
         */
        @Override
        public void run() {
            row = table.convertRowIndexToModel(table.getEditingRow());
            column = table.convertColumnIndexToModel(table.getEditingColumn());
            oldValue = table.getModel().getValueAt(row, column);
            //这里应对oldValue为null的情况做处理，否则将导致原值与新值均为空时仍被视为值改变
            if (oldValue == null)
                oldValue = "";
            newValue = null;
        }

        /*
         *  Update the Cell history when necessary
         */
        private void processEditingStopped() {
            newValue = table.getModel().getValueAt(row, column);
            //这里应对newValue为null的情况做处理，否则后面会抛出异常
            if (newValue == null)
                newValue = "";
            // The data has changed, invoke the supplied Action
            if (!newValue.equals(oldValue)) {
                // Make a copy of the data in case another cell starts editing
                // while processing this change
                TCTableCellListener tcl = new TCTableCellListener(
                        getTable(), getRow(), getColumn(), getOldValue(), getNewValue());
                ActionEvent event = new ActionEvent(
                        tcl,
                        ActionEvent.ACTION_PERFORMED,
                        "");
                action.actionPerformed(event);
            }
        }
    }

    public static class TableCellListener implements PropertyChangeListener, Runnable {
        private JTable table;
        private Action action;
        private int row;
        private int column;
        private Object oldValue;
        private Object newValue;

        /**
         * Create a TableCellListener.
         *
         * @param table  the table to be monitored for data changes
         * @param action the Action to invoke when cell data is changed
         */
        public TableCellListener(JTable table, Action action) {
            this.table = table;
            this.action = action;
            this.table.addPropertyChangeListener(this);
        }

        /**
         * Create a TableCellListener with a copy of all the data relevant to
         * the change of data for a given cell.
         *
         * @param row      the row of the changed cell
         * @param column   the column of the changed cell
         * @param oldValue the old data of the changed cell
         * @param newValue the new data of the changed cell
         */
        private TableCellListener(JTable table, int row, int column, Object oldValue, Object newValue) {
            this.table = table;
            this.row = row;
            this.column = column;
            this.oldValue = oldValue;
            this.newValue = newValue;
        }

        /**
         * Get the column that was last edited
         *
         * @return the column that was edited
         */
        public int getColumn() {
            return column;
        }

        /**
         * Get the new value in the cell
         *
         * @return the new value in the cell
         */
        public Object getNewValue() {
            return newValue;
        }

        /**
         * Get the old value of the cell
         *
         * @return the old value of the cell
         */
        public Object getOldValue() {
            return oldValue;
        }

        /**
         * Get the row that was last edited
         *
         * @return the row that was edited
         */
        public int getRow() {
            return row;
        }

        /**
         * Get the table of the cell that was changed
         *
         * @return the table of the cell that was changed
         */
        public JTable getTable() {
            return table;
        }

        //
        // Implement the PropertyChangeListener interface
        //
        @Override
        public void propertyChange(PropertyChangeEvent e) {
            // A cell has started/stopped editing
            if ("tableCellEditor".equals(e.getPropertyName())) {
                if (table.isEditing()) {
                    //System.out.printf("tableCellEditor is editing..%n");
                    processEditingStarted();
                } else {
                    //System.out.printf("tableCellEditor editing stopped..%n");
                    processEditingStopped();
                }
            }
        }

        /*
         * Save information of the cell about to be edited
         */
        private void processEditingStarted() {
            // The invokeLater is necessary because the editing row and editing
            // column of the table have not been set when the "tableCellEditor"
            // PropertyChangeEvent is fired.
            // This results in the "run" method being invoked
            SwingUtilities.invokeLater(this);
        }

        /*
         * See above.
         */
        @Override
        public void run() {
            row = table.convertRowIndexToModel(table.getEditingRow());
            column = table.convertColumnIndexToModel(table.getEditingColumn());
            oldValue = table.getModel().getValueAt(row, column);
            //这里应对oldValue为null的情况做处理，否则将导致原值与新值均为空时仍被视为值改变
            if (oldValue == null)
                oldValue = "";
            newValue = null;
        }

        /*
         *  Update the Cell history when necessary
         */
        private void processEditingStopped() {
            newValue = table.getModel().getValueAt(row, column);
            //这里应对newValue为null的情况做处理，否则后面会抛出异常
            if (newValue == null)
                newValue = "";
            // The data has changed, invoke the supplied Action
            if (!newValue.equals(oldValue)) {
                // Make a copy of the data in case another cell starts editing
                // while processing this change
                TableCellListener tcl = new TableCellListener(
                        getTable(), getRow(), getColumn(), getOldValue(), getNewValue());
                ActionEvent event = new ActionEvent(
                        tcl,
                        ActionEvent.ACTION_PERFORMED,
                        "");
                action.actionPerformed(event);
            }
        }
    }


    public static class ImageRenderer implements TableCellRenderer {


        @SuppressWarnings("unchecked")

        @Override

        public Component getTableCellRendererComponent(

                JTable table, Object value, boolean isSelected,

                boolean hasFocus, int rowIndex, int columnIndex) {

            if (isSelected) {
                table.setBackground(Color.BLUE);
            } else {
                table.setBackground(Color.WHITE);
            }

            if (value instanceof Image) {

                JLabel jLabel = new JLabel();

                jLabel.setLayout(new BorderLayout());//设置布局

                jLabel.setIcon(new ImageIcon((Image) value));//给jlable设置图片


                return jLabel;

            } else if (value instanceof File) {

                try {

                    return new JLabel(new ImageIcon(ImageIO.read((File) value)));

                } catch (IOException ex) {

                    throw new RuntimeException(ex.getMessage(), ex);

                }

            } else {

                String val = String.valueOf(value);

                try {

                    return new JLabel(new ImageIcon(ImageIO.read(new File(val))));

                } catch (IOException ex) {

                    throw new RuntimeException(ex.getMessage(), ex);

                }

            }

        }

    }

}



