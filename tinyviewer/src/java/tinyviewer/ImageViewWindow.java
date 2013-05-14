package tinyviewer;

import java.util.HashSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

import crossbase.ui.ViewWindowBase;
import crossbase.ui.ViewWindowsManager;

public class ImageViewWindow extends ViewWindowBase<ImageDocument>
{
	private ScrolledComposite scrolledComposite;
	private DropTarget scrolledCompositeDropTarget, imageViewDropTarget;
	private ImageView imageView;
	private ImageDocument imageDocument;
	private HashSet<DropTargetAdapter> dropTargetAdapters = new HashSet<DropTargetAdapter>();

	public void addDropTargetListener(DropTargetAdapter dropTargetAdapter)
	{
		dropTargetAdapters.add(dropTargetAdapter);
		
		if (imageViewDropTarget != null && !imageViewDropTarget.isDisposed())
		{
			scrolledCompositeDropTarget.addDropListener(dropTargetAdapter);
			imageViewDropTarget.addDropListener(dropTargetAdapter);
		}
	}
	
	public void removeDropTargetListener(DropTargetAdapter dropTargetAdapter)
	{
		dropTargetAdapters.remove(dropTargetAdapter);
		if (imageViewDropTarget != null && !imageViewDropTarget.isDisposed())
		{
			scrolledCompositeDropTarget.removeDropListener(dropTargetAdapter);
			imageViewDropTarget.removeDropListener(dropTargetAdapter);
		}
	}
	
	public ImageViewWindow(String applicationTitle, 
	                       ViewWindowsManager<ImageDocument, ImageViewWindow> windowsManager, 
	                       TinyViewerMenuConstructor menuConstructor)
	{
		super(applicationTitle, windowsManager, menuConstructor);
	}
	
	/**
	 * Create contents of the window.
	 * 
	 * @wbp.parser.entryPoint
	 */
	@Override
	protected Shell constructShell()
	{
		Shell shell = new Shell(SWT.TITLE | SWT.CLOSE | SWT.MIN | SWT.MAX | SWT.RESIZE | SWT.BORDER);

		shell.setBackground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
		shell.setMinimumSize(new Point(150, 200));
		shell.setImage(SWTResourceManager.getImage(ImageViewWindow.class,
				"/crossbase/icon.png"));
	
		shell.setLayout(new FillLayout(SWT.HORIZONTAL));

		scrolledComposite = new ScrolledComposite(shell, SWT.H_SCROLL | SWT.V_SCROLL | SWT.DOUBLE_BUFFERED);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		
		imageView = new ImageView(scrolledComposite, SWT.NONE | SWT.DOUBLE_BUFFERED);
		imageView.setBounds(0, 0, 200, 127);
		imageView.setVisible(false);
		imageView.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_BLACK));

		// Drop targets
		scrolledCompositeDropTarget = new DropTarget(scrolledComposite, DND.DROP_MOVE);
		scrolledCompositeDropTarget.setTransfer(new Transfer[] { FileTransfer.getInstance() });
		imageViewDropTarget = new DropTarget(imageView, DND.DROP_MOVE);
		imageViewDropTarget.setTransfer(new Transfer[] { FileTransfer.getInstance() });
		
		for (DropTargetAdapter adapter : dropTargetAdapters)
		{
			scrolledCompositeDropTarget.addDropListener(adapter);
			imageViewDropTarget.addDropListener(adapter);
		}
		
		scrolledComposite.setContent(imageView);
		scrolledComposite.setMinSize(imageView.desiredSize());
		scrolledComposite.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		
		scrolledComposite.addControlListener(new ControlListener()
		{
			@Override
			public void controlResized(ControlEvent arg0)
			{
				updateImageViewSize();
				
			}
			
			@Override
			public void controlMoved(ControlEvent arg0) { }
		});
			
		return shell;
	}

	private void updateImageViewSize()
	{
		Point desired = imageView.desiredSize();
		Point clientAreaSize = new Point(scrolledComposite.getClientArea().width, scrolledComposite.getClientArea().height); 
		
		int width = Math.max(clientAreaSize.x, desired.x); 
		int height = Math.max(clientAreaSize.y, desired.y);
		Point newSize = new Point(width, height);
		
		Point oldSize = imageView.getSize();
		
		if (!oldSize.equals(newSize))
		{
			imageView.setSize(newSize);
		}
	}
	
	@Override
	public void loadDocument(ImageDocument document)
	{
		this.imageDocument = (ImageDocument)document;
		
		imageView.setImage(imageDocument.getImage());
		shell.setText(imageDocument.getTitle() + " \u2013 " + getApplicationTitle());
		scrolledComposite.setMinSize(imageView.desiredSize());
		updateImageViewSize();
		imageView.setVisible(true);
		shell.forceActive();
		getMenuConstructor().updateMenus();
	}
	
	@Override
	public boolean documentIsLoaded()
	{
		return imageView.getImage() != null;
	}
	
	public boolean isDisposed()
	{
		return shell.isDisposed();
	}

	@Override
	public ImageDocument getDocument()
	{
		return imageDocument;
	}

	@Override
	public boolean supportsFullscreen()
	{
		return true;
	}

	@Override
	public boolean supportsMaximizing()
	{
		return true;
	}
}
