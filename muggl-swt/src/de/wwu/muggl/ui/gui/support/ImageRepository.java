package de.wwu.muggl.ui.gui.support;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import de.wwu.muggl.ui.gui.GUIException;

/**
 * Repository for images used by the SWT GUI.
 *
 * @author Tim Majchrzak
 * @version 1.0.0, 2010-08-18
 */
public final class ImageRepository {
	/**
	 * Icon of a folder.
	 */
	public Image folderImage;
	/**
	 * Icon of the moon.
	 */
	public Image moonImage;
	/**
	 * Icon of a check.
	 */
	public Image checkImage;
	/**
	 * Icon of a sheet for notes.
	 */
	public Image logfileImage;
	/**
	 * Icon of a question mark.
	 */
	public Image helpImage;
	/**
	 * Icon of an "i" sign.
	 */
	public Image infoImage;
	/**
	 * Icon of an image of a circle made up of two arrows.
	 */
	public Image refreshImage;
	/**
	 * Icon of a red and a green arrow.
	 */
	public Image editImage;
	/**
	 * Icon for "push".
	 */
	public Image pushImage;
	/**
	 * Icon for "pop".
	 */
	public Image popImage;
	/**
	 * Icon for "empty".
	 */
	public Image emptyImage;
	
	// Singleton.
	private static GUIException guiException = null;
	private static final ImageRepository REPOS = new ImageRepository();
	
	/**
	 * Private Constructor.
	 */
	private ImageRepository() {
		this.folderImage = new Image(Display.getDefault(),
		        "images/images/Fairytale_folder.png");
		this.moonImage = new Image(Display.getDefault(),
		        "images/images/Nuvola_apps_kmoon.png");
		this.checkImage = new Image(Display.getDefault(),
		        "images/images/400px-P_yes_green.svg.png");
		this.logfileImage = new Image(Display.getDefault(),
		        "images/images/Gartoon-Gedit-icon.png");
		this.helpImage = new Image(Display.getDefault(),
		        "images/images/Nuvola_apps_filetypes.png");
		this.infoImage = new Image(Display.getDefault(),
		        "images/images/Info_icon.png");
		this.refreshImage = new Image(Display.getDefault(),
		        "images/images/Arrow_refresh.png");
		this.editImage = new Image(Display.getDefault(),
		        "images/images/edit16.png");
		this.pushImage = new Image(Display.getDefault(),
		        "images/images/push16.png");
		this.popImage = new Image(Display.getDefault(),
		        "images/images/pop16.png");
		this.emptyImage = new Image(Display.getDefault(),
		        "images/images/empty16.png");
	}
	
	/**
	 * Getting the only instance of this class.
	 * 
	 * @return The only instance of this class.
	 * @throws GUIException On fatal errors loading images.
	 */
	public static ImageRepository getInst() throws GUIException {
		if (guiException != null) {
			throw guiException;
		}
		return REPOS;
	}
	
}
