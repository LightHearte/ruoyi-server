package com.ruoyi.web.controller.video;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.framework.config.ServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

/**
 * 视频请求处理
 *
 * @author ctf
 */
@RestController
@RequestMapping("/video")
public class VideoController {
    private static final Logger log = LoggerFactory.getLogger(VideoController.class);

    @Autowired
    private ServerConfig serverConfig;

    private static final String FILE_DELIMETER = ",";

    /**
     * 上传视频请求（单个视频上传）
     */
    @PostMapping("/upload")
    public AjaxResult uploadFile(MultipartFile file) throws Exception
    {
        String SavePath="/home/video";
        try
        {
            //获取文件后缀，因此此后端代码可接收一切文件，上传格式前端限定
            String fileExt = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1).toLowerCase();
            String fileName = FileUploadUtils.upload(SavePath, file);

            // 重构文件名称
            System.out.println("代码启动成功");
            //UUID(全局唯一标识符)randomUUID(随机生成标识符)toString(转成字符串)replaceAll(替换字符方法，因为随机生成的里面包括了 - ，这里意思是把 - 全部换成空)
            String pikId = UUID.randomUUID().toString().replaceAll("-", "");
            //视频名字拼接：唯一标识符加上点，再加上上面的视频后缀也就是MP4之类的。就组成了现在的视频名字，比如这样：c7bbc1f9664947a287d35dd7cdc48a95.mp4
            String newVideoName = pikId + "." + fileExt;
            //保存视频的原始名字
            String videoNameText = file.getOriginalFilename();
            //保存视频url路径地址
            String videoUrl = SavePath + "\\" + newVideoName;
            //调用数据库接口插入数据库方法save，把视频原名，视频路径，视频的唯一标识码传进去存到数据库内（还未搭建数据库，暂存操作）
            //videoUploadMapper.save(videoNameText,videoUrl,newVideoName);
            //判断SavePath这个路径也就是需要保存视频的文件夹是否存在
            File filepath = new File(SavePath, file.getOriginalFilename());
            if (!filepath.getParentFile().exists()) {
                //如果不存在，就创建一个这个路径的文件夹。
                filepath.getParentFile().mkdirs();
            }
            //保存视频：把视频按照前端传来的地址保存进去，还有视频的名字用唯一标识符显示，需要其他的名字可改这
            File fileSave = new File(SavePath, newVideoName);
            //下载视频到文件夹中
            file.transferTo(fileSave);



            AjaxResult ajax = AjaxResult.success();
            ajax.put("url", videoUrl);
            ajax.put("fileName", fileName);
            ajax.put("newFileName", FileUtils.getName(newVideoName));
            ajax.put("originalFilename", file.getOriginalFilename());
            return ajax;
        }
        catch (Exception e)
        {
            return AjaxResult.error(e.getMessage());
        }
    }



}
