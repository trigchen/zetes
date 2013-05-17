package crossbase.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import crossbase.abstracts.Document;
import crossbase.abstracts.MenuConstructor;
import crossbase.abstracts.ViewWindow;

public class MenuConstructorBase<TD extends Document, TVW extends ViewWindow<TD>> implements MenuConstructor<TD, TVW>
{
	private SelectionAdapter exitSelectionAdapter, aboutSelectionAdapter;
	private ArrayList<TVW> viewWindows;
	private HashMap<TVW, Set<MenuItem>> shellMenuItems;
	private Set<MenuItem> globalMenuItems;
	
	protected TVW getActiveViewWindow()
	{
		for (TVW viewWindow : viewWindows)
		{
			if (viewWindow.isActive())
			{
				return viewWindow;
			}
		}			
		return null;
	}
	
	/**
	 * Adds a menu to the storage
	 * @param viewWindow The window the menu connected to. If it's null, the global menu will be assumed 
	 * @param menu The menu
	 */
	protected void addShellMenu(TVW viewWindow, MenuItem menuItem)
	{
		if (menuItem == null) 
		{
			return;
		}
		
		if (viewWindow == null)
		{
			globalMenuItems.add(menuItem);
		}
		else
		{
			if (!shellMenuItems.containsKey(viewWindow))
			{
				shellMenuItems.put(viewWindow, new HashSet<MenuItem>());
			}
			shellMenuItems.get(viewWindow).add(menuItem);
		}
	}
	
	/**
	 * Erases all menus and menu items for the selected shell. 
	 * If the shell is null, erases all menus form global menu. 
	 * @param viewWindow The window to erase menus from
	 */
	protected void eraseAllMenusForWindow(TVW viewWindow)
	{
		if (viewWindow == null)
		{
			while (globalMenuItems.size() > 0)
			{
				for (MenuItem mi : globalMenuItems)
				{
					if (mi != null && !mi.isDisposed()) mi.dispose();
				}
				globalMenuItems.clear();
			}
		}
		else
		{
			if (shellMenuItems.containsKey(viewWindow))
			{
				while (shellMenuItems.get(viewWindow).size() > 0)
				{
					for (MenuItem mi : shellMenuItems.get(viewWindow))
					{
						if (!mi.isDisposed()) mi.dispose();
					}
					shellMenuItems.get(viewWindow).clear();
				}				
			}
		}
	}
	
	private void appendItems(Menu menu, List<MenuItem> items)
	{
		if (items == null) return;
		for (MenuItem item : items)
		{
			item.setMenu(menu);
		}
	}
	
	private MenuItem createAndAppendFileMenu(Menu menu)
	{
		MenuItem fileMenuItem = new MenuItem(menu, SWT.CASCADE);
		
		fileMenuItem.setText("&File");

		Menu fileMenu = new Menu(fileMenuItem);
		fileMenuItem.setMenu(fileMenu);

		appendCustomFileMenuItems(fileMenu);
		
		if (!SWT.getPlatform().equals("cocoa"))
		{
			if (fileMenu.getItemCount() > 0) 
			{
				// If the menu isn't empty yet, adding the new item
				new MenuItem(fileMenu, SWT.SEPARATOR);
			}

			// "Exit" menu item
			MenuItem mainMenuItemExit = new MenuItem(fileMenu, SWT.NONE);
			mainMenuItemExit.addSelectionListener(exitSelectionAdapter);

			mainMenuItemExit.setText("E&xit");
		}
		
		if (fileMenu.getItemCount() > 0)
		{
			return fileMenuItem;
		}
		else
		{
			fileMenuItem.dispose();
			return null;
		}
		
	}

	protected void appendCustomFileMenuItems(Menu fileMenu)
	{
		// To be overridden in the inherited classes
	}
	
	private MenuItem createAndAppendWindowsMenu(Menu menu, final boolean addFullscreen, final boolean addMaximize)
	{
		MenuItem windowMenuItem = new MenuItem(menu, SWT.CASCADE);
		
		windowMenuItem.setText("&Window");

		Menu windowMenu = new Menu(windowMenuItem);
		windowMenuItem.setMenu(windowMenu);
		
		// "Minimize" menu item
		final MenuItem minimizeMenuItem = new MenuItem(windowMenu, SWT.NONE);
		minimizeMenuItem.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent arg0)
			{
				if (getActiveViewWindow() != null)
				{
					getActiveViewWindow().toggleMinimized();
				}
			}
		});
		HotKey minimizeHotKey = new HotKey(HotKey.MOD1, 'M');
		minimizeMenuItem.setAccelerator(minimizeHotKey.toAccelerator());
		minimizeMenuItem.setText("&Minimize\t" + minimizeHotKey.toString());
		
		final MenuItem maximizeMenuItem;
		if (addMaximize)
		{
			// "Maximize" menu item
			maximizeMenuItem = new MenuItem(windowMenu, SWT.NONE);
			maximizeMenuItem.addSelectionListener(new SelectionAdapter()
			{
				@Override
				public void widgetSelected(SelectionEvent arg0)
				{
					if (getActiveViewWindow() != null)
					{
						getActiveViewWindow().toggleMaximized();
					}
				}
			});
			maximizeMenuItem.setText("Zoom");
		}
		else
		{
			maximizeMenuItem = null;
		}
		
		// "Fullscreen" menu item
		final MenuItem fullscreenMenuItem;
		if (addFullscreen)
		{
			fullscreenMenuItem = new MenuItem(windowMenu, SWT.NONE);
			fullscreenMenuItem.addSelectionListener(new SelectionAdapter()
			{
				@Override
				public void widgetSelected(SelectionEvent arg0)
				{
					if (Display.getCurrent() != null && !Display.getCurrent().isDisposed())
					{
						getActiveViewWindow().toggleFullScreen();
					}
				}
			});
			HotKey fullscreenHotKey = new HotKey(HotKey.MOD1 | HotKey.SHIFT, 'F');
			fullscreenMenuItem.setAccelerator(fullscreenHotKey.toAccelerator());
			fullscreenMenuItem.setText("Fullscreen\t" + fullscreenHotKey.toString());
		}
		else
		{
			fullscreenMenuItem = null;
		}
		
		// Custom items
		List<MenuItem> customItems = createCustomWindowMenuItems();

		if (customItems != null && customItems.size() > 0) 
		{
			// If the menu isn't empty yet, adding the new item
			new MenuItem(windowMenu, SWT.SEPARATOR);
		}
		
		appendItems(windowMenu, customItems);
		
		// Window items
		boolean anyWindowsToAdd = false;
		for (TVW viewWindow : viewWindows)
		{
			if (viewWindow.getDocument() != null)
			{
				anyWindowsToAdd = true;
				break;
			}
		}
		
		if (anyWindowsToAdd)
		{
			new MenuItem(windowMenu, SWT.SEPARATOR);
		}

		char hotKey = '1';
		for (TVW viewWindow : viewWindows)
		{
			if (viewWindow.getDocument() != null)
			{
				// An item for the window
				MenuItem windowItemMenuItem = new MenuItem(windowMenu, SWT.RADIO);
				HotKey windowHotKey = new HotKey(HotKey.MOD1, hotKey);
				windowItemMenuItem.setData(viewWindow);
				if (hotKey <= '9')
				{
					// Cmd+1, Cmd+2, ..., Cmd+9
					windowItemMenuItem.setAccelerator(windowHotKey.toAccelerator());
					windowItemMenuItem.setText(viewWindow.getDocument().getTitle() + "\t" + windowHotKey.toString());
					hotKey ++;
				}
				else
				{
					// No hotkey
					windowItemMenuItem.setText(viewWindow.getDocument().getTitle());
				}

				windowItemMenuItem.setSelection(viewWindow.isActive());
				windowItemMenuItem.addSelectionListener(new SelectionAdapter()
				{
					@Override
					public void widgetSelected(SelectionEvent arg0)
					{
						TVW targetWindow = (TVW)arg0.widget.getData();
						targetWindow.activate(false);
					}
				});
			}
		}
		
		// Add global listener to "Window" menu
		windowMenu.addMenuListener(new MenuListener()
		{
			@Override
			public void menuShown(MenuEvent arg0)
			{
				minimizeMenuItem.setEnabled(getActiveViewWindow() != null);
				if (addMaximize) maximizeMenuItem.setEnabled(getActiveViewWindow() != null);
				if (addFullscreen) fullscreenMenuItem.setEnabled(getActiveViewWindow() != null && getActiveViewWindow().supportsFullscreen());
			}
			
			@Override
			public void menuHidden(MenuEvent arg0)
			{
			}
		});

		
		if (windowMenu.getItemCount() > 0)
		{
			return windowMenuItem;
		}
		else
		{
			windowMenuItem.dispose();
			return null;
		}
	}
	
	/**
	 * Override this method to create custom menu items in 
	 * the middle of <i>Window</i> menu (between Minimize/Maximize and
	 * windows list)
	 * @return Create a list, add all the items to it and return it.
	 */
	protected List<MenuItem> createCustomWindowMenuItems()
	{
		return null;
		// To be overridden in the inherited classes
	}
	
	
	private MenuItem createAndAppendHelpMenu(Menu menu)
	{
		// "Help" menu item
		MenuItem helpMenuItem = new MenuItem(menu, SWT.CASCADE);
		helpMenuItem.setText("&Help");
		
		Menu helpMenu = new Menu(helpMenuItem);
		helpMenuItem.setMenu(helpMenu);

		boolean customItemsAppended = appendCustomHelpMenuItems(helpMenu);
		
		if (!SWT.getPlatform().equals("cocoa"))
		{
			if (customItemsAppended) 
			{
				// If the menu isn't empty yet, adding the new item
				new MenuItem(helpMenu, SWT.SEPARATOR);
			}

			// "About" menu item
			MenuItem aboutMenuItem = new MenuItem(helpMenu, SWT.NONE);
			aboutMenuItem.addSelectionListener(aboutSelectionAdapter);
			aboutMenuItem.setText("&About...");
		}
		
		if (helpMenu.getItemCount() > 0)
		{
			return helpMenuItem;
		}
		else
		{
			helpMenuItem.dispose();
			return null;
		}
	}
	
	/**
	 * Override this method to create custom menu items in 
	 * the top of <i>Help</i> menu (before About on Windows and Linux)
	 * @param helpMenu The <i>Help</i> menu &#151; you should add your new items here
	 * @return If you added any items, you should return true. Otherwise you should return false.
	 */
	protected boolean appendCustomHelpMenuItems(Menu helpMenu)
	{
		// To be overridden in the inherited classes
		return false;
	}
	
	
	public SelectionAdapter getExitSelectionAdapter()
	{
		return exitSelectionAdapter;
	}

	@Override
	public void setExitSelectionAdapter(SelectionAdapter exitSelectionAdapter)
	{
		this.exitSelectionAdapter = exitSelectionAdapter;
	}

	public SelectionAdapter getAboutSelectionAdapter()
	{
		return aboutSelectionAdapter;
	}

	@Override
	public void setAboutSelectionAdapter(SelectionAdapter aboutSelectionAdapter)
	{
		this.aboutSelectionAdapter = aboutSelectionAdapter;
	}

	protected void addMenusToGlobalMenu()
	{
		if (SWT.getPlatform().equals("cocoa"))
		{
			Display display = Display.getDefault();
			Menu menu = display.getMenuBar();
			addShellMenu(null, createAndAppendFileMenu(menu));
			MenuItem windowsMenuItem = createAndAppendWindowsMenu(menu, false, false);
			if (windowsMenuItem != null)
			{
				addShellMenu(null, windowsMenuItem);
			}
			addShellMenu(null, createAndAppendHelpMenu(menu));
		}
	}

	protected void addMenusToWindow(TVW viewWindow, boolean addFullscreen, boolean addMaximize)
	{
		Menu menu = viewWindow.getMenu();
		addShellMenu(viewWindow, createAndAppendFileMenu(menu));
		MenuItem windowsMenuItem = createAndAppendWindowsMenu(menu, addFullscreen, addMaximize);
		if (windowsMenuItem != null)
		{
			addShellMenu(viewWindow, windowsMenuItem);
		}
		addShellMenu(viewWindow, createAndAppendHelpMenu(menu));
	}

	@Override
	public void updateMenus()
	{
		eraseAllMenusForWindow(null);
		addMenusToGlobalMenu();
		for (TVW viewWindow : viewWindows)
		{
			eraseAllMenusForWindow(viewWindow);
			addMenusToWindow(viewWindow, viewWindow.supportsFullscreen(), viewWindow.supportsMaximizing());
		}
	}

	@Override
	public void addWindow(TVW viewWindow)
	{
		viewWindows.add(viewWindow);
		updateMenus();
	}

	@Override
	public void removeWindow(TVW viewWindow)
	{
		viewWindows.remove(viewWindow);
		eraseAllMenusForWindow(viewWindow);
		updateMenus();
	}

	public MenuConstructorBase()
	{
		viewWindows = new ArrayList<TVW>();
		shellMenuItems = new HashMap<TVW, Set<MenuItem>>();
		globalMenuItems = new HashSet<MenuItem>();
	}
}