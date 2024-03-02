package com.sky.controller.admin;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.graphbuilder.curve.MultiPath;
import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.UUID;

import org.aspectj.bridge.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/admin/common")
@Api(tags="通用接口")
@Slf4j
public class CommonController {


    @Autowired
    private AliOssUtil aliOssUtil;
    // 文件上传
    // 跟前端提交参数名保持一致
    @PostMapping("/upload")
    @ApiOperation("文件上传")
     public Result<String> upload(MultipartFile file){
        log.info("文件上传",file);
        try {
            // 原始文件名
            String originalFilename = file.getOriginalFilename();
            // 截取扩展名
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String objectname = UUID.randomUUID().toString() + extension;
            String upload = aliOssUtil.upload(file.getBytes(), objectname);
            return Result.success(upload);
        } catch (IOException e) {
            log.error("文件上传失败", e);
            e.printStackTrace();
        }
        return Result.error(MessageConstant.UPLOAD_FAILED);
     }
}
