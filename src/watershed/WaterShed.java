package watershed;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;

public class WaterShed {
	static class POINT {
		int x;
		int y;
	}

	/********
	 * 标记-分水岭算法对输入图像进行分割 输入参数： OriginalImage - 输入图像（灰度图，0~255） SeedImage -
	 * 标记图像（二值图像，0非标记，1标记） LabelImage - 输出图像（1第一个分割区域；2第二个分割区域；3...） row,col -
	 * 图像行列数目 返回值： 分割区域数目
	 */
	public static int Watershed(int OriginalImage[][], int SeedImage[][],
			int LabelImage[][]) {

		int row = OriginalImage.length;
		int col = OriginalImage[0].length;

		int Num = 0; // 标志区域号，从1开始
		int i, j;

		ArrayList<ArrayList<ArrayList<POINT>>> qu = new ArrayList<ArrayList<ArrayList<POINT>>>(); // 保存所有标记区域种子队列的数组

		POINT temp = new POINT();

		for (i = 0; i < row; i++) {
			for (j = 0; j < col; j++) {
				LabelImage[i][j] = 0;
			}
		}

		int m, n, k = 0;
		int up, down, right, left, upleft, upright, downleft, downright;
		// 预处理，提取区分每个标记区域，并初始化每个标记的种子队列
		// 种子是指标记区域边缘的点，它们在水位上升时向外生长。
		for (i = 0; i < row; i++) {
			for (j = 0; j < col; j++) {
				if (SeedImage[i][j] == 1 || SeedImage[i][j] == 255) // 找到一个标记区域
				{
					Num++; // 标志号加1
					ArrayList<ArrayList<POINT>> uu = new ArrayList<ArrayList<POINT>>(
							256);

					ArrayList<POINT> que = new ArrayList<POINT>(); // 临时种子队列
					for (int l = 0; l < 256; l++) {
						uu.add(new ArrayList<POINT>());
					}
					qu.add(uu);
					temp.x = i;
					temp.y = j;
					que.add(temp);
					LabelImage[i][j] = Num;
					SeedImage[i][j] = 127;

					while (!que.isEmpty()) {
						up = down = right = left = 0;
						upleft = upright = downleft = downright = 0;
						temp = que.remove(0);
						m = temp.x;
						n = temp.y;

						if (m > 0) {
							if (SeedImage[m - 1][n] == 1) {
								POINT point = new POINT();
								point.x = m - 1;
								point.y = n;
								que.add(point);
								LabelImage[m - 1][n] = Num;
								SeedImage[m - 1][n] = 127;
							} else {
								up = 1;
							}
						}
						if (m > 0 && n > 0) {
							if (SeedImage[m - 1][n - 1] == 1) {
								POINT point = new POINT();
								point.x = m - 1;
								point.y = n - 1;
								que.add(point);
								LabelImage[m - 1][n - 1] = Num;
								SeedImage[m - 1][n - 1] = 127;
							} else {
								upleft = 1;
							}
						}

						if (m < row - 1) {
							if (SeedImage[m + 1][n] == 1) {
								POINT point = new POINT();
								point.x = m + 1;
								point.y = n;
								que.add(point);
								LabelImage[m + 1][n] = Num;
								SeedImage[m + 1][n] = 127;
							} else {
								down = 1;
							}
						}
						if (m < (row - 1) && n < (col - 1)) {
							if (SeedImage[m + 1][n + 1] == 1) {
								POINT point = new POINT();
								point.x = m + 1;
								point.y = n + 1;
								que.add(point);
								LabelImage[m + 1][n + 1] = Num;
								SeedImage[m + 1][n + 1] = 127;
							} else {
								downright = 1;
							}
						}

						if (n < col - 1) {
							if (SeedImage[m][n + 1] == 1) {
								POINT point = new POINT();
								point.x = m;
								point.y = n + 1;
								que.add(point);
								LabelImage[m][n + 1] = Num;
								SeedImage[m][n + 1] = 127;
							} else {
								right = 1;
							}
						}
						if (m > 0 && n < (col - 1)) {
							if (SeedImage[m - 1][n + 1] == 1) {
								POINT point = new POINT();
								point.x = m - 1;
								point.y = n + 1;
								que.add(point);
								LabelImage[m - 1][n + 1] = Num;
								SeedImage[m - 1][n + 1] = 127;
							} else {
								upright = 1;
							}
						}

						if (n > 0) {
							if (SeedImage[m][n - 1] == 1) {
								POINT point = new POINT();
								point.x = m;
								point.y = n - 1;
								que.add(point);
								LabelImage[m][n - 1] = Num;
								SeedImage[m][n - 1] = 127;
							} else {
								left = 1;
							}
						}
						if (m < (row - 1) && n > 0) {
							if (SeedImage[m + 1][n - 1] == 1) {
								POINT point = new POINT();
								point.x = m + 1;
								point.y = n - 1;
								que.add(point);
								LabelImage[m + 1][n - 1] = Num;
								SeedImage[m + 1][n - 1] = 127;
							} else {
								downleft = 1;
							}
						}

						// 上下左右只要有一点不可生长，则本点为初始种子队列的一员
						if (up != 0 || down != 0 || right != 0 || left != 0
								|| upleft != 0 || downleft != 0 || upright != 0
								|| downright != 0) {
							POINT point = new POINT();
							point.x = m;
							point.y = n;
							qu.get(Num - 1).get(OriginalImage[m][n]).add(point);
						}
					}
				}
			}
		}
		boolean actives;// 某一水位，所有标记种子生长完的标志
		int WaterLevel = 1;
		for (WaterLevel = 1; WaterLevel < 255; WaterLevel++) {
			actives = true;
			while (actives) {
				actives = false;
				for (i = 0; i < Num; i++) {
					if (!qu.get(i).get(WaterLevel).isEmpty()) {
						actives = true;
						while (qu.get(i).get(WaterLevel).size() > 0) {
							temp = qu.get(i).get(WaterLevel).remove(0);
							m = temp.x;
							n = temp.y;
							if (m > 0) {
								if (LabelImage[m - 1][n] == 0) {
									POINT point = new POINT();
									point.x = m - 1;
									point.y = n;
									LabelImage[m - 1][n] = i + 1;

									// 上方点标记为已淹没区域。这个标记与扫描点区域号相同，一定在标号所在区域。
									if (OriginalImage[m - 1][n] <= WaterLevel) {
										qu.get(i).get(WaterLevel).add(point);
									} else {
										qu.get(i).get(OriginalImage[m - 1][n])
												.add(point);
									}
								}
							}

							if (m < row - 1) {
								if (LabelImage[m + 1][n] == 0) {
									POINT point = new POINT();
									point.x = m + 1;
									point.y = n;
									LabelImage[m + 1][n] = i + 1;

									if (OriginalImage[m + 1][n] <= WaterLevel) {
										qu.get(i).get(WaterLevel).add(point);
									} else {
										qu.get(i).get(OriginalImage[m + 1][n])
												.add(point);
									}
								}
							}

							if (n < col - 1) {
								if (LabelImage[m][n + 1] == 0) {
									POINT point = new POINT();
									point.x = m;
									point.y = n + 1;
									LabelImage[m][n + 1] = i + 1;

									if (OriginalImage[m][n + 1] <= WaterLevel) {
										qu.get(i).get(WaterLevel).add(point);
									} else {
										qu.get(i).get(OriginalImage[m][n + 1])
												.add(point);
									}
								}
							}

							if (n > 0) {
								if (LabelImage[m][n - 1] == 0) {
									POINT point = new POINT();
									point.x = m;
									point.y = n - 1;
									LabelImage[m][n - 1] = i + 1;

									if (OriginalImage[m][n - 1] <= WaterLevel) {
										qu.get(i).get(WaterLevel).add(point);
									} else {
										qu.get(i).get(OriginalImage[m][n - 1])
												.add(point);
									}
								}
							}
						}
					}
				}
			}
		}
		return Num;
	}

	public static void main(String[] args) throws IOException {
		BufferedImage bimg = ImageIO.read(new File("test.bmp"));
		int[][] OriginalImage = new int[bimg.getWidth()][bimg.getHeight()];
		int[][] SeedImage = new int[bimg.getWidth()][bimg.getHeight()];
		int[][] LabelImage = new int[bimg.getWidth()][bimg.getHeight()];
		for (int i = 0; i < LabelImage.length; i++) {
			for (int j = 0; j < LabelImage[0].length; j++) {
				int tempRGB = bimg.getRGB(i, j);
				int r = (tempRGB >> 16) & 0xff;
				int g = (tempRGB >> 8) & 0xff;
				int b = (tempRGB & 0xff);
				OriginalImage[i][j] = (r + g + b) / 3;

				if ((r == 0) && (g == 0) && (b == 255)) {
					SeedImage[i][j] = 1;
					bimg.setRGB(i, j, 0xFFFFFF);
				} else {

					bimg.setRGB(i, j, 0);
				}
			}
		}
		int num = WaterShed.Watershed(OriginalImage, SeedImage, LabelImage);
		Random random = new  Random();
		int color[] = new int[num];
		for (int i = 0; i < LabelImage.length; i++) {
			for (int j = 0; j < LabelImage[0].length; j++) {
				bimg.setRGB(i, j, 255 << (LabelImage[i][j] * 4));
			}
		}
		System.out.println(num);
		ImageIO.write(bimg, "bmp", new File("temp.bmp"));
	}
}
