package de.wwu.muggl.ui.gui.support;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import de.wwu.muggl.configuration.Globals;
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
		try {
			File mugglFile = new File(Globals.BASE_DIRECTORY + "/" + Globals.JAR_FILE_NAME);
			if (!mugglFile.exists()) {
				guiException = new GUIException("Unable to locate " + Globals.JAR_FILE_NAME + ".\nPlease check the path (" + Globals.BASE_DIRECTORY + ").");
			}
			JarFile mugglJar = new JarFile(mugglFile);
	
			this.folderImage = readImageFromJar(mugglJar, "images/Fairytale_folder.png");
			this.moonImage = readImageFromJar(mugglJar, "images/Nuvola_apps_kmoon.png");
			this.checkImage = readImageFromJar(mugglJar, "images/400px-P_yes_green.svg.png");
			this.logfileImage = readImageFromJar(mugglJar, "images/Gartoon-Gedit-icon.png");
			this.helpImage = readImageFromJar(mugglJar, "images/Nuvola_apps_filetypes.png");
			this.infoImage = readImageFromJar(mugglJar, "images/Info_icon.png");
			this.refreshImage = readImageFromJar(mugglJar, "images/Arrow_refresh.png");
			this.editImage = readImageFromJar(mugglJar, "images/edit16.png");
			this.pushImage = readImageFromJar(mugglJar, "images/push16.png");
			this.popImage = readImageFromJar(mugglJar, "images/pop16.png");
			this.emptyImage = readImageFromJar(mugglJar, "images/empty16.png");
		} catch (IOException e) {
			guiException = new GUIException("Reading from " + Globals.JAR_FILE_NAME + " failed. It might be corrupt or there was an I/O error.\nRoot cause: " + e.getMessage());
		} catch (IllegalStateException  e) {
			guiException = new GUIException("Reading from " + Globals.JAR_FILE_NAME + " failed. It might be corrupt or there was an I/O error.\nRoot cause: " + e.getMessage());
		} catch (GUIException e) {
			guiException = e;
		}
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
	
	/**
	 * Read an SWT image from a jar file.
	 *
	 * @param mugglJar The jar file to read the image from.
	 * @param path The path to the image.
	 * @return The SWT image.
	 * @throws GUIException If the image does not exist.
	 * @throws IOException On I/O errors.
	 */
	private Image readImageFromJar(JarFile jarFile, String path) throws GUIException, IOException {
		ZipEntry entry = jarFile.getEntry(path);
		if (entry == null) {
			throw new GUIException("Failed to load an image. " + Globals.JAR_FILE_NAME
					+ " might be corrupt or you are using a wrong version.");
		}
		return new Image(Display.getDefault(), jarFile.getInputStream(entry));
	}
	
}
