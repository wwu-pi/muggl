package test.papers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

/**
 *
 */
public class SWTBlur {
	private static final int BLURR_RUNS = 1;
	
	private Shell shell = null;
	protected Display display = null;
	private boolean isClosing = false;
	protected Canvas canvas;
	
	/**
	 * Build and show the Windows, dispose it after is is not longer needed.
	 *
	 * @throws IllegalStateException If the parent shell is unusable (null or disposed).
	 */
	public void show() {
		try {
			this.display = Display.getDefault();
			createShell();
			
			// Open only if it should be shown.
			if (!this.shell.isDisposed()) {
				this.shell.open();
			}

			while (!this.shell.isDisposed()) {
				if (!this.display.readAndDispatch())
					this.display.sleep();
				}		
		} finally {
			doExit();
		}
	}

	/**
	 * Create the Shell, setting up any elements that are not set up by the main Composite.
	 *
	 * @throws IllegalStateException If the parent shell is unusable (null or disposed).
	 */
	private void createShell() {
		this.shell = new Shell(this.display, SWT.BORDER | SWT.CLOSE | SWT.TITLE | SWT.MIN | SWT.RESIZE);
		this.shell.setText("Bla");
		this.shell.setLayout(new FillLayout(SWT.VERTICAL));

		File file = openFileDirectly();
		if (file == null || !file.exists()) {
			showMessageBox(this.shell, "Warnung", "Ohne Datei geht es nicht!", SWT.OK | SWT.ICON_WARNING);
			doExit();
			return;
		}
		
		Image image = null;
		try {
			image = new Image(this.display, new FileInputStream(file));
		} catch (FileNotFoundException e) {
			showMessageBox(this.shell, "Warnung", "Ohne Datei geht es nicht!", SWT.OK | SWT.ICON_WARNING);
			doExit();
			return;
		}
		
		this.canvas = new Canvas(this.shell, SWT.NONE);
		this.canvas.setBackgroundImage(image);
		this.canvas.setBounds(image.getBounds());
		
		this.canvas.addMouseListener(new MouseListener() {
			public void mouseDoubleClick(MouseEvent event) {
				blurTheImage();
			}

			public void mouseDown(MouseEvent event) {}

			public void mouseUp(MouseEvent event) {}
	    		
	    });
		
		this.shell.setBounds(this.canvas.getBounds());
	}

	/**
	 * Getter for the current Shell.
	 * @return The Shell.
	 */
	public Shell getShell() {
		return this.shell;
	}

	/**
	 * Close the current window.
	 * @return true, if the closing was successful, false otherwise.
	 */
	public synchronized boolean doExit() {
		if (!this.isClosing && !this.shell.isDisposed()) {
			this.isClosing = true;
			this.shell.close();
			this.shell.dispose();
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 *
	 * @return argl
	 */
	public File openFileDirectly() {
		FileDialog fileDialog = new FileDialog(this.shell, SWT.OPEN);
		String[] extensions = {"*.bmp", "*.jpg", "*.png"};
		String[] names = {"Bitmap (*.bmp)", "JPG (*.png)", "PNG (*.png)"};
		fileDialog.setFilterExtensions(extensions);
		fileDialog.setFilterNames(names);
		String path = fileDialog.open();
		if (path != null) {
			// First of all replace double backslashes against slashes.
			path = path.replace("\\\\", "\\");
			return new File(path);
		}
		return null;
	}
	
	/**
	 * 
	 */
	public void blurTheImage() {
		Image image = SWTBlur.this.canvas.getBackgroundImage();
		
		Rectangle rectangle = image.getBounds();
		int width = rectangle.width;
		int height = rectangle.height;
		int[] r = new int[width * height];
		int[] g = new int[width * height];
		int[] b = new int[width * height];
		ImageData imageData = image.getImageData();
		
		int redMask = imageData.palette.redMask;
		int greenMask = imageData.palette.greenMask;
		int blueMask = imageData.palette.blueMask;
		int redShift = imageData.palette.redShift;
		int greenShift = imageData.palette.greenShift;
		int blueShift = imageData.palette.blueShift;
		
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
    			int pixel = imageData.getPixel(x, y);
    			
    			int redValue = pixel & redMask;
    			redValue = (redShift < 0) ? redValue >>> -redShift : redValue << redShift;
    			r[x + y * width] = redValue;
    			
    			int greenValue = pixel & greenMask;
    			greenValue = (greenShift < 0) ? greenValue >>> -greenShift : greenValue << greenShift;
    			g[x + y * width] = greenValue;
    			
    			int blueValue = pixel & blueMask;
    			blueValue = (blueShift < 0) ? blueValue >>> -blueShift : blueValue << blueShift;
    			b[x + y * width] = blueValue;
			}
		}
		
		for (int a = 0; a < BLURR_RUNS; a++) {
    		r = blur(r, width);
    		g = blur(g, width);
    		b = blur(b, width);
		}
		
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
    			int pixel = 0;
    			
    			int redValue = r[x + y * width];
    			redValue = (redShift < 0) ? redValue << -redShift : redValue >>> redShift;
				pixel |= redValue;
				
				int greenValue = g[x + y * width];
				greenValue = (greenShift < 0) ? greenValue << -greenShift : greenValue >>> greenShift;
    			pixel |= greenValue;
    			
    			int blueValue = b[x + y * width];
    			blueValue = (blueShift < 0) ? blueValue << -blueShift : blueValue >>> blueShift;
    			pixel |= blueValue;
    			imageData.setPixel(x, y, pixel);
			}
		}
		image = new Image(SWTBlur.this.display, imageData);
		SWTBlur.this.canvas.setBackgroundImage(image);
		SWTBlur.this.canvas.redraw();
	}
	
	/**
	 * Show a message box in the Shell shell with the supplied parameters.
	 *
	 * This Method is overloaded and offers two more simply alternatives.
	 * @param shell The Shell to display the message box in.
	 * @param text The window title of the message box.
	 * @param message The message that box is to be filled with.
	 * @param type The type of the message box.
	 * @return The response from the message box as an int.
	 */
	public static int showMessageBox(Shell shell, String text, String message, int type) {
		MessageBox messageBox = new MessageBox(shell, type);
		messageBox.setMessage(message);
		messageBox.setText(text);
		return messageBox.open();
	}
	
	/**
	 * 
	 *
	 * @param strings
	 */
	public static void main(String... strings) {
		SWTBlur gui = new SWTBlur();
		gui.show();
	}
	
	/**
	 * 
	 *
	 * @param image
	 * @param n
	 * @return argl
	 */
	public static int[] blur(int[] image, int n) {
		int length = image.length;
		
		int[] image2 = new int[length];
		for (int a = 0; a < length; a++) {
			int neighbours = 0;
			long pixel = 0l;
			// Left
			if (a % n != 0) {
				neighbours++;
				pixel += image[a - 1];
			}

			// Upper
			if (a >= n) {
				neighbours++;
				pixel += image[a - n];
			}

			// Right
			if (a % n != n - 1) {
				neighbours++;
				pixel += image[a + 1];
			}

			// Lower
			if (a < length - n) {
				neighbours++;
				pixel += image[a + n];
			}

			// Calc
			pixel += (8 - neighbours) * image[a];
			pixel = ((pixel * 10 / 8l) + 5) / 10;
			image2[a] = (int) pixel ;
		}

		return image2;
	}
}
