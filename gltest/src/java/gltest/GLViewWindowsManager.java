package gltest;

import crossbase.ui.ViewWindowsManager;

public class GLViewWindowsManager extends ViewWindowsManager<GLDocument, GLViewWindow>
{
	private String applicationTitle;

	@Override
	protected GLViewWindow createViewWindow()
	{
		return new GLViewWindow(applicationTitle, this, getMenuConstructor());
	}
	
	public GLViewWindowsManager(String applicationTitle)
	{
		this.applicationTitle = applicationTitle;
	}
}