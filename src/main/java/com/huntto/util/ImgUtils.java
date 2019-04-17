package com.huntto.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
public class ImgUtils {

	public static void main(String[] args) throws IOException {
//		String path = "D:\\BaiduNetdiskDownload\\身份证\\1.jpg"; // 输入图片 测试要在C盘放一张图片1.jpg
		// 居民身份证 照片规格:358像素（宽）×441像素（高），分辨率350dpi。
		ImgUtils.scale("D:\\BaiduNetdiskDownload\\身份证\\1.jpg", "D:\\BaiduNetdiskDownload\\身份证\\yasuo.jpg", 358, 441, true);// 等比例缩放 输出缩放图片

		File newfile = new File("D:\\BaiduNetdiskDownload\\身份证\\yasuo.jpg");
		BufferedImage bufferedimage = ImageIO.read(newfile);
		int width = bufferedimage.getWidth();
		int height = bufferedimage.getHeight();
		// 目标将图片裁剪成 宽240，高160
		if (width > 240) {
			/* 开始x坐标 开始y坐标 结束x坐标 结束y坐标 */
			bufferedimage = ImgUtils.cropImage(bufferedimage, (int) ((width - 240) / 2), 0,
					(int) (width - (width - 240) / 2), (int) (height));
			if (height > 160) {
				bufferedimage = ImgUtils.cropImage(bufferedimage, 0, (int) ((height - 160) / 2), 240,
						(int) (height - (height - 160) / 2));
			}
		} else {
			if (height > 160) {
				bufferedimage = ImgUtils.cropImage(bufferedimage, 0, (int) ((height - 160) / 2), (int) (width),
						(int) (height - (height - 160) / 2));
			}
		}
		ImageIO.write(bufferedimage, "JPEG", new File("D:\\BaiduNetdiskDownload\\身份证\\caijian.jpg")); // 输出裁剪图片
	}
	
	/**
	 *  读取本地图片获取输入流
	 * @param path 图片所在路径
	 * @return
	 * @throws IOException
	 */
    public static FileInputStream readImage(String path) throws IOException {
        return new FileInputStream(new File(path));
    }
    
    /**
     *   读取表中图片获取输出流 
     * @param in
     * @param targetPath
     */
    public static void readBin2Image(InputStream in, String targetPath) {
        File file = new File(targetPath);
        String path = targetPath.substring(0, targetPath.lastIndexOf("/"));
        if (!file.exists()) {
            new File(path).mkdir();
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            int len = 0;
            byte[] buf = new byte[1024];
            while ((len = in.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != fos) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

	/**
	 * 缩放图片方法
	 * 
	 * @param srcImageFile 要缩放的图片路径
	 * @param result       缩放后的图片路径
	 * @param height       目标高度像素
	 * @param width        目标宽度像素
	 * @param bb           是否补白
	 */
	public final static void scale(String srcImageFile, String result, int height, int width, boolean bb) {
		try {
			double ratio = 0.0; // 缩放比例
			File f = new File(srcImageFile);
			if (!f.exists()) {
				return;
			}
			BufferedImage bi = ImageIO.read(f);
			if(bi == null)return;
			Image itemp = bi.getScaledInstance(width, height, Image.SCALE_SMOOTH);// bi.SCALE_SMOOTH
																				// 选择图像平滑度比缩放速度具有更高优先级的图像缩放算法。
			// 计算比例
			if ((bi.getHeight() > height) || (bi.getWidth() > width)) {
				double ratioHeight = (new Integer(height)).doubleValue() / bi.getHeight();
				double ratioWhidth = (new Integer(width)).doubleValue() / bi.getWidth();
				if (ratioHeight > ratioWhidth) {
					ratio = ratioHeight;
				} else {
					ratio = ratioWhidth;
				}
				AffineTransformOp op = new AffineTransformOp(AffineTransform// 仿射转换
						.getScaleInstance(ratio, ratio), null);// 返回表示剪切变换的变换
				itemp = op.filter(bi, null);// 转换源 BufferedImage 并将结果存储在目标 BufferedImage 中。
			}
			if (bb) {// 补白
				BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);// 构造一个类型为预定义图像类型之一的
																									// BufferedImage。
				Graphics2D g = image.createGraphics();// 创建一个 Graphics2D，可以将它绘制到此 BufferedImage 中。
				g.setColor(Color.white);// 控制颜色
				g.fillRect(0, 0, width, height);// 使用 Graphics2D 上下文的设置，填充 Shape 的内部区域。
				if (width == itemp.getWidth(null))
					g.drawImage(itemp, 0, (height - itemp.getHeight(null)) / 2, itemp.getWidth(null),
							itemp.getHeight(null), Color.white, null);
				else
					g.drawImage(itemp, (width - itemp.getWidth(null)) / 2, 0, itemp.getWidth(null),
							itemp.getHeight(null), Color.white, null);
				g.dispose();
				itemp = image;
			}
			ImageIO.write((BufferedImage) itemp, "JPEG", new File(result)); // 输出压缩图片
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 裁剪图片方法
	 * 
	 * @param bufferedImage 图像源
	 * @param startX        裁剪开始x坐标
	 * @param startY        裁剪开始y坐标
	 * @param endX          裁剪结束x坐标
	 * @param endY          裁剪结束y坐标
	 * @return
	 */
	public static BufferedImage cropImage(BufferedImage bufferedImage, int startX, int startY, int endX, int endY) {
		int width = bufferedImage.getWidth();
		int height = bufferedImage.getHeight();
		if (startX == -1) {
			startX = 0;
		}
		if (startY == -1) {
			startY = 0;
		}
		if (endX == -1) {
			endX = width - 1;
		}
		if (endY == -1) {
			endY = height - 1;
		}
		BufferedImage result = new BufferedImage(endX - startX, endY - startY, 4);
		for (int x = startX; x < endX; ++x) {
			for (int y = startY; y < endY; ++y) {
				int rgb = bufferedImage.getRGB(x, y);
				result.setRGB(x - startX, y - startY, rgb);
			}
		}
		return result;
	}
}
