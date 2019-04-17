package com.huntto;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import javax.sql.DataSource;

import com.huntto.util.FileUtil;
import com.huntto.util.ImgUtils;
import com.huntto.util.ValidateUtil;
import com.mchange.v2.c3p0.ComboPooledDataSource;

public class TbcxApp {
	public static Logger log = Logger.getLogger(TbcxApp.class.toString());
	private static DataSource dataSource = null;
	static {
		dataSource = new ComboPooledDataSource("helloc3p0");
	}
	
	private static String writeCYRYQUERYSql = "UPDATE CYRYQUERY T SET T.PHOTO =? WHERE T.ID =?";
	private static String writeCYRYJBXXSql = "UPDATE OLEXAM_CYRY_JBXX T SET T.PHOTO =? WHERE T.ID =?";
	
	private static String readCYRYJBXXSql = "select t.ID,t.PHOTO,t.IDCARD from OLEXAM_CYRY_JBXX t where 1=1 and t.PHOTO is not null and to_char(create_time,'yyyy-mm-dd') >= '2019-03-09'";
	private static String readCYRYQUERYSql = "select t.ID,t.PHOTO,t.IDCARD from CYRYQUERY t where 1=1 and t.PHOTO is not null and fzsj >= '2019-03-09'";
	
//	private static String readCYRYJBXXSql = "select t.ID,t.PHOTO,t.IDCARD from OLEXAM_CYRY_JBXX t where 1=1 and t.PHOTO is not null and to_char(create_time,'yyyy-mm-dd') >= '2018-04-04'";
//	private static String readCYRYQUERYSql = "select t.ID,t.PHOTO,t.IDCARD from CYRYQUERY t where 1=1 and t.PHOTO is not null and fzsj >= '2018-04-04'";

	public static void main(String[] args) {
		for(String str:args) {
			log.info("所传时间参数: "+str);
			System.out.println(str);
		}
		if(args.length > 0 ) {
			System.out.println(args[0]);
			log.info("所传第一个时间参数: "+args[0]);
			String ddsDate = args[0];
			if(!ValidateUtil.isDate8(ddsDate)) {
				readCYRYJBXXSql = "select t.ID,t.PHOTO,t.IDCARD from OLEXAM_CYRY_JBXX t where 1=1 and t.PHOTO is not null and to_char(create_time,'yyyy-mm-dd') >= '"+args[0]+"'";
				readCYRYQUERYSql = "select t.ID,t.PHOTO,t.IDCARD from CYRYQUERY t where 1=1 and t.PHOTO is not null and fzsj >= '"+args[0]+"'";
			}else {
				log.info("同步日期必须为yyyy-MM-dd格式");
			}
			
		}
		
		
		System.out.println("开始裁剪 CYRYQUERY 表图片");
		int count = readCYRYQUERYBlobs();
		System.out.println("裁剪 CYRYQUERY 表  "+count+"  张图片");
		
		System.out.println("开始裁剪 OLEXAM_CYRY_JBXX 表图片");
		int count1 = readCYRYJBXXBlobs();
		System.out.println("裁剪 OLEXAM_CYRY_JBXX 表  "+count1+"  张图片");
		System.out.println("同步完成，开始删除文件夹及文件。。。。");
		
	}
	/**
	 *  
	 * @Description  生成 路径
	 * @param str 拼接参数
	 * @return  String 返回拼接好的路径
	 */
//	public static String getPath(String str) {
//		StringBuilder sb = new StringBuilder();
//		sb.append("D:\\IMG\\idcard\\" + str + ".jpg");
//		return sb.toString();
//	}
//	public static String getCYRYQUERYPath(String str) {
//		StringBuilder sb = new StringBuilder();
//		sb.append("D:\\IMG\\CYRYQUERY\\" + str + ".jpg");
//		return sb.toString();
//	}
//	public static String getCYRYJBXXPath(String str) {
//		StringBuilder sb = new StringBuilder();
//		sb.append("D:\\IMG\\CYRYJBXX\\" + str + ".jpg");
//		return sb.toString();
//	}
	
	public static String getPath(String str) {
		StringBuilder sb = new StringBuilder();
		sb.append("C:\\IMG\\idcard\\" + str + ".jpg");
		return sb.toString();
	}
	public static String getCYRYQUERYPath(String str) {
		StringBuilder sb = new StringBuilder();
		sb.append("C:\\IMG\\CYRYQUERY\\" + str + ".jpg");
		return sb.toString();
	}
	public static String getCYRYJBXXPath(String str) {
		StringBuilder sb = new StringBuilder();
		sb.append("C:\\IMG\\CYRYJBXX\\" + str + ".jpg");
		return sb.toString();
	}
	/**
	 * 
	 * @Description  更新 CYRYQUERY 表的照片
	 * @param ID ID
	 * @param PHOTO PHOTO
	 */
	public static void writeCYRYQUERYBlobs(String ID,Blob PHOTO) {
		Connection connection = null;
        PreparedStatement preparedStatement = null;
	        
        try {
            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement(writeCYRYQUERYSql);
            preparedStatement.setBlob(1, PHOTO);
            preparedStatement.setString(2, ID);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
        	log.info("更新 CYRYQUERY 表的照片的时候出现异常");
            e.printStackTrace();
        } finally{
        }
	}
	public static void writeCYRYQUERYBlobs(Connection connection,PreparedStatement preparedStatement,String ID,FileInputStream PHOTO) {
        try {
            preparedStatement = connection.prepareStatement(writeCYRYQUERYSql);
            preparedStatement.setBinaryStream(1, PHOTO,PHOTO.available());
            preparedStatement.setString(2, ID);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.commit();
        } catch (Exception e) {
        	log.info("更新 CYRYQUERY 表的照片的时候出现异常");
            e.printStackTrace();
        } finally{
        }
	}
	/**
	 * 
	 * @Description  查询 CYRYQUERY 表的数据
	 * @return  List<Map<String,Object>>
	 */
	public static int readCYRYQUERYBlobs() {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		int count = 0;
		try {
			connection = dataSource.getConnection();
			preparedStatement = connection.prepareStatement(readCYRYQUERYSql);
			resultSet = preparedStatement.executeQuery(readCYRYQUERYSql);
			while (resultSet.next()) {
				count ++;
				String ID = resultSet.getString(1);
				Blob PHOTO = resultSet.getBlob(2);
//				String IDCARD = resultSet.getString(3);
//				if(PHOTO.getBinaryStream().available() > 2097152) {// 只裁剪大于 2M 的图片
					InputStream in = PHOTO.getBinaryStream();
					String filePath = getCYRYQUERYPath(ID);
					System.out.println("导出图片："+filePath);
					File file = new File(filePath);
					if (!file.getParentFile().exists()) {
						log.info("该目录不存在 创建目录成功");
						if (!file.getParentFile().mkdirs()) {
							log.info("该文件不存在 创建文件成功");
						}
					}
					
					OutputStream out = new FileOutputStream(filePath);
					byte[] buffer = new byte[1024];
					int len = 0;
					while ((len = in.read(buffer)) != -1) {
						out.write(buffer, 0, len);
					}
					out.close();
					in.close();
					ImgUtils.scale(filePath, filePath, 126, 102, true);// 等比例缩放 输出缩放图片
					FileInputStream PHOTOIO = new FileInputStream(new File(filePath));
					writeCYRYQUERYBlobs(connection,preparedStatement,ID,PHOTOIO);
					log.info("从本地导入到数据库"+filePath);
					FileUtil.deleteFile(filePath);
//				}
			}
			resultSet.close();
			preparedStatement.close();
			connection.close();
			return count;
		} catch (SQLException e) {
			log.info("查询 CYRYQUERY 表的数据的时候出现 sql 异常");
			e.printStackTrace();
		} catch (IOException e) {
			log.info("查询 CYRYQUERY 表的数据的时候出现 IOException 异常");
			e.printStackTrace();
		} finally {
		}
		return count;
	}
	/**
	 * 
	 * @Description  更新 OLEXAM_CYRY_JBXX 表的照片
	 * @param ID ID
	 * @param PHOTO PHOTO
	 */
	public static void writeCYRYJBXXBlobs(String ID,Blob PHOTO) {
		Connection connection = null;
        PreparedStatement preparedStatement = null;
	        
        try {
            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement(writeCYRYJBXXSql);
            preparedStatement.setBlob(1, PHOTO);
            preparedStatement.setString(2, ID);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
        	log.info("更新 OLEXAM_CYRY_JBXX 表的照片的时候出现异常");
            e.printStackTrace();
        } finally{
        }
	}
	public static void writeCYRYJBXXBlobs(Connection connection,PreparedStatement preparedStatement,String ID,FileInputStream PHOTO) {
        try {
            preparedStatement = connection.prepareStatement(writeCYRYJBXXSql);
            preparedStatement.setBinaryStream(1,PHOTO, PHOTO.available());
            preparedStatement.setString(2, ID);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.commit();
        } catch (Exception e) {
        	log.info("更新 OLEXAM_CYRY_JBXX 表的照片的时候出现异常");
            e.printStackTrace();
        } finally{
        }
	}
	/**
	 * 获取 OLEXAM_CYRY_JBXX 表的数据
	 * @Description  TODO
	 * @return  List<Map<String,Object>>
	 */
	public static int readCYRYJBXXBlobs() {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		int count = 0;
		try {
			connection = dataSource.getConnection();
			preparedStatement = connection.prepareStatement(readCYRYJBXXSql);
			resultSet = preparedStatement.executeQuery(readCYRYJBXXSql);
			while (resultSet.next()) {
				count ++;
				String ID = resultSet.getString(1);
				Blob PHOTO = resultSet.getBlob(2);
//				String IDCARD = resultSet.getString(3);
//				if(PHOTO.getBinaryStream().available() > 2097152) {// 只裁剪大于 2M 的图片
					InputStream in = PHOTO.getBinaryStream();
					String filePath = getCYRYJBXXPath(ID);
					System.out.println("导出图片："+filePath);
					File file = new File(filePath);
					if (!file.getParentFile().exists()) {
						log.info("该目录不存在 创建目录成功");
						if (!file.getParentFile().mkdirs()) {
							log.info("该文件不存在 创建文件成功");
						}
					}
					OutputStream out = new FileOutputStream(filePath);
					byte[] buffer = new byte[1024];
					int len = 0;
					while ((len = in.read(buffer)) != -1) {
						out.write(buffer, 0, len);
					}
					out.close();
					in.close();
					ImgUtils.scale(filePath, filePath, 126, 102, true);// 等比例缩放 输出缩放图片
					FileInputStream PHOTOIO = new FileInputStream(new File(filePath));
					writeCYRYJBXXBlobs(connection,preparedStatement,ID,PHOTOIO);
					log.info("从本地导入到数据库"+filePath);
					FileUtil.deleteFile(filePath);
//				}
			}
			resultSet.close();
			preparedStatement.close();
			connection.close();
			return count;
		} catch (SQLException e) {
			 log.info("查询 OLEXAM_CYRY_JBXX 表的数据的时候出现 sql 异常");
			e.printStackTrace();
		} catch (IOException e) {
			 log.info("查询 OLEXAM_CYRY_JBXX 表的数据的时候出现 IOException 异常");
			e.printStackTrace();
		} finally {
		}
		return count;
	}
}
