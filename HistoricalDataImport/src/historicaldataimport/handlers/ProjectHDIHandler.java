package historicaldataimport.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Shell;

import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCSession;

import historicaldataimport.dialog.ProjectHDIDialog;

public class ProjectHDIHandler extends AbstractHandler{

	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		AbstractAIFUIApplication app = AIFUtility.getCurrentApplication();
		Shell shell = app.getDesktop().getShell();
		TCSession session = (TCSession) app.getSession();
		new ProjectHDIDialog(shell, session);
		return null;
	}

}
