package kr.co.thkc.utils;

import com.mortennobel.imagescaling.AdvancedResizeOp;
import com.mortennobel.imagescaling.ResampleOp;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

public class ImageUtil {
	public static void sampledImage(MultipartFile originFile, File destFile) throws IOException {
		BufferedImage src = ImageIO.read(originFile.getInputStream());
		src = resize(src, 300, 300);
		src = convertRGBAToIndexed(src);
	    ImageIO.write(src, "png", destFile);
	}
	
	private static BufferedImage convertRGBAToIndexed(BufferedImage src) {
	    BufferedImage dest = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_BYTE_INDEXED);
	    Graphics g = dest.getGraphics();
	    g.setColor(new Color(231, 20, 189));

	    // fill with a hideous color and make it transparent
	    g.fillRect(0, 0, dest.getWidth(), dest.getHeight());
	    dest = makeTransparent(dest, 0, 0);

	    dest.createGraphics().drawImage(src, 0, 0, null);
	    return dest;
	}
	
    private static BufferedImage resize(BufferedImage img, int height, int width) {
    	ResampleOp rescale = new ResampleOp(300, 300);
        rescale.setUnsharpenMask(AdvancedResizeOp.UnsharpenMask.Soft);
        return rescale.filter(img, null);
    }

	private static BufferedImage makeTransparent(BufferedImage image, int x, int y) {
	    ColorModel cm = image.getColorModel();
	    if (!(cm instanceof IndexColorModel))
	        return image; // sorry...
	    IndexColorModel icm = (IndexColorModel) cm;
	    WritableRaster raster = image.getRaster();
	    int pixel = raster.getSample(x, y, 0); // pixel is offset in ICM's palette
	    int size = icm.getMapSize();
	    byte[] reds = new byte[size];
	    byte[] greens = new byte[size];
	    byte[] blues = new byte[size];
	    icm.getReds(reds);
	    icm.getGreens(greens);
	    icm.getBlues(blues);
	    IndexColorModel icm2 = new IndexColorModel(8, size, reds, greens, blues, pixel);
	    return new BufferedImage(icm2, raster, image.isAlphaPremultiplied(), null);
	}

}
