package com.foxconn.electronics.convertebom;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.wb.swt.SWTResourceManager;

import com.foxconn.electronics.managementebom.updatebom.service.UpdateEBOMService;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentPseudoFolder;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Label;

public class ShowDeriveModelDialog extends Dialog
{
    private Table                   table;
    private TCComponentItemRevision itemRev;

    /**
     * Create the dialog.
     * 
     * @param parentShell
     */
    public ShowDeriveModelDialog(Shell parentShell, TCComponentItemRevision itemRev)
    {
        super(parentShell);
        setShellStyle(SWT.CLOSE);
        this.itemRev = itemRev;
    }

    /**
     * Create contents of the dialog.
     * 
     * @param parent
     */
    @Override
    protected Control createDialogArea(Composite parent)
    {
        Composite container = (Composite) super.createDialogArea(parent);
        final TableViewer tableViewer = new TableViewer(container, SWT.BORDER | SWT.FULL_SELECTION | SWT.BORDER);
        table = tableViewer.getTable();
        table.setHeaderBackground(SWTResourceManager.getColor(SWT.COLOR_INFO_BACKGROUND));
        GridData gd_table = new GridData(SWT.CENTER, SWT.TOP, false, false, 1, 1);
        gd_table.heightHint = 175;
        gd_table.widthHint = 920;
        table.setLayoutData(gd_table);
        table.setLinesVisible(true);
        table.setHeaderVisible(true);
        // table.setBounds(97, 79, 373, 154);
        final TableColumn newColumnTableColumn0 = new TableColumn(table, SWT.NONE);
        newColumnTableColumn0.setWidth(120);
        newColumnTableColumn0.setText("機種名稱");
        final TableColumn newColumnTableColumn1 = new TableColumn(table, SWT.NONE);
        newColumnTableColumn1.setWidth(160);
        newColumnTableColumn1.setText("PCA PN");
        final TableColumn newColumnTableColumn2 = new TableColumn(table, SWT.NONE);
        newColumnTableColumn2.setWidth(320);
        newColumnTableColumn2.setText("英文描述");
        final TableColumn newColumnTableColumn3 = new TableColumn(table, SWT.NONE);
        newColumnTableColumn3.setWidth(320);
        newColumnTableColumn3.setText("中文描述");
        tableViewer.setContentProvider(new IStructuredContentProvider()
        {
            @Override
            public Object[] getElements(Object var1)
            {
                return (Object[]) var1;
            }
        });
        tableViewer.setLabelProvider(new MyTableLabelProvider());
        tableViewer.getTable().addMouseListener(new MouseListener()
        {
            @Override
            public void mouseUp(MouseEvent var1)
            {
            }

            @Override
            public void mouseDown(MouseEvent var1)
            {
            }

            @Override
            public void mouseDoubleClick(MouseEvent var1)
            {
                StructuredSelection selection = (StructuredSelection) tableViewer.getSelection();
                Object[] os = (Object[]) selection.getFirstElement();
                Object[] contents = new Object[] { os[1] };
                Clipboard board = new Clipboard(Display.getCurrent());
                Transfer[] t = new Transfer[] { TextTransfer.getInstance() };
                board.setContents(contents, t);
            }
        });
        if (itemRev != null)
        {
            Object[] input = UpdateEBOMService.getDeriveItems(itemRev);
            tableViewer.setInput(input);
        }
        return container;
    }

    class MyTableLabelProvider extends LabelProvider implements ITableLabelProvider
    {
        public String getColumnText(Object element, int columnIndex)
        {
            if (element instanceof Object[])
            {
                Object[] array = (Object[]) element;
                return array[columnIndex].toString();
            }
            return element.toString();
        }

        @Override
        public void addListener(ILabelProviderListener var1)
        {
        }

        @Override
        public void dispose()
        {
        }

        @Override
        public boolean isLabelProperty(Object var1, String var2)
        {
            return false;
        }

        @Override
        public void removeListener(ILabelProviderListener var1)
        {
        }

        @Override
        public Image getColumnImage(Object var1, int var2)
        {
            return null;
        }
    }

    /**
     * Create contents of the button bar.
     * 
     * @param parent
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent)
    {
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CLOSE_LABEL, false);
    }

    /**
     * Return the initial size of the dialog.
     */
    @Override
    protected Point getInitialSize()
    {
        return new Point(980, 308);
    }

    @Override
    protected void configureShell(Shell shell)
    {
        super.configureShell(shell);
        shell.setText("受影響衍生機種");
    }
}
