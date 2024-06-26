package com.shine.bookshop.dao;

import java.util.List;

import com.shine.bookshop.bean.UpLoadImg;

/**
 * 图书图片上传dao层
 * @author thuih
 *
 */
public interface UpLoadImgDao {
	
	//图片增加
	boolean imgAdd(UpLoadImg upLoadImg);
	//根据图书图片名称获取id（名称用的uuid唯一识别码）
	Integer findIdByImgName(String imgName);
	//更新图片信息
	boolean imgUpdate(UpLoadImg upImg);
	//通过id删除图片
	boolean imgDelById(int imgId);
	//通过imgIds查询图片信息
	List<UpLoadImg> findImgByIds(String imgIds);
	//批量删除
	boolean imgBatDelById(String imgIds);
}
