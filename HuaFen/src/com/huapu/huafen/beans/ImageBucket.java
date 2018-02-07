package com.huapu.huafen.beans;

import java.io.Serializable;
import java.util.List;
/**
 * 本地图片文件夹
 * @author liang_xs
 *
 */
public class ImageBucket implements Serializable {
	public int count = 0;
	public String bucketName;
	public List<ImageItem> imageList;

}
