package com.sky.utils;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import com.sky.properties.QiniuOssProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

/**
 * 七牛云文件上传
 */

@Slf4j
public class QiniuOssUtil {

  public static String upload(QiniuOssProperties qiniuOssProperties, MultipartFile file) {
      //构造一个带指定 Region 对象的配置类
      Configuration cfg = new Configuration(Region.huadong());

      UploadManager uploadManager = new UploadManager(cfg);
      //...生成上传凭证，然后准备上传
      String accessKey = qiniuOssProperties.getAccessKey();
      String secretKey = qiniuOssProperties.getSecretKey();
      String bucket = qiniuOssProperties.getBucketName();

      //默认不指定key的情况下，以文件内容的hash值作为文件名
      String key = "";
//         TODO:这里没有判断上传的文件类型。
      Auth auth = Auth.create(accessKey, secretKey);
      String upToken = auth.uploadToken(bucket);
      key = "sky/" +String.valueOf(UUID.randomUUID()) + file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
      String path = "";
      try {
          Response response = uploadManager.put(file.getBytes(), key, upToken);
          //解析上传成功的结果
          DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
          System.out.println(putRet.key);
          path = qiniuOssProperties.getHttAddressName() + putRet.key;
          System.out.println(putRet.hash);
      } catch (QiniuException ex) {
          ex.printStackTrace();
          if (ex.response != null) {
              System.err.println(ex.response);
              try {
                  String body = ex.response.toString();
                  System.err.println(body);
              } catch (Exception ignored) {
              }
          }
      } catch (IOException e) {
          throw new RuntimeException(e);
      }
      return path;
  }


}
